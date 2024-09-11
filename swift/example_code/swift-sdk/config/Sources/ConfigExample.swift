// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// An example demonstrating how to configure an Amazon S3 client using the AWS
// SDK for Swift. The same principle applies to all services.

import Foundation
import AWSClientRuntime
import AWSS3
import SmithyRetries
import SmithyRetriesAPI

@main
struct ConfigExample {
    static func main() async {
        // snippet-start:[config.swift.use-custom-configuration]
        // Create an Amazon S3 client configuration object that specifies the
        // region as "us-east-1", an exponential backoff strategy, the
        // adaptive retry mode, and the maximum number of retries as 5.

        let config: S3Client.S3ClientConfiguration

        do {
            // snippet-start:[config.swift.create-configuration]
            config = try await S3Client.S3ClientConfiguration(
                region: "us-east-1", 
                retryStrategyOptions: RetryStrategyOptions(
                    backoffStrategy: SmithyRetries.ExponentialBackoffStrategy(),
                    maxRetriesBase: 5,
                    rateLimitingMode: RetryStrategyOptions.RateLimitingMode.adaptive
                )
            )
            // snippet-end:[config.swift.create-configuration]
        } catch {
            print("Error: Unable to create configuration")
            dump(error)
            exit(1)
        }

        // Create an Amazon S3 client using the configuration created above.

        // snippet-start:[config.swift.create-client]
        let client = S3Client(config: config)
        // snippet-end:[config.swift.create-client]
        // snippet-end:[config.swift.use-custom-configuration]

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
