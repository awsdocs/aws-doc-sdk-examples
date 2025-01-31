// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// An example demonstrating how to configure retries using the AWS SDK for
// Swift. This example uses Amazon S3, but the same principle applies to every
// AWS service.

import Foundation
// snippet-start:[retry.swift.imports]
import AWSS3
import SmithyRetries
import SmithyRetriesAPI
// snippet-end:[retry.swift.imports]

@main
struct RetryExample {
    static func main() async {
        // snippet-start:[retry.swift.setup]
        let config: S3Client.S3ClientConfiguration

        // Create an Amazon S3 client configuration object that specifies the
        // adaptive retry mode and sets the maximum number of attempts to 3.
        // If that fails, create a default configuration instead.

        do {
            // snippet-start:[retry.swift.configure]
            config = try await S3Client.S3ClientConfiguration(
                awsRetryMode: .adaptive,
                maxAttempts: 3
            )
            // snippet-end:[retry.swift.configure]
        } catch {
            do {
                config = try await S3Client.S3ClientConfiguration()
            } catch {
                print("Error: Unable to configure Amazon S3.")
                dump(error)
                return
            }
        }

        // Create an Amazon S3 client using the configuration created above.

        let client = S3Client(config: config)
        // snippet-end:[retry.swift.setup]

        // Use the client to list the user's buckets. Return without any
        // output if no buckets are found.

        do {
            let output = try await client.listBuckets(input: ListBucketsInput())

            guard let buckets = output.buckets else {
                return
            }

            for bucket in buckets {
                print("\(bucket.name ?? "<unknown>")")
            }
        } catch {
            print("Error: Unable to get a list of buckets.")
            dump(error)
            return
        }
    }
}
