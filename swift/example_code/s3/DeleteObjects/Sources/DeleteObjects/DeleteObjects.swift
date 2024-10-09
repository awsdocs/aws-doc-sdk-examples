// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// Swift Example: DeleteObjects
//
// An example showing how to use the Amazon S3 `S3Client` function
// `DeleteObjects()`.

// snippet-start:[s3.swift.deleteobjects.example]
// snippet-start:[s3.swift.deleteobjects.main.imports]
import ArgumentParser
import Foundation
import ServiceHandler

// snippet-end:[s3.swift.deleteobjects.main.imports]

/// The command-line arguments and options available for this
/// example command.
// snippet-start:[s3.swift.deleteobjects.command]
struct ExampleCommand: ParsableCommand {
    @Option(help: "AWS Region the bucket where the bucket is")
    var region: String?

    @Argument(help: "Name of the S3 bucket to delete objects in")
    var bucketName: String

    @Argument(help: "Names of the files to delete from the bucket")
    var fileNames: [String]

    static var configuration = CommandConfiguration(
        commandName: "deleteobjects",
        abstract: "Deletes the specified objects from an S3 bucket.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[s3.swift.deleteobjects.command.runasync]
    func runAsync() async throws {
        do {
            let serviceHandler = try await ServiceHandler(region: region)
            try await serviceHandler.deleteObjects(bucket: bucketName,
                                                   keys: fileNames)
        } catch {
            print("*** Error. Unable to complete deleting the objects.")
        }
    }
    // snippet-end:[s3.swift.deleteobjects.command.runasync]
}

// snippet-end:[s3.swift.deleteobjects.command]

//
// Main program entry point.
//
// snippet-start:[s3.swift.deleteobjects.main]
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

// snippet-end:[s3.swift.deleteobjects.main]
// snippet-end:[s3.swift.deleteobjects.example]
