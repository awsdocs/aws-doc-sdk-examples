// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// An example demonstrating how to perform multi-part uploads to Amazon S3
/// using the AWS SDK for Swift.

// snippet-start:[swift.s3.mp-checksums.imports]
import ArgumentParser
import AWSClientRuntime
import AWSS3
import Foundation
import Smithy
// snippet-end:[swift.s3.mp-checksums.imports]

// -MARK: - Async command line tool

struct ExampleCommand: ParsableCommand {
    // -MARK: Command arguments
    @Option(help: "Path of local file to upload to Amazon S3")
    var file: String
    @Option(help: "Name of the Amazon S3 bucket to upload to")
    var bucket: String
    @Option(help: "Key name to give the file on Amazon S3")
    var key: String?
    @Option(help: "Name of the Amazon S3 Region to use")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "mpchecksums",
        abstract: """
        This example shows how to use checksums with multi-part uploads.
        """,
        discussion: """
        """
    )

    // -MARK: - File uploading

    // snippet-start:[swift.s3.mp-checksums.uploadfile]
    /// Upload a file to Amazon S3.
    /// 
    /// - Parameters:
    ///   - file: The path of the local file to upload to Amazon S3.
    ///   - bucket: The name of the bucket to upload the file into.
    ///   - key: The key (name) to give the object on Amazon S3.
    ///
    /// - Throws: Errors from `TransferError`
    func uploadFile(file: String, bucket: String, key: String?) async throws {
        let fileURL = URL(fileURLWithPath: file)
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

        let multiPartUploadOutput: CreateMultipartUploadOutput

        // First, create the multi-part upload, using SHA256 checksums.
        
        do {
            multiPartUploadOutput = try await s3Client.createMultipartUpload(
                input: CreateMultipartUploadInput(
                    bucket: bucket,
                    checksumAlgorithm: .sha256,
                    key: key
                )
            )
        } catch {
            throw TransferError.multipartStartError
        }
        
        // Get the upload ID. This needs to be included with each part sent.
        
        guard let uploadID = multiPartUploadOutput.uploadId else {
            throw TransferError.uploadError("Unable to get the upload ID")
        }

        // Open a file handle and prepare to send the file in chunks. Each chunk
        // is 5 MB, which is the minimum size allowed by Amazon S3.

        do {
            let blockSize = Int(5 * 1024 * 1024)
            let fileHandle = try FileHandle(forReadingFrom: fileURL)
            let fileSize = try getFileSize(file: fileHandle)
            let blockCount = Int(ceil(Double(fileSize) / Double(blockSize)))
            var completedParts: [S3ClientTypes.CompletedPart] = []

            // Upload the blocks one at as Amazon S3 object parts.

            print("Uploading...")

            for partNumber in 1...blockCount {
                let data: Data
                let startIndex = UInt64(partNumber - 1) * UInt64(blockSize)

                // Read the block from the file.
                
                data = try readFileBlock(file: fileHandle, startIndex: startIndex, size: blockSize)
                                
                let uploadPartInput = UploadPartInput(
                    body: ByteStream.data(data),
                    bucket: bucket,
                    checksumAlgorithm: .sha256,
                    key: key,
                    partNumber: partNumber,
                    uploadId: uploadID
                )
                
                // Upload the part with a SHA256 checksum.

                do {
                    let uploadPartOutput = try await s3Client.uploadPart(input: uploadPartInput)

                    guard let eTag = uploadPartOutput.eTag else {
                        throw TransferError.uploadError("Missing eTag")
                    } 
                    guard let checksum = uploadPartOutput.checksumSHA256 else {
                        throw TransferError.checksumError
                    }
                    print("Part \(partNumber) checksum: \(checksum)")

                    // Append the completed part description (including its
                    // checksum, ETag, and part number) to the
                    // `completedParts` array.

                    completedParts.append(
                        S3ClientTypes.CompletedPart(
                            checksumSHA256: checksum,
                            eTag: eTag,
                            partNumber: partNumber
                        )
                    )
                } catch {
                    throw TransferError.uploadError(error.localizedDescription)
                }                                
            }

            // Tell Amazon S3 that all parts have been uploaded.
        
            do {
                let partInfo = S3ClientTypes.CompletedMultipartUpload(parts: completedParts)
                let multiPartCompleteInput = CompleteMultipartUploadInput(
                    bucket: bucket,
                    key: key,
                    multipartUpload: partInfo,
                    uploadId: uploadID
                )
                _ = try await s3Client.completeMultipartUpload(input: multiPartCompleteInput)
            } catch {
                throw TransferError.multipartFinishError(error.localizedDescription)
            }
        } catch {
            throw TransferError.uploadError("Error uploading the file: \(error)")
        }

        print("Done. Uploaded as \(fileName) in bucket \(bucket).")
    }
    // snippet-end:[swift.s3.mp-checksums.uploadfile]

    // -MARK: - File access

    /// Get the size of a file in bytes.
    ///
    /// - Parameter file: `FileHandle` identifying the file to return the size of.
    ///
    /// - Returns: The number of bytes in the file.
    func getFileSize(file: FileHandle) throws -> UInt64 {
        let fileSize: UInt64
        
        // Get the total size of the file in bytes, then compute the number
        // of blocks it will take to transfer the whole file.
        
        do {
            try file.seekToEnd()
            fileSize = try file.offset()
        } catch {
            throw TransferError.readError
        }
        return fileSize
    }
    
    /// Read the specified range of bytes from a file and return them in a
    /// new `Data` object.
    ///
    /// - Parameters:
    ///   - file: The `FileHandle` to read from.
    ///   - startIndex: The index of the first byte to read.
    ///   - size: The number of bytes to read.
    ///
    /// - Returns: A new `Data` object containing the specified range of bytes.
    ///
    /// - Throws: `TransferError.readError` if the read fails.
    func readFileBlock(file: FileHandle, startIndex: UInt64, size: Int) throws -> Data {
        file.seek(toFileOffset: startIndex)
        do {
            let data = try file.read(upToCount: size)
            guard let data else {
                throw TransferError.readError
            }
            return data
        } catch {
            throw TransferError.readError
        }
    }

    // -MARK: - Asynchronous main code
    
    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        try await uploadFile(file: file, bucket: bucket,
                    key: key)
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
