// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/// Errors thrown by the example's functions.
enum TransferError: Error {
    /// The checksum is missing or erroneous.
    case checksumError
    /// An error occurred when completing a multi-part upload to Amazon S3.
    case multipartFinishError(_ message: String = "")
    /// An error occurred when starting a multi-part upload to Amazon S3.
    case multipartStartError
    /// An error occurred while uploading a file to Amazon S3.
    case uploadError(_ message: String = "")
    /// An error occurred while reading the file's contents.
    case readError

    var errorDescription: String? {
        switch self {
        case .checksumError:
            return "The checksum is missing or incorrect"
        case .multipartFinishError(message: let message):
            return "An error occurred when completing a multi-part upload to Amazon S3. \(message)"
        case .multipartStartError:
            return "An error occurred when starting a multi-part upload to Amazon S3."
        case .uploadError(message: let message):
            return "An error occurred attempting to upload the file: \(message)"
        case .readError:
            return "An error occurred while reading the file data"
        }
    }
}
