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
/// using the global `ListAttachedRolePoliciesTests.serviceHandler` property,
/// and manage the demo cleanup handler object using the global
/// `ListAttachedRolePoliciesTests.demoCleanup` property.
final class ListAttachedRolePoliciesTests: XCTestCase {
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
            ListAttachedRolePoliciesTests.serviceHandler = await ServiceHandler()
            tdSem.signal()
        }
        tdSem.wait()
    }

    /// Creates a test role.
    ///
    /// - Parameters:
    ///   - name: The name of the new policy.
    ///   - policyDocument: The policy document to assign to the new policy.
    private func createTestRole(name: String? = nil) async throws -> String {
        let roleName = name ?? String.uniqueName()

        // Get information about the user running this example. This user
        // will be granted the new role.
        let user = try await ListAttachedRolePoliciesTests.serviceHandler!.getUser(name: nil)

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
            _ = try await ListAttachedRolePoliciesTests.serviceHandler!.createRole(name: roleName,
                            policyDocument: policyDocument)
            return roleName
        } catch {
            throw error
        }
    }

    private func createTestPolicy(name: String? = nil) async throws -> String {
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
            let policy = try await ListAttachedRolePoliciesTests.serviceHandler!.createPolicy(policyName: policyName,
                                policyDocument: policyDocument)

            guard let arn = policy.arn else {
                throw ServiceHandlerError.noSuchPolicy
            }
            return arn
        } catch {
            throw error
        }
    }

    func testListAttachedRolePolicies() async throws {
        do {
            let testRole = try await createTestRole()

            // Add some policies to the role.

            var testPolicyARNs: [String] = []
            for _ in 1...5 {
                let policyArn = try await createTestPolicy()
                testPolicyARNs.append(policyArn)
            }

            // Attach the policies to the test role.

            for policyArn in testPolicyARNs {
                try await ListAttachedRolePoliciesTests.serviceHandler!.attachRolePolicy(role: testRole, policyArn: policyArn)
            }

            // Retrieve the role's policy list.

            var attachedPolicies = try await ListAttachedRolePoliciesTests.serviceHandler!.listAttachedRolePolicies(role: testRole)

            // The lengths of the two lists should match.

            XCTAssertEqual(testPolicyARNs.count, attachedPolicies.count, "Policy count mismatch: should be \(testPolicyARNs.count) but \(attachedPolicies.count) found")

            // Sort the arrays for easier comparing.

            testPolicyARNs = testPolicyARNs.sorted()
            attachedPolicies = attachedPolicies.sorted(by: { $0.policyArn! < $1.policyArn! })

            // Compare the retrieved policy names to the expected values.

            var index = 0
            for attachedPolicy in attachedPolicies {
                XCTAssertEqual(testPolicyARNs[index], attachedPolicy.policyArn, 
                    "Policy list mismatch: \(testPolicyARNs[index]) expected but \(attachedPolicy.policyArn ?? "<no name>") found")
                index += 1
            }

            // Delete the policies.

            for arn in testPolicyARNs {
                _ = try await ListAttachedRolePoliciesTests.serviceHandler!.detachRolePolicy(role: testRole, policyArn: arn)
                _ = try await ListAttachedRolePoliciesTests.serviceHandler!.deletePolicy(policyArn: arn)
            }

            // Delete the role.

            _ = try await ListAttachedRolePoliciesTests.serviceHandler!.deleteRole(name: testRole)
        } catch {
            throw error
        }
    }
}
