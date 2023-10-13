//
// Tests for the `ListBuckets` example for Amazon Simple Storage Service
// (Amazon S3) using the AWS SDK for Swift.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//

import XCTest
import Foundation
import ClientRuntime
import AWSS3

@testable import listbuckets

/// Contains the information needed to describe a single bucket.
struct BucketInfo {
    /// The name of the bucket.
    var name: String
    /// The bucket's creation date, in the form `M/d/yy, h:mm:ss a z`.
    var date: String
}

/// The `MockS3Session` type implements `S3SessionProtocol` but instead of
/// calling through to the AWS SDK for Swift, its implementations of the SDK
/// functions return mocked results.
public struct MockS3Session: S3SessionProtocol {
    /// An array of `BucketInfo` objects describing the mock buckets to return
    /// when `listBuckets()` is called.
    var testData: [BucketInfo]

    /// Initialize the mock Amazon S3 session to use the specified mock bucket
    /// information.
    /// 
    /// - Parameter data: An array of objects with the data to use to
    ///   construct fake bucket records.
    init(data: [BucketInfo]) {
        self.testData = data
    }

    /// An implementation of the Amazon S3 function `listBuckets()` that
    /// returns bucket objects using data from the mock bucket descriptions
    /// provided when the session was created.
    ///
    /// - Parameter input: The input to the `listBuckets()` function.
    ///
    /// - Returns: A `ListBucketsOutput` object containing the list of
    ///   buckets.
    public func listBuckets(input: ListBucketsInput) async throws
            -> ListBucketsOutput {
        var bucketList: [S3ClientTypes.Bucket] = []
        let df = DateFormatter()
        df.dateFormat = "M/d/yy, h:mm:ss a z"

        // Build the array of Amazon S3 bucket objects from the test data.
        
        for item in self.testData {
            let bucket = S3ClientTypes.Bucket(
                creationDate: df.date(from: item.date),
                name: item.name
            )
            bucketList.append(bucket)
        }
        
        // Create and return the `ListBucketsOutput` object containing
        // the results.

        let response = ListBucketsOutput(
            buckets: bucketList,
            owner: nil
        )
        return response
    }
}

/// The tests for the ListBuckets example.
final class ListBucketsTests: XCTestCase {
    /// The session to use for Amazon S3 calls. In this case, it's a mock
    /// implementation. 
    var session: MockS3Session? = nil
    /// The `S3Manager` that uses the session to perform Amazon S3 operations.
    var s3: S3Manager? = nil

    /// Perform one-time initialization before executing any tests.
    override class func setUp() {
        super.setUp()
        SDKLoggingSystem.initialize(logLevel: .error)
    }

    /// Set up things that need to be done just before each
    /// individual test function is called.
    override func setUp() {
        super.setUp()

        // Note that the character between the seconds and AM/PM is a narrow
        // non-breaking space Unicode character. This is what the
        // `DateFormatter` class uses.
        var testData: [BucketInfo] = []
        testData.append(BucketInfo(name: "Testfile-1", date: "2/4/65, 1:23:45 AM UTC"))
        testData.append(BucketInfo(name: "Another-file", date: "1/13/72, 4:43:21 PM UTC"))
        testData.append(BucketInfo(name: "Very-foo-file", date: "8/17/47, 12:34:00 PM UTC"))

        self.session = MockS3Session(data: testData)
        self.s3 = S3Manager(session: self.session!)
    }

    /// Test the command's ``getAllBuckets()`` function.
    func testGetAllBuckets() async throws {
        var itemList = self.session!.testData
        let bucketList = try await s3!.getAllBuckets()
        let df = DateFormatter()
        df.dateStyle = .short
        df.timeStyle = .long

        // Go through the results and make sure they match what we expect.
        for bucket in bucketList {
            var dateStr: String? = nil

            if bucket.creationDate != nil {
                dateStr = df.string(from: bucket.creationDate!)
            }
            
            itemList = itemList.filter { $0.name != bucket.name && $0.date != dateStr}
        }

        XCTAssertTrue(itemList.count == 0, "Retrieved list doesn't match")
    }

    /// Test the ``bucketString()`` function by calling it with a known bucket
    /// name and a date for which the string is known. Then compare the result
    /// to the expected value.
    func testBucketString() async throws {
        let testDate = "1/23/45, 6:07:08 PM UTC"
        let testName = "test-bucket-name"
        let testString = "\(testName) (created \(testDate))"

        let df = DateFormatter()
        df.dateFormat = "M/d/yy, h:mm:ss a z"
        
        // Create a bucket object to use for the test.
        let bucket = S3ClientTypes.Bucket(
            creationDate: df.date(from: testDate),
            name: testName
        )

        let bs = bucketString(bucket)
        
        XCTAssertEqual(bs, testString, "Converted date doesn't match original")
    }

    /// Test the function ``dateToString()`` by calling it with a `Date`
    /// object for which we know the expected string result.
    func testDateToString() async throws {
        let testDate = Date(timeIntervalSinceReferenceDate: -123456789.0)
        let testString = "2/2/97, 2:26:51 AM UTC"

        let ds = dateToString(testDate)

        XCTAssertEqual(ds, testString, "Converted date doesn't match expected string")
    }

    /// Test that calling ``dateToString()`` with a `nil` input returns the
    /// expected result.
    func testDateToStringUnknown() async throws {
        let ds = dateToString(nil)

        XCTAssertEqual(ds, "<unknown>", "Result of dateToString(nil) should be 'unknown' but was '\(ds)'")
    }
}