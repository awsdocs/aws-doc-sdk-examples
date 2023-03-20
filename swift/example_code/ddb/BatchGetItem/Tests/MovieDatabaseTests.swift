/*
   Tests for the `MovieDatabase` class.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSDynamoDB
import ClientRuntime

@testable import batchgetitem

/// Perform tests on the `MovieDatabase` class.
final class MovieDatabaseTests: XCTestCase {
    let region = "us-east-2"
    let jsonPath: String = "../../../../resources/sample_files/movies.json"

    /// Class-wide setup function for the test case, which is run *once*
    /// before any tests are run.
    ///
    /// This function configures the AWS SDK log system to only log errors.
    override class func setUp() {
        super.setUp()

    }

    /// Test attempting to initialize the database with a non-valid JSON file
    /// path name.
    func testInitWithInvalidJSONPath() async {
        do {
            _ = try await MovieDatabase(jsonPath: "path/that/does/not/exist.json")
        } catch {
            XCTAssertEqual(2, error._code, "Unexpected error returned when creating database.")
            return
        }
        XCTFail("Attempt to initialize the database succeeded with invalid JSON path specified.")
    }

    /// Test that creating and deleting the movie database table works as
    /// expected.
    func testInitAndDelete() async {
        var database: MovieDatabase

        do {
            database = try await MovieDatabase(jsonPath: jsonPath)
        } catch {
            XCTFail("Error creating movie database: \(error.localizedDescription)")
            return
        }

        // Delete the movie table.

        do {
            try await database.deleteTable()
        } catch {
            XCTFail("Error deleting movie table: \(error.localizedDescription)")
        }
    }

    /// Test that getting the table's status reports the correct value and
    /// does not generate an error unexpectedly.
    func testGetTableStatus() async {
        var database: MovieDatabase

        do {
            database = try await MovieDatabase(jsonPath: jsonPath)
        } catch {
            XCTFail("Error creating movie database: \(error.localizedDescription)")
            return
        }

        var dbReady = false

        while dbReady == false {
            do {
                let status = try await database.getTableStatus()

                if status == .active {
                    dbReady = true
                }
            } catch MovieError.StatusUnknown {
                XCTFail("Unexpected status detected on table.")
                return
            } catch MovieError.TableNotFound {
                // The table doesn't exist yet so ignore the error and wait
                // for it to show up.
            } catch {
                XCTFail("Unexpected error response from getTableStatus(): \(error.localizedDescription)")
                return
            }
        }

        // Delete the table.

        do {
            try await database.deleteTable()
        } catch {
            XCTFail("Unexpected error deleting movie table: \(error.localizedDescription)")
        }
    }

    /// Test that the tableExists() function returns the expected value and
    /// does not experience an error.
    func testTableExists() async {
        var database: MovieDatabase

        // Create the movie table.

        do {
            database = try await MovieDatabase(jsonPath: jsonPath)
        } catch {
            XCTFail("Error creating movie database: \(error.localizedDescription)")
            return
        }

        // Call tableExists() to see if the table exists.

        do {
            let exists = try await database.tableExists()
            XCTAssertTrue(exists, "Table doesn't exist after creating it.")
        } catch {
            XCTFail("Unexpected error calling tableExists(): \(error.localizedDescription)")
        }

        // Delete the table to clean up after the test.

        do {
            try await database.deleteTable()
        } catch {
            XCTFail("Unexpected error deleting table: \(error.localizedDescription)")
        }
    }

    /// Test batchGet() with a set of valid movie keys.
    func testBatchGetMatches() async {
        var database: MovieDatabase

        // Create the movie table.

        do {
            database = try await MovieDatabase(jsonPath: jsonPath)
        } catch {
            XCTFail("Error creating movie database: \(error.localizedDescription)")
            return
        }

        // Ask for information about three movies and ensure we get three
        // responses.

        do {
            let movies = try await database.batchGet(keys: [
                (title: "The Shawshank Redemption", year: 1994),
                (title: "Titanic", year: 1997),
                (title: "The Hobbit: An Unexpected Journey", year: 2012)
            ])
            XCTAssertEqual(movies.count, 3, "Incorrect number of matching movies returned.")
        } catch {
            XCTFail("Unexpected error from batchGet(): \(error.localizedDescription)")
        }

        // Delete the table to clean up after the test.

        do {
            try await database.deleteTable()
        } catch {
            XCTFail("Unexpected error deleting table: \(error.localizedDescription)")
        }
    }

    /// Test batchGet() with some valid movie keys and some keys that will not
    /// match.
    func testBatchGetSomeMatches() async {
        var database: MovieDatabase

        // Create the movie table.

        do {
            database = try await MovieDatabase(jsonPath: jsonPath)
        } catch {
            XCTFail("Error creating movie database: \(error.localizedDescription)")
            return
        }

        // Ask about three movies, but one of them with the wrong year so we
        // get only two responses.

        do {
            let movies = try await database.batchGet(keys: [
                (title: "The Shawshank Redemption", year: 1994),
                (title: "Titanic", year: 1776),
                (title: "The Hobbit: An Unexpected Journey", year: 2012)
            ])
            XCTAssertEqual(movies.count, 2, "Incorrect number of matching movies returned.")
        } catch {
            XCTFail("Unexpected error from batchGet(): \(error.localizedDescription)")
        }

        // Delete the table to clean up after the test.

        do {
            try await database.deleteTable()
        } catch {
            XCTFail("Unexpected error deleting table: \(error.localizedDescription)")
        }
    }

    /// Test batchGet() with a set of movie keys that will yield no matches.
    func testBatchGetNoMatches() async {
        var database: MovieDatabase

        // Create the movie table.

        do {
            database = try await MovieDatabase(jsonPath: jsonPath)
        } catch {
            XCTFail("Error creating movie database: \(error.localizedDescription)")
            return
        }

        // Ask about two movies that don't exist. One has both a title that
        // doesn't exist and a year far in the future, and the other is a
        // title that exists with a year in the past.

        do {
            let movies = try await database.batchGet(keys: [
                (title: "This Is Not a Real Movie", year: 3050),
                (title: "Titanic", year: 1776)
            ])
            XCTAssertEqual(movies.count, 0, "Movies were found when no matches expected.")
        } catch {
            XCTFail("Unexpected error from batchGet(): \(error.localizedDescription)")
        }

        // Delete the table to clean up after the test.

        do {
            try await database.deleteTable()
        } catch {
            XCTFail("Unexpected error deleting table: \(error.localizedDescription)")
        }
    }
}