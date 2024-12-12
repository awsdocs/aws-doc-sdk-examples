// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/// Errors thrown by the example's functions.
enum TransferError: Error {
    /// The destination directory for a download is missing or inaccessible.
    case directoryError
    /// An error occurred while downloading a file from Amazon S3.
    case downloadError(_ message: String = "")
    /// An error occurred while writing the file's contents.
    case writeError

    var errorDescription: String? {
        switch self {
        case .directoryError:
            return "The destination directory could not be located or created"
        case .downloadError(message: let message):
            return "An error occurred attempting to download the file: \(message)"
        case .writeError:
            return "An error occurred while writing the file data"
        }
    }
}
