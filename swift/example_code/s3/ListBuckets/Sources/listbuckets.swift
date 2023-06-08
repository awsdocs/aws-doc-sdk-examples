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
        let s3 = S3Manager(session: try S3Session(region: awsRegion))
        SDKLoggingSystem.initialize(logLevel: .error)

        let bucketList = try await s3.getAllBuckets()

        if bucketList.count != 0 {
            print("Found \(bucketList.count) buckets:")
            for bucket in bucketList {
                print("  \(bucketString(bucket))")
            }
        } else {
            print("No buckets found.")
        }
    }

    /// Convert a date into the string format we want to display.
    ///
    /// - Parameter date: A `Date` to convert into a string.
    /// - Returns: A string containing the date in the format `mm/dd/yy,
    ///   h:mm:ss pp UTC`, or `<unknown>` if the date is `nil`.
    func dateToString(_ date:Date?) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .short
        dateFormatter.timeStyle = .long

        if date != nil {
            return dateFormatter.string(from: date!)
        } else {
            return "<unknown>"
        }
    }

    func bucketString(_ bucket: S3ClientTypes.Bucket) -> String {
        let dateString = dateToString(bucket.creationDate)
        return"\(bucket.name ?? "<unknown>") (created \(dateString))"
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
