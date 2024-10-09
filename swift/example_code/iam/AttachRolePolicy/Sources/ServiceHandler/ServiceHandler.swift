// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
   A class containing functions that interact with AWS services.
*/

// snippet-start:[iam.swift.attachrolepolicy.handler]
// snippet-start:[iam.swift.attachrolepolicy.handler.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.attachrolepolicy.handler.imports]

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
    // snippet-start:[iam.swift.attachrolepolicy.handler.init]
    public init() async throws {
        do {
            client = try await IAMClient()
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            throw error
        }
    }
    // snippet-end:[iam.swift.attachrolepolicy.handler.init]

    /// Attaches the specified managed policy to the given role.
    ///
    /// - Parameters:
    ///   - role: The name of the role to attach the policy to.
    ///   - policyArn: The ARN of the policy to attach.
    ///
    // snippet-start:[iam.swift.attachrolepolicy.handler.AttachRolePolicy]
    public func attachRolePolicy(role: String, policyArn: String) async throws {
        let input = AttachRolePolicyInput(
            policyArn: policyArn,
            roleName: role
        )
        do {
            _ = try await client.attachRolePolicy(input: input)
        } catch {
            print("ERROR: Attaching a role policy:", dump(error))
            throw error
        }
    }
    // snippet-end:[iam.swift.attachrolepolicy.handler.AttachRolePolicy]
}
// snippet-end:[iam.swift.attachrolepolicy.handler]
