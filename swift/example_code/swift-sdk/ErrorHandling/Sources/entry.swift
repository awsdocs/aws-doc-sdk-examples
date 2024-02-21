// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// An example showing how to handle service and HTTP errors when using the AWS
// SDK for Swift.
import Foundation
import AwsCommonRuntimeKit
import ClientRuntime
import AWSS3

/// Main entry point.
@main
struct ErrorHandlingExample {
    static func main() async {
        await SDKLoggingSystem.initialize(logLevel: .error)

        print("Calling ListBuckets using a Region name that doesn't exist. This")
        print("should result in a DNS resolution error.")

        // snippet-start:[errors.swift.service-error]
        do {
            let client = try S3Client(region: "un-real-1")

            _ = try await client.listBuckets(input: ListBucketsInput())
            print("Done")
        } catch let error as ServiceError {
            print("Service error of type {}: {}",
                error.typeName ?? "<unknown>",
                error.message ?? "<no message>"
            )
        } catch let error as CommonRunTimeError {
            switch error {
                case .crtError(let error):
                    print("Common RunTime error: (code \(error.code)) (\(error.name)): \(error.message)")
                    break
                default:
                    // This should never happen in current versions of the
                    // SDK, but is here to future-proof this error handler.
                    dump(error, name: "Unknown type of CommonRunTimeError")
            }
        } catch {
            print("Some other error")
        }
        // snippet-end:[errors.swift.service-error]

        print("\n\n")
        print("Calling GetObject with bucket and key names that likely don't")
        print("exist. If that's the case, this will result in an HTTP error 403")
        print("(Forbidden).")

        // snippet-start:[errors.swift.http-error]
        do {
            let client = try S3Client(region: "us-east-1")

            _ = try await client.getObject(input: GetObjectInput(
                bucket: "not-a-real-bucket",
                key: "not-a-real-key"
            ))
            print("Found a matching bucket but shouldn't have!")
        } catch let error as HTTPError {
            print("HTTP error; status code: \(error.httpResponse.statusCode.rawValue)")
        } catch {
            dump(error)
        }
        // snippet-end:[errors.swift.http-error]
    }
}