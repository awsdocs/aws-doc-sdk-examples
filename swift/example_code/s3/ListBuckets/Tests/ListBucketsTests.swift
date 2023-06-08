//
// Tests for the `ListBuckets` example for Amazon Simple Storage Service (S3)
// using the AWS SDK for Swift.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//

import XCTest
import Foundation
import AWSS3
import ClientRuntime

@testable import listbuckets

struct TestItem {
    var name: String
    var date: String
}

public struct MockS3Session: S3SessionProtocol {
    var testData: [TestItem]

    init(data: [TestItem]) {
        self.testData = data
    }

    public func listBuckets(input: ListBucketsInput) async throws
            -> ListBucketsOutputResponse {
        var bucketList: [S3ClientTypes.Bucket] = []
        let df = DateFormatter()
        df.dateFormat = "M/d/yy, h:mm:ss a z"

        for item in self.testData {
            let bucket = S3ClientTypes.Bucket(
                creationDate: df.date(from: item.date),
                name: item.name
            )
            bucketList.append(bucket)
        }
        
        let response = ListBucketsOutputResponse(
            buckets: bucketList,
            owner: nil
        )
        return response
    }
}

final class ListBucketsTests: XCTestCase {
    var session: MockS3Session? = nil
    var s3: S3Manager? = nil

    /// Perform one-time initialization that is done before starting to
    /// execute the tests.
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
        var testData: [TestItem] = []
        testData.append(TestItem(name: "Testfile-1", date: "2/4/65, 1:23:45 AM UTC"))
        testData.append(TestItem(name: "Another-file", date: "1/13/72, 4:43:21 PM UTC"))
        testData.append(TestItem(name: "Very-foo-file", date: "8/17/47, 12:34:00 PM UTC"))

        self.session = MockS3Session(data: testData)
        self.s3 = S3Manager(session: self.session!)
    }

    func testGetAllBuckets() async throws {
        var itemList = self.session!.testData
        let bucketList = try await s3!.getAllBuckets()
        let df = DateFormatter()
        df.dateStyle = .short
        df.timeStyle = .long

        for bucket in bucketList {
            var dateStr: String? = nil

            if bucket.creationDate != nil {
                dateStr = df.string(from: bucket.creationDate!)
            }
            
            itemList = itemList.filter { $0.name != bucket.name && $0.date != dateStr}
        }

        XCTAssertTrue(itemList.count == 0, "Retrieved list doesn't match")
    }

    /// Test the command's ``bucketString()`` function by calling it with a
    /// known bucket name and a date for which the string is known. Then
    /// compare the result to the expected value.
    func testBucketString() async throws {
        let testDate = "1/23/45, 6:07:08 PM UTC"
        let testName = "test-bucket-name"
        let testString = "\(testName) (created \(testDate))"

        let df = DateFormatter()
        df.dateFormat = "M/d/yy, h:mm:ss a z"
        
        let bucket = S3ClientTypes.Bucket(
            creationDate: df.date(from: testDate),
            name: testName
        )

        // Create an ExampleCommand to on which to call `bucketString()`.

        let command = try ExampleCommand.parse([])
        let bs = command.bucketString(bucket)

        XCTAssertEqual(bs, testString, "Converted date doesn't match original")
    }

    /// Test the ``ExampleCommand`` function ``dateToString()`` by calling it
    /// with a `Date` object for which we know the expected string result.
    func testDateToString() async throws {
        let testDate = Date(timeIntervalSinceReferenceDate: -123456789.0)
        let testString = "2/2/97, 2:26:51 AM UTC"

        // Create an ExampleCommand on which to call `dateToString()`. Then
        // call `dateToString()` and check the result.

        let command = try ExampleCommand.parse([])
        let ds = command.dateToString(testDate)
        
        XCTAssertEqual(ds, testString, "Converted date doesn't match expected string")
    }
}