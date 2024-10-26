// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
   A class containing functions that interact with AWS services.
*/

// snippet-start:[iam.swift.getpolicy.handler]
// snippet-start:[iam.swift.getpolicy.handler.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.getpolicy.handler.imports]

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
    // snippet-start:[iam.swift.getpolicy.handler.init]
    public init() async throws {
        do {
            client = try await IAMClient()
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            throw error
        }
    }
    // snippet-end:[iam.swift.getpolicy.handler.init]

    /// Returns information about the specified policy in a
    /// `IAMClientTypes.Policy` structure.
    ///
    /// - Parameter arn: The ARN of the policy to return.
    /// - Returns: A `IAMClientTypes.Policy` with the policy information.
    // snippet-start:[iam.swift.getpolicy.handler.GetPolicy]
    public func getPolicy(arn: String) async throws -> IAMClientTypes.Policy {
        let input = GetPolicyInput(
            policyArn: arn
        )
        do {
            let output = try await client.getPolicy(input: input)
            guard let policy = output.policy else {
                throw ServiceHandlerError.noSuchPolicy
            }
            return policy
        } catch {
            print("ERROR: getPolicy:", dump(error))
            throw error
        }
    }
    // snippet-end:[iam.swift.getpolicy.handler.GetPolicy]
}
// snippet-end:[iam.swift.getpolicy.handler]
