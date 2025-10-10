// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// The main code for file upload download example for the S3 Transfer Manager
// in the AWS SDK for Swift. This example doesn't include progress monitoring.
//
// The bucket downloading example
// (swift/example_code/s3-transfer-manager/download-streaming) includes an
// example of how to use progress monitoring.


// snippet-start:[swift.s3tm.upload.imports]
import AWSS3
import AWSClientRuntime
import S3TransferManager
import Foundation
import Smithy
import SmithyStreams
// snippet-end:[swift.s3tm.upload.imports]

class Example {
    let filePath: String
    let bucketName: String
    
    init(path: String, bucket: String) {
        self.filePath = path
        self.bucketName = bucket
    }
    
    /// The body of the example.
    func run() async throws {
        // snippet-start:[swift.s3tm.upload.uploadObject]
        let s3tm = try await S3TransferManager()

        let fileURL = URL(string: filePath)
        guard let fileURL else {
            print("*** The file at \(filePath) doesn't exist.")
            return
        }

        // Prepare the upload request.

        let fileName = fileURL.lastPathComponent
        let fileHandle = try FileHandle(forReadingFrom: fileURL)
        let byteStream = ByteStream.stream(FileStream(fileHandle: fileHandle))

        let uploadObjectInput = UploadObjectInput(
            body: byteStream,
            bucket: bucketName,
            key: fileName,
            transferListeners: [UploadObjectLoggingTransferListener()]
        )

        // Start the upload, then wait for it to complete.

        do {
            let uploadObjectTask = try s3tm.uploadObject(
                input: uploadObjectInput
            )

            _ = try await uploadObjectTask.value
        } catch let error as AWSServiceError {
            if error.errorCode == "NoSuchBucket" {
                print("*** The specified bucket, \(bucketName), doesn't exist.")
                return
            } else {
                print("An unrecognized error occurred: \(error.message ?? "<unknown>")")
                return
            }
        } catch {
            print("*** An error occurred uploading the file: \(error.localizedDescription)")
        }
        // snippet-end:[swift.s3tm.upload.uploadObject]
    }
}
