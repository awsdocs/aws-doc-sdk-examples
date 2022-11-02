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
/// using the global `ListUsersTests.serviceHandler` property. Also, manage the
/// demo cleanup handler object using the global `ListUsersTests.demoCleanup`
/// property.
final class CreateRoleTests: XCTestCase {
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
            CreateRoleTests.serviceHandler = await ServiceHandler()
            tdSem.signal()
        }
        tdSem.wait()
    }

    func testCreateRole() async throws {
        let name = String.uniqueName()

        do {
            // Get information about the user running this example. This user
            // will be granted the new role.
            let user = try await CreateRoleTests.serviceHandler!.getUser(name: nil)

            guard let userARN = user.arn else {
                return 
            }

            // Policy document for the new role.
            let policyDocument = """
            {
                "Version": "2012-10-17",
                "Statement": [{
                    "Effect": "Allow",
                    "Principal": {"AWS": "\(userARN)"},
                    "Action": "sts:AssumeRole"
                }]
            }
            """

            let createdID = try await CreateRoleTests.serviceHandler!.createRole(name: name, policyDocument: policyDocument)
            let getID = try await CreateRoleTests.serviceHandler!.getRoleID(name: name)

            XCTAssertTrue(createdID == getID, "Created role's ID (\(createdID)) doesn't match retrieved role ID (\(getID))")

            // Delete the created user.
            _ = try await CreateRoleTests.serviceHandler!.deleteRole(name: name)
        } catch {
            throw error
        }
    }
}
