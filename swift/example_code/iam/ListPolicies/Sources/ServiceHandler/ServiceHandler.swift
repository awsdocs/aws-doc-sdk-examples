// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 A class containing functions that interact with AWS services.
 */

// snippet-start:[iam.swift.listpolicies.handler]
// snippet-start:[iam.swift.listpolicies.handler.imports]
import AWSClientRuntime
import AWSIAM
import ClientRuntime
import Foundation

// snippet-end:[iam.swift.listpolicies.handler.imports]

/// Errors returned by `ServiceHandler` functions.
enum ServiceHandlerError: Error {
    case noSuchGroup
    case noSuchRole
    case noSuchPolicy
    case noSuchUser
}

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class ServiceHandler {
    public let client: IAMClient

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls used for the example.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[iam.swift.listpolicies.handler.init]
    public init() async throws {
        do {
            client = try await IAMClient()
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            throw error
        }
    }
    // snippet-end:[iam.swift.listpolicies.handler.init]

    /// Returns a list of the names of all AWS Identity and Access Management
    /// (IAM) policy names.
    ///
    /// - Returns: An array of user records.
    // snippet-start:[iam.swift.listpolicies.handler.ListPolicies]
    public func listPolicies() async throws -> [MyPolicyRecord] {
        var policyList: [MyPolicyRecord] = []

        // Use "Paginated" to get all the policies.
        // This lets the SDK handle the 'isTruncated' in "ListPoliciesOutput".
        let input = ListPoliciesInput()
        let output = client.listPoliciesPaginated(input: input)

        do {
            for try await page in output {
                guard let policies = page.policies else {
                    print("Error: no policies returned.")
                    continue
                }

                for policy in policies {
                    guard let name = policy.policyName,
                          let id = policy.policyId,
                          let arn = policy.arn
                    else {
                        throw ServiceHandlerError.noSuchPolicy
                    }
                    policyList.append(MyPolicyRecord(name: name, id: id, arn: arn))
                }
            }
        } catch {
            print("ERROR: listPolicies:", dump(error))
            throw error
        }

        return policyList
    }
    // snippet-end:[iam.swift.listpolicies.handler.ListPolicies]
}

// snippet-end:[iam.swift.listpolicies.handler]
