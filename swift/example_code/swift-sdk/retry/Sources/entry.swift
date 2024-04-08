// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// An example demonstrating how to configure retries using the AWS SDK for
// Swift. This example uses Amazon S3, but the same principle applies to every
// AWS service.

import Foundation
import ClientRuntime
import AWSS3

@main
struct RetryExample {
    static func main() async {
        // snippet-start:[retry.swift.setup]
        let config: S3Client.S3ClientConfiguration

        // Create an Amazon S3 client configuration object that specifies the
        // the adaptive retry mode and the base maximum number of retries as 5.

        do {
            // snippet-start:[retry.swift.configure]
            config = try await S3Client.S3ClientConfiguration(
                retryStrategyOptions: RetryStrategyOptions(
                    maxRetriesBase: 5,
                    rateLimitingMode: .adaptive
                )
            )
            // snippet-end:[retry.swift.configure]
        } catch {
            print("Error: Unable to create configuration")
            dump(error)
            exit(1)
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
            print("Error: Unable to get list of buckets")
            dump(error)
            exit(2)
        }
    }
}
