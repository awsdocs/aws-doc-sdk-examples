//
// Swift Example: ListUsers
//
// An example showing how to use the Amazon S3 `IamClient` function
// `ListUsers()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.listroles.example]
// snippet-start:[iam.swift.listroles.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
// snippet-end:[iam.swift.listroles.main.imports]

/// The command-line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.listroles.command]
struct ExampleCommand: ParsableCommand {
    static var configuration = CommandConfiguration(
        commandName: "listroles",
        abstract: "Lists all the IAM roles on your AWS account.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.listroles.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            let roles = try await serviceHandler.listRoles()
            
            print("Found \(roles.count) roles")
            for roleName in roles {
                print("  \(roleName)")
            }
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.listroles.command.runasync]
}
// snippet-end:[iam.swift.listroles.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.listroles.main]
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
// snippet-end:[iam.swift.listroles.main]
// snippet-end:[iam.swift.listroles.example]