/*
   A class containing functions that interact with the AWS Simple Storage
   Service (Amazon S3).

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
    /// The AWS Region to use for Amazon S3 operations.
    let region: String

    /// The S3Client used to interact with Amazon S3.
    var s3Client: S3Client

    /// Initialize the Amazon S3 client, optionally with credentials.
    ///
    /// - Parameters:
    ///   - region: A `String` providing the AWS Region to use for Amazon S3. 
    ///     operations. If not provided, us-east-2 is assumed.
    ///   - accessKeyId: An optional `String` giving the access key ID of the
    ///     credentials to use.
    ///   - secretAccessKey: An optional `String` giving the credentials'
    ///     secret access key.
    ///   - sessionToken: An optional string specifying the session token.
    // snippet-start:[iam.swift.basics.s3.init]
    public init(region: String = "us-east-2",
                accessKeyId: String? = nil,
                secretAccessKey: String? = nil,
                sessionToken: String? = nil) async {
        do {
            self.region = region

            // If the access key ID isn't provided, initialize the Amazon
            // S3 client with the Region. Otherwise, use the credentials.

            if accessKeyId == nil {
                s3Client = try S3Client(region: self.region)
            } else {
                // Use the given access key ID, secret access key, and session token
                // to generate a static credentials provider suitable for use when
                // initializing an Amazon S3 client.

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

                // Create an Amazon S3 configuration specifying the credentials
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

    /// Set the credentials to use when making Amazon S3 calls. This is done by
    /// replacing the internal `S3Client` with a new one that uses the
    /// credentials.
    ///
    /// - Parameters:
    ///   - accessKeyId: The access key ID.
    ///   - secretAccessKey: The secret access key.
    ///   - sessionToken: The optional session token string.
    // snippet-start:[iam.swift.basics.s3.setcredentials]
    public func setCredentials(accessKeyId: String, secretAccessKey: String,
                sessionToken: String? = nil) async throws {
        do {
            // Use the given access key ID, secret access key, and session token
            // to generate a static credentials provider suitable for use when
            // initializing an Amazon S3 client.

            let credentialsProvider = try AWSCredentialsProvider.fromStatic(
                AWSCredentialsProviderStaticConfig(
                    accessKey: accessKeyId,
                    secret: secretAccessKey,
                    sessionToken: sessionToken
                )
            )

            // Create a new Amazon S3 client with the specified access
            // credentials.

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

    /// Switch to using the default credentials for future Amazon S3 calls.
    /// Replace the internal Amazon S3 client with a client created without a
    /// given a specific set of credentials.
    // snippet-start:[iam.swift.basics.s3.resetcredentials]
    public func resetCredentials() async throws {
        do {
            s3Client = try S3Client(region: self.region)
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.s3.resetcredentials]

    /// Returns an array of `S3ClientTypes.Bucket` objects providing
    /// information about each bucket in the Amazon S3 account.
    /// 
    /// - Returns: An array of `S3ClientTypes.Bucket` objects listing the
    ///   buckets in the Amazon S3 account.
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
