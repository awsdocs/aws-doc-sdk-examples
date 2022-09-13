/*
   A class containing functions that interact with AWS services.
   
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[iam.swift.createrole.handler]
// snippet-start:[iam.swift.createrole.handler.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.createrole.handler.imports]

// snippet-start:[iam.swift.createrole.enum.service-error]

/// Errors returned by `ServiceHandler` functions.
enum ServiceHandlerError: Error {
    case noSuchUser            /// No matching user found.
    case noSuchRole            /// No matching role found, or unable to create the role.
}
// snippet-end:[iam.swift.createrole.enum.service-error]

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class ServiceHandler {
    public let client: IamClient

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls used for the example. The region string
    /// `AWS_GLOBAL` is used because users are shared across all regions.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[iam.swift.createrole.handler.init]
    public init() async {
        do {
            client = try IamClient(region: "AWS_GLOBAL")
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            exit(1)
        }
    }
    // snippet-end:[iam.swift.createrole.handler.init]

    /// Create a new IAM role.
    ///
    /// - Parameter name: The name of the new IAM role.
    ///
    /// - Returns: The ID of the newly created role.
    // snippet-start:[iam.swift.createrole.handler.createrole]
    public func createRole(name: String, policyDocument: String) async throws -> String {
        let input = CreateRoleInput(
            assumeRolePolicyDocument: policyDocument,
            roleName: name
        )
        do {
            let output = try await client.createRole(input: input)
            guard let role = output.role else {
                throw ServiceHandlerError.noSuchRole
            }
            guard let id = role.roleId else {
                throw ServiceHandlerError.noSuchRole
            }
            return id
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.createrole.handler.createrole]

    /// Get information about the specified user
    ///
    /// - Parameter name: A `String` giving the name of the user to get. If
    ///   this parameter is `nil`, the default user's information is returned.
    /// - Returns: An `IamClientTypes.User` record describing the user.
    // snippet-start:[iam.swift.createrole.handler.getuser]
    public func getUser(name: String?) async throws -> IamClientTypes.User {
        let input = GetUserInput(
            userName: name
        )
        do {
            let output = try await client.getUser(input: input)
            guard let user = output.user else {
                throw ServiceHandlerError.noSuchUser
            }
            return user
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.createrole.getuser]
}
