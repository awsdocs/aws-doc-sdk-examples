// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 A class containing functions that interact with AWS Identity and Access
 Management (IAM).
 */

// snippet-start:[iam.swift.basics.iam]
// snippet-start:[iam.swift.basics.iam.imports]
import AWSIAM
import AWSSDKIdentity
import ClientRuntime
import Foundation
import SwiftUtilities

// snippet-end:[iam.swift.basics.iam.imports]

public class ServiceHandlerIAM {
    /// The `IAMClient` used to interact with IAM.
    var iamClient: IAMClient

    /// Initialize the IAM client, optionally with credentials.
    ///
    /// - Parameters:
    ///   - accessKeyId: An optional `String` giving the access key ID of the
    ///     credentials to use.
    ///   - secretAccessKey: An optional `String` giving the credentials'
    ///     secret access key.
    ///   - sessionToken: An optional string specifying the session token.
    // snippet-start:[iam.swift.basics.iam.init]
    public init(accessKeyId: String? = nil,
                secretAccessKey: String? = nil,
                sessionToken: String? = nil) async throws
    {
        do {
            if accessKeyId == nil {
                iamClient = try await IAMClient()
            } else {
                // Use the given access key ID, secret access key, and session token
                // to generate a static credentials provider suitable for use when
                // initializing an IAM client.

                guard let keyId = accessKeyId,
                      let secretKey = secretAccessKey
                else {
                    throw ServiceHandlerError.authError
                }

                let credentials: AWSCredentialIdentity = AWSCredentialIdentity(
                    accessKey: keyId,
                    secret: secretKey,
                    sessionToken: sessionToken
                )
                let identityResolver = try StaticAWSCredentialIdentityResolver(credentials)

                // Create an IAM configuration specifying the credentials
                // provider. Then create a new `IAMClient` using those
                // permissions.

                let iamConfig = try await IAMClient.IAMClientConfiguration(
                    awsCredentialIdentityResolver: identityResolver
                )
                iamClient = IAMClient(config: iamConfig)
            }
        } catch {
            print("Error initializing the AWS IAM client: ", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.init]

    /// Update the IAM handler with a new `IAMClient` set up to use the
    /// specified credentials.
    ///
    /// - Parameters:
    ///   - accessKeyId: A string containing the AWS access key ID.
    ///   - secretAccessKey: A string containing the AWS secret access key.
    ///   - sessionToken: An optional string containing the AWS session token.
    // snippet-start:[iam.swift.basics.iam.setcredentials]
    public func setCredentials(accessKeyId: String, secretAccessKey: String,
                               sessionToken: String? = nil) async throws
    {
        do {
            // Use the given access key ID, secret access key, and session
            // token to generate a static credentials provider suitable for
            // use when initializing an IAM client.

            let credentials: AWSCredentialIdentity = AWSCredentialIdentity(
                accessKey: accessKeyId,
                secret: secretAccessKey,
                sessionToken: sessionToken
            )
            let identityResolver = try StaticAWSCredentialIdentityResolver(credentials)

            // Create a new `IAMClient` using the new identity resolver.

            let iamConfig = try await IAMClient.IAMClientConfiguration(
                awsCredentialIdentityResolver: identityResolver
            )

            iamClient = IAMClient(config: iamConfig)
        } catch {
            print("ERROR: setCredentials:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.setcredentials]

    /// Reset IAM credentials by replacing the internal `iamClient` with a
    /// fresh one that uses the default configuration.
    // snippet-start:[iam.swift.basics.iam.resetcredentials]
    public func resetCredentials() async throws {
        do {
            iamClient = try await IAMClient()
        } catch {
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.resetcredentials]

    /// Create a new IAM user.
    ///
    /// - Parameters:
    ///   - name: The user's name.
    ///
    /// - Returns: The newly created user, as an `IAMClientTypes.User` object.
    // snippet-start:[iam.swift.basics.iam.createuser]
    public func createUser(name: String) async throws -> IAMClientTypes.User {
        let input = CreateUserInput(
            userName: name
        )
        do {
            let output = try await iamClient.createUser(input: input)
            guard let user = output.user else {
                throw ServiceHandlerError.noSuchUser
            }

            return user
        } catch {
            print("ERROR: createUser:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.createuser]

    /// Create a new IAM role.
    ///
    /// - Parameters:
    ///     - name: The name of the new IAM role.
    ///     - policyDocument: The policy document to associate with the new
    ///       role.
    ///
    /// - Returns: The new `IAMClientTypes.Role`.
    // snippet-start:[iam.swift.basics.iam.createrole]
    public func createRole(name: String, policyDocument: String) async throws -> IAMClientTypes.Role {
        let input = CreateRoleInput(
            assumeRolePolicyDocument: policyDocument,
            roleName: name
        )
        do {
            let output = try await iamClient.createRole(input: input)
            guard let role = output.role else {
                throw ServiceHandlerError.noSuchRole
            }
            return role
        } catch {
            print("ERROR: createRole:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.createrole]

    /// Create an IAM access key for a user.
    ///
    /// - Parameter userName: A `String` giving the user name.
    ///
    /// - Returns: An `IAMClientTypes.AccessKey` object with the access key
    ///            details.
    // snippet-start:[iam.swift.basics.iam.CreateAccessKey]
    public func createAccessKey(userName: String) async throws -> IAMClientTypes.AccessKey {
        let input = CreateAccessKeyInput(
            userName: userName
        )
        do {
            let output = try await iamClient.createAccessKey(input: input)
            guard let accessKey = output.accessKey else {
                throw ServiceHandlerError.keyError
            }
            return accessKey
        } catch {
            print("ERROR: createAccessKey:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.CreateAccessKey]

    /// Create a new AWS Identity and Access Management (IAM) policy.
    ///
    /// - Parameters:
    ///   - name: The name of the new policy.
    ///   - policyDocument: The policy document to assign to the new policy.
    ///
    /// - Returns: An `IAMClientTypes.Policy` object describing the new policy.
    ///
    // snippet-start:[iam.swift.basics.iam.CreatePolicy]
    public func createPolicy(name: String, policyDocument: String) async throws -> IAMClientTypes.Policy {
        let input = CreatePolicyInput(
            policyDocument: policyDocument,
            policyName: name
        )
        do {
            let output = try await iamClient.createPolicy(input: input)
            guard let policy = output.policy else {
                throw ServiceHandlerError.noSuchPolicy
            }
            return policy
        } catch {
            print("ERROR: createPolicy:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.CreatePolicy]

    /// Add an inline policy to an AWS Identity and Access Management (IAM)
    /// user.
    ///
    /// - Parameters:
    ///   - policyDocument: A `String` indicating the policy
    ///     document to add to the user.
    ///   - policyName: A string giving the policy's name.
    ///   - user: The `IAMClientTypes.User` specifying the user.
    ///
    // snippet-start:[iam.swift.basics.iam.PutUserPolicy]
    func putUserPolicy(policyDocument: String, policyName: String, user: IAMClientTypes.User) async throws {
        let input = PutUserPolicyInput(
            policyDocument: policyDocument,
            policyName: policyName,
            userName: user.userName
        )
        do {
            _ = try await iamClient.putUserPolicy(input: input)
        } catch {
            print("ERROR: putUserPolicy:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.PutUserPolicy]

    /// Delete the specified inline user policy.
    ///
    /// - Parameters:
    ///   - user: The `IAMClientTypes.User` indicating the user from which to
    ///     delete the policy.
    ///   - policyName: The name of the policy to delete.
    ///
    // snippet-start:[iam.swift.basics.iam.DeleteUserPolicy]
    func deleteUserPolicy(user: IAMClientTypes.User, policyName: String) async throws {
        let input = DeleteUserPolicyInput(
            policyName: policyName,
            userName: user.userName
        )
        do {
            _ = try await iamClient.deleteUserPolicy(input: input)
        } catch {
            print("ERROR: deleteUserPolicy:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.DeleteUserPolicy]

    /// Attach a managed policy to a role.
    ///
    /// - Parameters:
    ///   - policy: The policy to attach to the role, as an
    ///     `IAMClientTypes.Policy` object.
    ///   - role: An `IAMClientTypes.Role` indicating the role to attach the
    ///     policy to.
    // snippet-start:[iam.swift.basics.iam.attachrolepolicy]
    public func attachRolePolicy(policy: IAMClientTypes.Policy, role: IAMClientTypes.Role) async throws {
        let input = AttachRolePolicyInput(
            policyArn: policy.arn,
            roleName: role.roleName
        )
        do {
            _ = try await iamClient.attachRolePolicy(input: input)
        } catch {
            print("ERROR: attachRolePolicy:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.attachrolepolicy]

    /// Detach a policy from a role.
    ///
    /// - Parameters:
    ///   - policy: The policy to be detached from the role.
    ///   - role: The role from which to detach a policy.
    // snippet-start:[iam.swift.basics.iam.DetachRolePolicy]
    public func detachRolePolicy(policy: IAMClientTypes.Policy, role: IAMClientTypes.Role) async throws {
        let input = DetachRolePolicyInput(
            policyArn: policy.arn,
            roleName: role.roleName
        )

        do {
            _ = try await iamClient.detachRolePolicy(input: input)
        } catch {
            print("ERROR: detachRolePolicy:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.DetachRolePolicy]

    /// Delete the specified policy.
    ///
    /// - Parameter policy: The `IAMClientTypes.Policy` object identifying the
    ///   policy to delete.
    // snippet-start:[iam.swift.basics.iam.DeletePolicy]
    public func deletePolicy(policy: IAMClientTypes.Policy) async throws {
        let input = DeletePolicyInput(
            policyArn: policy.arn
        )
        do {
            _ = try await iamClient.deletePolicy(input: input)
        } catch {
            print("ERROR: deletePolicy:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.DeletePolicy]

    /// Delete an IAM user.
    ///
    /// - Parameter user: The `IAMClientTypes.User` object describing the IAM
    ///   user to delete.
    ///
    // snippet-start:[iam.swift.basics.iam.DeleteUser]
    public func deleteUser(user: IAMClientTypes.User) async throws {
        let input = DeleteUserInput(
            userName: user.userName
        )
        do {
            _ = try await iamClient.deleteUser(input: input)
        } catch {
            print("ERROR: deleteUser:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.DeleteUser]

    /// Delete an access key.
    /// - Parameters:
    ///   - user: The user to delete, as an `IAMClientTypes.User` object.
    ///           If not specified or `nil`, IAM assumes the user name from the
    ///           access key signing the request.
    ///   - key: An `IAMClientTypes.AccessKey` object representing the key to
    ///          delete.
    // snippet-start:[iam.swift.basics.iam.DeleteAccessKey]
    public func deleteAccessKey(user: IAMClientTypes.User? = nil,
                                key: IAMClientTypes.AccessKey) async throws
    {
        let userName: String?

        if user != nil {
            userName = user!.userName
        } else {
            userName = nil
        }

        let input = DeleteAccessKeyInput(
            accessKeyId: key.accessKeyId,
            userName: userName
        )
        do {
            _ = try await iamClient.deleteAccessKey(input: input)
        } catch {
            print("ERROR: deleteAccessKey:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.DeleteAccessKey]

    /// Delete an IAM role.
    ///
    /// - Parameter name: The IAM role to delete.
    // snippet-start:[iam.swift.basics.iam.DeleteRole]
    public func deleteRole(role: IAMClientTypes.Role) async throws {
        let input = DeleteRoleInput(
            roleName: role.roleName
        )
        do {
            _ = try await iamClient.deleteRole(input: input)
        } catch {
            print("ERROR: deleteRole:", dump(error))
            throw error
        }
    }

    // snippet-end:[iam.swift.basics.iam.DeleteRole]

    /// Get information about the specified user.
    ///
    /// - Parameter name: A `String` giving the name of the user to get. If
    ///   this parameter is `nil`, the default user's information is returned.
    ///
    /// - Returns: An `IAMClientTypes.User` record describing the user.
    // snippet-start:[iam.swift.basics.iam.GetUser]
    public func getUser(name: String? = nil) async throws -> IAMClientTypes.User {
        let input = GetUserInput(
            userName: name
        )
        do {
            let output = try await iamClient.getUser(input: input)
            guard let user = output.user else {
                throw ServiceHandlerError.noSuchUser
            }
            return user
        } catch {
            print("ERROR: getUser:", dump(error))
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.iam.GetUser]
}

// snippet-end:[iam.swift.basics.iam]
