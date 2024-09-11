// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0
///
/// A simple example that shows how to use the AWS SDK for Swift with the
/// Amazon Simple Storage Service (Amazon S3) function `ListBuckets`.

// snippet-start:[s3.swift.intro.imports]
import Foundation
import ClientRuntime
import AWSS3
// snippet-end:[s3.swift.intro.imports]

// snippet-start:[s3.swift.intro.getbucketnames]
// Return an array containing the names of all available buckets.
//
// - Returns: An array of strings listing the buckets.
func getBucketNames() async throws -> [String] {
    // Get an S3Client with which to access Amazon S3.
    // snippet-start:[s3.swift.intro.client-init]
    let client = try S3Client(region: "us-east-1")
    // snippet-end:[s3.swift.intro.client-init]

    // snippet-start:[s3.swift.intro.listbuckets]
    let output = try await client.listBuckets(
        input: ListBucketsInput()
    )
    // snippet-end:[s3.swift.intro.listbuckets]

    // Get the bucket names.
    var bucketNames: [String] = []

    guard let buckets = output.buckets else {
        return bucketNames
    }
    for bucket in buckets {
        bucketNames.append(bucket.name ?? "<unknown>")
    }

    return bucketNames
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
        } catch let error as ServiceError {
            print("An Amazon S3 service error occurred: \(error.message ?? "No details available")")
        } catch {
            print("An unknown error occurred: \(dump(error))")
        }
    }
}
// snippet-end:[s3.swift.intro.main]
