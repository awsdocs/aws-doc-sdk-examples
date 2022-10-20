/*
   Extensions to the `ServiceHandler` class to handle tasks we need
   for testing that aren't the purpose of this example.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation
import AWSIAM
import AWSClientRuntime
import ClientRuntime
import SwiftUtilities

@testable import ServiceHandler

public extension ServiceHandler {

    /// Get the ID of an IAM role.
    ///
    /// - Parameter name: The name of the IAM role whose ID is wanted.
    /// - Returns: A `String` containing the role's ID.
    func getRoleID(name: String) async throws -> String {
        let input = GetRoleInput(
            roleName: name
        )
        do {
            let output = try await client.getRole(input: input)

            guard let role = output.role,
                  let id = role.roleId else {
                throw ServiceHandlerError.noSuchRole
            }

            return id
        } catch {
            throw error
        }
    }

    /// Delete an IAM role.
    ///
    /// - Parameter name: The name of the IAM role to delete.
    func deleteRole(name: String) async throws {
        let input = DeleteRoleInput(
            roleName: name
        )
        do {
            _ = try await client.deleteRole(input: input)
        } catch {
            throw error
        }
    }

    /// Delete a service-linked role, given its name.
    ///
    /// - Parameter name: The name of the server-linked role to delete.
    func deleteServiceLinkedRole(name: String) async throws {
        let input = DeleteServiceLinkedRoleInput(
            roleName: name
        )
        do {
            _ = try await client.deleteServiceLinkedRole(input: input)
        } catch {
            throw error
        }
    }

    /// Get information about the specified user
    ///
    /// - Parameter name: A `String` giving the name of the user to get. If
    ///   this parameter is `nil`, the default user's information is returned.
    /// - Returns: An `IamClientTypes.User` record describing the user.
    func getUser(name: String?) async throws -> IamClientTypes.User {
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
}