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

    /// Create a new AWS Identity and Access Management (IAM) policy embedded
    /// in a role.
    ///
    /// - Parameters:
    ///   - name: The name of the new policy.
    ///   - policyDocument: The policy document to assign to the new policy.
    func putRolePolicy(roleName: String, policyName: String, policyDocument: String) async throws {
        let input = PutRolePolicyInput(
            policyDocument: policyDocument,
            policyName: policyName,
            roleName: roleName
        )
        do {
            _ = try await client.putRolePolicy(input: input)
        } catch {
            throw error
        }
    }

    func deleteRolePolicy(role: String, policyName: String) async throws {
        let input = DeleteRolePolicyInput(
            policyName: policyName,
            roleName: role
        )
        do {
            _ = try await client.deleteRolePolicy(input: input)
        } catch {
            throw error
        }
    }

    /// Create a new IAM role.
    ///
    /// - Parameter name: The name of the new IAM role.
    ///
    /// - Returns: The ID of the newly created role.
    func createRole(name: String, policyDocument: String) async throws -> String {
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

    /// Get information about the specified user.
    ///
    /// - Parameter name: A `String` giving the name of the user to get. If
    ///   this parameter is `nil`, the default user's information is returned.
    ///
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