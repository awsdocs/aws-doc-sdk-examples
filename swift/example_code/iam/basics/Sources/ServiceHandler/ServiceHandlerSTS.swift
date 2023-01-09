/*
   A class containing functions that interact with the AWS Security Token
   Service (AWS STS).

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[iam.swift.basics.sts]
// snippet-start:[iam.swift.basics.sts.imports]
import Foundation
import AWSIAM
import AWSSTS
import ClientRuntime
import AWSClientRuntime
import SwiftUtilities
// snippet-end:[iam.swift.basics.sts.imports]

/// A class providing functions for interacting with the AWS Security Token
/// Service (AWS STS).
public class ServiceHandlerSTS {
    /// The AWS Region to use for AWS STS operations.
    let region: String

    /// The STSClient used to interact with AWS STS.
    var stsClient: STSClient

    /// Initialize the AWS STS client, optionally with credentials.
    ///
    /// - Parameters:
    ///   - region: A string specifying the AWS Region in which to perform
    ///     AWS STS operations. If not specified, us-east-2 is used.
    ///   - accessKeyId: An optional string giving the access key ID to
    ///     use for AWS STS operations.
    ///   - secretAccessKey: The secret access key string, if credentials are
    ///     to be used.
    ///   - sessionToken: The optional session token string part of the
    ///     credentials.
    ///
    // snippet-start:[iam.swift.basics.sts.init]
    public init(region: String = "us-east-2",
                accessKeyId: String? = nil,
                secretAccessKey: String? = nil,
                sessionToken: String? = nil) async {
        do {
            self.region = region

            if accessKeyId == nil {
                stsClient = try STSClient(region: self.region)
            } else {
                // Use the given access key ID, secret access key, and session token
                // to generate a static credentials provider suitable for use when
                // initializing an AWS STS client.

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

                // Create an AWS STS configuration specifying the credentials
                // provider. Then create a new `STSClient` using those permissions.

                let s3Config = try STSClient.STSClientConfiguration(
                    credentialsProvider: credentialsProvider,
                    region: self.region
                )
                stsClient = STSClient(config: s3Config)
            }
        } catch {
            print("Error initializing the AWS S3 client: ", dump(error))
            exit(1)
        }
    }
    // snippet-end:[iam.swift.basics.sts.init]

    /// Set the credentials to use when making AWS STS calls. This is done by
    /// replacing the internal `STSClient` with a new one that uses the
    /// credentials.
    ///
    /// - Parameters:
    ///   - accessKeyId: The access key ID.
    ///   - secretAccessKey: The secret access key.
    ///   - sessionToken: The optional session token string.
    // snippet-start:[iam.swift.basics.sts.setcredentials]
    public func setCredentials(accessKeyId: String, secretAccessKey: String,
                sessionToken: String? = nil) async throws {
        do {
            // Use the given access key ID, secret access key, and session token
            // to generate a static credentials provider suitable for use when
            // initializing an AWS STS client.

            let credentialsProvider = try AWSCredentialsProvider.fromStatic(
                AWSCredentialsProviderStaticConfig(
                    accessKey: accessKeyId,
                    secret: secretAccessKey,
                    sessionToken: sessionToken
                )
            )

            // Create a new AWS STS client with the specified access credentials.

            let stsConfig = try STSClient.STSClientConfiguration(
                credentialsProvider: credentialsProvider,
                region: self.region
            )
            stsClient = STSClient(config: stsConfig)
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.sts.setcredentials]

    /// Switch to using the default credentials for future AWS STS calls by
    /// replacing the internal AWS STS client with one created without a given
    /// set of credentials.
    // snippet-start:[iam.swift.basics.sts.resetcredentials]
    public func resetCredentials() async throws {
        do {
            stsClient = try STSClient(region: self.region)
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.sts.resetcredentials]

    /// Assume the specified role.
    ///
    /// - Parameters:
    ///   - role: The role to assume, specified as an `IAMClientTypes.Role`
    ///     object.
    ///   - sessionName: A string giving the role session a name.
    ///
    /// - Returns: An `STSClientTypes.Credentials` object containing the
    ///   credential information to use when performing calls using the role.
    ///
    // snippet-start:[iam.swift.basics.sts.assumerole]
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
    // snippet-end:[iam.swift.basics.sts.assumerole]
}
// snippet-end:[iam.swift.basics.sts]

