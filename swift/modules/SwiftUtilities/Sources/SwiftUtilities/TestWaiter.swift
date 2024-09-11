// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
   A Dispatch-based semaphore type for easily blocking until
   a segment of code is done.
*/

import Foundation

/// Set `DEBUG_SEM` to `true` to output debugging information to console from
/// within `TestWaiter` operations.
let DEBUG_SEM = false

/// A structure type which manages a Dispatch-based semaphore for you.
///
/// To begin a block, create a new `TestWaiter` object:
/// ```swift
/// waiter = TestWaiter.init(name: "MyWaiter")
/// ```
/// After the code that should block, release the lock by calling the waiter's
/// `signal()` function:
/// ```swift
/// waiter.signal()
/// ```
/// At the point in your code that needs to wait for the blocking segment to
/// complete, call `TestWaiter.wait()`:
/// ```swift
/// waiter.wait()
/// ```
///
/// For example:
/// ```swift
/// let waiter = TestWaiter("Teardown")
/// Task() {
///     await someAsyncOperation()
///     waiter.signal()
/// }
///
/// waiter.wait()
/// ```
public struct TestWaiter {
    let sem: DispatchSemaphore
    var name: String
    var timeout: Double

    /// Initialize a new `TestWaiter` with a custom name and timeout. If you
    /// don't provide a value for `timeout`, the default is 2 seconds.
    /// - Parameters:
    ///   - name: A name for the `TestWaiter`; used only for debug output.
    ///   - timeout: The number of seconds for `TestWaiter.wait()` to wait
    ///     before timing out. The default is 2 seconds.
    public init(name: String = "Unnamed", timeout: Double = 2.0) {
        self.name = name
        self.timeout = timeout

        sem = DispatchSemaphore(value: 0)
        if DEBUG_SEM == true {
            print("Semaphore initialized: \(name).")
        }
    }

    /// Signal the dispatch semaphore to unlock it.
    public func signal() {
        if DEBUG_SEM == true {
            print("Locking semaphore: \(name).")
        }
        sem.signal()
    }

    /// Wait until the semaphore is unlocked or until the timeout period has
    /// elapsed, whichever comes first.
    ///
    /// - Returns: `true` if the timeout elapsed; `false` if the semaphore was
    ///            signaled. Ignorable.
    @discardableResult public func wait() -> Bool {
        var timedOut: Bool = false

        if DEBUG_SEM == true {
            print("Wait for semaphore \(name): Start.")
        }

        let timeoutResult = sem.wait(timeout: .now() + self.timeout)
        if (timeoutResult == .timedOut) {
            timedOut = true
        }

        if DEBUG_SEM == true {
            print("Wait for semaphore \(name). Complete.")
        }
        return timedOut
    }
}