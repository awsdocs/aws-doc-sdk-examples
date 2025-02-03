// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// An example demonstrating how to configure checksums when uploading to
/// Amazon S3.

// snippet-start:[swift.s3.checksums-upload.imports]
import ArgumentParser
import AWSClientRuntime
import AWSS3
import Foundation
import Smithy
// snippet-end:[swift.s3.checksums-upload.imports]

// -MARK: - Async command line tool

struct ExampleCommand: ParsableCommand {
    // -MARK: Command arguments
    @Option(help: "Path of local file to upload to Amazon S3")
    var source: String
    @Option(help: "Name of the Amazon S3 bucket to upload to")
    var bucket: String
    @Option(help: "Destination file path within the bucket")
    var dest: String?
    @Option(help: "Name of the Amazon S3 Region to use (default: us-east-1)")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "checksums",
        abstract: """
        This example shows how to configure checksums when uploading to Amazon S3.
        """,
        discussion: """
        """
    )

    // -MARK: - File upload
    
    func uploadFile(sourcePath: String, bucket: String, key: String?) async throws {
        let fileURL = URL(fileURLWithPath: sourcePath)
        let fileName: String

        // If no key was provided, use the last component of the filename.
        
        if key == nil {
            fileName = fileURL.lastPathComponent
        } else {
            fileName = key!
        }
        
        // Create an Amazon S3 client in the desired Region.

        let config = try await S3Client.S3ClientConfiguration(region: region)
        let s3Client = S3Client(config: config)
    
        print("Uploading file from \(fileURL.path) to \(bucket)/\(fileName).")

        let fileData = try Data(contentsOf: fileURL)
        let dataStream = ByteStream.data(fileData)

        // Use PutObject to send the file to Amazon S3. The checksum is
        // specified by setting the `checksumAlgorithm` property. In this
        // example, SHA256 is used.
        
        do {
            // snippet-start:[swift.s3.checksums.upload-file]
            _ = try await s3Client.putObject(
                input: PutObjectInput(
                    body: dataStream,
                    bucket: bucket,
                    checksumAlgorithm: .sha256,                                                                                                                                                                                                                                                                                                                                                                             
                    key: fileName
                )
            )
            // snippet-end:[swift.s3.checksums.upload-file]
        } catch {
            throw TransferError.uploadError("Error uploading file: \(error.localizedDescription)")
        }
        print("Uploaded \(sourcePath) to \(bucket)/\(fileName).")
    }

    // -MARK: - Asynchronous main code
    
    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        try await uploadFile(sourcePath: source, bucket: bucket, key: dest)
    }
}

// -MARK: - Entry point

/// The program's asynchronous entry point.
@main
struct Main {
    static func main() async {
        let args = Array(CommandLine.arguments.dropFirst())

        do {
            let command = try ExampleCommand.parse(args)
            try await command.runAsync()
        } catch let error as TransferError {
            print("ERROR: \(error.errorDescription ?? "Unknown error")")
        } catch {
            ExampleCommand.exit(withError: error)
        }
    }    
}
