// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import Foundation
import ArgumentParser

/// Flags used to identify whether the file is to be uploaded or downloaded.
enum TransferDirection: String, EnumerableFlag {
    /// The file transfer is an upload.
    case up
    /// The file transfer is a download.
    case down
}
