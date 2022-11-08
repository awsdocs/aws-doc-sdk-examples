//
// Swift Example: CreateUser
//
// An example showing how to use the Amazon Identity and Access Management (IAM)
// `IamClient` function `createUser()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.createuser.example]
// snippet-start:[iam.swift.createuser.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
// snippet-end:[iam.swift.createuser.main.imports]

/// The command line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.createuser.command]
struct ExampleCommand: ParsableCommand {
    @Argument(help: "Name of the new IAM user")
    var username: String

    static var configuration = CommandConfiguration(
        commandName: "createuser",
        abstract: "Creates a new IAM user on your AWS account.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.createuser.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            let userID = try await serviceHandler.createUser(name: username)
            print("Created new user \(username) with ID \(userID)")
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.createuser.command.runasync]
}
// snippet-end:[iam.swift.createuser.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.createuser.main]
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
// snippet-end:[iam.swift.createuser.main]
// snippet-end:[iam.swift.createuser.example]