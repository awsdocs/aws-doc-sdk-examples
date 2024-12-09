// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0
///
/// A simple example that shows how to use the AWS SDK for Swift with the
/// Amazon Simple Storage Service (Amazon S3) function `ListBuckets`.

// snippet-start:[s3.swift.intro.imports]
import AWSClientRuntime
import AWSS3
import Foundation

// snippet-end:[s3.swift.intro.imports]

// snippet-start:[s3.swift.intro.getbucketnames]
// Return an array containing the names of all available buckets.
//
// - Returns: An array of strings listing the buckets.
func getBucketNames() async throws -> [String] {
    do {
        // Get an S3Client with which to access Amazon S3.
        // snippet-start:[s3.swift.intro.client-init]
        let configuration = try await S3Client.S3ClientConfiguration()
        //   configuration.region = "us-east-2" // Uncomment this to set the region programmatically.
        let client = S3Client(config: configuration)
        // snippet-end:[s3.swift.intro.client-init]

        // snippet-start:[s3.swift.intro.listbuckets_getnames]
        // Use "Paginated" to get all the buckets.
        // This lets the SDK handle the 'continuationToken' in "ListBucketsOutput".
        let pages = client.listBucketsPaginated(
            input: ListBucketsInput( maxBuckets: 10)
        )
        // snippet-end:[s3.swift.intro.listbuckets_getnames]

        // Get the bucket names.
        var bucketNames: [String] = []

        do {
            for try await page in pages {
                guard let buckets = page.buckets else {
                    print("Error: no buckets returned.")
                    continue
                }

                for bucket in buckets {
                    bucketNames.append(bucket.name ?? "<unknown>")
                }
            }

            return bucketNames
        } catch {
            print("ERROR: listBuckets:", dump(error))
            throw error
        }
    }
}

// snippet-end:[s3.swift.intro.getbucketnames]

// snippet-start:[s3.swift.intro.main]
/// The program's asynchronous entry point.
@main
struct Main {
    static func main() async {
        do {
            let names = try await getBucketNames()

            print("Found \(names.count) buckets:")
            for name in names {
                print("  \(name)")
            }
        } catch let error as AWSServiceError {
            print("An Amazon S3 service error occurred: \(error.message ?? "No details available")")
        } catch {
            print("An unknown error occurred: \(dump(error))")
        }
    }
}

// snippet-end:[s3.swift.intro.main]
