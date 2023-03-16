/// An example that shows how to use the AWS SDK for Swift with Amazon
/// DynamoDB function batchGetItem().
///
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ddb.swift.batchgetitem]
import Foundation
import ArgumentParser
import AWSDynamoDB
import ClientRuntime

struct ExampleCommand: ParsableCommand {
    @Argument(help: "The path of the sample movie data JSON file.")
    var jsonPath: String = "../../../../resources/sample_files/movies.json"

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
        commandName: "batchgetitem",
        abstract: """
        An example demonstrating the AWS SDK for Swift function
        DynamoDBClient.batchGetItem().
        """,
        discussion: """
        An example showing how to use Amazon DynamoDB to fetch multiple items
        from the database in one batch, by first creating a movie database, then
        loading the records for a specific list of movies.
        """
    )

    /// Called by ``main()`` to asynchronously run the AWS example.
    func runAsync() async throws {
        SDKLoggingSystem.initialize(logLevel: .error)
        print("Welcome to the AWS SDK for Swift batchGetItem() example for Amazon DynamoDB!")
        print("Please wait while the database is installed and searched...\n")

        let database = try await MovieDatabase(jsonPath: jsonPath)
        
        let movies = try await database.batchGet(keys: [
            (title: "Titanic", year: 1997),
            (title: "The Shawshank Redemption", year: 1994),
            (title: "Some Fake Movie", year: 1955)
        ])

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
// snippet-end:[ddb.swift.batchgetitem]
