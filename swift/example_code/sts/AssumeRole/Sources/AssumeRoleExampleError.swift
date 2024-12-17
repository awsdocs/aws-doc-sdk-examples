// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/// An `Error` type used to return errors from the
/// `assumeRole(identityResolver: roleArn:)` function.
enum AssumeRoleExampleError: Error {
    /// An error indicating that the STS `AssumeRole` request failed.
    case assumeRoleFailed
    /// An error indicating that the returned credentials were missing
    /// required information.
    case incompleteCredentials
    /// An error indicating that no credentials were returned by `AssumeRole`.
    case missingCredentials

    /// Return a human-readable explanation of the error.
    var errorDescription: String? {
        switch self {
        case .assumeRoleFailed:
            return "Unable to assume the specified role."
        case .incompleteCredentials:
            return "AWS STS returned incomplete credentials."
        case .missingCredentials:
            return "AWS STS did not return any credentials for the specified role."
        }
    }
}
