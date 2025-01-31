// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// A simple example that shows how to use the AWS SDK for Swift to upload
/// small files, both with and without presigning the requests.
///
/// Note that this example does not support multi-part uploads, so it can only
/// upload small files.

// snippet-start:[swift.s3.presigned-upload.imports]
import ArgumentParser
import AsyncHTTPClient
import AWSClientRuntime
import AWSS3
import Foundation
import Smithy
// snippet-end:[swift.s3.presigned-upload.imports]

// -MARK: - Async command line tool

struct ExampleCommand: ParsableCommand {
    // -MARK: Command arguments
    @Flag(help: "Presign the file upload request")
    var presign = false
    @Option(help: "Path of local file to upload to Amazon S3")
    var source: String
    @Option(help: "Name of the Amazon S3 bucket to upload to")
    var bucket: String
    @Option(help: "Destination file path within the bucket")
    var dest: String?
    @Option(help: "Name of the Amazon S3 Region to use (default: us-east-1)")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "presigned-upload",
        abstract: """
        This example shows how to upload small files to Amazon S3, optionally
        with presigning.
        """,
        discussion: """
        """
    )

    // -MARK: - File upload
    
    // snippet-start:[swift.s3.presigned.upload-file]
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

        // Use PutObject to send the file to Amazon S3.
        
        do {
            let putInput = PutObjectInput(
                body: dataStream,
                bucket: bucket,                                                                                                                                                                                                                                                                                                                                                                                                                                                     
                key: fileName
            )
            _ = try await s3Client.putObject(input: putInput)
        } catch {
            throw TransferError.uploadError("Error uploading file: \(error.localizedDescription)")
        }
        print("Uploaded \(sourcePath) to \(bucket)/\(fileName).")
    }
    // snippet-end:[swift.s3.presigned.upload-file]

    // -MARK: - Presigned upload

    // snippet-start:[swift.s3.presigned.presign-upload-file]
    /// Upload the specified file to the Amazon S3 bucket with the given name.
    /// The request is presigned.
    ///
    /// - Parameters:
    ///   - sourcePath: The pathname of the source file on a local disk.
    ///   - bucket: The Amazon S3 bucket to store the file into.
    ///   - key: The key (name) to give the uploaded file. The filename of the
    ///     source is used by default.
    ///
    /// - Throws: `TransferError.uploadError`
    func uploadFilePresigned(sourcePath: String, bucket: String, key: String?) async throws {
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
        
        // Look to see if the file already exists in the target bucket.
        
        do {
            let headInput = HeadObjectInput(bucket: bucket, key: fileName)
            let headResult = try await s3Client.headObject(input: headInput)
            
            // If HeadObject is successful, the file definitely exists.
            
            print("File exists with ETag \(headResult.eTag ?? "<no ETag>"). Skipping upload.")
            return
        } catch let error as AWSServiceError {
            switch(error.errorCode) {
            case "NotFound":
                break
            default:
                throw TransferError.readError
            }
        }

        print("Uploading file from \(fileURL.path) to \(bucket)/\(fileName).")

        // Presign the `PutObject` request with a 10-minute expiration. The
        // presigned request has a custom configuration that asks for up to
        // six attempts to put the file.
        //
        // The presigned `URLRequest` can then be sent using the URL Loading System
        // (https://developer.apple.com/documentation/foundation/url_loading_system/uploading_data_to_a_website).

        // snippet-start:[swift.s3.presigned.presign-PutObject-advanced]
        let fileData = try Data(contentsOf: fileURL)
        let dataStream = ByteStream.data(fileData)
        let presignedURL: URL

        // Create a presigned URL representing the `PutObject` request that
        // will upload the file to Amazon S3. If no URL is generated, a
        // `TransferError.signingError` is thrown.

        let putConfig = try await S3Client.S3ClientConfiguration(
            maxAttempts: 6,
            region: region
        )
        
        do {
            let url = try await PutObjectInput(
                body: dataStream,
                bucket: bucket,
                key: fileName
            ).presignURL(
                config: putConfig,
                expiration: TimeInterval(10 * 60)
            )

            guard let url = url else {
                throw TransferError.signingError
            }
            presignedURL = url
        } catch {
            throw TransferError.signingError
        }

        // Send the HTTP request and upload the file to Amazon S3.

        var request = HTTPClientRequest(url: presignedURL.absoluteString)
        request.method = .PUT
        request.body = .bytes(fileData)

        _ = try await HTTPClient.shared.execute(request, timeout: .seconds(5*60))
        // snippet-end:[swift.s3.presigned.presign-PutObject-advanced]

        print("Uploaded (presigned) \(sourcePath) to \(bucket)/\(fileName).")
    }
    // snippet-end:[swift.s3.presigned.presign-upload-file]

    // -MARK: - Asynchronous main code
    
    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        if presign {
            try await uploadFilePresigned(sourcePath: source, bucket: bucket,
                        key: dest)
        } else {
            try await uploadFile(sourcePath: source, bucket: bucket,
                        key: dest)
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
