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
/// using the global `ListRolePoliciesTests.serviceHandler` property. Also, manage
/// the demo cleanup handler object using the global
/// `ListRolePoliciesTests.demoCleanup` property.
final class ListRolePoliciesTests: XCTestCase {
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
            ListRolePoliciesTests.serviceHandler = await ServiceHandler()
            tdSem.signal()
        }
        tdSem.wait()
    }

    private func createTestRole(name: String? = nil) async throws -> String {
        let roleName = name ?? String.uniqueName()

        // Get information about the user running this example. This user
        // will be granted the new role.
        let user = try await ListRolePoliciesTests.serviceHandler!.getUser(name: nil)

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
            _ = try await ListRolePoliciesTests.serviceHandler!.createRole(name: roleName,
                            policyDocument: policyDocument)
            return roleName
        } catch {
            throw error
        }
    }

    private func createTestPolicy(role: String, name: String? = nil) async throws -> String {
        let policyName = name ?? String.uniqueName()

        // The policy document is a JSON string describing the policy. For
        // details, see:
        // https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies.html.
        let policyDocument = """
        {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Action": "logs:CreateLogGroup",
                    "Resource": "*"
                },
                {
                    "Effect": "Allow",
                    "Action": [
                        "dynamodb:DeleteItem",
                        "dynamodb:GetItem",
                        "dynamodb:PutItem",
                        "dynamodb:Scan",
                        "dynamodb:UpdateItem"
                    ],
                    "Resource": "arn:aws:dynmamodb:*"
                }
            ]
        }
        """

        do {
            _ = try await ListRolePoliciesTests.serviceHandler!.putRolePolicy(roleName: role,
                            policyName: policyName, policyDocument: policyDocument)

            return policyName
        } catch {
            throw error
        }
    }

    func testListRolePolicies() async throws {
        do {
            let testRole = try await createTestRole()

            // Add some policies to the role.

            var testPolicyNames: [String] = []
            for _ in 1...5 {
                let policyName = try await createTestPolicy(role: testRole)
                testPolicyNames.append(policyName)
            }

            // Get a list of the role's policies.

            var returnedPolicyNames = try await ListRolePoliciesTests.serviceHandler!.listRolePolicies(role: testRole)

            // Sort the arrays for easier comparing.

            testPolicyNames = testPolicyNames.sorted()
            returnedPolicyNames = returnedPolicyNames.sorted()

            // Compare the retrieved policy names to the expected values.

            XCTAssertEqual(testPolicyNames, returnedPolicyNames, "Retrieved policy list doesn't match the created policies.")

            // Delete the policies.

            for policy in testPolicyNames {
                _ = try await ListRolePoliciesTests.serviceHandler!.deleteRolePolicy(role: testRole, policyName: policy)
            }

            // Delete the role.

            _ = try await ListRolePoliciesTests.serviceHandler!.deleteRole(name: testRole)

        } catch {
            throw error
        }
    }
}
