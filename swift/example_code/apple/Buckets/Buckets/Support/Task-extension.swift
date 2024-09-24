//
//  Task-extension.swift
//  Buckets
//
//  Created by Shepherd, Eric on 9/5/24.
//

import Foundation

// Extend the `Task` class to add a `synchronous(priority:operation)` function
// by blocking until the task is complete.
extension Task where Failure == Error {
    /// Performs an asynchronous task in a synchronous context.
    ///
    /// > Note: This function blocks the thread until the given operation is
    ///   finished. The caller is responsible for managing multithreading.
    static func synchronous(priority: TaskPriority? = nil, operation: @escaping @Sendable () async throws -> Success) {
        let semaphore = DispatchSemaphore(value: 0)

        Task(priority: priority) {
            defer { semaphore.signal() }
            return try await operation()
        }

        semaphore.wait()
    }
}
