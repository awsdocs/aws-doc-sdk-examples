// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// A simple example that shows how to use the AWS SDK for Swift to
/// perform file uploads and downloads to Amazon S3, both with and
/// without presigned requests. Also included is code to perform
/// multi-part uploads.

// snippet-start:[swift.s3.presigned.imports]
import ArgumentParser
import AWSClientRuntime
import AWSS3
import Foundation
import Smithy
import SmithyHTTPAPI
// snippet-end:[swift.s3.presigned.imports]

// -MARK: - Async command line tool

struct ExampleCommand: ParsableCommand {
    // -MARK: Command arguments
    @Flag(help: "Transfer direction of the file transfer (up or down)")
    var direction: TransferDirection
    @Flag(help: "Enable multi-part upload (default: no)")
    var multiPart: Bool = false
    @Option(help: "Source path or Amazon S3 file path")
    var source: String
    @Option(help: "Destination path or Amazon S3 file path")
    var dest: String?
    @Option(help: "Name of the Amazon S3 bucket containing the file")
    var bucket: String
    @Option(help: "Name of the Amazon S3 Region to use (default: us-east-1)")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "presigned",
        abstract: """
        This example shows how to use presigned requests when transferring files
        using Amazon Simple Storage Service (Amazon S3). In addition, you can
        optionally use a flag on the command line to use multi-part uploads.
        """,
        discussion: """
        """
    )
    
    // -MARK: - File upload
    
    // snippet-start:[swift.s3.presigned.presign-upload-file]
    /// Upload the specified file to the Amazon S3 bucket with the given name.
    ///
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
        
        print("Uploading file from \(fileURL.absoluteString) to \(bucket)/\(fileName).")

        // Create the presigned request for the `PostObject` request.
        
        // snippet-start:[swift.s3.presigned.presign-PutObject]
        let fileData = try Data(contentsOf: fileURL)
        let dataStream = ByteStream.data(fileData)

        let config = try await S3Client.S3ClientConfiguration(region: region)
        let s3Client = S3Client(config: config)
        let putInput = PutObjectInput(
            body: dataStream,
            bucket: bucket,
            key: fileName
        )
        
        // Presign the `PutObject` request with a 5-minute expiration. The
        // presigned `URLRequest` can then be sent using the URL Loading System
        // (https://developer.apple.com/documentation/foundation/url_loading_system/uploading_data_to_a_website).
        
        let presignedRequest: URLRequest
        do {
            presignedRequest = try await s3Client.presignedRequestForPutObject(input: putInput, expiration: TimeInterval(5 * 60))
        } catch {
            throw TransferError.signingError
        }
        // snippet-end:[swift.s3.presigned.presign-PutObject]

        // Send the HTTP request and upload the file to Amazon S3.
        
        try await httpSendFileRequest(request: presignedRequest, source: fileURL)
    }
    // snippet-end:[swift.s3.presigned.presign-upload-file]

    // -MARK: Multi-part upload
    
    // snippet-start:[swift.s3.presigned.upload-multipart]
    /// Upload the specified file into the given bucket using a multi-part upload.
    ///
    /// - Parameters:
    ///   - sourcePath: The pathname of the source file to upload.
    ///   - bucket: The name of the bucket to store the file into.
    ///   - key: The name to give the file in the bucket. If not specified, the
    ///     file's name is used.
    /// - Throws: `TransferError.uploadError`, `TransferError.readError`
    func uploadFileMultipart(sourcePath: String, bucket: String, key: String?) async throws {
        let source = URL(fileURLWithPath: sourcePath)
        let fileName: String
        
        // If the key isn't specified, use the last component of the path instead.
        
        if key == nil {
            fileName = source.lastPathComponent
        } else {
            fileName = key!
        }
        
        var completedParts: [S3ClientTypes.CompletedPart] = []

        // First, create the multi-part upload.

        let uploadID = try await startMultipartUpload(bucket: bucket, key: fileName)
        
        // Open a file handle and prepare to send the file in chunks. Each chunk
        // is 5 MB since that's the smallest chunk size allowed by Amazon S3.

        let blockSize = Int(5 * 1024 * 1024)
        let fileHandle = try FileHandle(forReadingFrom: source)
        let fileSize = try getFileSize(file: fileHandle)
        let blockCount = Int(ceil(Double(fileSize) / Double(blockSize)))
        
        // Upload the blocks one at a time as Amazon S3 object parts.
        
        print("Uploading...")
        
        // snippet-start:[swift.s3.presigned.presign-upload-loop]
        for partNumber in 1...blockCount {
            let data: Data
            let startIndex = UInt64(partNumber - 1) * UInt64(blockSize)

            // Read the block from the file.
            
            data = try readFileBlock(file: fileHandle, startIndex: startIndex, size: blockSize)
            
            // Upload the part to Amazon S3 and append the `CompletedPart` to
            // the array `completedParts` for use after all parts are uploaded.
            
            let completedPart = try await uploadPart(uploadID: uploadID, bucket: bucket, key: fileName, partNumber: partNumber, data: data)
            completedParts.append(completedPart)
            
            let percent = Double(partNumber) / Double(blockCount) * 100
            print(String(format: " %.1f%%", percent))
        }
        // snippet-end:[swift.s3.presigned.presign-upload-loop]

        // Finish the upload.
        
        try await finishMultipartUpload(uploadId: uploadID, bucket: bucket,
                                        key: fileName, parts: completedParts)

        print("Done. Uploaded as \(fileName) in bucket \(bucket).")
    }
    // snippet-end:[swift.s3.presigned.upload-multipart]

    // snippet-start:[swift.s3.presigned.presign-upload-create]
    /// Start a multi-part upload to Amazon S3.
    /// - Parameters:
    ///   - bucket: The name of the bucket to upload into.
    ///   - key: The name of the object to store in the bucket.
    ///
    /// - Returns: A string containing the `uploadId` of the multi-part
    ///   upload job.
    ///
    /// - Throws: `
    func startMultipartUpload(bucket: String, key: String) async throws -> String {
        let multiPartUploadOutput: CreateMultipartUploadOutput

        // First, create the multi-part upload.
        
        do {
            let config = try await S3Client.S3ClientConfiguration(region: region)
            let s3Client = S3Client(config: config)

            multiPartUploadOutput = try await s3Client.createMultipartUpload(
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
    // snippet-end:[swift.s3.presigned.presign-upload-create]

    // snippet-start:[swift.s3.presigned.presign-upload-complete]
    /// Complete a multi-part upload by creating a `CompletedMultipartUpload`
    /// with the array of completed part descriptions. This is used as the
    /// value of the `multipartUpload` property when calling
    /// `completeMultipartUpload(input:)`.
    ///
    /// - Parameters:
    ///   - uploadId: The multi-part upload's ID string.
    ///   - bucket: The name of the bucket the upload is targeting.
    ///   - key: The name of the object being written to the bucket.
    ///   - parts: An array of `CompletedPart` objects describing each part
    ///     of the upload.
    ///
    /// - Throws: `TransferError.multipartFinishError`
    func finishMultipartUpload(uploadId: String, bucket: String, key: String,
                               parts: [S3ClientTypes.CompletedPart]) async throws {
        do {
            let config = try await S3Client.S3ClientConfiguration(region: region)
            let s3Client = S3Client(config: config)

            let partInfo = S3ClientTypes.CompletedMultipartUpload(parts: parts)
            let multiPartCompleteInput = CompleteMultipartUploadInput(
                bucket: bucket,
                key: key,
                multipartUpload: partInfo,
                uploadId: uploadId
            )
            _ = try await s3Client.completeMultipartUpload(input: multiPartCompleteInput)
        } catch {
            throw TransferError.multipartFinishError
        }
    }
    // snippet-end:[swift.s3.presigned.presign-upload-complete]

    // snippet-start:[swift.s3.presigned.presign-upload-part]
    /// Upload the specified data as part of an Amazon S3 multi-part upload.
    ///
    /// - Parameters:
    ///   - uploadID: The upload ID of the multi-part upload to add the part to.
    ///   - bucket: The name of the bucket the data is being written to.
    ///   - key: A string giving the key which names the Amazon S3 object the file is being added to.
    ///   - partNumber: The part number within the file that the specified data represents.
    ///   - data: The data to send as the specified object part number in the object.
    ///
    /// - Throws: `TransferError.signingError`, `TransferError.uploadError`
    ///
    /// - Returns: A `CompletedPart` object describing the part that was uploaded.
    ///   contains the part number as well as the ETag returned by Amazon S3.
    func uploadPart(uploadID: String, bucket: String, key: String, partNumber: Int,
                    data: Data) async throws -> S3ClientTypes.CompletedPart {
        let uploadPartInput = UploadPartInput(
            body: ByteStream.data(data),
            bucket: bucket,
            key: key,
            partNumber: partNumber,
            uploadId: uploadID
        )
        
        let request: URLRequest
        do {
            let config = try await S3Client.S3ClientConfiguration()
            let s3Client = S3Client(config: config)

            request = try await s3Client.presignedRequestForUploadPart(input: uploadPartInput, expiration: 2 * 60)
        } catch {
            throw TransferError.signingError
        }
        
        let (_, response) = try await URLSession.shared.upload(for: request, from: data)
        guard let response = response as? HTTPURLResponse else {
            throw TransferError.uploadError("No response from Amazon S3")
        }
        
        if response.statusCode != 200 {
            throw TransferError.uploadError(
                "Upload of part \(partNumber) failed with status code \(response.statusCode)"
            )
        } else {
            let eTag = response.value(forHTTPHeaderField: "ETag")
            return S3ClientTypes.CompletedPart(eTag: eTag, partNumber: partNumber)
        }
    }
    // snippet-end:[swift.s3.presigned.presign-upload-part]

    // MARK: Support
    
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

    // MARK: - File download
    
    // snippet-start:[swift.s3.presigned.download-file]
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
                    for: .downloadsDirectory,
                    in: .userDomainMask,
                    appropriateFor: URL(string: key),
                    create: true
                ).appending(component: key)
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
        
        try await httpFetchRequest(request: presignedRequest, dest: fileURL)
    }
    // snippet-end:[swift.s3.presigned.download-file]

    // -MARK: - HTTP file transfers

    /// Send a file to S3 using the specified presigned `URLRequest`.
    ///
    /// - Parameters:
    ///   - request: A presigned Amazon S3 `URLRequest`.
    ///   - source: A `URL` indicating the location of the source file.
    ///
    /// - Throws: `TransferError.uploadError`
    func httpSendFileRequest(request: URLRequest, source: URL) async throws {
        let (_, response) = try await URLSession.shared.upload(for: request, fromFile: source)
        guard let response = response as? HTTPURLResponse else {
            throw TransferError.uploadError("No response from Amazon S3")
        }
        
        if response.statusCode != 200 {
            throw TransferError.uploadError(
                "Upload failed with status code: \(response.statusCode)"
            )
        } else {
            print("File uploaded to \(source.absoluteString)")
        }
    }

    /// Use the specified `URLRequest` to download a file.
    ///
    /// - Parameters:
    ///   - request: The presigned URLRequest to perform.
    ///   - dest: The file system URL to relocated the fully downloaded file to.
    ///
    /// - Throws: `TransferError.downloadError`, `TransferError.writeError`,
    ///   `TransferError.fileMoveError`
    ///
    /// The file is first downloaded to the user's Downloads directory, then
    /// it's copied to the destination URL.
    func httpFetchRequest(request: URLRequest, dest: URL) async throws {
        let (fileURL, response) = try await URLSession.shared.download(for: request)
        guard let response = response as? HTTPURLResponse else {
            throw TransferError.downloadError("No response from Amazon S3")
        }
        
        // If the download was successful, move the file to the destination.
        
        if response.statusCode == 200 {
            do {
                try FileManager.default.moveItem(at: fileURL, to: dest)
                print("File saved as \(dest.lastPathComponent)")
            } catch {
                throw TransferError.fileMoveError
            }
        } else {
            print("ERROR: Download failed with HTTP status code: \(response.statusCode)")
            throw TransferError.downloadError(
                "Download failed with HTTP status code: \(response.statusCode)"
            )
        }
    }

    // -MARK: - Asynchronous main code
    
    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        switch direction {
        case .up:
            if multiPart == false {
                try await uploadFile(sourcePath: source, bucket: bucket,
                                     key: dest)
            } else {
                try await uploadFileMultipart(sourcePath: source, bucket: bucket,
                                              key: dest)
            }
        case .down:
            try await downloadFile(bucket: bucket, key: source,
                                   destPath: dest)
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
