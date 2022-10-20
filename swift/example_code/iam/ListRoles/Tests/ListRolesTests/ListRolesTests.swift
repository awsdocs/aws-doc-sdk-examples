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

/// Perform tests on the S3Basics program. Call Amazon S3 service functions
/// using the global `ListRolesTests.serviceHandler` property, and manage
/// the demo cleanup handler object using the global
/// `ListRolesTests.demoCleanup` property.
final class ListRolesTests: XCTestCase {
    static var serviceHandler: ServiceHandler? = nil

    /// Class-wide setup function for the test case, which is run *once*
    /// before any tests are run.
    ///
    /// This function sets up the following:
    ///
    ///     Configures AWS SDK log system to only log errors.
    ///     Initializes the service handler, which is used to call
    ///     Amazon S3 functions.
    ///     Initializes the demo cleanup handler, which is used to
    ///     track the names of the files and buckets created by the tests
    ///     in order to remove them after testing is complete.
    override class func setUp() {
        let tdSem = TestWaiter(name: "Setup")
        super.setUp()
        SDKLoggingSystem.initialize(logLevel: .error)

        Task() {
            ListRolesTests.serviceHandler = await ServiceHandler()
            tdSem.signal()
        }
        tdSem.wait()
    }

    /// Called after **each** `testX()` function that follows, in order
    /// to clean up after each test is run.
    override func tearDown() async throws {
        let tdSem = TestWaiter(name: "Teardown")

        Task() {
            tdSem.signal()
        }
        tdSem.wait()
    }

    private func createTestRole(name: String? = nil) async throws -> String {
        let roleName = name ?? String.uniqueName()

        // Get information about the user running this example. This user
        // will be granted the new role.
        let user = try await ListRolesTests.serviceHandler!.getUser(name: nil)

        guard let userARN = user.arn else {
            throw ServiceHandlerError.noSuchUser 
        }

        // The policy document is a JSON string describing the role. For
        // details, see:
        // https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies.html.
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

        do {
            _ = try await ListRolesTests.serviceHandler!.createRole(name: roleName,
                            policyDocument: policyDocument)
            return roleName
        } catch {
            throw error
        }
    }

    func testListRoles() async throws {
        do {
            var createdRoles: [String] = []

            let previousRoles = try await ListRolesTests.serviceHandler!.listRoles()

            for _ in 1...5 {
                let newName = try await createTestRole()
                createdRoles.append(newName)
            }

            // Get the list of roles including the new ones we just created.
            let roles = try await ListRolesTests.serviceHandler!.listRoles()
            XCTAssertEqual(roles.count, createdRoles.count + previousRoles.count, "Incorrect number of roles created. Should be \(createdRoles.count + previousRoles.count) but is instead \(roles.count).")

            // Remove the created roles.            
            for role in createdRoles {
                _ = try await ListRolesTests.serviceHandler!.deleteRole(name: role)
            }
        } catch {
            throw error
        }
    }
}
