// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// A simple example that shows how to use the AWS SDK for Swift to
/// download files, both with and without presigning the requests.

// snippet-start:[swift.s3.presigned.imports]
import ArgumentParser
import AWSClientRuntime
import AWSS3
import Foundation
import Smithy
import SmithyHTTPAPI

// Include FoundationNetworking on non-Apple platforms.

#if canImport(FoundationNetworking)
import FoundationNetworking
#endif
// snippet-end:[swift.s3.presigned.imports]

// -MARK: - Async command line tool

struct ExampleCommand: ParsableCommand {
    // -MARK: Command arguments
    @Flag(help: "Presign the file download request")
    var presign = false
    @Option(help: "Amazon S3 file path of file to download")
    var source: String
    @Option(help: "Destination file path")
    var dest: String?
    @Option(help: "Name of the Amazon S3 bucket to download from")
    var bucket: String
    @Option(help: "Name of the Amazon S3 Region to use (default: us-east-1)")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "presigned-download",
        abstract: """
        This example shows how to download files from Amazon S3, optionally
        with presigning.
        """,
        discussion: """
        """
    )

    // snippet-start:[swift.s3.download-file]
    /// Download a file from the specified bucket and store it in the local
    /// filesystem.
    ///
    /// - Parameters:
    ///   - bucket: The Amazon S3 bucket name from which to retrieve the file.
    ///   - key: The name (or path) of the file to download from the `bucket`.
    ///   - destPath: The pathname on the local filesystem at which to store
    ///     the downloaded file.
    func downloadFile(bucket: String, key: String, destPath: String?) async throws {
        let fileURL: URL

        print("Downloading the file \(bucket)/\(key)")

        // If the destination path is `nil`, create a file URL that will save
        // the file with the same name in the user's Downloads directory.
        // Otherwise create the file URL directly from the specified destination
        // path.
        
        if destPath == nil {
            do {
                try fileURL = FileManager.default.url(
                    for: .downloadsDirectory,
                    in: .userDomainMask,
                    appropriateFor: URL(string: key),
                    create: true
                ).appendingPathComponent(key)
            } catch {
                throw TransferError.directoryError
            }
        } else {
            fileURL = URL(fileURLWithPath: destPath!)
        }
                
        let s3Client = try await S3Client()

        // Download the file using `GetObject` and the stream's `readData()`.
        
        let getInput = GetObjectInput(
            bucket: bucket,
            key: key
        )

        do {
            let getOutput = try await s3Client.getObject(input: getInput)

            guard let body = getOutput.body else {
                throw TransferError.downloadError("Error: No data returned for download")
            }

            guard let data = try await body.readData() else {
                throw TransferError.downloadError("Download error")
            }

            try data.write(to: fileURL)
        } catch {
            throw TransferError.downloadError("Error downloading the file: \(error)")
        }

        print("File downloaded to \(fileURL.path).")
    }
    // snippet-end:[swift.s3.download-file]

    // snippet-start:[swift.s3.presigned.download-file]
    /// Download a file from the specified bucket and store it in the local
    /// filesystem. Demonstrates using a custom configuration when presigning
    /// a request.
    ///
    /// - Parameters:
    ///   - bucket: The Amazon S3 bucket name from which to retrieve the file.
    ///   - key: The name (or path) of the file to download from the `bucket`.
    ///   - destPath: The pathname on the local filesystem at which to store
    ///     the downloaded file.
    func downloadFilePresigned(bucket: String, key: String, destPath: String?) async throws {
        let fileURL: URL

        print("Downloading (with presigning) the file \(bucket)/\(key)")
        
        // If the destination path is `nil`, create a file URL that will save
        // the file with the same name in the user's Downloads directory.
        // Otherwise create the file URL directly from the specified destination
        // path.
        
        if destPath == nil {
            do {
                try fileURL = FileManager.default.url(
                    for: .downloadsDirectory,
                    in: .userDomainMask,
                    appropriateFor: URL(string: key),
                    create: true
                ).appendingPathComponent(key)
            } catch {
                throw TransferError.directoryError
            }
        } else {
            fileURL = URL(fileURLWithPath: destPath!)
        }
                
        let s3Client = try await S3Client()

        // Create a presigned URLRequest with the `GetObject` action.
        
        // snippet-start:[swift.s3.presigned.getobject]
        let getInput = GetObjectInput(
            bucket: bucket,
            key: key
        )

        let presignedRequest: URLRequest
        do {
            presignedRequest = try await s3Client.presignedRequestForGetObject(
                input: getInput,
                expiration: TimeInterval(5 * 60)
            )
        } catch {
            throw TransferError.signingError
        }
        // snippet-end:[swift.s3.presigned.getobject]

        // Use the presigned request to fetch the file from Amazon S3 and
        // store it at the location given by the `destPath` parameter.
        
        let downloadTask = URLSession.shared.downloadTask(with: presignedRequest) {
                        localURL, downloadResponse, error in
            guard let localURL else {
                print("Error: no file downloaded")
                return
            }

            if error != nil {
                print("Error downloading file: \(error.debugDescription)")
                return
            }

            do {
                try FileManager.default.moveItem(at: localURL, to: fileURL)
            } catch {
                print("Error moving file to final location")
                return
            }
        }
        downloadTask.resume()

        // Wait for the file to finish downloading. Since there isn't a way to
        // cancel provided by this example, the .canceling state isn't
        // checked.
        
        while downloadTask.state != .completed {
            sleep(1)
        }

        // The download is complete, or has failed. If it's failed, display an
        // appropriate error message.
        if downloadTask.error != nil {
            throw TransferError.downloadError("Error downloading file: \(downloadTask.error.debugDescription)")
        }
        print("File downloaded to \(fileURL.path).")
    }
    // snippet-end:[swift.s3.presigned.download-file]

    // -MARK: - Asynchronous main code
    
    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        if presign {
            try await downloadFilePresigned(bucket: bucket, key: source,
                        destPath: dest)

        } else {
            try await downloadFile(bucket: bucket, key: source, destPath: dest)
        }
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
