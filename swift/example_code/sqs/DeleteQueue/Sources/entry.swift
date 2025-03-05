// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// An example demonstrating how to delete an Amazon SQS queue.

// snippet-start:[swift.sqs.basics]
import ArgumentParser
import AWSClientRuntime
import AWSSQS
import Foundation

struct ExampleCommand: ParsableCommand {
    @Argument(help: "The URL of the Amazon SQS queue to delete")
    var queueUrl: String
    @Option(help: "Name of the Amazon Region to use (default: us-east-1)")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "deletequeue",
        abstract: """
        This example shows how to delete an Amazon SQS queue.
        """,
        discussion: """
        """
    )
    
    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        // snippet-start:[swift.sqs.DeleteQueue]
        let config = try await SQSClient.SQSClientConfiguration(region: region)
        let sqsClient = SQSClient(config: config)

        do {
            _ = try await sqsClient.deleteQueue(
                input: DeleteQueueInput(
                    queueUrl: queueUrl
                )
            )
        } catch _ as AWSSQS.QueueDoesNotExist {
            print("Error: The specified queue doesn't exist.")
            return
        }
        // snippet-end:[swift.sqs.DeleteQueue]
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
// snippet-end:[swift.sqs.basics]
