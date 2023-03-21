/// An example that shows how to use the AWS SDK for Swift with Amazon
/// DynamoDB function batchGetItem().
///
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ddb.swift.listtables]
import Foundation
import ArgumentParser
import AWSDynamoDB
import ClientRuntime

struct ExampleCommand: ParsableCommand {
    @Option(help: "The AWS Region to run AWS API calls in.")
    var awsRegion = "us-east-2"

    @Option(
        help: ArgumentHelp("The level of logging for the Swift SDK to perform."),
        completion: .list([
            "critical",
            "debug",
            "error",
            "info",
            "notice",
            "trace",
            "warning"
        ])
    )
    var logLevel: String = "error"

    /// Configuration details for the command.
    static var configuration = CommandConfiguration(
        commandName: "listtables",
        abstract: """
        An example demonstrating the AWS SDK for Swift function
        DynamoDBClient.listTables().
        """,
        discussion: """
        An example that uses the DynamoDB function listTables() to output a
        list of tables available on an AWS account.
        """
    )

    /// Called by ``main()`` to asynchronously run the AWS example.
    func runAsync() async throws {
        SDKLoggingSystem.initialize(logLevel: .error)

        print("Found \(movies.count) matching movies...")
        for movie in movies {
            print("\(movie.title): rating \(movie.info.rating != nil ? String(movie.info.rating!) : "(unrated)")")
        }

        try await database.deleteTable()
    }
}

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
// snippet-end:[ddb.swift.listtables]
