/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSIAM
import AWSClientRuntime
import ClientRuntime
import SwiftUtilities

@testable import ServiceHandler

/// Perform tests on the sample program. Call Amazon service functions
/// using the global `ListUsersTests.serviceHandler` property. Also, manage
/// the demo cleanup handler object using the global
/// `ListUsersTests.demoCleanup` property.
final class CreateUserTests: XCTestCase {
    static var serviceHandler: ServiceHandler? = nil

    /// Class-wide setup function for the test case, which is run *once*
    /// before any tests are run.
    ///
    /// This function sets up the following:
    ///
    ///     Configures the AWS SDK log system to only log errors.
    ///     Initializes the service handler, which is used to call
    ///     Amazon Identity and Access Management (IAM) functions.
    ///     Initializes the demo cleanup handler, which is used to
    ///     track the names of the files and buckets created by the tests
    ///     in order to remove them after testing is complete.
    override class func setUp() {
        let tdSem = TestWaiter(name: "Setup")
        super.setUp()
        SDKLoggingSystem.initialize(logLevel: .error)

        Task() {
            CreateUserTests.serviceHandler = await ServiceHandler()
            tdSem.signal()
        }
        tdSem.wait()
    }

    func testCreateUser() async throws {
        let name = String.uniqueName()

        do {
            let createdID = try await CreateUserTests.serviceHandler!.createUser(name: name)
            let getID = try await CreateUserTests.serviceHandler!.getUserID(name: name)

            XCTAssertTrue(createdID == getID, "Created user's ID (\(createdID)) doesn't match retrieved user's ID (\(getID))")

            // Delete the created user.
            _ = try await CreateUserTests.serviceHandler!.deleteUser(name: name)
        } catch {
            throw error
        }
    }
}
