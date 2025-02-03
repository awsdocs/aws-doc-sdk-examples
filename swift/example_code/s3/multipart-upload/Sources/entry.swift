// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// An example demonstrating how to perform multi-part uploads to Amazon S3
/// using the AWS SDK for Swift.

// snippet-start:[swift.s3.multipart-upload.imports]
import ArgumentParser
import AWSClientRuntime
import AWSS3
import Foundation
import Smithy
// snippet-end:[swift.s3.multipart-upload.imports]

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
        commandName: "mpupload",
        abstract: """
        This example shows how to upload files to Amazon S3 using multi-part
        uploads.
        """,
        discussion: """
        """
    )

    // -MARK: - File uploading

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

        // Start the multi-part upload process and get the upload ID.

        var completedParts: [S3ClientTypes.CompletedPart] = []

        let uploadID = try await startMultipartUpload(client: s3Client,
                                    bucket: bucket, key: fileName)

        // Open a file handle and prepare to send the file in chunks. Each chunk
        // is 5 MB, which is the minimum size allowed by Amazon S3.

        do {
            let blockSize = Int(5 * 1024 * 1024)
            let fileHandle = try FileHandle(forReadingFrom: fileURL)
            let fileSize = try getFileSize(file: fileHandle)
            let blockCount = Int(ceil(Double(fileSize) / Double(blockSize)))

            // Upload the blocks one at as Amazon S3 object parts.

            print("Uploading...")

            // snippet-start:[swift.s3.multipart-upload.upload-loop]
            for partNumber in 1...blockCount {
                let data: Data
                let startIndex = UInt64(partNumber - 1) * UInt64(blockSize)

                // Read the block from the file.
                
                data = try readFileBlock(file: fileHandle, startIndex: startIndex, size: blockSize)
                
                // Upload the part to Amazon S3 and append the `CompletedPart` to
                // the array `completedParts` for use after all parts are uploaded.
                
                let completedPart = try await uploadPart(
                    client: s3Client, uploadID: uploadID,
                    bucket: bucket, key: fileName,
                    partNumber: partNumber, data: data
                )
                completedParts.append(completedPart)
                
                let percent = Double(partNumber) / Double(blockCount) * 100
                print(String(format: " %.1f%%", percent))
            }
            // snippet-end:[swift.s3.multipart-upload.upload-loop]

            // Finish the upload.
        
            try await finishMultipartUpload(client: s3Client, uploadId: uploadID, 
                                            bucket: bucket, key: fileName,
                                            parts: completedParts)
        } catch {
            throw TransferError.uploadError("Error uploading the file: \(error)")
        }

        print("Done. Uploaded as \(fileName) in bucket \(bucket).")
    }

    // snippet-start:[swift.s3.multipart-upload.CreateMultipartUpload]
    /// Start a multi-part upload to Amazon S3.
    /// - Parameters:
    ///   - bucket: The name of the bucket to upload into.
    ///   - key: The name of the object to store in the bucket.
    ///
    /// - Returns: A string containing the `uploadId` of the multi-part
    ///   upload job.
    ///
    /// - Throws:
    func startMultipartUpload(client: S3Client, bucket: String, key: String) async throws -> String {
        let multiPartUploadOutput: CreateMultipartUploadOutput

        // First, create the multi-part upload.
        
        do {
            multiPartUploadOutput = try await client.createMultipartUpload(
                input: CreateMultipartUploadInput(
                    bucket: bucket,
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

        return uploadID
    }
    // snippet-end:[swift.s3.multipart-upload.CreateMultipartUpload]

    // snippet-start:[swift.s3.multipart-upload.UploadPart]
    /// Upload the specified data as part of an Amazon S3 multi-part upload.
    ///
    /// - Parameters:
    ///   - client: The S3Client to use to upload the part.
    ///   - uploadID: The upload ID of the multi-part upload to add the part to.
    ///   - bucket: The name of the bucket the data is being written to.
    ///   - key: A string giving the key which names the Amazon S3 object the file is being added to.
    ///   - partNumber: The part number within the file that the specified data represents.
    ///   - data: The data to send as the specified object part number in the object.
    ///
    /// - Throws: `TransferError.uploadError`
    ///
    /// - Returns: A `CompletedPart` object describing the part that was uploaded.
    ///   contains the part number as well as the ETag returned by Amazon S3.
    func uploadPart(client: S3Client, uploadID: String, bucket: String,
                    key: String, partNumber: Int, data: Data)
                    async throws -> S3ClientTypes.CompletedPart {
        let uploadPartInput = UploadPartInput(
            body: ByteStream.data(data),
            bucket: bucket,
            key: key,
            partNumber: partNumber,
            uploadId: uploadID
        )
        
        // Upload the part.
        do {
            let uploadPartOutput = try await client.uploadPart(input: uploadPartInput)

            guard let eTag = uploadPartOutput.eTag else {
                throw TransferError.uploadError("Missing eTag")
            } 

            return S3ClientTypes.CompletedPart(
                eTag: eTag,
                partNumber: partNumber
            )
        } catch {
            throw TransferError.uploadError(error.localizedDescription)
        }
    }
    // snippet-end:[swift.s3.multipart-upload.UploadPart]

    // snippet-start:[swift.s3.multipart-upload.CompleteMultipartUpload]
    /// Complete a multi-part upload using an array of `CompletedMultipartUpload`
    /// objects describing the completed parts.
    ///
    /// - Parameters:
    ///   - client: The S3Client to finish uploading with.
    ///   - uploadId: The multi-part upload's ID string.
    ///   - bucket: The name of the bucket the upload is targeting.
    ///   - key: The name of the object being written to the bucket.
    ///   - parts: An array of `CompletedPart` objects describing each part
    ///     of the upload.
    ///
    /// - Throws: `TransferError.multipartFinishError`
    func finishMultipartUpload(client: S3Client, uploadId: String, bucket: String, key: String,
                               parts: [S3ClientTypes.CompletedPart]) async throws {
        do {
            let partInfo = S3ClientTypes.CompletedMultipartUpload(parts: parts)
            let multiPartCompleteInput = CompleteMultipartUploadInput(
                bucket: bucket,
                key: key,
                multipartUpload: partInfo,
                uploadId: uploadId
            )
            _ = try await client.completeMultipartUpload(input: multiPartCompleteInput)
        } catch {
            dump(error)
            throw TransferError.multipartFinishError(error.localizedDescription)
        }
    }
    // snippet-end:[swift.s3.multipart-upload.CompleteMultipartUpload]

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
