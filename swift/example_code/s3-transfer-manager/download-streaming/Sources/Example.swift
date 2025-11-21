// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// The main code for the streaming bucket download example for the
// S3 Transfer Manager in the AWS SDK for Swift.

// snippet-start:[swift.s3tm.streaming.imports]
import AWSS3
import S3TransferManager
import Foundation
// snippet-end:[swift.s3tm.streaming.imports]

class Example {
    let region: String
    let bucketName: String
    let s3Prefix: String?
    
    init(region: String, bucket: String, s3Prefix: String?) {
        self.region = region
        self.bucketName = bucket
        self.s3Prefix = s3Prefix
    }
    
    /// The body of the example.
    func run() async throws {
        // snippet-start:[swift.s3tm.streaming.config-create]
        let s3Config = try await S3Client.S3ClientConfiguration(
            region: region
        )
        
        // Create an S3TransferManager object.
        
        let s3tmConfig = try await S3TransferManagerConfig(
            s3ClientConfig: s3Config,                   // Configuration for the S3Client
            targetPartSizeBytes: 16 * 1024 * 1024,      // 16 MB part size
            multipartUploadThresholdBytes: 128 * 1024 * 1024,   // 128 MB threshold
            multipartDownloadType: .part
        )
        
        let s3tm = S3TransferManager(config: s3tmConfig)
        // snippet-end:[swift.s3tm.streaming.config-create]

        // Create a listener for events from the download of the bucket, to
        // monitor the overall state of the download.
        
        // snippet-start:[swift.s3tm.streaming.bucket-listener]
        let downloadBucketStreamingTransferListener = DownloadBucketStreamingTransferListener()
        
        Task {
            for try await downloadBucketTransferEvent in downloadBucketStreamingTransferListener.eventStream {
                switch downloadBucketTransferEvent {
                case .initiated(let input, _):
                    print("Download of bucket \(input.bucket) started...")
                    
                case .complete(let input, _, let snapshot):
                    print("Download of bucket \(input.bucket) complete. Downloaded \(snapshot.transferredFiles) files.")
                    downloadBucketStreamingTransferListener.closeStream()
                    
                case .failed(let input, let snapshot):
                    print("*** Download of bucket \(input.bucket) failed after downloading \(snapshot.transferredFiles) files.")
                    downloadBucketStreamingTransferListener.closeStream()
                }
            }
        }
        // snippet-end:[swift.s3tm.streaming.bucket-listener]

        // Create the directory to download the bucket into. The new directory
        // is placed into the user's Downloads folder and has the same name as
        // the bucket being downloaded.
        
        guard let downloadsDirectory = FileManager.default.urls(for: .downloadsDirectory, in: .userDomainMask).first else {
            print("*** Unable to locate the Downloads directory.")
            return
        }
        
        let targetDirectory = downloadsDirectory.appending(component: bucketName, directoryHint: .isDirectory)
        try FileManager.default.createDirectory(at: targetDirectory, withIntermediateDirectories: true)
        
        // Start downloading the bucket by calling S3TransferManager.downloadBucket(input:).
        
        // snippet-start:[swift.s3tm.streaming.downloadBucket]
        let downloadBucketTask = try s3tm.downloadBucket(
            input: DownloadBucketInput(
                bucket: bucketName,
                destination: targetDirectory,
                s3Prefix: s3Prefix,
                // The listener for the overall bucket download process.
                directoryTransferListeners: [downloadBucketStreamingTransferListener],
                // A factory that creates a listener for each file being downloaded.
                objectTransferListenerFactory: {
                    let objectListener = DownloadObjectStreamingTransferListener()
                    
                    Task {
                        for try await downloadObjectTransferEvent in objectListener.eventStream {
                            switch downloadObjectTransferEvent {
                            // The download of a file has begun.
                            case .initiated(let input, _):
                                print("Downloading file \(input.key)...")
            
                            // The number of bytes received so far has been updated.
                            case .bytesTransferred(let input, let snapshot):
                                print("    Transferred \(snapshot.transferredBytes) total bytes of file \(input.key)...")
                                
                            // A file download has completed.
                            case .complete(let input, _, let snapshot):
                                print("Finished downloading file \(input.key) (\(snapshot.transferredBytes) bytes).")
                                objectListener.closeStream()
                                
                            // The download of the file has failed.
                            case .failed(let input, let snapshot):
                                print("*** Download of file \(input.key) failed after \(snapshot.transferredBytes) bytes.")
                                objectListener.closeStream()
                            }
                        }
                    }

                    return [
                        objectListener
                    ]
                }
            )
        )
        // snippet-end:[swift.s3tm.streaming.downloadBucket]

        // Wait for the bucket to finish downloading, then display the results.
        
        // snippet-start:[swift.s3tm.streaming.wait-for-download]
        do {
            let downloadBucketOutput = try await downloadBucketTask.value
            print("Total files downloaded: \(downloadBucketOutput.objectsDownloaded)")
            print("Number of failed downloads: \(downloadBucketOutput.objectsFailed)")
        } catch {
            print("*** Error downloading the bucket: \(error.localizedDescription)")
            dump(error)
        }
        // snippet-end:[swift.s3tm.streaming.wait-for-download]
    }
}
