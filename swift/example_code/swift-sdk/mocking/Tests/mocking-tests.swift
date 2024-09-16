// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// Tests for the AWS SDK for Swift example. This demonstrates how to mock SDK
// functions.

import XCTest
import Foundation
import ClientRuntime
import AWSS3

@testable import mocking

// snippet-start:[mocking.swift.tests]
final class MockingTests: XCTestCase {
    /// The session to use for Amazon S3 calls. In this case, it's a mock
    /// implementation. 
    var session: MockS3Session? = nil
    /// The ``BucketManager`` that uses the session to perform Amazon S3
    /// operations.
    var bucketMgr: BucketManager? = nil

    /// Perform one-time initialization before executing any tests.
    override class func setUp() {
        super.setUp()
    }

    /// Set up things that need to be done just before each
    /// individual test function is called.
    override func setUp() {
        super.setUp()

        // snippet-start:[mocking.swift.tests-setup]
        self.session = MockS3Session()
        self.bucketMgr = BucketManager(session: self.session!)
        // snippet-end:[mocking.swift.tests-setup]
    }

    // snippet-start:[mocking.swift.tests-call]
    /// Test that `getBucketNames()` returns the expected results.
    func testGetBucketNames() async throws {
        let returnedNames = try await self.bucketMgr!.getBucketNames()
        XCTAssertTrue(self.session!.checkBucketNames(names: returnedNames),
                "Bucket names don't match")
    }
    // snippet-end:[mocking.swift.tests-call]
}
// snippet-end:[mocking.swift.tests]
