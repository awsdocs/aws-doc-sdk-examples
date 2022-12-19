/*
   A class containing functions that interact with the AWS Identity and Access
   Management (IAM) service.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[iam.swift.basics.s3]
// snippet-start:[iam.swift.basics.s3.imports]
import Foundation
import AWSS3
import ClientRuntime
import AWSClientRuntime
import SwiftUtilities
// snippet-end:[iam.swift.basics.s3.imports]

public class ServiceHandlerS3 {
    /// The AWS Region to use for S3 operations.
    let region: String

    /// The S3Client used to interact with AWS IAM.
    var s3Client: S3Client
    var credentialsProvider: AWSCredentialsProvider? = nil

    /// Initialize the IAM client, optionally with credentials.
    // snippet-start:[iam.swift.basics.s3.init]
    public init(region: String = "us-east-2",
                accessKeyId: String? = nil,
                secretAccessKey: String? = nil,
                sessionToken: String? = nil) async {
        do {
            self.region = region

            if accessKeyId == nil {
                s3Client = try S3Client(region: self.region)
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
                // provider. Then create a new `S3Client` using those permissions.

                let s3Config = try S3Client.S3ClientConfiguration(
                    credentialsProvider: credentialsProvider,
                    region: self.region
                )
                s3Client = S3Client(config: s3Config)
            }
        } catch {
            print("Error initializing the AWS S3 client: ", dump(error))
            exit(1)
        }
    }
    // snippet-end:[iam.swift.basics.s3.init]

    // snippet-start:[iam.swift.basics.s3.setcredentials]
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

            let s3Config = try S3Client.S3ClientConfiguration(
                credentialsProvider: credentialsProvider,
                region: self.region
            )
            s3Client = S3Client(config: s3Config)
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.s3.setcredentials]

    // snippet-start:[iam.swift.basics.s3.resetcredentials]
    public func resetCredentials() async throws {
        do {
            s3Client = try S3Client(region: self.region)
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.s3.resetcredentials]

    // snippet-start:[iam.swift.basics.s3.listbuckets]
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
    // snippet-end:[iam.swift.basics.s3.listbuckets]
}
// snippet-end:[iam.swift.basics.s3]
