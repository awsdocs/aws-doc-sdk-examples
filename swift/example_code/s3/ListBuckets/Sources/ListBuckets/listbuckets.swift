// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/// An example that shows how to use the AWS SDK for Swift with the Amazon
/// Simple Storage Service (Amazon S3) function `ListBuckets`.
///
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0

// snippet-start:[s3.swift.listbuckets.command]
// snippet-start:[s3.swift.listbuckets.command.imports]
import ArgumentParser
import AWSClientRuntime
import AWSS3
import Foundation

// snippet-end:[s3.swift.listbuckets.command.imports]

// snippet-start:[s3.swift.listbuckets.command.parsable]
/// Use a `ParsableCommand` from the Swift Argument Parser package to set up
/// this program to accept an optional argument that specifies which AWS
/// Region to operate in. Also includes an optional argument that specifies
/// which log level to use.
struct ExampleCommand: ParsableCommand {
    @Option(help: "The AWS Region for which to list S3 buckets.")
    var region = "us-east-1"

    @Option(
        help: ArgumentHelp("The level of logging for the SDK for Swift to perform."),
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

    // snippet-start:[s3.swift.listbuckets.command.config]
    /// Configuration details for the command.
    static var configuration = CommandConfiguration(
        // The name of the command.
        commandName: "listbuckets",
        // A short description of the command.
        abstract: """
        An example demonstrating the AWS SDK for Swift function
        S3Client.listBuckets().
        """,
        // Additional details about the command.
        discussion: """
        Outputs a list of the user's Amazon S3 buckets.
        """
    )
    // snippet-end:[s3.swift.listbuckets.command.config]

    /// Called by ``main()`` to asynchronously run the AWS example.
    func runAsync() async throws {
        let s3 = try await S3Manager(session: S3Session(region: region))
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
}

// snippet-end:[s3.swift.listbuckets.command.parsable]

// snippet-start:[s3.swift.listbuckets.datetostring]
/// Convert a date into a string for display.
///
/// - Parameter date: A `Date` to convert into a string.
///
/// - Returns: A string containing the date in the format `mm/dd/yy, h:mm:ss
///   pp UTC`, or `<unknown>` if the date is `nil`.
func dateToString(_ date: Date?) -> String {
    let dateFormatter = DateFormatter()
    dateFormatter.dateStyle = .short
    dateFormatter.timeStyle = .long
    dateFormatter.timeZone = TimeZone(identifier: "GMT")

    if date != nil {
        return dateFormatter.string(from: date!)
    } else {
        return "<unknown>"
    }
}

// snippet-end:[s3.swift.listbuckets.datetostring]

// snippet-start:[s3.swift.listbuckets.bucketstring]
/// Given a single bucket, returns a string with the desired information,
/// formatted for display.
///
/// - Parameter bucket: The bucket for which to generate output.
///
/// - Returns: A string containing the formatted text describing the bucket.
func bucketString(_ bucket: S3ClientTypes.Bucket) -> String {
    let dateString = dateToString(bucket.creationDate)
    return "\(bucket.name ?? "<unknown>") (created \(dateString))"
}

// snippet-end:[s3.swift.listbuckets.bucketstring]

// snippet-start:[s3.swift.listbuckets.main]
/// The program's asynchronous entry point.
@main
struct Main {
    static func main() async {
        // Drop the command name. It's not needed anymore.
        let args = Array(CommandLine.arguments.dropFirst())

        // Parse the command's arguments and run the body of the program
        // asynchronously by calling ``runAsync()``.
        do {
            let command = try ExampleCommand.parse(args)
            try await command.runAsync()
        } catch {
            ExampleCommand.exit(withError: error)
        }
    }
}

// snippet-end:[s3.swift.listbuckets.main]
// snippet-end:[s3.swift.listbuckets.command]
