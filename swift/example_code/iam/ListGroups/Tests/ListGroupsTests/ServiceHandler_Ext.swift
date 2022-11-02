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
    case noSuchGroup
}

public extension ServiceHandler {

    /// Create a new AWS Identity and Access Management (IAM) group.
    ///
    /// - Parameter name: A `String` containing the name to assign to the new
    ///   IAM group.
    /// - Returns: A `String` containing the ID of the new group.
    func createGroup(name: String) async throws -> String? {
        let input = CreateGroupInput(
            groupName: name
        )
        do {
            let output = try await client.createGroup(input: input)
            guard let group = output.group else {
                throw ServiceHandlerError.noSuchGroup
            }
            return group.groupId
        } catch {
            throw error
        }
    }

    /// Delete an IAM group.
    ///
    /// - Parameter name: A `String` giving the name of the group to delete.
    func deleteGroup(name: String) async throws {
        let input = DeleteGroupInput(
            groupName: name
        )
        do {
            _ = try await client.deleteGroup(input: input)
        } catch {
            throw error
        }
    }
}