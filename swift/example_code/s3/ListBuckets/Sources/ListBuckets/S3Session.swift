// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/// The `S3Manager` class provides functions to access Amazon Simple Storage
/// Service (Amazon S3). `S3Manager` hands off the actual Amazon S3 operations
/// to a worker object that implements the protocol ``S3SessionProtocol``.
///
/// When creating an ``S3Manager`` object that actually calls Amazon S3 using
/// the AWS SDK for Swift, specify an `S3Session` instance. Use a
/// `MockS3Session` instance to use mock data for testing.
///
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0

import AWSClientRuntime
import AWSS3
import Foundation

// snippet-start:[s3.swift.listbuckets.s3sessionprotocol]
/// A protocol defining the Amazon S3 functions we want to mock
/// during testing.
public protocol S3SessionProtocol {
    func listBuckets(input: ListBucketsInput) async throws -> [S3ClientTypes.Bucket]
}

// snippet-end:[s3.swift.listbuckets.s3sessionprotocol]

// snippet-start:[s3.swift.listbuckets.s3session]
/// An implementation of ``S3SessionProtocol`` that calls the equivalent
/// functions in the AWS SDK for Swift.
public struct S3Session: S3SessionProtocol {
    let client: S3Client

    /// Initialize the session to use the specified AWS Region.
    ///
    /// - Parameter region: The optional AWS Region to use.
    init(region: String? = nil) async throws {
        do {
            let config = try await S3Client.S3ClientConfiguration()
            if let region = region {
                config.region = region
            }

            // Create an ``S3Client`` to use for AWS SDK for Swift calls.
            self.client = S3Client(config: config)
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon S3 client"))
            throw error
        }
    }

    /// Call through to the ``S3Client`` function `listBuckets()`.
    /// - Parameter input: The input to pass through to the SDK function
    ///   `listBuckets()`.
    /// - Returns: An array of ``S3ClientTypes.Bucket`` objects describing
    ///   each bucket.
    public func listBuckets(input: ListBucketsInput) async throws
        -> [S3ClientTypes.Bucket]
    {
        // snippet-start:[s3.swift.intro.listbuckets]
        // Use "Paginated" to get all the buckets.
        // This lets the SDK handle the 'continuationToken' in "ListBucketsOutput".
        let pages = client.listBucketsPaginated(
            input: input
        )
        // snippet-end:[s3.swift.intro.listbuckets]

        // Get the bucket names.
        var allBuckets: [S3ClientTypes.Bucket] = []

        do {
            for try await page in pages {
                guard let buckets = page.buckets else {
                    print("Error: no buckets returned.")
                    continue
                }
                allBuckets += buckets
            }

            return allBuckets
        } catch {
            print("ERROR: listBuckets:", dump(error))
            throw error
        }
    }
}

// snippet-end:[s3.swift.listbuckets.s3session]

// snippet-start:[s3.swift.listbuckets.s3manager]
/// A class that uses an object that implements ``S3SessionProtocol`` to
/// access Amazon S3.
public class S3Manager {
    var client: S3SessionProtocol

    // snippet-start:[s3.swift.listbuckets.s3manager.init]
    /// Initialize the ``S3Manager`` to call Amazon S3 functions using the
    /// specified object that implements ``S3SessionProtocol``.
    ///
    /// - Parameter session: The session object to use when calling Amazon S3.
    init(session: S3SessionProtocol) {
        self.client = session
    }

    // snippet-end:[s3.swift.listbuckets.s3manager.init]

    // snippet-start:[s3.swift.listbuckets.ListBuckets]
    /// Return an array containing information about every available bucket.
    ///
    /// - Returns: An array of ``S3ClientTypes.Bucket`` objects describing
    ///   each bucket.
    public func getAllBuckets() async throws -> [S3ClientTypes.Bucket] {
        return try await client.listBuckets(input: ListBucketsInput())
    }
    // snippet-end:[s3.swift.listbuckets.ListBuckets]
}

// snippet-end:[s3.swift.listbuckets.s3manager]
