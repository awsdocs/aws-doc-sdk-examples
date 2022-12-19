/*
   A class containing functions that interact with the AWS Identity and Access
   Management (IAM) service.

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

public class ServiceHandlerSTS {
    /// The AWS Region to use for S3 operations.
    let region: String

    /// The STSClient used to interact with AWS STS.
    var stsClient: STSClient
    var credentialsProvider: AWSCredentialsProvider? = nil

    /// Initialize the IAM client, optionally with credentials.
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

    // snippet-start:[iam.swift.basics.sts.setcredentials]
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

    // snippet-start:[iam.swift.basics.sts.resetcredentials]
    public func resetCredentials() async throws {
        do {
            stsClient = try STSClient(region: self.region)
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.sts.resetcredentials]

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
// snippet-start:[iam.swift.basics.sts]

