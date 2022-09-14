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
/// using the global `ListPoliciesTests.serviceHandler` property, and manage
/// the demo cleanup handler object using the global
/// `ListPoliciesTests.demoCleanup` property.
final class ListPoliciesTests: XCTestCase {
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
            ListPoliciesTests.serviceHandler = await ServiceHandler()
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

    private func createTestPolicy(name: String? = nil) async throws -> MyPolicyRecord {
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
            let policy = try await ListPoliciesTests.serviceHandler!.createPolicy(name: policyName,
                            policyDocument: policyDocument)

            return policy
        } catch {
            throw error
        }
    }

    func testListPolicies() async throws {
        do {
            var createdPolicies: [MyPolicyRecord] = []

            let previousPolicies = try await ListPoliciesTests.serviceHandler!.listPolicies()

            for _ in 1...5 {
                let newPolicy = try await createTestPolicy()
                createdPolicies.append(newPolicy)
            }

            // Get the list of policies including the new ones we just created.
            var policies = try await ListPoliciesTests.serviceHandler!.listPolicies()
            XCTAssertTrue(policies.count == createdPolicies.count + previousPolicies.count, "Incorrect number of policies created. Should be \(createdPolicies.count + previousPolicies.count) but is instead \(policies.count).")

            // Remove the created policies.            
            for policy in createdPolicies {
                _ = try await ListPoliciesTests.serviceHandler!.deletePolicy(policy: policy)
            }
        } catch {
            throw error
        }
    }
}
