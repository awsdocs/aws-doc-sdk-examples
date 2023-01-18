/*
   General-purpose utility functions for Swift examples.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation
import Dispatch

public enum STimerError: Error {
    case InvalidDuration
}

/// A simple timer that can be used to pause a thread for a specified amount
/// of time.
public struct STimer {
    /// The duration of the timer in seconds.
    let duration: Double
    /// The time at which the timer started.
    private var startTime: Date?
    /// The time at which the timer ended.
    private var endTime: Date?

    init(duration: Double = 1.0) throws {
        if duration <= 0.0 {
            throw STimerError.InvalidDuration
        }

        self.duration = duration
        self.startTime = nil
        self.endTime = nil
    }

    /// Wait for the timer to expire, displaying optional messages before
    /// and/or after the timer runs.
    mutating func wait(startMessage: String? = nil, endMessage: String? = nil) {
        if startMessage != nil {
            print(startMessage!)
        }

        startTime = Date(timeIntervalSinceNow: 0.0)
        Thread.sleep(forTimeInterval: self.duration)
        self.endTime = Date(timeIntervalSinceNow: 0.0)
        
        if endMessage != nil {
            print(endMessage!)
        }
    }

    /// Return the number of seconds the timer actually took to complete, or a
    /// negative value if the timer has not completed.
    var actualDuration: Double {
        guard let start = self.startTime,
              let end = self.endTime else {
            return -1.0
        }
        return end.timeIntervalSince(start)
    }
}