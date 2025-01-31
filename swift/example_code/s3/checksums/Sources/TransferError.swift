// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/// Errors thrown by the example's functions.
enum TransferError: Error {
    /// The destination directory for a download is missing or inaccessible.
    case directoryError
    /// An error occurred while downloading a file from Amazon S3.
    case downloadError(_ message: String = "")
    /// An error occurred moving the file to its final destination.
    case fileMoveError
    /// An error occurred when completing a multi-part upload to Amazon S3.
    case multipartFinishError
    /// An error occurred when starting a multi-part upload to Amazon S3.
    case multipartStartError
    /// An error occurred while uploading a file to Amazon S3.
    case uploadError(_ message: String = "")
    /// An error occurred while reading the file's contents.
    case readError
    /// An error occurred while presigning the URL.
    case signingError
    /// An error occurred while writing the file's contents.
    case writeError

    var errorDescription: String? {
        switch self {
        case .directoryError:
            return "The destination directory could not be located or created"
        case .downloadError(message: let message):
            return "An error occurred attempting to download the file: \(message)"
        case .fileMoveError:
            return "The file couldn't be moved to the destination directory"
        case .multipartFinishError:
            return "An error occurred when completing a multi-part upload to Amazon S3."
        case .multipartStartError:
            return "An error occurred when starting a multi-part upload to Amazon S3."
        case .uploadError(message: let message):
            return "An error occurred attempting to upload the file: \(message)"
        case .readError:
            return "An error occurred while reading the file data"
        case .signingError:
            return "An error occurred while pre-signing the URL"
        case .writeError:
            return "An error occurred while writing the file data"
        }
    }
}
