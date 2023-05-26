/// An example that shows how to use the AWS SDK for Swift with Amazon
/// Simple Storage Service (S3) function `ListBuckets`.
///
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0

import Foundation
import ArgumentParser
import AWSS3
import ClientRuntime

struct ExampleCommand: ParsableCommand {
    @Option(help: "The AWS Region for which to list S3 buckets.")
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
        commandName: "listbuckets",
        abstract: """
        An example demonstrating the AWS SDK for Swift function
        S3Client.listBuckets().
        """,
        discussion: """
        Outputs a list of the user's Amazon S3 buckets.
        """
    )

    /// Called by ``main()`` to asynchronously run the AWS example.
    func runAsync() async throws {
        let session = try S3Session(region: awsRegion)
        let s3 = S3Manager(session: session)
        SDKLoggingSystem.initialize(logLevel: .error)

        let bucketList = try await s3.getAllBuckets()

        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .short
        dateFormatter.timeStyle = .long

        if bucketList.count != 0 {
            print("Found \(bucketList.count) buckets:")
            for bucket in bucketList {
                var dateString: String

                if bucket.creationDate != nil {
                    dateString = dateFormatter.string(from: bucket.creationDate!)
                } else {
                    dateString = "<unknown>"
                }
                print("  \(bucket.name ?? "<unknown>") (created \(dateString))")
            }
        } else {
            print("No buckets found.")
        }
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
