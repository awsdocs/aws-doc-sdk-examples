/// A class containing functions to access Amazon Identity and Access
/// Management, with a protocol to allow mocking.
///
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0
import Foundation
import ClientRuntime
import AWSIAM

// snippet-start:[iam.swift.listusers.usersession]
/// A protocol describing the Amazon IAM functions used by this project. By
/// calling the SDK through the protocol, it's possible to mock the functions.
public protocol UserSession {
    func listUsers(input: ListUsersInput) async throws -> ListUsersOutputResponse
}
// snippet-end:[iam.swift.listusers.usersession]

// snippet-start:[iam.swift.listusers.iamusersession]
/// Defines functions in `UserSession` that simply call through
/// to the real SDK functions of the same name.
public struct IAMUserSession: UserSession {
    let client: IAMClient

    /// Initialize the `IAMClient` used by the ``IAMUserSession``.
    init() throws {
        self.client = try IAMClient(region: "AWS_GLOBAL")
    }

    /// The `listUsers()` function calls through to the AWS SDK for Swift's
    /// `IAMClient` function of the same name.
    public func listUsers(input: ListUsersInput) async throws
                -> ListUsersOutputResponse {
        return try await client.listUsers(input: input)
    }
}
// snippet-end:[iam.swift.listusers.iamusersession]

