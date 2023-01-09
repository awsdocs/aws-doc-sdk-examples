/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import XCTest
@testable import SwiftUtilities

final class SUtilitiesTests: XCTestCase {
    /// Test creating a valid timer with a typical duration.
    func testSimpleWait() {
        do {
            var timer = try STimer(duration: 2.0)

            timer.wait()
            XCTAssertTrue(timer.actualDuration >= 0, "The timer did not return successfully.")
            let absDeviation = abs(timer.actualDuration - 2.0)
            XCTAssertTrue(absDeviation <= 0.01, "The timer took too long to return.")
        } catch {
            XCTFail("Unexpected exception creating valid timer.")
        }

    }

    /// Test to confirm that trying to create a timer with a negative duration
    /// properly throws an exception.
    func testInvalidDurationWait() {
        do {
            _ = try STimer(duration: -1.5)
            XCTFail("Timer was created with a negative duration.")
        } catch {
            return
        }
    }

    /// Test that calling wait() with a start message doesn't throw an
    /// exception.
    ///
    /// There isn't a way to test that the output is as expected in XCTest.
    func testWaitWithStartOutput() {
        do {
            var timer = try STimer(duration: 1.0)

            timer.wait(startMessage: "Starting 1 second timer (no end message).")
        } catch {
            XCTFail("Unexpected error creating valid timer.")
        }
    }

    /// Test that calling wait() with an end message doesn't throw an
    /// exception.
    ///
    /// There isn't a way to test that the output is as expected in XCTest.
    func testWaitWithEndOutput() {
        do {
            var timer = try STimer(duration: 3.0)

            timer.wait(endMessage: "Three second timer ended (no start message).")
        } catch {
            XCTFail("Unexpected error creating valid timer.")
        }
    }

    /// Test that calling wait() with a both start and end messages doesn't
    /// throw an exception.
    ///
    /// There isn't a way to test that the output is as expected in XCTest.
    func testWaitWithOutput() {
        do {
            var timer = try STimer(duration: 5.0)

            timer.wait(startMessage: "Starting five second timer.",
                       endMessage: "Five second timer ended.")
        } catch {
            XCTFail("Unexpected error creating valid timer.")
        }
    }
}