// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// Swift Example: ListGroups
//
// An example showing how to use the Amazon Identity and Access Management (IAM)
// `IamClient` function `listGroups()`.

// snippet-start:[iam.swift.listgroups.example]
// snippet-start:[iam.swift.listgroups.main.imports]
import ArgumentParser
import Foundation
import ServiceHandler

// snippet-end:[iam.swift.listgroups.main.imports]

/// The command line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.listgroups.command]
struct ExampleCommand: ParsableCommand {
    static var configuration = CommandConfiguration(
        commandName: "listgroups",
        abstract: "Lists all the IAM groups on your AWS account.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.listgroups.command.runasync]
    func runAsync() async throws {
        do {
            let serviceHandler = try await ServiceHandler()

            let groups = try await serviceHandler.listGroups()
            print("Found \(groups.count) groups")
            for group in groups {
                print("  \(group)")
            }
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.listgroups.command.runasync]
}

// snippet-end:[iam.swift.listgroups.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.listgroups.main]
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

// snippet-end:[iam.swift.listgroups.main]
// snippet-end:[iam.swift.listgroups.example]
