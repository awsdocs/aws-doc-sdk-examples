/*
   An enum encapsulating errors returned by service handler functions.
   
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[iam.swift.basics.enum.service-error]
public enum ServiceHandlerError: Error {
    case noSuchUser            /// No matching user found, or unable to create the user.
    case keyError
    case authError
    case noSuchRole
    case noSuchPolicy
    case bucketError
    case idMismatch
    case arnMismatch
}
// snippet-end:[iam.swift.basics.enum.service-error]
