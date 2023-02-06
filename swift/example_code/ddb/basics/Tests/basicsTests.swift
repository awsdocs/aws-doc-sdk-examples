/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSDynamoDB
import AWSClientRuntime
import ClientRuntime
import SwiftUtilities

/// Perform tests on the S3Basics program. Call Amazon S3 service functions
/// using the global `BasicsTests.serviceHandler` property, and manage the demo
/// cleanup handler object using the global `BasicsTests.demoCleanup` property.
final class BasicsTests: XCTestCase {

    /// Class-wide setup function for the test case, which is run *once* before
    /// any tests are run.
    /// 
    /// This function initializes logging with the desired configuration.
    override class func setUp() {
        super.setUp()
        SDKLoggingSystem.initialize(logLevel: .error)
    }
}