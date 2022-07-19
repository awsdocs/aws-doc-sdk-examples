/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation

let DEBUG_SEM = false
struct TestWaiter {
    let sem: DispatchSemaphore
    var name: String = "Unnamed"
    var timeout: Double = 2.0

    init() {
        sem = DispatchSemaphore(value: 0)
        if DEBUG_SEM == true {
            print("Sem init: \(name)")
        }
    }

    init(name: String, timeout: Double = 2.0) {
        self.name = name
        self.timeout = timeout

        sem = DispatchSemaphore(value: 0)
        if DEBUG_SEM == true {
            print("Sem init: \(name)")
        }
    }

    /// Signal the DispatchSemaphore, ending the lock.
    public func signal() {
        if DEBUG_SEM == true {
            print("Sem signal: \(name)")
        }
        sem.signal()
    }

    /// Wait until the semaphore is unlocked, or until
    /// the timeout period has elapsed, whichever comes
    /// first.
    ///
    /// - Returns: true if the timeout elapsed; false if
    ///            the semaphore was signaled. Ignorable.
    @discardableResult public func wait() -> Bool {
        var timedOut: Bool = false

        if DEBUG_SEM == true {
            print("Sem wait: \(name) start")
        }
        
        let timeoutResult = sem.wait(timeout: .now() + self.timeout)
        if (timeoutResult == .timedOut) {
            timedOut = true
        }

        if DEBUG_SEM == true {
            print("Sem wait: \(name) complete")
        }
        return timedOut
    }
}