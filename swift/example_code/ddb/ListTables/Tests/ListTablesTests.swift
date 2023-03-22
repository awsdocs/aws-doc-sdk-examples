/*
   Tests for the `listtables` example.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSDynamoDB
import ClientRuntime

@testable import listtables

/// Perform tests on the `getTableList()` function.
final class ListTablesTests: XCTestCase {
    static let region = "us-east-2"

    /// Class-wide setup function for the test case, which is run *once*
    /// before any tests are run.
    ///
    /// This function configures the AWS SDK log system to only log errors.
    override class func setUp() {
        super.setUp()
        SDKLoggingSystem.initialize(logLevel: .error)
    }

    func testWithNoTables() async {
        
    }
}