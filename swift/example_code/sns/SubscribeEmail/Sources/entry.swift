// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// An example demonstrating how to subscribe an email address to an Amazon Simple
// Notification Service (SNS) topic.

import ArgumentParser
import AWSClientRuntime
import AWSSNS
import Foundation

struct ExampleCommand: ParsableCommand {
    @Argument(help: "The ARN of the Amazon SNS topic to subscribe to")
    var arn: String
    @Argument(help: "The email address to subscribe to the topic")
    var email: String
    @Option(help: "Name of the Amazon Region to use (default: us-east-1)")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "subscribe-email",
        abstract: """
        Subscribes an email address to an Amazon SNS topic.
        """,
        discussion: """
        """
    )
    
    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        // snippet-start:[swift.sns.SubscribeEmail]
        let config = try await SNSClient.SNSClientConfiguration(region: region)
        let snsClient = SNSClient(config: config)

        let output = try await snsClient.subscribe(
            input: SubscribeInput(
                endpoint: email,
                protocol: "email",
                returnSubscriptionArn: true,
                topicArn: arn
            )
        )

        guard let subscriptionArn = output.subscriptionArn else {
            print("No subscription ARN received from Amazon SNS.")
            return
        }
        
        print("Subscription \(subscriptionArn) created.")
        // snippet-end:[swift.sns.SubscribeEmail]
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
