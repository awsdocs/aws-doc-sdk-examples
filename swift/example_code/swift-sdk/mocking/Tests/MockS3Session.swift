// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// An implementation of ``S3SessionProtocol`` that returns mock data instead
// of calling through to Amazon Web Services (AWS).

import Foundation
import ClientRuntime
import AWSS3

@testable import mocking

/// A structure format used to provide the data from which mock Amazon S3
/// buckets are built.
struct MockBucketInfo {
    /// The bucket's name.
    var name: String
    /// The bucket's creation timestamp.
    var created: Date
}

// snippet-start:[mocking.swift.mocksession]
/// The ``MockS3Session`` type implements ``S3SessionProtocol`` but instead of
/// calling through to the AWS SDK for Swift, its implementation of SDK
/// functions return mocked results.
public class MockS3Session: S3SessionProtocol {
    /// An array of data used to construct the mock bucket descriptions.
    private(set) var mockInfo: [MockBucketInfo]
    /// An array of mock bucket descriptions as SDK `Bucket` objects.
    var mockBuckets: [S3ClientTypes.Bucket] = []
    /// A date formatter to convert ISO format date strings into timestamps.
    let isoDateFormatter = ISO8601DateFormatter()

    /// Initialize the mock session with some pretend buckets.
    init() {
        self.mockInfo = [
            MockBucketInfo(
                name: "swift",
                created: isoDateFormatter.date(from: "2014-06-02T11:45:00-07:00")!
            ),
            MockBucketInfo(
                name: "amazon",
                created: isoDateFormatter.date(from: "1995-07-16T08:00:00-07:00")!
            ),
            MockBucketInfo(
                name: "moon",
                created: isoDateFormatter.date(from: "1969-07-20T13:17:39-07:00")!
            )
        ]

        // Construct an array of `S3ClientTypes.Bucket` objects containing the
        // mock bucket data. The bucket objects only contain the minimum data
        // needed to test against. Update this if additional bucket
        // information is used by the main program.

        for item in self.mockInfo {
            let bucket = S3ClientTypes.Bucket(
                creationDate: item.created,
                name: item.name
            )
            self.mockBuckets.append(bucket)
        }
    }

    /// Compare the specified names to the mock data and see if the names match.
    ///
    /// - Parameter names: An array of bucket names to compare against the
    ///   expected names.
    ///
    /// - Returns: `true` if the names match. `false` if they don't.
    func checkBucketNames(names: [String]) -> Bool {
        let sortedMockNames = (self.mockInfo.map { $0.name }).sorted()

        return sortedMockNames == names.sorted()
    }

    // snippet-start:[mocking.swift.implement-mock]
    /// An implementation of the Amazon S3 function `listBuckets()` that
    /// returns the mock data instead of accessing AWS.
    ///
    /// - Parameter input: The input to the `listBuckets()` function.
    ///
    /// - Returns: A `ListBucketsOutput` object containing the list of
    ///   buckets.
    public func listBuckets(input: ListBucketsInput) async throws
            -> ListBucketsOutput {
        let response = ListBucketsOutput(
            buckets: self.mockBuckets,
            owner: nil
        )
        return response
    }
    // snippet-end:[mocking.swift.implement-mock]
}
// snippet-end:[mocking.swift.mocksession]
