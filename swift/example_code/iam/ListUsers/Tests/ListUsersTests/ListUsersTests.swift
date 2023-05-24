/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSIAM
import ClientRuntime
import SwiftUtilities

@testable import listusers

/// An implementation of `UserSession` that returns mocked data instead of
/// calling AWS SDK for Swift.
public struct MockUserSession: UserSession {
    /// The number of users to make up.
    var numUsers: Int
    /// The list of imaginary user names.
    var nameList: [String] = []

    /// Initialize the session by generating the specified number of fake
    /// user names.
    ///
    /// - Parameters:
    ///   - numUsers: The number of pretend users to create for use when
    ///     testing.
    init(numUsers: Int = 250) {
        self.numUsers = numUsers
    
        for _ in 1...numUsers {
            nameList.append(self.fakeName())
        }
    }

    /// Return a fake user name by randomly concatenating one of a number
    /// of surnames and one of a number of given names at random. Then a
    /// partial UUID is appended to that to further ensure uniqueness.
    ///
    /// - Returns: A string of the form "surname-givenname-partialuuid".
    private func fakeName() -> String {
        let givenNames = [
            "Amanda", "Bailey", "Carlos", "Denise", "Eduard", "Fatima",
            "Imani", "Jakob", "Kwame", "Malik", "Ahmed", "Thomas", "Ravi",
            "Gerard", "Samuel", "Isaac", "Lakshmi", "Xavier", "Fumiko"
        ]
        let surnames = [
            "Jones", "Tanaka", "Richards", "Watanabe", "Yamaguchi",
            "Aguta", "Ndiaye", "Atkins", "Qasim", "Ayad", "Habib",
            "Estevez", "Garcia", "Hernandez", "Blackrock", "Patel"
        ]

        let name = surnames.randomElement()! + "-" + givenNames.randomElement()!
        return String.uniqueName(withPrefix: name, maxDigits: 11)
    }

    /// Determine whether or not the given list of users matches the fake
    /// set of users supposedly available through IAM.
    ///
    /// - Parameters:
    ///   - names: An array of user name strings.
    ///
    /// - Returns: `true` if the specified list of user names matches the
    /// pre-generated list.
    public func verifyList(names: [String]) -> Bool {
        return nameList == names
    }

    /// Mocked implementation of the `IAMClient.listUsers()` function.
    /// 
    /// - Parameters:
    ///   - input: The input parameters in a ``ListUsersInput`` record.
    ///
    /// - Returns: A `ListUsersOutputResponse` with the mock results.
    ///
    /// > Note: Most of the data in the returned record is the same for
    ///   every returned user, and is mostly not valid. The only data
    ///   we test for is the user name, since that's all the main program
    ///   cares about.
    public func listUsers(input: ListUsersInput) async throws
                -> ListUsersOutputResponse {
        var output = ListUsersOutputResponse(
            isTruncated: false,
            marker: nil,
            users: nil
        )

        // Determine where in the user list to start at given the value
        // of the input's `marker`.

        var start = 0
        if input.marker != nil {
            start = Int(input.marker!)!
        }

        var maxItems = 100

        // Adjust the requested number of items based on the caller's
        // specification and the actual available number of items.

        if input.maxItems != nil {
            maxItems = input.maxItems!
        }

        if start + maxItems > self.numUsers {
            maxItems = self.numUsers - start
        }

        // Make a list of just the names to return.

        let names = Array(nameList[start ..< Swift.min(
                                start + maxItems, self.numUsers)])

        // Now create the IAMClientTypes.User objects for each user.

        var users: [IAMClientTypes.User] = []

        for name in names {
            let user = IAMClientTypes.User(
                arn: "arn:this-is-not-a-real-arn",
                createDate: Date(),
                passwordLastUsed: nil,
                path: name,
                permissionsBoundary: nil,
                tags: [],
                userId: "0000000000",
                userName: name
            )
            users.append(user)
        }

        output.users = users

        // Update the `isTruncated` flag and the `marker` to let the caller
        // know whether or not there are more results available, and to record
        // where the next batch of results begins.

        start += maxItems
        if start < self.numUsers {
            output.marker = String(start)
            output.isTruncated = true
        } else {
            output.marker = nil
            output.isTruncated = false
        }

        return output
    }
}

/// Perform tests on the sample program without using AWS requests, by
/// using the mocked Amazon IAM functions through `MockUserSession`.
final class ListUsersTests: XCTestCase {
    /// Class-wide setup function for the test case, which is run *once*
    /// before any tests are run.
    override class func setUp() {
        super.setUp()
        SDKLoggingSystem.initialize(logLevel: .error)
    }

    /// Test the mocked ``listUsers()`` function itself.
    func testListUsers() async throws {
        let session = MockUserSession()

        var input = ListUsersInput(maxItems: 33)
        var output: ListUsersOutputResponse
        var userNames: [String] = []

        repeat {
            output = try await session.listUsers(input: input)
            
            guard let users = output.users else {
                break
            }

            for user in users {
                userNames.append(user.userName ?? "<unknown>")
            }

            input.marker = output.marker
        } while output.isTruncated == true

        XCTAssertTrue(session.verifyList(names: userNames),
                "Returned list of names doesn't match.")
    }

    /// Test the main program's ``getUserNames()`` function.
    func testGetUserNames() async throws {
        let session = MockUserSession()

        let command = try ExampleCommand.parse([])
        let names = try await command.getUserNames(session: session)

        XCTAssertTrue(session.verifyList(names: names),
                "Returned list of names doesn't match.")
    }
}
