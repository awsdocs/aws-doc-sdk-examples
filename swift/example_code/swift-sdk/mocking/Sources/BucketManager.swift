//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// A class to manage Amazon Simple Storage Service (Amazon S3) operations
// using the ``S3Session`` class to access S3.
//

import Foundation
import ClientRuntime
import AWSS3

/// A class that uses an object that uses an object of type
/// ``S3SessionProtocol`` to access Amazon S3.
// snippet-start:[mocking.swift.using-session.class]
public class BucketManager {
    /// The object based on the ``S3SessionProtocol`` protocol through which to
    /// call SDK for swift functions. This may be either ``S3Session`` or
    /// ``MockS3Session``.
    var session: S3SessionProtocol

    /// Initialize the ``BucketManager`` to call Amazon S3 functions using the
    /// specified object that implements ``S3SessionProtocol``.
    ///
    /// - Parameter session: The session object to use when calling Amazon S3.
    // snippet-start:[mocking.swift.using-session.init]
    init(session: S3SessionProtocol) {
        self.session = session
    }
    // snippet-end:[mocking.swift.using-session.init]

    /// Return an array listing all of the user's buckets by calling the
    /// ``S3SessionProtocol`` function `listBuckets()`.
    /// 
    /// - Returns: An array of bucket name strings.
    ///
    // snippet-start:[mocking.swift.using-session.calling]
    public func getBucketNames() async throws -> [String] {
        let output = try await session.listBuckets(input: ListBucketsInput())

        guard let buckets = output.buckets else {
            return []
        }
        
        return buckets.map { $0.name ?? "<unknown>" }
    }
    // snippet-end:[mocking.swift.using-session.calling]
}
// snippet-end:[mocking.swift.using-session.class]
