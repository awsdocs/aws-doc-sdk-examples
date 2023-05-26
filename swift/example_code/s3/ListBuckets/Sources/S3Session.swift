/// A class containing functions to access Amazon Simple Storage Service (S3).
/// The functions are defined by the protocol ``S3SessionProtocol``, which is
/// then implemented by S3Session, which simply forwards requests to the AWS
/// SDK for Swift.
///
/// For testing purposes (see the file `S3SessionTests.swift`), the protocol
/// is implemented by ``S3SessionMock``.
///
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0

import Foundation
import ClientRuntime
import AWSS3

public protocol S3SessionProtocol {
    func listBuckets(input: ListBucketsInput) async throws -> ListBucketsOutputResponse
}

public struct S3Session: S3SessionProtocol {
    let client: S3Client
    let awsRegion: String

    init(region: String = "us-east-1") throws {
        self.awsRegion = region
        self.client = try S3Client(region: awsRegion)
    }

    public func listBuckets(input: ListBucketsInput) async throws
            -> ListBucketsOutputResponse {
        return try await self.client.listBuckets(input: input)
    }
}

public class S3Manager {
    var session: S3SessionProtocol

    init(session: S3SessionProtocol) {
        self.session = session
    }

    /// Return an array containing information about every available bucket.
    /// 
    /// - Returns: An array of ``S3ClientTypes.Bucket`` objects describing
    ///   each bucket.
    public func getAllBuckets() async throws -> [S3ClientTypes.Bucket] {
        let output = try await self.session.listBuckets(input: ListBucketsInput())

        guard let buckets = output.buckets else {
            return []
        }
        return buckets
    }
}