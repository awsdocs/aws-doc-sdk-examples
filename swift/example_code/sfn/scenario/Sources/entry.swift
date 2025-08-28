// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// snippet-start:[swift.sfn.scenario]
// An example that shows how to use the AWS SDK for Swift to perform a simple
// operation using Amazon Elastic Compute Cloud (EC2).
//

import ArgumentParser
import Foundation

struct ExampleCommand: ParsableCommand {
    @Option(help: "The AWS Region to run AWS API calls in.")
    var awsRegion = "us-east-1"

    @Option(help: "The user's name.")
    var username = "Johanna Doe"

    @Option(help: "The name of the activity to find or create.")
    var activityName = "scenario-example-activity"

    @Option(help: "The name of the state machine to find or create.")
    var stateMachineName = "scenario-example-state-machine"

    @Option(help: "Path of the State Machine definition file.")
    var definitionPath: String

    static var configuration = CommandConfiguration(
        commandName: "sfn-scenario",
        abstract: """
        Demonstrates a variety of AWS Step Function features.
        """,
        discussion: """
        """
    )

    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        let example = try await Example(region: awsRegion, username: username,
                                        activityName: activityName,
                                        stateMachineName: stateMachineName,
                                        definitionPath: definitionPath)

        await example.run()
    }
}

/// The program's asynchronous entry point.
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
// snippet-end:[swift.sfn.scenario]
