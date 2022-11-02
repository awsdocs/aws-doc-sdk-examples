//
// Swift Example: ListUsers
//
// An example showing how to use the Amazon Identity and Access Management (IAM)
// `IamClient` function `listUsers()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.listusers.example]
// snippet-start:[iam.swift.listusers.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
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

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.listusers.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            var users: [MyUserRecord]
            users = try await serviceHandler.listUsers()
            
            print("Found \(users.count) users")
            for user in users {
                print("  \(user.name) (\(user.id))")
            }
        } catch {
            throw error
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