//
// Swift Example: ListUsers
//
// An example showing how to use the Amazon Identity and Access Management (IAM)
// `IAMClient` function `listUsers()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.listusers.example]
// snippet-start:[iam.swift.listusers.main.imports]
import Foundation
import ArgumentParser
import AWSIAM
// snippet-end:[iam.swift.listusers.main.imports]

/// The command line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.listusers.command]
struct ExampleCommand: ParsableCommand {
    static var configuration = CommandConfiguration(
        commandName: "listusers",
        abstract: "Lists all the users on your AWS account.",
        discussion: """
        """
    )

    // snippet-start:[iam.swift.listusers.command.getusernames]
    /// Return an array of strings listing the users available on the active
    /// Amazon IAM account.
    ///
    /// - Returns: An array of strings, each giving the name of one IAM user.
    func getUserNames(session: UserSession) async throws -> [String] {
        /// An array to store the found user names.
        var userNames: [String] = []

        /// A marker string used by IAM to indicate where the next batch of
        /// users begins. `nil` indicates the first user.
        var marker: String? = nil

        /// Whether or not the current batch of users is incomplete.
        var isTruncated: Bool = false

        // Call `listUsers()` repeatedly until the returned list
        // is not truncated.
        repeat {
            let input = ListUsersInput(marker: marker)
            let output = try await session.listUsers(input: input)

            guard let users = output.users else {
                continue
            }

            // Get the returned users' names and add them to the list of
            // users in `userNames`.
            for user in users {
                guard let name = user.userName else {
                    continue
                }

                userNames.append(name)

                // Set up for the next batch, if there is one.
                marker = output.marker
                isTruncated = output.isTruncated
            }
        } while isTruncated == true

        return userNames
    }
    // snippet-end:[iam.swift.listusers.command.getusernames]

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.listusers.command.runasync]
    func runAsync() async throws {
        let session = try IAMUserSession()
        let userNames = try await getUserNames(session: session)

        print("Found \(userNames.count) users")
        for user in userNames {
            print("  \(user)")
        }
    }
    // snippet-end:[iam.swift.listusers.command.runasync]
}
// snippet-end:[iam.swift.listusers.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.listusers.main]
@main
struct Main {
    static func main() async {
        let args = Array(CommandLine.arguments.dropFirst())

        do {
            let command = try ExampleCommand.parse(args)

            try await command.runAsync()
        } catch {
            ExampleCommand.exit(withError: error)
        }
    }    
}
// snippet-end:[iam.swift.listusers.main]
// snippet-end:[iam.swift.listusers.example]