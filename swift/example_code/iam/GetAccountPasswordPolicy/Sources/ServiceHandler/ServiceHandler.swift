/*
   A class containing functions that interact with AWS services.
   
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[iam.swift.getaccountpasswordpolicy.handler]
// snippet-start:[iam.swift.getaccountpasswordpolicy.handler.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.getaccountpasswordpolicy.handler.imports]

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
    // snippet-start:[iam.swift.getaccountpasswordpolicy.handler.init]
    public init() async {
        do {
            client = try IamClient(region: "AWS_GLOBAL")
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            exit(1)
        }
    }
    // snippet-end:[iam.swift.getaccountpasswordpolicy.handler.init]

    /// Returns information about the account's password policy. Throws an
    /// exception if no password policy is set on the account.
    ///
    /// - Returns: A `IamClientTypes.PasswordPolicy` with the account password
    ///   policy information.
    // snippet-start:[iam.swift.getaccountpasswordpolicy.handler.getaccountpasswordpolicy]
    public func getAccountPasswordPolicy() async throws -> IamClientTypes.PasswordPolicy {
        let input = GetAccountPasswordPolicyInput()
        do {
            let output = try await client.getAccountPasswordPolicy(input: input)

            guard let policy = output.passwordPolicy else {
                throw ServiceHandlerError.noSuchPolicy
            }
            return policy
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.getaccountpasswordpolicy.handler.getaccountpasswordpolicy]
}
// snippet-end:[iam.swift.getaccountpasswordpolicy.handler]
