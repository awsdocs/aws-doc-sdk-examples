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

    /// Create a new managed AWS Identity and Access Management (IAM) policy.
    ///
    /// - Parameters:
    ///   - name: The name of the new policy.d
    ///   - policyDocument: The policy document to assign to the new policy.
    ///
    /// - Returns: An `IamClientTypes.Policy` object representing the new policy.
    func createPolicy(policyName: String, policyDocument: String) async throws -> IamClientTypes.Policy {
        let input = CreatePolicyInput(
            policyDocument: policyDocument,
            policyName: policyName
        )
        do {
            let output = try await client.createPolicy(input: input)

            guard let policy = output.policy else {
                throw ServiceHandlerError.noSuchPolicy
            }
            return policy
        } catch {
            throw error
        }
    }

    func deletePolicy(policyArn: String) async throws {
        let input = DeletePolicyInput(
            policyArn: policyArn
        )
        do {
            _ = try await client.deletePolicy(input: input)
        } catch {
            throw error
        }
    }

    func detachRolePolicy(role: String, policyArn: String) async throws {
        let input = DetachRolePolicyInput(
            policyArn: policyArn,
            roleName: role
        )

        do {
            _ = try await client.detachRolePolicy(input: input)
        } catch {
            throw error
        }
    }

    func listAttachedRolePolicies(role: String) async throws -> [String] {
        var policyList: [String] = []
        var marker: String? = nil
        var isTruncated: Bool
        
        repeat {
            let input = ListAttachedRolePoliciesInput(
                marker: marker,
                roleName: role
            )
            let output = try await client.listAttachedRolePolicies(input: input)
            
            guard let policies = output.attachedPolicies else {
                return policyList
            }

            for policy in policies {
                guard let arn = policy.policyArn else {
                    throw ServiceHandlerError.noSuchPolicy
                }
                policyList.append(arn)
            }
            marker = output.marker
            isTruncated = output.isTruncated
        } while isTruncated == true
        return policyList
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