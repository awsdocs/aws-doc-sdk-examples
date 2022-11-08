//
// Swift Example: ListPolicies
//
// An example showing how to use the Amazon Identity and Access Management (IAM)
// `IamClient` function `listPolicies()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.listpolicies.example]
// snippet-start:[iam.swift.listpolicies.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
import SwiftyTextTable
// snippet-end:[iam.swift.listpolicies.main.imports]

/// The command line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.listpolicies.command]
struct ExampleCommand: ParsableCommand {
    static var configuration = CommandConfiguration(
        commandName: "listpolicies",
        abstract: "Lists all the IAM policies on your AWS account.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.listpolicies.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            let nameCol = TextTableColumn(header: "Policy Name")
            let idCol = TextTableColumn(header: "ID")
            let arnCol = TextTableColumn(header: "ARN")
            var table = TextTable(columns: [nameCol, idCol, arnCol])

            let policies = try await serviceHandler.listPolicies()
            
            table.header = "Found \(policies.count) policies"
            for policy in policies {
                table.addRow(values: [policy.name, policy.id, policy.arn])
            }

            print(table.render())
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.listpolicies.command.runasync]
}
// snippet-end:[iam.swift.listpolicies.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.listpolicies.main]
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
// snippet-end:[iam.swift.listpolicies.main]
// snippet-end:[iam.swift.listpolicies.example]