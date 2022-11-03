//
// Swift Example: CreateRole
//
// An example showing how to use the Amazon Identity and Access Management (IAM)
// `IamClient` function `createRole()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.createrole.example]
// snippet-start:[iam.swift.createrole.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
// snippet-end:[iam.swift.createrole.main.imports]

/// The command line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.createrole.command]
struct ExampleCommand: ParsableCommand {
    @Argument(help: "Name of the new IAM role")
    var rolename: String

    static var configuration = CommandConfiguration(
        commandName: "createrole",
        abstract: "Creates a new IAM role on your AWS account.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.createrole.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            // Get information about the user running this example. This user
            // will be granted the new role.
            let user = try await serviceHandler.getUser(name: nil)

            guard let userName = user.userName,
                  let userID = user.userId,
                  let userARN = user.arn else {
                    return 
                  }
            print("Creating new role for user \(userName) (ID \(userID); ARN: \(userARN)")

            // The policy document is a JSON string describing the role. For
            // details, see:
            // https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies.html.
            let policyDocument = """
            {
                "Version": "2012-10-17",
                "Statement": [{
                    "Effect": "Allow",
                    "Principal": {"AWS": "\(userARN)"},
                    "Action": "sts:AssumeRole"
                }]
            }
            """

            // Create the role and output its ID.
            let roleID = try await serviceHandler.createRole(name: rolename, policyDocument: policyDocument)
            print("Created new role \(rolename) with ID \(roleID)")
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.createrole.command.runasync]
}
// snippet-end:[iam.swift.createrole.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.createrole.main]
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
// snippet-end:[iam.swift.createrole.main]
// snippet-end:[iam.swift.createrole.example]