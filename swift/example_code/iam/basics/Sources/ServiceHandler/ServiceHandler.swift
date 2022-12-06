/*
   A class containing functions that interact with AWS services.
   
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[iam.swift.basics.handler]
// snippet-start:[iam.swift.basics.handler.imports]
import Foundation
import AWSIAM
import AWSS3
import AWSSTS
import ClientRuntime
import AWSClientRuntime
import SwiftUtilities
// snippet-end:[iam.swift.basics.handler.imports]

// snippet-start:[iam.swift.basics.enum.service-error]

/// Errors returned by `ServiceHandler` functions.
public enum ServiceHandlerError: Error {
    case noSuchUser            /// No matching user found, or unable to create the user.
    case keyError
    case authError
    case noSuchRole
    case noSuchPolicy
    case bucketError
    case idMismatch
    case arnMismatch
}
// snippet-end:[iam.swift.basics.enum.service-error]

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class ServiceHandler {
    public let region: String

    // The AWS service clients will change over the course of this example.
    // They are initially created with the permissions of the default account,
    // but get replaced to take on the assumed role's credentials.

    public var iamClient: IAMClient
    public var stsClient: STSClient
    public var s3Client: S3Client

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls used for the example. The Region string
    /// `AWS_GLOBAL` is used for the IAM client because users are shared
    /// across all Regions.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[iam.swift.basics.handler.init]
    public init(region: String = "us-east-2", logLevel: String? = nil) async {
        do {
            self.region = region

            // If specified, set the logging level.

            if logLevel != nil {
                var level: SDKLogLevel

                switch(logLevel) {
                    case "critical":
                        level = .critical
                    case "debug":
                        level = .debug
                    case "error":
                        level = .error
                    case "info":
                        level = .info
                    case "notice":
                        level = .notice
                    case "trace":
                        level = .trace
                    case "warning":
                        level = .warning
                    default:
                        level = .notice
                }
                SDKLoggingSystem.initialize(logLevel: level)
            }

            // Create the default service clients.
            
            iamClient = try IAMClient(region: "AWS_GLOBAL")
            s3Client = try S3Client(region: self.region)
            stsClient = try STSClient(region: self.region)
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon clients"))
            exit(1)
        }
    }
    // snippet-end:[iam.swift.basics.handler.init]

    // *** AWS Identity and Access Management (IAM) ***

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

            // Create an AWS S3 configuration specifying the credentials provider
            // we just created, then use it to create a new `S3Client` that will
            // use the corresponding permissions.

            let s3Config = try S3Client.S3ClientConfiguration(
                credentialsProvider: credentialsProvider,
                region: self.region
            )
            s3Client = S3Client(config: s3Config)

            // Create an AWS IAM configuration specifying the credentials
            // provider. Then create a new `IAMClient` using those permissions.

            let iamConfig = try IAMClient.IAMClientConfiguration(
                credentialsProvider: credentialsProvider,
                region: "AWS_GLOBAL"
            )
            iamClient = IAMClient(config: iamConfig)

            // Create a new STS client with the specified access credentials.

            let stsConfig = try STSClient.STSClientConfiguration(
                credentialsProvider: credentialsProvider,
                region: self.region
            )
            stsClient = STSClient(config: stsConfig)
        } catch {
            throw error
        }
    }

    public func resetCredentials() async throws {
        iamClient = try IAMClient(region: "AWS_GLOBAL")
        s3Client = try S3Client(region: self.region)
        stsClient = try STSClient(region: self.region)
    }

    /// Create a new AWS Identity and Access Management (IAM) user.
    ///
    /// - Parameter name: The user's name.
    ///
    /// - Returns: The newly created user, as an `IAMClientTypes.User` object.
    // snippet-start:[iam.swift.basics.handler.basics]
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
    // snippet-end:[iam.swift.basics.handler.basics]

    /// Create a new AWS Identity and Access Management (IAM) role.
    ///
    /// - Parameters:
    ///     - name: The name of the new IAM role.
    ///     - policyDocument: The policy document to associate with the new
    ///       role.
    ///
    /// - Returns: The new `IAMClientTypes.Role`.
    // snippet-start:[iam.swift.basics.handler.createrole]
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
    // snippet-end:[iam.swift.basics.handler.createrole]

    // snippet-start:[iam.swift.basics.handler.createaccesskey]

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
    // snippet-end:[iam.swift.basics.handler.createaccesskey]

    /// Create a new AWS Identity and Access Management (IAM) policy.
    ///
    /// - Parameters:
    ///   - name: The name of the new policy.
    ///   - policyDocument: The policy document to assign to the new policy.
    ///
    /// - Returns: An `IAMClientTypes.Policy` object describing the new policy.
    ///
    // snippet-start:[iam.swift.basics.handler.createpolicy]
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
    // snippet-end:[iam.swift.basics.handler.createpolicy]

    // snippet-start:[iam.swift.basics.handler.attachrolepolicy]
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
    // snippet-end:[iam.swift.basics.handler.attachrolepolicy]

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
    // snippet-start:[iam.swift.basics.handler.getuser]
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
    // snippet-end:[iam.swift.basics.handler.getuser]

    // *** Amazon Simple Storage Service (S3) ***

    public func listBuckets() async throws -> [S3ClientTypes.Bucket] {
        let input = ListBucketsInput()

        do {
            let output = try await s3Client.listBuckets(input: input)
        
            guard let buckets = output.buckets else {
                throw ServiceHandlerError.bucketError
            }
            return buckets
        } catch {
            throw error
        }
    }

    // *** AWS Security Token Service (STS) ***

    public func assumeRole(role: IAMClientTypes.Role, sessionName: String)
                    async throws -> STSClientTypes.Credentials {
        let input = AssumeRoleInput(
            roleArn: role.arn,
            roleSessionName: sessionName
        )
        do {
            let output = try await stsClient.assumeRole(input: input)

            guard let credentials = output.credentials else {
                throw ServiceHandlerError.authError
            }

            return credentials
        } catch {
            throw error
        }
    }
}
// snippet-end:[iam.swift.basics.handler]
