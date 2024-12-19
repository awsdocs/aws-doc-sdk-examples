// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[swift.transcribe-streaming.transcribeerror]
/// Errors thrown by the example's functions.
enum ExampleError: Error {
    /// A role by the specified name already exists.
    case roleAlreadyExists
    /// Unable to create the role.
    case roleCreateError

    var errorDescription: String? {
        switch self {
        case .roleAlreadyExists:
            return "A role by the specified name already exists."
        case .roleCreateError:
            return "Unable to create the specfied role."
        }
    }
}
// snippet-end:[swift.transcribe-streaming.transcribeerror]
