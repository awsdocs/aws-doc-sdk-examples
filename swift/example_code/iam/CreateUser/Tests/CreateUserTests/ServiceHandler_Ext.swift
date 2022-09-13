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

    /// Get the ID of an IAM user.
    ///
    /// - Parameter name: The name of the IAM user whose ID is wanted.
    /// - Returns: A `String` containing the user's ID.
    func getUserID(name: String) async throws -> String {
        let input = GetUserInput(
            userName: name
        )

        do {
            let output = try await client.getUser(input: input)
            guard let user = output.user else {
                throw ServiceHandlerError.noSuchUser
            }
            guard let id = user.userId else {
                throw ServiceHandlerError.noSuchUser
            }
            return id
        } catch {
            throw error
        }
    }

    /// Delete an IAM user.
    ///
    /// - Parameter name: The name of the IAM user to delete.
    func deleteUser(name: String) async throws {
        let input = DeleteUserInput(
            userName: name
        )
        do {
            _ = try await client.deleteUser(input: input)
        } catch {
            throw error
        }
    }
}