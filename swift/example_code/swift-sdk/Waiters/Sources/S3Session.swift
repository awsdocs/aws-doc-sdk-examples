// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/// The `S3Manager` class provides functions to access Amazon Simple Storage
/// Service (Amazon S3). `S3Manager` hands off the actual Amazon S3 operations
/// to a worker object that implements the protocol ``S3SessionProtocol``.
///
/// When creating an ``S3Manager`` object that actually calls Amazon S3 using
/// the AWS SDK for Swift, specify an `S3Session` instance. Use a
/// `MockS3Session` instance to use mock data for testing.

import Foundation
import AWSS3
// snippet-start:[waiters.swift.imports]
import SmithyWaitersAPI
// snippet-end:[waiters.swift.imports]

/// A protocol defining the Amazon S3 functions we want to mock
/// during testing.
public protocol S3SessionProtocol {
    mutating func createBucket(input: CreateBucketInput) async throws -> CreateBucketOutput
    mutating func deleteBucket(input: DeleteBucketInput) async throws -> DeleteBucketOutput
    func waitUntilBucketExists(options: WaiterOptions, input: HeadBucketInput) async throws -> WaiterOutcome<HeadBucketOutput>
}

/// An implementation of ``S3SessionProtocol`` that calls the equivalent
/// functions in the AWS SDK for Swift.
public struct S3Session: S3SessionProtocol {
    let client: S3Client
    let awsRegion: String

    /// Initialize the session to use the specified AWS Region.
    ///
    /// - Parameter region: The AWS Region to use. Default is `us-east-1`.
    init(region: String = "us-east-1") throws {
        self.awsRegion = region

        // Create an ``S3Client`` to use for AWS SDK for Swift calls.
        self.client = try S3Client(region: awsRegion)
    }

    /// Call through to the ``S3Client`` function `listBuckets()`.
    ///
    /// - Parameter input: The input record.
    /// - Returns: The output record.
    public func createBucket(input: CreateBucketInput) async throws
                -> CreateBucketOutput {
        return try await self.client.createBucket(input: input)
    }

    /// Call through to the ``S3Client`` function `waitUntilBucketExists()`.
    ///
    /// - Parameter input: The input record.
    /// - Returns: The output record.
    public func waitUntilBucketExists(options: WaiterOptions,
                input: HeadBucketInput) async throws
                -> WaiterOutcome<HeadBucketOutput> {
        return try await self.client.waitUntilBucketExists(options: options,
            input: input)
    }

    /// Call through to the ``S3Client`` function `deleteBucket()`.
    ///
    /// - Parameter input: The input record.
    /// - Returns: The output record.
    public func deleteBucket(input: DeleteBucketInput) async throws
                -> DeleteBucketOutput {
        return try await self.client.deleteBucket(input: input)
    }
}

/// A class that uses an object that implements ``S3SessionProtocol`` to
/// access Amazon S3.
public class S3Manager {
    var client: S3SessionProtocol

    /// Initialize the ``S3Manager`` to call Amazon S3 functions using the
    /// specified object that implements ``S3SessionProtocol``.
    ///
    /// - Parameter session: The session object to use when calling Amazon S3.
    init(session: S3SessionProtocol) {
        self.client = session
    }

    /// Create a new test bucket and return its name.
    ///
    /// - Returns: The new bucket's name as a string.
    ///
    public func createBucket() async throws -> String {
        // Generate a name for the sample bucket.

        let bucketName = "waiter-example-\(String(Int.random(in:1000000...9999999)))"

        // Create a bucket.

        _ = try await client.createBucket(
            input: CreateBucketInput(bucket: bucketName)
        )

        // Return the name of the new bucket.

        return bucketName
    }

    // snippet-start:[using-waiters.swift.waiter]
    /// Wait until a bucket with the specified name exists, then return
    /// to the caller. Times out after 60 seconds. Throws an error if the
    /// wait fails.
    ///
    /// - Parameter bucketName: A string giving the name of the bucket
    ///   to wait for.
    /// 
    /// - Returns: `true` if the bucket was found or `false` if not.
    ///
    public func waitForBucket(name bucketName: String) async throws -> Bool {
        // Because `waitUntilBucketExists()` internally uses the Amazon S3
        // action `HeadBucket` to look for the bucket, the input is specified
        // with a `HeadBucketInput` structure.

        let output = try await client.waitUntilBucketExists(
            options: WaiterOptions(maxWaitTime: 60.0),
            input: HeadBucketInput(bucket: bucketName)
        )

        switch output.result {
            case .success:
                return true
            case .failure:
                return false
        }
    }
    // snippet-end:[using-waiters.swift.waiter]

    /// Delete the specified bucket. Throw an exception if an error occurs.
    ///
    /// - Parameter bucketName: The name of the bucket to delete.
    public func deleteBucket(name bucketName: String) async throws {
        _ = try await client.deleteBucket(
            input: DeleteBucketInput(
                bucket: bucketName
            )
        )
    }
}
