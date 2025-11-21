// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import ArgumentParser
import Foundation

struct ExampleCommand: ParsableCommand {
    @Option(help: "AWS Region name")
    var region = "us-east-1"
    @Argument(help: "Name of the Amazon S3 bucket to download")
    var bucketName: String
    @Argument(help: "Prefix to download (allows downloading a subset of the bucket)")
    var s3Prefix: String?

    static var configuration = CommandConfiguration(
        commandName: "download-streaming",
        abstract: """
        Downloads a bucket from Amazon S3 using the S3 Transfer Manager, with progress updates.
        """,
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    func runAsync() async throws {
        let example = Example(region: region, bucket: bucketName, s3Prefix: s3Prefix)

        try await example.run()
    }
}

/// The program's asynchronous entry point.
@main
struct Main {
    /// The function that serves as the main asynchronous entry point for the
    /// example. It parses the command line using the Swift Argument Parser,
    /// then calls the `runAsync()` function to run the example itself.
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
