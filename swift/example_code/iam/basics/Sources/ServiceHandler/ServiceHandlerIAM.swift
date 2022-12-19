/*
   A class containing functions that interact with the AWS Identity and Access
   Management (IAM) service.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[iam.swift.basics.iam]
// snippet-start:[iam.swift.basics.iam.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
import SwiftUtilities
// snippet-end:[iam.swift.basics.iam.imports]

public class ServiceHandlerIAM {
    /// IAM always uses the global value for the Region.
    let region = "AWS_GLOBAL"

    /// The IAMClient used to interact with AWS IAM.
    var iamClient: IAMClient
    var credentialsProvider: AWSCredentialsProvider? = nil

    /// Initialize the IAM client, optionally with credentials.
    // snippet-start:[iam.swift.basics.iam.init]
    public init(accessKeyId: String? = nil, 
                secretAccessKey: String? = nil,
                sessionToken: String? = nil) async {
        do {
            if accessKeyId == nil {
                iamClient = try IAMClient(region: "AWS_GLOBAL")
            } else {
                // Use the given access key ID, secret access key, and session token
                // to generate a static credentials provider suitable for use when
                // initializing an AWS S3 client.

                guard   let keyId = accessKeyId,
                        let secretKey = secretAccessKey else {
                            throw ServiceHandlerError.authError
                        }
                let credentialsProvider = try AWSCredentialsProvider.fromStatic(
                    AWSCredentialsProviderStaticConfig(
                        accessKey: keyId,
                        secret: secretKey,
                        sessionToken: sessionToken
                    )
                )

                // Create an AWS IAM configuration specifying the credentials
                // provider. Then create a new `IAMClient` using those permissions.

                let iamConfig = try IAMClient.IAMClientConfiguration(
                    credentialsProvider: credentialsProvider,
                    region: self.region
                )
                iamClient = IAMClient(config: iamConfig)
            }
        } catch {
            print("Error initializing the AWS IAM client: ", dump(error))
            exit(1)
        }
    }
    // snippet-end:[iam.swift.basics.iam.init]

    // snippet-start:[iam.swift.basics.iam.setcredentials]
    public func setCredentials(accessKeyId: String, secretAccessKey: String,
                sessionToken: String? = nil) async throws {
        do {
            // Use the given access key ID, secret access key, and session token
            // to generate a static credentials provider suitable for use when
            // initializing an AWS S3 client.

            let credentialsProvider = try AWSCredentialsProvider.fromStatic(
                AWSCredentialsProviderStaticConfig(
                    accessKey: accessKeyId,
                    secret: secretAccessKey,
                    sessionToken: sessionToken
                )
            )

            // Create a new STS client with the specified access credentials.

            let iamConfig = try IAMClient.IAMClientConfiguration(
                credentialsProvider: credentialsProvider,
                region: self.region
            )
            iamClient = IAMClient(config: iamConfig)
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.iam.setcredentials]

    public func resetCredentials() async throws {
        do {
            iamClient = try IAMClient(region: "AWS_GLOBAL")
        } catch {
            throw error
        }
    }

    /// Create a new AWS Identity and Access Management (IAM) user.
    ///
    /// - Parameter name: The user's name.
    ///
    /// - Returns: The newly created user, as an `IAMClientTypes.User` object.
    // snippet-start:[iam.swift.basics.iam.basics]
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
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.iam.basics]

    /// Create a new AWS Identity and Access Management (IAM) role.
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
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.iam.createrole]

    // snippet-start:[iam.swift.basics.iam.createaccesskey]

    /// Create an IAM access key for a user.
    ///
    /// - Parameter userName: A `String` giving the user name.
    ///
    /// - Returns: An `IAMClientTypes.AccessKey` object with the access key
    ///            details.
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
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.iam.createaccesskey]

    /// Create a new AWS Identity and Access Management (IAM) policy.
    ///
    /// - Parameters:
    ///   - name: The name of the new policy.
    ///   - policyDocument: The policy document to assign to the new policy.
    ///
    /// - Returns: An `IAMClientTypes.Policy` object describing the new policy.
    ///
    // snippet-start:[iam.swift.basics.iam.createpolicy]
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
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.iam.createpolicy]

    // snippet-start:[iam.swift.basics.iam.attachrolepolicy]
    public func attachRolePolicy(policy: IAMClientTypes.Policy, role: IAMClientTypes.Role) async throws {
        let input = AttachRolePolicyInput(
            policyArn: policy.arn,
            roleName: role.roleName
        )
        do {
            _ = try await iamClient.attachRolePolicy(input: input)
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.iam.attachrolepolicy]

    public func detachRolePolicy(policy: IAMClientTypes.Policy, role: IAMClientTypes.Role) async throws {
        let input = DetachRolePolicyInput(
            policyArn: policy.arn,
            roleName: role.roleName
        )

        do {
            _ = try await iamClient.detachRolePolicy(input: input)
        } catch {
            throw error
        }
    }

    public func deletePolicy(policy: IAMClientTypes.Policy) async throws {
        let input = DeletePolicyInput(
            policyArn: policy.arn
        )
        do {
            _ = try await iamClient.deletePolicy(input: input)
        } catch {
            throw error
        }
    }

    /// Delete an IAM user.
    ///
    /// - Parameter user: The `IAMClientTypes.User` object describing the IAM
    ///   user to delete.
    public func deleteUser(user: IAMClientTypes.User) async throws {
        let input = DeleteUserInput(
            userName: user.userName
        )
        do {
            _ = try await iamClient.deleteUser(input: input)
        } catch {
            throw error
        }
    }

    public func deleteAccessKey(key: IAMClientTypes.AccessKey) async throws {
        let input = DeleteAccessKeyInput(
            accessKeyId: key.accessKeyId
        )
        do {
            _ = try await iamClient.deleteAccessKey(input: input)
        } catch {
            throw error
        }
    }

    /// Delete an IAM role.
    ///
    /// - Parameter name: The IAM role to delete.
    public func deleteRole(role: IAMClientTypes.Role) async throws {
        let input = DeleteRoleInput(
            roleName: role.roleName
        )
        do {
            _ = try await iamClient.deleteRole(input: input)
        } catch {
            throw error
        }
    }

    /// Get information about the specified user
    ///
    /// - Parameter name: A `String` giving the name of the user to get. If
    ///   this parameter is `nil`, the default user's information is returned.
    ///
    /// - Returns: An `IAMClientTypes.User` record describing the user.
    // snippet-start:[iam.swift.basics.iam.getuser]
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
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.iam.getuser]
}
// snippet-end:[iam.swift.basics.iam]
