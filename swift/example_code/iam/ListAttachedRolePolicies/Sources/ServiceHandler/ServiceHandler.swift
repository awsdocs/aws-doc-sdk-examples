/*
   A class containing functions that interact with AWS services.
   
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[iam.swift.listattachedrolepolicies.handler]
// snippet-start:[iam.swift.listattachedrolepolicies.handler.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.listattachedrolepolicies.handler.imports]

/// Errors returned by `ServiceHandler` functions.
enum ServiceHandlerError: Error {
    case noSuchGroup
    case noSuchRole
    case noSuchPolicy
    case noSuchUser
}

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class ServiceHandler {
    public let client: IamClient

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls used for the example. The region string
    /// `AWS_GLOBAL` is used because users are shared across all regions.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[iam.swift.listattachedrolepolicies.handler.init]
    public init() async {
        do {
            client = try IamClient(region: "AWS_GLOBAL")
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            exit(1)
        }
    }
    // snippet-end:[iam.swift.listattachedrolepolicies.handler.init]

    // snippet-start:[iam.swift.listattachedrolepolicies.handler.listattachedrolepolicies]

    /// Returns a list of IAM policies attached to the role.
    ///
    /// - Parameter role: The IAM role to return the policy list for.
    /// - Returns: An array of `IamClientTypes.AttachedPolicy` objects describing each
    ///   managed policy attached to the role.
    public func listAttachedRolePolicies(role: String) async throws -> [IamClientTypes.AttachedPolicy] {
        var policyList: [IamClientTypes.AttachedPolicy] = []
        var marker: String? = nil
        var isTruncated: Bool
        
        repeat {
            let input = ListAttachedRolePoliciesInput(
                marker: marker,
                roleName: role
            )
            let output = try await client.listAttachedRolePolicies(input: input)
            
            guard let attachedPolicies = output.attachedPolicies else {
                return policyList
            }

            for attachedPolicy in attachedPolicies {
                policyList.append(attachedPolicy)
            }
            marker = output.marker
            isTruncated = output.isTruncated
        } while isTruncated == true
        return policyList
    }
    // snippet-end:[iam.swift.listattachedrolepolicies.handler.listattachedrolepolicies]

}
// snippet-end:[iam.swift.listattachedrolepolicies.handler]
