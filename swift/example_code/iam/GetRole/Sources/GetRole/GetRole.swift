//
// Swift Example: ListUsers
//
// An example showing how to use the Amazon S3 `IamClient` function
// `ListUsers()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.getrole.example]
// snippet-start:[iam.swift.getrole.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
// snippet-end:[iam.swift.getrole.main.imports]

/// The command-line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.getrole.command]
struct ExampleCommand: ParsableCommand {
    @Argument(help: "Name of the new IAM role")
    var rolename: String

    static var configuration = CommandConfiguration(
        commandName: "getrole",
        abstract: "Gets information about an IAM role on your AWS account.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.getrole.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            let role = try await serviceHandler.getRole(name: rolename)
            
            guard   let roleName = role.roleName,
                    let roleID = role.roleId else {
                        print("Error: Unknown role \(rolename).")
                        return
            }
            let arn = role.arn ?? "<unknown>"
            print("Role:         \(roleName) (\(roleID))")
            print("              \(arn)")

            // Creation date and maximum session length.

            let createDate = role.createDate ?? Date(timeIntervalSince1970: 0)
            let maxSession = role.maxSessionDuration ?? 0
            let dateFormatter = DateFormatter()
            dateFormatter.dateStyle = .medium
            dateFormatter.timeStyle = .medium

            print("Created:      \(dateFormatter.string(from: createDate))")
            print("Max. session: \(maxSession/(60*60)) hours")

            // Role description, if available.

            let desc = role.description ?? ""
            if (desc != "") {
                print("      \(desc)")
            }

            let assumeRolePolicyDoc = role.assumeRolePolicyDocument ?? ""
            let policy = assumeRolePolicyDoc.removingPercentEncoding ?? ""

            if (policy != "") {
                print("Assume role policy document")
                print("---------------------------")
                print(policy)
                print("---------------------------")
            }
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.getrole.command.runasync]
}
// snippet-end:[iam.swift.getrole.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.getrole.main]
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
// snippet-end:[iam.swift.getrole.main]
// snippet-end:[iam.swift.getrole.example]