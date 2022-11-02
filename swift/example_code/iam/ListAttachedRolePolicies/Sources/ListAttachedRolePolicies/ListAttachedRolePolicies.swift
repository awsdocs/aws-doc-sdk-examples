//
// Swift Example: ListAttachedRolePolicies
//
// An example showing how to use the Amazon Identity and Access Management (IAM)
// `IamClient` function `listAttachedRolePolicies()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.listattachedrolepolicies.example]
// snippet-start:[iam.swift.listattachedrolepolicies.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
// snippet-end:[iam.swift.listattachedrolepolicies.main.imports]

/// The command line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.listattachedrolepolicies.command]
struct ExampleCommand: ParsableCommand {
    @Argument(help: "Name of the IAM role to attach the policy to.")
    var rolename: String

    static var configuration = CommandConfiguration(
        commandName: "listattachedrolepolicies",
        abstract: "Lists the managed IAM policies attached to the specified role.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.listattachedrolepolicies.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            let attachedPolicies = try await serviceHandler.listAttachedRolePolicies(role: rolename)

            print("Found \(attachedPolicies.count) policies attached to role \(rolename)")
            for policy in attachedPolicies {
                print("  \(policy.policyName ?? "<unnamed>")")
            }
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.listattachedrolepolicies.command.runasync]
}
// snippet-end:[iam.swift.listattachedrolepolicies.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.listattachedrolepolicies.main]
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
// snippet-end:[iam.swift.listattachedrolepolicies.main]
// snippet-end:[iam.swift.listattachedrolepolicies.example]