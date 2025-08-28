// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// snippet-start:[swift.sfn.hello]
// An example that shows how to use the AWS SDK for Swift to perform a simple
// operation using Amazon Elastic Compute Cloud (EC2).
//

import ArgumentParser
import Foundation

// snippet-start:[swift.sfn.import]
import AWSSFN
// snippet-end:[swift.sfn.import]

struct ExampleCommand: ParsableCommand {
    @Option(help: "The AWS Region to run AWS API calls in.")
    var awsRegion = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "hello-sfn",
        abstract: """
        Demonstrates a simple operation using AWS Step Functions.
        """,
        discussion: """
        An example showing how to make a call to AWS Step Functions using the
        AWS SDK for Swift.
        """
    )

    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        let sfnConfig = try await SFNClient.SFNClientConfiguration(region: awsRegion)
        let sfnClient = SFNClient(config: sfnConfig)

        // snippet-start:[swift.sfn.hello.ListStateMachines]
        do {
            let output = try await sfnClient.listStateMachines(
                input: ListStateMachinesInput(
                    maxResults: 10
                )
            )

            guard let stateMachines = output.stateMachines else {
                print("*** No state machines found.")
                return
            }

            print("Found \(stateMachines.count) state machines (capped to 10)...")
            for machine in stateMachines {
                print("    \(machine.name ?? "<unnamed>"): \(machine.stateMachineArn ?? "<unknown>")")
            }
        } catch {
            print("*** Error fetching state machine list: \(error.localizedDescription)")
        }
        // snippet-end:[swift.sfn.hello.ListStateMachines]
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
// snippet-end:[swift.sfn.hello]
