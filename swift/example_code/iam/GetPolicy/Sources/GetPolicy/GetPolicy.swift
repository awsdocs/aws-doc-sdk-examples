//
// Swift Example: ListUsers
//
// An example showing how to use the Amazon S3 `IamClient` function
// `ListUsers()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.getpolicy.example]
// snippet-start:[iam.swift.getpolicy.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
// snippet-end:[iam.swift.getpolicy.main.imports]

/// The command-line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.getpolicy.command]
struct ExampleCommand: ParsableCommand {
    @Argument(help: "ARN of the IAM policy to output")
    var arn: String

    static var configuration = CommandConfiguration(
        commandName: "getpolicy",
        abstract: "Gets information about the specified IAM policy.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.getpolicy.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            let policy = try await serviceHandler.getPolicy(arn: arn)

            guard   let policyName = policy.policyName,
                    let policyID = policy.policyId,
                    let policyARN = policy.arn else {
                        print("Error: No policy found with ARN \(arn)")
                        return
                    }
            
            print("Policy:  \(policyName) (\(policyID))")
            print("         \(policyARN)")
            
            let policyCreateDate = policy.createDate ?? Date(timeIntervalSince1970: 0)
            let dateFormatter = DateFormatter()
            dateFormatter.dateStyle = .medium
            dateFormatter.timeStyle = .medium

            print("Created: \(dateFormatter.string(from: policyCreateDate))")

            let policyDesc = policy.description ?? ""
            if policyDesc != "" {
                print("        \(policyDesc)")
            }

        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.getpolicy.command.runasync]
}
// snippet-end:[iam.swift.getpolicy.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.getpolicy.main]
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
// snippet-end:[iam.swift.getpolicy.main]
// snippet-end:[iam.swift.getpolicy.example]