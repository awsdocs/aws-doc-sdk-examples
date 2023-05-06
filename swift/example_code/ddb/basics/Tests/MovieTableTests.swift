/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSDynamoDB
import ClientRuntime
import SwiftUtilities

@testable import MovieList

/// Perform tests on the S3Basics program. Call Amazon S3 service functions
/// using the global `BasicsTests.serviceHandler` property, and manage the demo
/// cleanup handler object using the global `BasicsTests.demoCleanup` property.
final class MovieTableTests: XCTestCase {
    /// Class-wide setup function for the test case, which is run *once* before
    /// any tests are run.
    /// 
    /// This function sets up the following:
    ///
    ///     Configures the AWS SDK log system to only log errors.
    ///     Instantiates the service handler, which is used to call
    ///     Amazon S3 functions.
    ///     Instantiates the demo cleanup handler, which is used to
    ///     track the names of the files and buckets created by the tests
    ///     in order to remove them after testing is complete.
    override class func setUp() {
        super.setUp()
        SDKLoggingSystem.initialize(logLevel: .error)
    }

    func testInit() async throws {
        // Test creating a table with an automatically-generated table name.

        let list1 = try await MovieTable(tableName: String.uniqueName(withPrefix: "ddb-movies-sample", maxDigits: 6))
        XCTAssertTrue(list1.tableName.starts(with:"ddb-movies-sample"), "Generated table name is not what is expected.")

        do {
            try await list1.awaitTableActive()
        } catch {
            XCTFail("Table 1 doesn't exist or other error waiting for new table to become active.")
            return
        }
        try await list1.deleteTable()

        // Repeat the test with a specific table name.

        let list2 = try await MovieTable(tableName: String.uniqueName(withPrefix: "booberry", maxDigits: 6))
        XCTAssertTrue(list2.tableName.starts(with:"booberry"), "Table name is not what is expected.")

        do {
            try await list2.awaitTableActive()
        } catch {
            XCTFail("Table 2 doesn't exist or other error waiting for new table to become active.")
            return
        }
        try await list2.deleteTable()
    }
}