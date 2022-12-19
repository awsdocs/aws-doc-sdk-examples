/*
   Extensions to the `ServiceHandlerIAM` class to handle tasks we need
   for testing that aren't the purpose of this example.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation
import AWSIAM
import AWSClientRuntime
import ClientRuntime
import SwiftUtilities

public extension ServiceHandlerIAM {
    /// Add an inline policy to an AWS Identity and Access Management (IAM)
    /// user.
    ///
    /// - Parameters:
    ///   - policyDocument: A `String` indicating the policy
    ///     document to add to the user.
    ///   - policyName: A string giving the policy's name.
    ///   - user: The `IAMClientTypes.User` specifying the user.
    func putUserPolicy(policyDocument: String, policyName: String, user: IAMClientTypes.User) async throws {
        let input = PutUserPolicyInput(
            policyDocument: policyDocument,
            policyName: policyName,
            userName: user.userName
        )
        do {
            _ = try await iamClient.putUserPolicy(input: input)
        } catch {
            throw error
        }
    }

    /// Get the ID of an AWS Identity and Access Management (IAM) user.
    ///
    /// - Parameter name: The name of the IAM user.
    ///
    /// - Returns: A `String` containing the user's ID.
    func getUserID(name: String) async throws -> String {
        do {
            let user = try await self.getUser(name: name)
            guard let id = user.userId else {
                throw ServiceHandlerError.noSuchUser
            }
            return id
        } catch {
            throw error
        }
    }

    func getRole(name: String) async throws -> IAMClientTypes.Role {
        let input = GetRoleInput(
            roleName: name
        )
        do {
            let output = try await iamClient.getRole(input: input)
            guard let role = output.role else {
                throw ServiceHandlerError.noSuchRole
            }
            return role
        } catch {
            throw error
        }
    }

    /// Get the ID of an AWS Identity and Access Management (IAM) role.
    ///
    /// - Parameter name: The name of the IAM role.
    ///
    /// - Returns: A `String` containing the role's ID.
    func getRoleID(name: String) async throws -> String {
        do {
            let role = try await self.getRole(name: name)
            guard let id = role.roleId else {
                throw ServiceHandlerError.noSuchRole
            }
            return id
        } catch {
            throw error
        }
    }

    func getPolicy(arn: String) async throws -> IAMClientTypes.Policy {
        let input = GetPolicyInput(
            policyArn: arn
        )
        do {
            let output = try await iamClient.getPolicy(input: input)
            guard let policy = output.policy else {
                throw ServiceHandlerError.noSuchPolicy
            }
            return policy
        } catch {
            throw error
        }
    }

    func getPolicyID(arn: String) async throws -> String {
        do {
            let policy = try await self.getPolicy(arn: arn)
            guard let policyID = policy.policyId else {
                throw ServiceHandlerError.idMismatch
            }
            return policyID
        }
    }

    func getRolePolicyDocument(policyName: String, roleName: String) async throws -> String {
        let input = GetRolePolicyInput(
            policyName: policyName,
            roleName: roleName
        )
        do {
            let output = try await iamClient.getRolePolicy(input: input)
            guard let policyDocument = output.policyDocument else {
                throw ServiceHandlerError.noSuchPolicy
            }
            return policyDocument
        } catch {
            throw error
        }
    }
}