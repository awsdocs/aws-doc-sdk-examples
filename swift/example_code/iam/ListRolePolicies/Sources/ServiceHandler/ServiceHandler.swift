// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 A class containing functions that interact with AWS services.
 */

// snippet-start:[iam.swift.listrolepolicies.handler]
// snippet-start:[iam.swift.listrolepolicies.handler.imports]
import AWSClientRuntime
import AWSIAM
import ClientRuntime
import Foundation

// snippet-end:[iam.swift.listrolepolicies.handler.imports]

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
    // snippet-start:[iam.swift.listrolepolicies.handler.init]
    public init() async throws {
        do {
            client = try await IAMClient()
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            throw error
        }
    }

    // snippet-end:[iam.swift.listrolepolicies.handler.init]

    /// Returns a list of all AWS Identity and Access Management (IAM) policy
    /// names.
    ///
    /// - Returns: An array of the available policy names.
    // snippet-start:[iam.swift.listrolepolicies.handler.ListRolePolicies]
    public func listRolePolicies(role: String) async throws -> [String] {
        var policyList: [String] = []

        // Use "Paginated" to get all the role policies.
        // This lets the SDK handle the 'isTruncated' in "ListRolePoliciesOutput".
        let input = ListRolePoliciesInput(
            roleName: role
        )
        let pages = client.listRolePoliciesPaginated(input: input)

        do {
            for try await page in pages {
                guard let policies = page.policyNames else {
                    print("Error: no role policies returned.")
                    continue
                }

                for policy in policies {
                    policyList.append(policy)
                }
            }
        } catch {
            print("ERROR: listRolePolicies:", dump(error))
            throw error
        }
        return policyList
    }
    // snippet-end:[iam.swift.listrolepolicies.handler.ListRolePolicies]
}

// snippet-end:[iam.swift.listrolepolicies.handler]
