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
/// using the global `GetAccountPasswordPolicyTests.serviceHandler` property, and manage
/// the demo cleanup handler object using the global
/// `GetAccountPasswordPolicyTests.demoCleanup` property.
final class GetAccountPasswordPolicyTests: XCTestCase {
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
            GetAccountPasswordPolicyTests.serviceHandler = await ServiceHandler()
            tdSem.signal()
        }
        tdSem.wait()
    }

    private func createTestPolicy(name: String? = nil) async throws -> IamClientTypes.Policy {
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
            let policy = try await GetAccountPasswordPolicyTests.serviceHandler!.createPolicy(name: policyName,
                            policyDocument: policyDocument)

            return policy
        } catch {
            throw error
        }
    }

    func testGetAccountPasswordPolicy() async throws {
        do {
            let createdPolicy = try await createTestPolicy()

            guard let createdARN = createdPolicy.arn else {
                XCTFail("Created test policy does not have a valid ARN.")
                return
            }

            let policy = try await GetAccountPasswordPolicyTests.serviceHandler!.getAccountPasswordPolicy(arn: createdARN)

            XCTAssertEqual(createdPolicy.policyName, policy.policyName,
                "Retrieved policy name does not match. Expected \(createdPolicy.policyName ?? "<none>") but got \(policy.policyName ?? "<none>")")
            XCTAssertEqual(createdPolicy.policyId, policy.policyId,
                "Retrieved policy ID does not match. Expected \(createdPolicy.policyId ?? "<none>") but got \(policy.policyId ?? "<none>")")
            XCTAssertEqual(createdPolicy.arn, policy.arn,
                "Retrieved ARN does not match. Expected \(createdPolicy.arn ?? "<none>") but got \(policy.arn ?? "<none>")")
            XCTAssertEqual(createdPolicy.createDate, policy.createDate,
                "Retrieved creation date does not match. Expected \(createdPolicy.createDate ?? Date(timeIntervalSince1970: 0)) but got \(policy.createDate ?? Date(timeIntervalSince1970: 0))")

            _ = try await GetAccountPasswordPolicyTests.serviceHandler!.deletePolicy(policy: createdPolicy)
        } catch {
            throw error
        }
    }
}
