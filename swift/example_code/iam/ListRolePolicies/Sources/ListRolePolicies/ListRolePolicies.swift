// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// Swift Example: ListRolePolicies
//
// An example showing how to use the Amazon Identity and Access Management (IAM)
// `IAMClient` function `listRolePolicies()`.

// snippet-start:[iam.swift.listrolepolicies.example]
// snippet-start:[iam.swift.listrolepolicies.main.imports]
import ArgumentParser
import Foundation
import ServiceHandler

// snippet-end:[iam.swift.listrolepolicies.main.imports]

/// The command line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.listrolepolicies.command]
struct ExampleCommand: ParsableCommand {
    @Argument(help: "Name of the IAM role to list the policies of.")
    var rolename: String

    static var configuration = CommandConfiguration(
        commandName: "listrolepolicies",
        abstract: "Lists all the IAM policies embedded in a specific role.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.listrolepolicies.command.runasync]
    func runAsync() async throws {
        do {
            let serviceHandler = try await ServiceHandler()

            let policies = try await serviceHandler.listRolePolicies(role: rolename)

            print("Found \(policies.count) policies in role \(rolename)")
            for policy in policies {
                print("  \(policy)")
            }
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.listrolepolicies.command.runasync]
}

// snippet-end:[iam.swift.listrolepolicies.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.listrolepolicies.main]
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

// snippet-end:[iam.swift.listrolepolicies.main]
// snippet-end:[iam.swift.listrolepolicies.example]
