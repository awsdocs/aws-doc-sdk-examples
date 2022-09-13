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

enum ServiceHandlerError: Error {
    case noSuchUser
}

public extension ServiceHandler {

    /// Create a new IAM user.
    ///
    /// - Parameter name: The user's name.
    ///
    /// - Returns: The ID of the newly created user.
    func createUser(name: String) async throws -> String? {
        let input = CreateUserInput(
            userName: name
        )
        do {
            let output = try await client.createUser(input: input)
            guard let user = output.user else {
                throw ServiceHandlerError.noSuchUser
            }
            return user.userId
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