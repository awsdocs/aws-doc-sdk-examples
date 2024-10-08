// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Tests for the `listtables` example for Amazon DynamoDB using the AWS SDK
// for Swift.
//

import XCTest
import Foundation
import AWSDynamoDB
import ClientRuntime

@testable import listtables

// A list of fake table names to return.

let fakeTableNames = [
    "Promise-Last-Arrive",
    "Start-Tear-Till",
    "Belgium-Your-Step",
    "Stand-Control-Fresh",
    "Fingers-Ohio-Prepare",
    "Dinner-France-Tear",
    "Thing-Work-Major",
    "Raise-Addition-Triangle",
    "Except-Short-Science",
    "Clock-Airplane-Daughter",
    "Learn-Look-Measure",
    "Game-Sweden-Observe",
    "Paris-Build-Boat",
    "Known-Birds-Cloth",
    "Said-Charge-Height",
    "Talk-Partial-That",
    "Keep-Nepal-Shirt",
    "Dice-Texas-Word",
    "They-Grass-Nose",
    "Toward-Under-Weather",
    "Space-Strange-Share",
    "Phrase-Dice-Little",
    "Hers-Start-Pounds",
    "Held-Escape-Women",
    "Rush-Last-Rope",
    "Spell-Line-Said",
    "Smiled-Land-Hers",
    "Done-Rain-Well",
    "Lake-Among-Moment",
    "Listen-Swim-Feel",
    "Industry-Saturn-Hunting",
    "Mayor-Hope-Farm",
    "Banker-Brazil-Where",
    "Mean-Italy-Become",
    "Dollars-Swim-Month",
    "Wall-Feet-Teacher",
    "Practice-Mother-Able",
    "House-Branches-Soldier",
    "Quick-Page-Warm",
    "Game-Language-Fish",
    "Field-Greek-Seem",
    "Chair-King-Farmers",
    "Company-Dark-Sight",
    "Fraction-Ring-Monday",
    "Strike-Attempt-Bottle"
]

/// An implementation of the `DatabaseSession` protocol that returns table
/// names from an array rather than by fetching the list from DynamoDB.
public struct MockDBSession: DatabaseSession {
    var testTableNames: [String] = []

    /// Initialize the mock database session.
    ///
    /// - Parameter tables: An optional array of strings providing the list of
    ///   table names to return when `listTables()` is called.
    ///
    /// If `tables` isn't specified, the array `fakeTableNames` is used.
    init(tables: [String]) {
        testTableNames = tables
    }

    /// Mock version of the DynamoDB client's `listTables()` function. Returns
    /// values from the string array `fakeTableNames` or the one specified as
    /// an optional input when creating the `MockDBSession`.
    public func listTables(input: ListTablesInput) async throws -> [String] {
         var maxTables = testTableNames.count
        if let limit = input.limit {
            maxTables = Swift.min(limit, maxTables)
        }

        return Array(testTableNames[0..<maxTables])
    }
}

/// Perform tests on the `getTableList()` function.
final class ListTablesTests: XCTestCase {
 
    /// Class-wide setup function for the test case, which is run *once*
    /// before any tests are run.
    ///
    /// This function configures the AWS SDK log system to only log errors.
    override class func setUp() {
        super.setUp()
    }

    /// Test fetching a list of table names that's long enough to require
    /// multiple calls to the `listTables()` function.
    func testWithTables() async throws {
        let session = MockDBSession(tables: fakeTableNames)
        let dbManager = DatabaseManager(session: session)

        let list = try await dbManager.getTableList()
        XCTAssertEqual(list, fakeTableNames, "Returned list of table names doesn't match original list.")
    }

    /// Test fetching an empty table list.
    func testWithNoTables() async throws {
        let session = MockDBSession(tables: [])
        let dbManager = DatabaseManager(session: session)

        let list = try await dbManager.getTableList()
        XCTAssertEqual(list.count, 0, "List is not empty but should be.")
    }

    /// Test fetching a list of table names that's short enough to only
    /// require one call to `listTables()`.    
    func testWithOneChunk() async throws {
        let testNames = Array(fakeTableNames[0...20])
        let session = MockDBSession(tables: testNames)
        let dbManager = DatabaseManager(session: session)

        let list = try await dbManager.getTableList()
        XCTAssertEqual(list, testNames, "Returned list of table names doesn't match original list.")
    }
}
