//
// Swift Example: ListUsers
//
// An example showing how to use the Amazon S3 `IamClient` function
// `ListUsers()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.listsamlproviders.example]
// snippet-start:[iam.swift.listsamlproviders.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
import SwiftyTextTable
// snippet-end:[iam.swift.listsamlproviders.main.imports]

/// The command-line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.listsamlproviders.command]
struct ExampleCommand: ParsableCommand {
    static var configuration = CommandConfiguration(
        commandName: "listsamlproviders",
        abstract: "Gets information about the current IAM account's SAML providers.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.listsamlproviders.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            let arnCol = TextTableColumn(header: "ARN")
            let createdCol = TextTableColumn(header: "Date Created")
            let expiresCol = TextTableColumn(header: "Expiration Date")
            var table = TextTable(columns: [arnCol, createdCol, expiresCol])

            let providerList = try await serviceHandler.listSAMLProviders()

            table.header = "Found \(providerList.count) SAML providers"
            for provider in providerList {
                let arn = provider.arn ?? "<unknown>"
                let createDate = provider.createDate ?? Date(timeIntervalSince1970: 0)
                let validUntil = provider.validUntil ?? Date(timeIntervalSince1970: 0)
                table.addRow(values: [arn, createDate, validUntil])
            }
            
            print(table.render())
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.listsamlproviders.command.runasync]
}
// snippet-end:[iam.swift.listsamlproviders.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.listsamlproviders.main]
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
// snippet-end:[iam.swift.listsamlproviders.main]
// snippet-end:[iam.swift.listsamlproviders.example]