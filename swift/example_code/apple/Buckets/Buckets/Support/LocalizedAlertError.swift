// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import Foundation

/// An error intended to be displayed in an alert due to an error.
struct LocalizedAlertError: LocalizedError {
    /// The error described by this object.
    let underlyingError: LocalizedError
    /// A human-readable description of the error.
    var errorDescription: String? {
        underlyingError.errorDescription
    }
    /// A human-readable suggestion for what caused the problem.
    var recoverySuggestion: String? {
        underlyingError.recoverySuggestion
    }

    /// Initialize the `LocalizedAlertError`.
    init?(error: Error?) {
        guard let localizedError = error as? LocalizedError else { return nil }
        underlyingError = localizedError
    }
}
