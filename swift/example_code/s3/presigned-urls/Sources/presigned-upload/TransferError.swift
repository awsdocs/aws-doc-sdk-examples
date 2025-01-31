// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/// Errors thrown by the example's functions.
enum TransferError: Error {
    /// An error occurred while uploading a file to Amazon S3.
    case uploadError(_ message: String = "")
    /// An error occurred while reading the file's contents.
    case readError

    var errorDescription: String? {
        switch self {
        case .uploadError(message: let message):
            return "An error occurred attempting to upload the file: \(message)"
        case .readError:
            return "An error occurred while reading the file data"
        }
    }
}
