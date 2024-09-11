// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import XCTest
import Foundation
import AWSS3
//import AWSClientRuntime
import SmithyWaitersAPI
import SmithyHTTPAPI

@testable import Waiters

public enum MockS3Error: Error {
    case invalidBucketName
    case bucketAlreadyExists
}

public struct MockS3Session: S3SessionProtocol {
    var buckets: [String] = []

    /// Simulate the ``S3Client`` function `createBucket()`.
    ///
    /// - Parameter input: The input record.
    /// - Returns: The output record.
    public mutating func createBucket(input: CreateBucketInput) async throws
                -> CreateBucketOutput {
        if input.bucket == nil {
            throw MockS3Error.invalidBucketName
        }
        if buckets.contains(input.bucket!) {
            throw MockS3Error.bucketAlreadyExists
        }

        buckets.append(input.bucket!)
        
        return CreateBucketOutput(location: "/\(input.bucket!)")
    }

    /// Simulate the ``S3Client`` function `waitUntilBucketExists()`.
    ///
    /// - Parameter input: The input record.
    /// - Returns: The output record.
    public func waitUntilBucketExists(options: WaiterOptions,
                input: HeadBucketInput) async throws
                -> WaiterOutcome<HeadBucketOutput> {
        let output = HeadBucketOutput()

        return WaiterOutcome<HeadBucketOutput>(
            attempts: 1,
            result: .success(output)
        )
    }

    /// Simulate the ``S3Client`` function `deleteBucket()`.
    ///
    /// - Parameter input: The input record.
    /// - Returns: The output record.
    public mutating func deleteBucket(input: DeleteBucketInput) async throws
                -> DeleteBucketOutput {
        let output = DeleteBucketOutput()

        return output
    }
}

/// Perform tests on the functions in the main.swift file.
final class MainTests: XCTestCase {
    /// The mock session implementation to use for Amazon S3 calls.
    var session: MockS3Session? = nil

    /// The `S3Manager` that uses the session to perform Amazon S3
    /// operations.
    var s3: S3Manager? = nil

    /// Class-wide setup function for the test case, which is run *once*
    /// before any tests are run.
    ///
    /// Configures the AWS SDK log system to only log errors. This cleans up
    /// the test output to let the results be more visible.
    override class func setUp() {
        super.setUp()
    }

    /// Set up things that need to be done just before each individual
    /// test function is called.
    override func setUp() {
        super.setUp()

        self.session = MockS3Session()
        self.s3 = S3Manager(session: self.session!)
    }

    func testCreateBucket() async throws {
        let bucketName = try await s3!.createBucket()

        // Verify the format of the string.

        let nameStart = "waiter-example-"
        let namePart1 = String(bucketName.prefix(nameStart.count))
        let namePart2 = String(bucketName.suffix(bucketName.count - nameStart.count))

        XCTAssertEqual(nameStart, namePart1, "The bucket name's prefix is not correct.")
        XCTAssertTrue(Int(namePart2) != nil, "The bucket name's random number isn't a number.")

        // Check for the bucket to exist.

        let bucketExists = try await s3!.waitForBucket(name: bucketName)
        XCTAssertTrue(bucketExists, "The bucket was not successfully created.")

        // Clean up.

        try await s3!.deleteBucket(name: bucketName)
    }

    func testWaitForBucket() async throws {
        // Create a bucket and see if it shows up.
        // Wait for a bucket that doesn't exist.
        // Delete the bucket created previously and wait for it to exist,
        // make sure it fails.
    }
}