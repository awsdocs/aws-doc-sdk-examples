// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// The main code for the streaming bucket download example for the
// S3 Transfer Manager in the AWS SDK for Swift.

// snippet-start:[swift.s3tm.getbucket.imports]
import AWSS3
import S3TransferManager
import Foundation
// snippet-end:[swift.s3tm.getbucket.imports]

class Example {
    let region: String
    let bucketName: String
    
    init(region: String, bucket: String) {
        self.region = region
        self.bucketName = bucket
    }
    
    /// The body of the example.
    func run() async throws {
        // snippet-start:[swift.s3tm.getbucket.config-create]
        // Create an S3ClientConfiguration object.
        let s3Config = try await S3Client.S3ClientConfiguration(
            region: region
        )
        
        // Create an S3TransferManager using the S3 configuration.
        
        let s3tmConfig = try await S3TransferManagerConfig(
            s3ClientConfig: s3Config
        )
        
        let s3tm = S3TransferManager(config: s3tmConfig)
        // snippet-end:[swift.s3tm.getbucket.config-create]

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
        
        // snippet-start:[swift.s3tm.getbucket.downloadBucket]
        let downloadBucketTask = try s3tm.downloadBucket(
            input: DownloadBucketInput(
                bucket: bucketName,
                destination: targetDirectory
            )
        )

        do {
            let downloadBucketOutput = try await downloadBucketTask.value
            print("Total files downloaded: \(downloadBucketOutput.objectsDownloaded)")
            print("Number of failed downloads: \(downloadBucketOutput.objectsFailed)")
        } catch {
            print("*** Error downloading the bucket: \(error.localizedDescription)")
        }
        // snippet-end:[swift.s3tm.getbucket.downloadBucket]
    }
}
