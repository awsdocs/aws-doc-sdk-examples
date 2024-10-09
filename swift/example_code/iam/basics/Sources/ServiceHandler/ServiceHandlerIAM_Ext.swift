// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 Extensions to the `ServiceHandlerIAM` class to handle tasks needed
 for testing that aren't the purpose of this example.
 */

import AWSIAM
import ClientRuntime
import Foundation
import SwiftUtilities

public extension ServiceHandlerIAM {
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
            print("ERROR: getUserID:", dump(error))
            throw error
        }
    }

    /// Returns the role with the given name.
    ///
    /// - Parameter name: The role name.
    /// - Returns: An `IAMClientTypes.Role` object describing the role.
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
            print("ERROR: getRole:", dump(error))
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
            print("ERROR: getRoleID:", dump(error))
            throw error
        }
    }

    /// Get the policy whose ARN matches the specified string.
    ///
    /// - Parameter arn: A string giving the policy's ARN.
    ///
    /// - Returns: An `IAMClientTypes.Policy` object describing the policy.
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
            print("ERROR: getPolicy:", dump(error))
            throw error
        }
    }

    /// Get the ID of the policy whose ARN matches the specified string.
    ///
    /// - Parameter arn: A string giving the policy's ARN.
    ///
    /// - Returns: A `String` containing the policy's ID.
    func getPolicyID(arn: String) async throws -> String {
        do {
            let policy = try await self.getPolicy(arn: arn)
            guard let policyID = policy.policyId else {
                throw ServiceHandlerError.idMismatch
            }
            return policyID
        }
    }

    /// Get the policy document with a particular name for a given role.
    ///
    /// - Parameters:
    ///   - policyName: The name of the policy to get the policy document for.
    ///   - roleName: The name of the role the policy is part of.
    /// - Returns: A string containing the policy document text.
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
            print("ERROR: getPgetRolePolicyDocumentolicy:", dump(error))
            throw error
        }
    }

    /// Return a list of the policies attached to a role, as an array of
    /// `IAMClientTypes.AttachedPolicy` objects.
    ///
    /// - Parameter role: The role for which to return the attached policies.
    /// - Returns: An array of `IAMClientTypes.AttachedPolicy` objects giving
    ///   the names and ARNs of the policies attached to the role.
    func getAttachedPolicies(forRole role: IAMClientTypes.Role) async throws
        -> [IAMClientTypes.AttachedPolicy]
    {
        var policyList: [IAMClientTypes.AttachedPolicy] = []

        // Use "Paginated" to get all the objects.
        // This lets the SDK handle the 'isTruncated' in "ListAttachedRolePoliciesOutput".
        let input = ListAttachedRolePoliciesInput(
            roleName: role.roleName
        )
        let output = iamClient.listAttachedRolePoliciesPaginated(input: input)

        do {
            for try await page in output {
                guard let attachedPolicies = page.attachedPolicies else {
                    print("Error: no attached policies returned.")
                    continue
                }
                for attachedPolicy in attachedPolicies {
                    policyList.append(attachedPolicy)
                }
            }
        } catch {
            print("ERROR: listAttachedRolePolicies:", dump(error))
            throw error
        }

        return policyList
    }
}
