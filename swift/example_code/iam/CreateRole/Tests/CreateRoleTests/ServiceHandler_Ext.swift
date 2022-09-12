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
                throw ServiceError.noSuchRole
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
}