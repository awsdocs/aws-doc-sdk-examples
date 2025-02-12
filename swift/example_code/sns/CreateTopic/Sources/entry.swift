// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// An example demonstrating how to set up and use an Amazon Simple Notification
// Service (SNS) client to create a new topic.

import ArgumentParser
import AWSClientRuntime
import AWSSNS
import Foundation

struct ExampleCommand: ParsableCommand {
    @Argument(help: "Name to give the new Amazon SNS topic")
    var name: String
    @Option(help: "Name of the Amazon Region to use (default: us-east-1)")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "createtopic",
        abstract: """
        This example shows how to create an Amazon SNS topic.
        """,
        discussion: """
        """
    )
    
    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        // snippet-start:[swift.sns.CreateTopic]
        let config = try await SNSClient.SNSClientConfiguration(region: region)
        let snsClient = SNSClient(config: config)

        let output = try await snsClient.createTopic(
            input: CreateTopicInput(name: name)
        )

        guard let arn = output.topicArn else {
            print("No topic ARN returned by Amazon SNS.")
            return
        }
        // snippet-end:[swift.sns.CreateTopic]

        print("New topic created with ARN: \(arn)")
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
