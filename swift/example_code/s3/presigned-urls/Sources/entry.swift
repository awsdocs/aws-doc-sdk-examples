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
//import AsyncHTTPClient
//import NIOFoundationCompat
// snippet-end:[swift.s3.presigned.imports]

enum TransferError: Error {
    /// The destination directory for a download is missing or inaccessible.
    case directoryError
    /// An error occurred while downloading a file from Amazon S3.
    case downloadError
    /// An error occurred while uploading a file to Amazon S3.
    case uploadError
    /// An error occurred while reading the file's contents.
    case readError
    /// An error occurred while presigning the URL.
    case signingError
    /// An error occurred while writing the file's contents.
    case writeError

    var errorDescription: String? {
        switch self {
        case .directoryError:
            return "The destination directory could not be located or created"
        case .downloadError:
            return "An error occurred attempting to download the file"
        case .uploadError:
            return "An error occurred attempting to upload the file"
        case .readError:
            return "An error occurred while reading the file data"
        case .signingError:
            return "An error occurred while pre-signing the URL"
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
    var dest: String?
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
    
    /// Upload the specified file to the Amazon S3 bucket with the given name.
    /// - Parameters:
    ///   - sourcePath: The pathname of the source file on a local disk.
    ///   - bucket: The Amazon S3 bucket to store the file into.
    ///   - key: The key (name) to give the uploaded file. The filename of the
    ///     source is used by default.
    ///
    /// - Throws: `TransferError.uploadError`
    func uploadFile(sourcePath: String, bucket: String, key: String?) async throws {
        let fileURL = URL(fileURLWithPath: sourcePath)
        let fileName: String
        
        // If no key was provided, use the last component of the filename.
        
        if key == nil {
            fileName = fileURL.lastPathComponent
        } else {
            fileName = key!
        }
        
        // Create the presigned URL for the `PostObject` request.
        
        do {
            let fileData = try Data(contentsOf: fileURL)
            let dataStream = ByteStream.data(fileData)

            let s3Client = try await S3Client()
            let putInput = PutObjectInput(
                body: dataStream,
                bucket: bucket,
                key: fileName
            )

            print("Uploading file from \(fileURL.absoluteString) to \(bucket)/\(fileName).")
            
            // Presign the `PutObject` request.
            let presignedRequest: URLRequest
            do {
                presignedRequest = try await s3Client.presignedRequestForPutObject(input: putInput, expiration: TimeInterval(5 * 60))
            } catch {
                print("ERROR: Failed to presign the request:", error)
                throw TransferError.signingError
            }

            // Send the HTTP request and upload the file to Amazon S3.
            
            try await httpSend(request: presignedRequest, source: fileURL)
        } catch {
            print("ERROR: file upload failure", error)
            throw TransferError.uploadError
        }
    }
    
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
        
        // If the destination path is `nil`, create a file URL that will save
        // the file with the same name in the user's Downloads directory.
        // Otherwise create the file URL directly from the specified destination
        // path.
        if destPath == nil {
            do {
                try fileURL = FileManager.default.url(
                    for: .downloadsDirectory, in: .userDomainMask, appropriateFor: URL(string: key), create: true).appending(component: key)
            } catch {
                print("ERROR: Unable to find the user's downloads directory: ", error)
                throw TransferError.directoryError
            }
        } else {
            fileURL = URL(fileURLWithPath: destPath!)
        }
                
        do {
            let s3Client = try await S3Client()

            // Create a presigned URLRequest for the `putObject()` call.
            
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

            // Use the presigned request to fetch the file from Amazon S3 and
            // store it at the location given by the `destPath` parameter.
            
            do {
                print("Sending request")
                try await httpFetch(request: presignedRequest, dest: fileURL)
                print("Back from httpFetch")
            } catch {
                throw TransferError.readError
            }
        } catch {
            print("ERROR: failed to download the file", error)
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

/// Send a file to S3 using the specified presigned `URLRequest`.
///
/// - Parameters:
///   - request: A presigned Amazon S3 `URLRequest`.
///   - source: A `URL` indicating the location of the source file.
///
/// - Throws: `TransferError.uploadError` if the upload fails.
func httpSend(request: URLRequest, source: URL) async throws {
    let (data, response) = try await URLSession.shared.upload(for: request, fromFile: source)
    guard let response = response as? HTTPURLResponse else {
        throw TransferError.uploadError
    }
    
    if response.statusCode != 200 {
        print("ERROR: Upload failed with status code: \(response.statusCode)")
        throw TransferError.uploadError
    } else {
        print("File uploaded to \(source.absoluteString)")
    }
}

/// Download a file to temporary storage and move it to the specified location
/// when finished.
///
/// - Parameters:
///   - request: The presigned URLRequest to perform.
///   - dest: The file system URL to relocated the fully downloaded file to.
///
/// - Throws: Throws `TransferError.downloadError` on unexpected errors from
///   the URL loading system or `TransferError.writeError` if an error occurs
///   while moving the file to the `dest` URL.
func httpFetch(request: URLRequest, dest: URL) async throws {
    let (fileURL, response) = try await URLSession.shared.download(for: request)
    guard let response = response as? HTTPURLResponse else {
        throw TransferError.downloadError
    }
    
    if response.statusCode == 200 {
        do {
            try FileManager.default.moveItem(at: fileURL, to: dest)
            print("File saved as \(dest.lastPathComponent)")
        } catch {
            print("ERROR: Error relocating file", error)
        }
    } else {
        print("ERROR: Download failed with status code: \(response.statusCode)")
        throw TransferError.writeError
    }
}

/// The program's asynchronous entry point.
@main
struct Main {
    static func main() async {
        print("MAKE SURE YOU ADD CHUNKED FILE TRANSFERS IF NOT FREE WITH URLRequest!")
        let args = Array(CommandLine.arguments.dropFirst())

        do {
            let command = try ExampleCommand.parse(args)
            try await command.runAsync()
        } catch {
            ExampleCommand.exit(withError: error)
        }
    }    
}
