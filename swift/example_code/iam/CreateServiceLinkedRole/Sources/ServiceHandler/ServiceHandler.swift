// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
   A class containing functions that interact with AWS services.
*/

// snippet-start:[iam.swift.createservicelinkedrole.handler]
// snippet-start:[iam.swift.createservicelinkedrole.handler.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.createservicelinkedrole.handler.imports]

// snippet-start:[iam.swift.createservicelinkedrole.enum.service-error]

/// Errors returned by `ServiceHandler` functions.
enum ServiceHandlerError: Error {
    case noSuchUser            /// No matching user found.
    case noSuchRole            /// No matching role found, or unable to create the role.
}
// snippet-end:[iam.swift.createservicelinkedrole.enum.service-error]

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class ServiceHandler {
    public let client: IAMClient

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls used for the example.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[iam.swift.createservicelinkedrole.handler.init]
    public init() async throws {
        do {
            client = try await IAMClient()
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            throw error
        }
    }
    // snippet-end:[iam.swift.createservicelinkedrole.handler.init]

    /// Create a new AWS Identity and Access Management (IAM) role.
    ///
    /// - Parameters:
    ///   - service: The name of the service to link the new role to.
    ///   - suffix: A suffix string to append to the service name to use as
    ///     the new role's name.
    ///   - description: An optional `String` describing the new role.
    ///
    /// - Returns: A `IAMClientTypes.Role` object describing the new role.
    ///
    /// The `service` parameter should be a string derived that looks like a
    /// URL but has no `http://` at the beginning, such as
    /// `elasticbeanstalk.amazonaws.com`.
    // snippet-start:[iam.swift.createservicelinkedrole.handler.CreateServiceLinkedRole]
    public func createServiceLinkedRole(service: String, suffix: String? = nil, description: String?)
                    async throws -> IAMClientTypes.Role {
        let input = CreateServiceLinkedRoleInput(
            awsServiceName: service,
            customSuffix: suffix,
            description: description
        )
        do {
            let output = try await client.createServiceLinkedRole(input: input)
            guard let role = output.role else {
                throw ServiceHandlerError.noSuchRole
            }
            return role
        } catch {
            print("ERROR: createServiceLinkedRole:", dump(error))
            throw error
        }
    }
    // snippet-end:[iam.swift.createservicelinkedrole.handler.CreateServiceLinkedRole]
}
// snippet-end:[iam.swift.createservicelinkedrole.handler]
