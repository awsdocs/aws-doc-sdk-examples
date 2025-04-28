// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// snippet-start:[swift.http-config]
// An example demonstrating how to customize the configuration of the HTTP
// client used by an Amazon Web Services (AWS) service client.

import ArgumentParser
import AWSClientRuntime
import ClientRuntime
import AWSS3
import Foundation
import SmithyHTTPAPI
import AwsCommonRuntimeKit

struct ExampleCommand: ParsableCommand {
    @Option(help: "Name of the Amazon Region to use")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "http-config",
        abstract: """
        Demonstrates how to configure the HTTP client used by an AWS service client.
        """,
        discussion: """
        """
    )

    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        //await SDKLoggingSystem().initialize(logLevel: .trace)
        //SDKDefaultIO.setLogLevel(level: .trace)
        // snippet-start:[swift.http-config.configure]
        let config = try await S3Client.S3ClientConfiguration(
            region: region,
            httpClientConfiguration: HttpClientConfiguration(
                connectTimeout: 2,
                socketTimeout: 5,
                defaultHeaders: Headers(
                    [
                        "X-My-Custom-Header": "CustomHeaderValue",
                        "X-Another-Custom-Header": "AnotherCustomValue"
                    ]
                )
            )
        )
        let s3Client = S3Client(config: config)
        // snippet-end:[swift.http-config.configure]

        print("*** Getting list of buckets ***")
        let output: ListBucketsOutput = try await s3Client.listBuckets(input: ListBucketsInput())

        print("*** Getting bucket list with very short timeout ***")
        
        // snippet-start: [swift.http-config.timeouts]
        do {
            let config = try await S3Client.S3ClientConfiguration(
                region: region,
                httpClientConfiguration: HttpClientConfiguration(
                    connectTimeout: 0.001,
                    socketTimeout: 0.005
                )
            )
            let s3Client = S3Client(config: config)
            let output: ListBucketsOutput = try await s3Client.listBuckets(input: ListBucketsInput())
        } catch CommonRunTimeError.crtError(let crtError) {
            print("*** An error occurred accessing the bucket list: \(crtError.message)")
        } catch {
            print("*** Unexpected error requesting bucket list.")
        }
        // snippet-end: [swift.http-config.timeouts]

    }
}

/// The program's asynchronous entry point.
@main
struct Main {
    static func main() async {
        let args = Array(CommandLine.arguments.dropFirst())

        do {
            let command = try ExampleCommand.parse(args)
            try await command.runAsync()
        } catch {
            ExampleCommand.exit(withError: error)
        }
    }    
}
// snippet-end:[swift.http-config]
