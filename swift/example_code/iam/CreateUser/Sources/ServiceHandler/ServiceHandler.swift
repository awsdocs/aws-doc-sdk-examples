// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
   A class containing functions that interact with AWS services.
*/

// snippet-start:[iam.swift.createuser.handler]
// snippet-start:[iam.swift.createuser.handler.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.createuser.handler.imports]

// snippet-start:[iam.swift.createuser.enum.service-error]

/// Errors returned by `ServiceHandler` functions.
enum ServiceHandlerError: Error {
    case noSuchUser             /// No matching user found, or unable to create the user.
}
// snippet-end:[iam.swift.createuser.enum.service-error]

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class ServiceHandler {
    public let client: IAMClient

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls used for the example.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[iam.swift.createuser.handler.init]
    public init() async throws {
        do {
            client = try await IAMClient()
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            throw error
        }
    }
    // snippet-end:[iam.swift.createuser.handler.init]

    /// Create a new AWS Identity and Access Management (IAM) user.
    ///
    /// - Parameter name: The user's name.
    ///
    /// - Returns: The ID of the newly created user.
    // snippet-start:[iam.swift.createuser.handler.CreateUser]
    public func createUser(name: String) async throws -> String {
        let input = CreateUserInput(
            userName: name
        )
        do {
            let output = try await client.createUser(input: input)
            guard let user = output.user else {
                throw ServiceHandlerError.noSuchUser
            }
            guard let id = user.userId else {
                throw ServiceHandlerError.noSuchUser
            }
            return id
        } catch {
            print("ERROR: createUser:", dump(error))
            throw error
        }
    }
    // snippet-end:[iam.swift.createuser.handler.CreateUser]
}
// snippet-end:[iam.swift.createuser.handler]
