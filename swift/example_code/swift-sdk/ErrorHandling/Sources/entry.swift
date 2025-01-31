// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// An example showing how to handle service and HTTP errors when using the AWS
// SDK for Swift.
import Foundation
import AwsCommonRuntimeKit
import ClientRuntime
import AWSClientRuntime
import AWSS3

let body = "This is the body of an Amazon S3 object."

/// Main entry point.
@main
struct ErrorHandlingExample {
    static func main() async {
        let bucketName = "ErrorHandling-Bucket-\(Int.random(in: 1000000..<10000000))"
        let objectKey = "ErrorHandling-Object-\(Int.random(in: 1000000..<10000000))"

        // SAMPLE 1: Handling a service error.

        print("=== Sample 1: Service errors ===\n")
        print("Attempting to add an object to an Amazon S3 bucket that doesn't")
        print("exist. This should result in a ServiceError of type NoSuchError...")

        // snippet-start:[errors.swift.service-error]
        do {
            let client = try S3Client(region: "us-east-1")

            _ = try await client.putObject(input: PutObjectInput(
                body: ByteStream.data(Data(body.utf8)),
                bucket: bucketName,
                key: objectKey
            ))
            print("Done.")
        } catch let error as AWSServiceError {
            let errorCode = error.errorCode ?? "<none>"
            let message = error.message ?? "<no message>"

            switch errorCode {
            case "NoSuchBucket":
                print("   | The bucket \"\(bucketName)\" doesn't exist. This is the expected result.")
                print("   | In a real app, you might ask the user whether to use a different name or")
                print("   | create the bucket here.")
            default:
                print("   | Service error of type \(error.errorCode ?? "<unknown>"): \(message)")
            }
        } catch {
            print("Some other error occurred.")
        }
        // snippet-end:[errors.swift.service-error]

        print("\n=== Sample 2: HTTP errors ===\n")
        print("Calling GetObject with bucket and key names that likely don't")
        print("exist. If they don't, this will result in an HTTP error 403")
        print("(Forbidden).")

        // snippet-start:[errors.swift.http-error]
        do {
            let client = try S3Client(region: "us-east-1")

            _ = try await client.getObject(input: GetObjectInput(
                bucket: "not-a-real-bucket",
                key: "not-a-real-key"
            ))
            print("   | Found a matching bucket but shouldn't have!")
        } catch let error as HTTPError {
            print("   | HTTP error; status code: \(error.httpResponse.statusCode.rawValue). This is the")
            print("   | expected result.")
        } catch {
            dump(error, name: "   | An unexpected error occurred.")
        }
        // snippet-end:[errors.swift.http-error]

        print("\n=== Sample 3: Common Runtime errors ===\n")
        print("Calling ListBuckets using a Region name that doesn't exist. This")
        print("should result in a DNS resolution error in the underlying Common")
        print("Runtime (CRT)...")

        // snippet-start:[errors.swift.crt-error]
        do {
            let client = try S3Client(region: "un-real-1")
            
            _ = try await client.listBuckets(input: ListBucketsInput()) 
            print("Done.")
        } catch let error as CommonRunTimeError {
            switch error {
            case .crtError(let error):
                print("   | Common RunTime error: (code \(error.code)) (\(error.name)): \(error.message)")
                print("   | This is the expected result.")
                break
            default:
                // This should never happen in current versions of the
                // SDK, but is here to future-proof this error handler.
                dump(error, name: "   | Unknown type of CommonRunTimeError. This is not expected.")
            }
        } catch {
            print("Some other error")
        }
        // snippet-end:[errors.swift.crt-error]
    }
}