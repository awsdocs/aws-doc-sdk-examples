//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// A protocol and implementation to allow calling and mocking of the AWS SDK
// for Swift's `S3Client.listBuckets(input:)` function.
//

import Foundation
import ClientRuntime
import AWSS3

// snippet-start:[mocking.swift.protocol]
/// The S3SessionProtocol protocol describes the Amazon S3 functions this
/// program uses during an S3 session. It needs to be implemented once to call
/// through to the corresponding SDK for Swift functions, and a second time to
/// instead return mock results.
public protocol S3SessionProtocol {
    func listBuckets(input: ListBucketsInput) async throws
            -> ListBucketsOutput
}
// snippet-end:[mocking.swift.protocol]

/// An implementation of ``S3SessionProtocol`` that calls the equivalent
/// functions in the AWS SDK for Swift. This class is used by the main program
/// instead of calling the SDK directly.
// snippet-start:[mocking.swift.session]
public class S3Session: S3SessionProtocol {
    let client: S3Client
    let region: String

    /// Initialize the session to use the specified AWS Region.
    ///
    /// - Parameter region: The AWS Region to use. Default is `us-east-1`.
    init(region: String = "us-east-1") throws {
        self.region = region

        // Create an ``S3Client`` to use for AWS SDK for Swift calls.
        self.client = try S3Client(region: self.region)
    }

    /// Call through to the ``S3Client`` function `listBuckets()`.
    ///
    /// - Parameter input: The input to pass through to the SDK function
    ///   `listBuckets()`.
    ///
    /// - Returns: A ``ListBucketsOutput`` with the returned data.
    ///
    // snippet-start:[mocking.swift.implement-real]
    public func listBuckets(input: ListBucketsInput) async throws
            -> ListBucketsOutput {
        return try await self.client.listBuckets(input: input)
    }
    // snippet-end:[mocking.swift.implement-real]
}
// snippet-end:[mocking.swift.session]
