// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/// An example that shows how to use waiters when using the AWS SDK
/// for Swift.

import Foundation
import AWSClientRuntime
import AWSS3

/// The static main entry point.
@main
struct WaiterExample {
    static func main() async {
        let bucketName: String
        let s3: S3Manager

        // Create the object that handles calling Amazon S3, using the
        // `S3Session` implementation of the `S3SessionProtocol` to direct
        // calls to the Amazon S3 service rather than mocking.

        do {
            s3 = S3Manager(session: try S3Session())
        } catch {
            print("*** Unable to create the Amazon S3 session.")
            exit(1)
        }

        // Create the bucket.

        do {
            bucketName = try await s3.createBucket()
        } catch {
            print("*** Error creating the bucket: \(error)")
            return
        }

        // Wait for the bucket to exist.

        do {
            let bucketExists = try await s3.waitForBucket(name: bucketName)

            if bucketExists == true {
                print("Bucket \(bucketName) was found.")
            } else {
                print("Unable to find bucket \(bucketName).")
            }
        } catch {
            print("*** Error while waiting for \(bucketName) to exist: \(error)")
            return
        }

        // Clean up by deleting the bucket.

        do {
            try await s3.deleteBucket(name: bucketName)
            print("Bucket \(bucketName) deleted.")
        } catch {
            print("*** Error deleting the bucket: \(error)")
        }
    }
}
