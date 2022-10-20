//
// Swift Example: CreateServiceLinkedRole
//
// An example showing how to use the Amazon S3 `IamClient` function
// `CreateServiceLinkedRole()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.createservicelinkedrole.example]
// snippet-start:[iam.swift.createservicelinkedrole.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
// snippet-end:[iam.swift.createservicelinkedrole.main.imports]

/// The command-line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.createservicelinkedrole.command]
struct ExampleCommand: ParsableCommand {
    @Argument(help: "The name of the service to create a linked role for.")
    var servicename: String

    @Option(help: "A custom string to append to the service-provided prefix to be used as the new role's name.")
    var suffix: String?

    @Option(help: "A human-readable description of the new role.")
    var description: String?

    static var configuration = CommandConfiguration(
        commandName: "createservicelinkedrole",
        abstract: "Creates a new IAM role linked to a specific service.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.createservicelinkedrole.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            // Create the role and output information about it.
            let role = try await serviceHandler.createServiceLinkedRole(
                    service: servicename, suffix: suffix, description: description)
            
            let roleID = role.roleId ?? "<unknown>"
            let roleARN = role.arn ?? "<unknown>"
            let roleDesc = role.description ?? "<none>"
            let roleName = role.roleName ?? "<unknown>"

            let roleCreateDate = role.createDate ?? Date(timeIntervalSince1970: 0)

            print("  Role name: \(roleName)")
            print("    Role ID: \(roleID)")
            print("   Role ARN: \(roleARN)")

            let dateFormatter = DateFormatter()
            dateFormatter.dateStyle = .medium
            dateFormatter.timeStyle = .medium

            print("    Created: \(dateFormatter.string(from: roleCreateDate))")
            print("Description: \(roleDesc)")
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.createservicelinkedrole.command.runasync]
}
// snippet-end:[iam.swift.createservicelinkedrole.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.createservicelinkedrole.main]
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
// snippet-end:[iam.swift.createservicelinkedrole.main]
// snippet-end:[iam.swift.createservicelinkedrole.example]