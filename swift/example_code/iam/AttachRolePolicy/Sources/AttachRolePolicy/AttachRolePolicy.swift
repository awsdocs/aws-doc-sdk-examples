//
// Swift Example: ListUsers
//
// An example showing how to use the Amazon S3 `IamClient` function
// `ListUsers()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.attachrolepolicy.example]
// snippet-start:[iam.swift.attachrolepolicy.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
// snippet-end:[iam.swift.attachrolepolicy.main.imports]

/// The command-line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.attachrolepolicy.command]
struct ExampleCommand: ParsableCommand {
    @Argument(help: "Name of the IAM role to attach the policy to.")
    var rolename: String

    @Argument(help: "ARN of the managed policy to attach to the role.")
    var policyArn: String

    static var configuration = CommandConfiguration(
        commandName: "attachrolepolicy",
        abstract: "Attaches a managed IAM policy to the specified role.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.attachrolepolicy.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            _ = try await serviceHandler.attachRolePolicy(role: rolename, policyArn: policyArn)
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.attachrolepolicy.command.runasync]
}
// snippet-end:[iam.swift.attachrolepolicy.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.attachrolepolicy.main]
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
// snippet-end:[iam.swift.attachrolepolicy.main]
// snippet-end:[iam.swift.attachrolepolicy.example]