// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import Foundation

/// Represents errors that need to be handled or reported to the user.
enum BucketsAppError: Error, LocalizedError {
    /// Sign In With Apple request failed.
    case signInWithAppleFailed
    /// Sign In With Apple was canceled by the user.
    case signInWithAppleCanceled
    /// The `AssumeRoleWithWebIdentity` request failed.
    case assumeRoleFailed
    /// The credentials returned by Sign In With Apple is missing
    /// required information.
    case credentialsIncomplete
    /// The authentication request failed.
    case credentialsFailed
    /// The `ListBuckets` request did not successfully return a list
    /// of Amazon S3 buckets.
    case bucketListMissing
    /// The token file could not be written to storage.
    case tokenFileError(reason: String = "Unable to create the token.")
    
    var errorDescription: String? {
        switch self {
        case .signInWithAppleFailed:
            return "Unable to sign into AWS"
        case .signInWithAppleCanceled:
            return "Sign in canceled"
        case .assumeRoleFailed:
            return "Unable to access AWS S3"
        case .credentialsIncomplete:
            return "Unable to authenticate"
        case .credentialsFailed:
            return "Invalid web token"
        case .bucketListMissing:
            return "Unable to access AWS S3"
        case .tokenFileError:
            return "Token error"
        }
    }
    
    /// A human-readable error message string corresponding to the
    /// error returned.
    var recoverySuggestion: String? {
        switch self {
        case .signInWithAppleFailed:
            return "Sign In With Apple returned an error."
        case .signInWithAppleCanceled:
            return "User did not authenticate."
        case .assumeRoleFailed:
            return "The role could not be assumed using the web token returned by Sign In With Apple."
        case .credentialsIncomplete:
            return "The credentials returned by AssumeRoleWithWebIdentity are incomplete."
        case .credentialsFailed:
            return "An error occurred while attempting to retrieve credentials from AWS."
        case .bucketListMissing:
            return "Amazon S3 did not return a valid bucket list."
        case .tokenFileError(let reason):
            return "An error occurred with the local Sign In With Apple token: \(reason)"
        }
    }
}
