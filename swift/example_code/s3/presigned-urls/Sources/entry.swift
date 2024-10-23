// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// A simple example that shows how to use the AWS SDK for Swift to
/// authenticate using optional static credentials and an AWS IAM role ARN.

// snippet-start:[swift.s3.presigned.imports]
import ArgumentParser
import AWSClientRuntime
import AWSS3
import Foundation
import Smithy
// snippet-end:[swift.s3.presigned.imports]

enum TransferError: Error {
    /// An error occurred while downloading a file from Amazon S3.
    case downloadError
    /// An error occurred while uploading a file to Amazon S3.
    case uploadError
    /// An error occurred while reading the file's contents.
    case readError
    /// An error occurred while writing the file's contents.
    case writeError

    var errorDescription: String? {
        switch self {
        case .downloadError:
            return "An error occurred attempting to download the file"
        case .uploadError:
            return "An error occurred attempting to upload the file"
        case .readError:
            return "An error occurred while reading the file data"
        case .writeError:
            return "An error occurred while writing the file data"
        }
    }
}

enum TransferDirection: String, EnumerableFlag {
    case up
    case down
}

struct ExampleCommand: ParsableCommand {
    @Flag(help: "Transfer direction of the file transfer (up or down)")
    var direction: TransferDirection
    @Option(help: "Source path or Amazon S3 file path")
    var source: String
    @Option(help: "Destination path or Amazon S3 file path")
    var dest: String
    @Option(help: "Name of the Amazon S3 bucket containing the file")
    var bucket: String

    static var configuration = CommandConfiguration(
        commandName: "presigned",
        abstract: """
        Transfer a file up or down between the local device and an Amazon S3
        URI.
        """,
        discussion: """
        """
    )

    func uploadFile(sourcePath: String, bucket: String, key: String)
                async throws {
        let fileUrl = URL(fileURLWithPath: sourcePath)
        do {
            let fileData = try Data(contentsOf: fileUrl)
            let dataStream = ByteStream.data(fileData)

            let s3Client = try await S3Client()
            let putInput = PutObjectInput(
                body: dataStream,
                bucket: bucket,
                key: key
            )

            let putOutput = try await s3Client.putObject(input: putInput)
        } catch {
            print("ERROR: file upload failure", dump(error))
            throw TransferError.uploadError
        }
    }

    func downloadFile(bucket: String, key: String, destPath: String)
                async throws {
        let fileUrl = URL(fileURLWithPath: destPath)
        
        do {
            let getInput = GetObjectInput(
                bucket: bucket,
                key: key
            )

            let presignedURL = S3Client.presignedRequestForGetObject(
                input: getInput,
                expiration: TimeInterval(3600)
            )

            let s3Client = try await S3Client()
            let getOutput = try await s3Client.getObject(input: getInput)

            guard let body = getOutput.body else {
                throw TransferError.downloadError
            }

            guard let data = try await body.readData() else {
                throw TransferError.readError
            }

            // Write the file to disk.

            try data.write(to: fileUrl)
        } catch {
            print("ERROR: failed to download the file", dump(error))
            throw error
        }
    }

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    func runAsync() async throws {
        switch direction {
        case .up:
            try await uploadFile(sourcePath: source, bucket: bucket, 
                    key: dest)
        case .down:
            try await downloadFile(bucket: bucket, key: source,
                    destPath: dest)
        }
    }
}

func httpFetch(srcURL: URL, destURL: URL) async throws -> Data {
    let task = URLSession.shared.downloadTask(with: srcURL) {
            urlOrNil, responseOrNil, errorOrNil in
        
        guard let fileURL = urlOrNil else {
            return
        }

        do {
            try FileManager.default.moveItem(at: srcURL, to: destURL)
        } catch {
            print("ERROR: Error retrieving file", error)
        }
    }
    task.resume()
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
