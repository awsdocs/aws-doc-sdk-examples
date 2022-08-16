/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import XCTest
@testable import SwiftUtilities

final class TestWaiterTests: XCTestCase {
    /// Create a waiter with default settings and use it. Make sure task result
    /// is as expected.
    func testDefaultSettings() async throws {
        let waiter = TestWaiter()

        let testTask = Task { () -> String in
            defer {
                waiter.signal()
            }
            return "Output is correct"
        }

        do {
            // Check that timeout did not elapse.
            XCTAssertFalse(waiter.wait(), "Timeout elapsed but should not have")

            // Check that result string is correct.
            let result = await testTask.result
            let resultStr = try result.get()
            XCTAssertTrue(resultStr == "Output is correct", "Task result is not correct")
        } catch {
            throw error
        }
    }

    /// Create a waiter with specific timeout and make sure it times out after
    /// that amount of time.
    func testTimeout() async {
        let waiter = TestWaiter(timeout: 0.5)

        Task() {
            Thread.sleep(forTimeInterval: 1)
            waiter.signal()
        }

        let didTimeout = waiter.wait()
        XCTAssertTrue(didTimeout, "Timeout did not expire but should have")
    }
}