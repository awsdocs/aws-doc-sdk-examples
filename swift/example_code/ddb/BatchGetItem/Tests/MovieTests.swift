/*
   Tests for the `Movie` struct.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSDynamoDB
import ClientRuntime

@testable import batchgetitem

/// Perform tests on the `Movie` struct.
final class MovieTests: XCTestCase {
    static let region = "us-east-2"

    /// Class-wide setup function for the test case, which is run *once*
    /// before any tests are run.
    ///
    /// This function configures the AWS SDK log system to only log errors.
    override class func setUp() {
        super.setUp()
        SDKLoggingSystem.initialize(logLevel: .error)
    }

    /// A structure containing the results of validating a `Movie` or movie
    /// item by calling `verifyMovie()` or `verifyItem()`. For each property
    /// of a movie, the values in a `VerifyResults` record are `true` if the
    /// property matched the expected value, or `false` if not.
    struct VerifyResults {
        /// Whether or not the title matched when verified.
        let titleOK: Bool
        /// Whether or not the year matched when verified.
        let yearOK: Bool
        /// Whether or not the rating matched when verified.
        let ratingOK: Bool
        /// Whether or not the plot matched when verified.
        let plotOK: Bool

        /// Reports whether or not all properties matched when validated.
        ///
        /// - Returns: `true` if all properties matched. If any one or more
        /// properties fail to match, `false` is returned.
        func isValid() -> Bool {
            return titleOK && yearOK && ratingOK && plotOK
        }

        /// Returns a human-readable string with the results of the verify
        /// function.
        ///
        /// - Returns: A `String` whose value is "correct" if all properties
        ///   matched when verified. Otherwise, a string is returned listing
        ///   the mismatched properties.
        ///
        ///   For example, if the `year` and `rating` properties don't match,
        ///   this function returns the string "mismatched: year, rating".
        func toString() -> String {
            if self.titleOK && self.yearOK && self.ratingOK && self.plotOK == true {
                return "correct"
            }

            var sep = ""
            var str = "mismatch: "
            
            if self.titleOK == false {
                str += "\(sep)title"
                sep = ", "
            }
            if self.yearOK == false {
                str += "\(sep)year"
                sep = ", "
            }
            if self.ratingOK == false {
                str += "\(sep)rating"
                sep = ", "
            }
            if self.plotOK == false {
                str += "\(sep)plot"
            }
            return str
        }
    }

    /// Verifies that the specified `Movie`'s properties match the specified
    /// values.
    ///
    /// - Parameters:
    ///   - movie: The `Movie` to verify.
    ///   - expectedTitle: The expected value of `title` (`String`).
    ///   - expectedYear: The expected `year` value (`Int`).
    ///   - expectedRating: The movie's expected `rating` value (optional
    ///     `Double`).
    ///   - expectedPlot: The movie's expected `plot` value (optional
    ///     `String`).
    ///
    /// - Returns: A `VerifyResults` structure with the results of the
    ///   comparison.
    func verifyMovie(movie: Movie, expectedTitle: String, expectedYear: Int,
                     expectedRating: Double?, expectedPlot: String?) -> VerifyResults {
        return VerifyResults(
            titleOK: movie.title == expectedTitle,
            yearOK: movie.year == expectedYear,
            ratingOK: movie.info.rating == expectedRating,
            plotOK: movie.info.plot == expectedPlot
        )
    }

    /// Verifies that the values of the properties listed in the specified
    /// map of attribute names to their DynamoDB values match expectations.
    ///
    /// - Parameters:
    ///   - item: The array of attributes to verify
    ///     (`[String:DynamoDBTypes.AttributeValue]`).
    ///   - expectedTitle: The expected value of `title` (`String`).
    ///   - expectedYear: The expected `year` value (`Int`).
    ///   - expectedRating: The movie's expected `rating` value (optional
    ///     `Double`).
    ///   - expectedPlot: The movie's expected `plot` value (optional
    ///     `String`).
    ///
    /// - Returns: A `VerifyResults` structure with the results of the
    ///   comparison.
    func verifyItem(item: [String:DynamoDBClientTypes.AttributeValue], expectedTitle: String, expectedYear: Int,
                  expectedRating: Double?, expectedPlot: String?) -> VerifyResults {
        var titleOK = false
        var yearOK = false
        var ratingOK = false
        var plotOK = false

        let titleAttr = item["title"]
        let yearAttr = item["year"]
        let infoAttr = item["info"]

        if case .s(let titleVal) = titleAttr {
            titleOK = titleVal == expectedTitle
        }
        
        if case .n(let yearVal) = yearAttr {
            yearOK = Int(yearVal)! == expectedYear
        }

        if case .m(let infoVal) = infoAttr {
            let ratingAttr = infoVal["rating"]
            let plotAttr = infoVal["plot"]

            if ratingAttr == nil && expectedRating == nil {
                ratingOK = true
            }
            if plotAttr == nil && expectedPlot == nil {
                plotOK = true
            }

            if ratingAttr != nil, case .n(let ratingVal) = ratingAttr {
                ratingOK = Double(ratingVal) == expectedRating
            }
            if plotAttr != nil, case .s(let plotVal) = plotAttr {
                plotOK = plotVal == expectedPlot
            }
        }

        return VerifyResults(
            titleOK: titleOK,
            yearOK: yearOK,
            ratingOK: ratingOK,
            plotOK: plotOK
        )
    }

    /// Utility function to create a DynamoDB attribute list from the
    /// component values that make up a `Movie`.
    ///
    /// - Parameters:
    ///   - title: The name of the movie (optional `String`).
    ///   - year: The movie's release year (optional `Int`).
    ///   - rating: The movie's rating (optional `Double`).
    ///   - plot: The movie's plot (optional `String`).
    ///
    /// - Returns: An array mapping attribute names to their values.
    func makeItem(title: String, year: Int, rating: Double? = nil, plot: String? = nil)
                -> [Swift.String:DynamoDBClientTypes.AttributeValue] {
        var item: [Swift.String:DynamoDBClientTypes.AttributeValue] = [
            "year": .n(String(year)),
            "title": .s(title)
        ]

        var info: [Swift.String:DynamoDBClientTypes.AttributeValue] = [:]
        if rating != nil {
            info["rating"] = .n(String(rating!))
        }
        if plot != nil {
            info["plot"] = .s(plot!)
        }
        item["info"] = .m(info)
        return item
    }

    /// Test `init()` with separate input parameters for the title, year,
    /// rating, and plot, for each possible combination of non-`nil` values.
    func testSimpleInit() async throws {
        var movie: Movie
        var valid: VerifyResults

        movie = Movie(title: "Not a Real Movie", year: 2001)
        valid = self.verifyMovie(movie: movie,
                                 expectedTitle: "Not a Real Movie",
                                 expectedYear: 2001, expectedRating: nil,
                                 expectedPlot: nil)
        XCTAssertTrue(valid.isValid(), "Test 1: \(valid.toString())")

        movie = Movie(title: "Not a Real Movie", year: 2002, rating: 9.8)
        valid = self.verifyMovie(movie: movie,
                                 expectedTitle: "Not a Real Movie",
                                 expectedYear: 2002, expectedRating: 9.8,
                                 expectedPlot: nil)
        XCTAssertTrue(valid.isValid(), "Test 2: \(valid.toString())")

        movie = Movie(title: "Not a Real Movie", year: 2003, rating: 9.7,
                      plot: "A totally plotless movie.")
        valid = self.verifyMovie(movie: movie,
                                 expectedTitle: "Not a Real Movie",
                                 expectedYear: 2003, expectedRating: 9.7,
                                 expectedPlot: "A totally plotless movie.")
        XCTAssertTrue(valid.isValid(), "Test 3: \(valid.toString())")

        movie = Movie(title: "Not a Real Movie", year: 2004,
                      plot: "A totally plotless movie.")
        valid = self.verifyMovie(movie: movie,
                                 expectedTitle: "Not a Real Movie",
                                 expectedYear: 2004, expectedRating: nil,
                                 expectedPlot: "A totally plotless movie.")
        XCTAssertTrue(valid.isValid(), "Test 4: \(valid.toString())")
    }

    /// Test the `init()` variant that includes the `withItem` paramter.
    func testInitWithItem() async throws {
        var item: [String : DynamoDBClientTypes.AttributeValue]
        var movie: Movie
        var valid: VerifyResults

        item = makeItem(title: "Not a Real Movie", year: 2000)
        movie = try Movie(withItem: item)
        valid = self.verifyMovie(movie: movie,
                                 expectedTitle: "Not a Real Movie",
                                 expectedYear: 2000, expectedRating: nil,
                                 expectedPlot: nil)
        XCTAssertTrue(valid.isValid(), "Test 1: \(valid.toString())")

        item = makeItem(title: "Not a Real Movie", year: 2001, rating: 9.2)
        movie = try Movie(withItem: item)
        valid = self.verifyMovie(movie: movie,
                                 expectedTitle: "Not a Real Movie",
                                 expectedYear: 2001, expectedRating: 9.2,
                                 expectedPlot: nil)
        XCTAssertTrue(valid.isValid(), "Test 2: \(valid.toString())")

        item = makeItem(title: "Not a Real Movie", year: 2002, plot: "A boring film.")
        movie = try Movie(withItem: item)
        valid = self.verifyMovie(movie: movie,
                                 expectedTitle: "Not a Real Movie",
                                 expectedYear: 2002, expectedRating: nil,
                                 expectedPlot: "A boring film.")
        XCTAssertTrue(valid.isValid(), "Test 3: \(valid.toString())")

        item = makeItem(title: "Not a Real Movie", year: 2003, rating: 7.5,
                        plot: "A boring film.")
        movie = try Movie(withItem: item)
        valid = self.verifyMovie(movie: movie,
                                 expectedTitle: "Not a Real Movie",
                                 expectedYear: 2003, expectedRating: 7.5,
                                 expectedPlot: "A boring film.")
        XCTAssertTrue(valid.isValid(), "Test 4: \(valid.toString())")
    }

    /// Test the `getAsItem()` function by using every combination of possible
    /// input cases.
    func testGetAsItem() async throws {
        var item: [String : DynamoDBClientTypes.AttributeValue]
        var movie: Movie
        var valid: VerifyResults

        movie = Movie(title: "Not a Real Movie", year: 2020)
        item = try await movie.getAsItem()
        valid = self.verifyItem(item: item, expectedTitle: "Not a Real Movie", 
                                expectedYear: 2020, expectedRating: nil,
                                expectedPlot: nil)
        XCTAssertTrue(valid.isValid(), "Test 1: \(valid.toString())")

        movie = Movie(title: "Not a Real Movie", year: 2021, rating: 8.2)
        item = try await movie.getAsItem()
        valid = self.verifyItem(item: item, expectedTitle: "Not a Real Movie", 
                                expectedYear: 2021, expectedRating: 8.2,
                                expectedPlot: nil)
        XCTAssertTrue(valid.isValid(), "Test 2: \(valid.toString())")

        movie = Movie(title: "Not a Real Movie", year: 2022,
                             plot: "Some kind of story.")
        item = try await movie.getAsItem()
        valid = self.verifyItem(item: item, expectedTitle: "Not a Real Movie", 
                                expectedYear: 2022, expectedRating: nil,
                                expectedPlot: "Some kind of story.")
        XCTAssertTrue(valid.isValid(), "Test 3: \(valid.toString())")

        movie = Movie(title: "Not a Real Movie", year: 2023, rating: 3.5,
                             plot: "Some kind of story.")
        item = try await movie.getAsItem()
        valid = self.verifyItem(item: item, expectedTitle: "Not a Real Movie", 
                                expectedYear: 2023, expectedRating: 3.5,
                                expectedPlot: "Some kind of story.")
        XCTAssertTrue(valid.isValid(), "Test 4: \(valid.toString())")
    }
}