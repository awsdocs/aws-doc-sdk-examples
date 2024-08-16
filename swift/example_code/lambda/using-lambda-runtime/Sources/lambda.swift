// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[lambda.swift.function.complete]
// snippet-start:[lambda.swift.function.imports]
import Foundation
import AWSLambdaRuntime
import AWSS3

import protocol AWSClientRuntime.AWSServiceError
import enum Smithy.ByteStream
// snippet-end:[lambda.swift.function.imports]

// snippet-start:[lambda.swift.function.types]
// snippet-start:[lambda.swift.function.struct.request]
/// Represents the contents of the requests being received from the client.
/// This structure must be `Decodable` to indicate that its initializer
/// converts an external representation into this type.
struct Request: Decodable, Sendable {
    /// The request body.
    let body: String
}
// snippet-end:[lambda.swift.function.struct.request]

// snippet-start:[lambda.swift.function.struct.response]
/// The contents of the response sent back to the client. This must be
/// `Encodable`.
struct Response: Encodable, Sendable {
    /// The ID of the request this response corresponds to.
    let req_id: String
    /// The body of the response message.
    let body: String
}
// snippet-end:[lambda.swift.function.struct.response]

// snippet-start:[lambda.swift.function.errors]
/// The errors that the Lambda function can return.
enum S3ExampleLambdaErrors: Error {
    /// A required environment variable is missing. The missing variable is
    /// specified.
    case noEnvironmentVariable(String)
    /// The Amazon Simple Storage Service (S3) client couldn't be created.
    case noS3Client
}
// snippet-end:[lambda.swift.function.errors]
// snippet-end:[lambda.swift.function.types]

// snippet-start:[lambda.swift.function.handler]
/// A Swift AWS Lambda Runtime `LambdaHandler` lets you both perform needed
/// initialization and handle AWS Lambda requests. There are other handler
/// protocols available for other use cases.
@main
struct S3ExampleLambda: LambdaHandler {
    let s3Client: S3Client?

    // snippet-start:[lambda.swift.function.handler.init]
    /// Initialize the AWS Lambda runtime.
    ///
    /// ^ The logger is a standard Swift logger. You can control the verbosity
    /// by setting the `LOG_LEVEL` environment variable.
    init(context: LambdaInitializationContext) async throws {
        // Display the `LOG_LEVEL` configuration for this process.
        context.logger.info(
            "Log Level env var : \(ProcessInfo.processInfo.environment["LOG_LEVEL"] ?? "info" )"
        )

        // Initialize the Amazon S3 client. This single client is used for every
        // request.
        let currentRegion = ProcessInfo.processInfo.environment["AWS_REGION"] ?? "us-east-1"
        self.s3Client = try? S3Client(region: currentRegion)
    }
    // snippet-end:[lambda.swift.function.handler.init]

    // snippet-start:[lambda.swift.function.handler.putobject]
    /// Write the specified text into a given Amazon S3 bucket. The object's
    /// name is based on the current time.
    ///
    /// - Parameters:
    ///   - s3Client: The `S3Client` to use when sending the object to the
    ///     bucket.
    ///   - bucketName: The name of the Amazon S3 bucket to put the object
    ///     into.
    ///   - body: The string to write into the new object.
    ///
    /// - Returns: A string indicating the name of the file created in the AWS
    ///   S3 bucket.
    private func putObject(client: S3Client, 
                           bucketName: String,
                           body: String) async throws -> String {
        // Generate an almost certainly unique object name based on the current
        // timestamp.
        let objectName = "\(Int(Date().timeIntervalSince1970*1_000_000)).txt"

        // Create a Smithy `ByteStream` that represents the string to write into
        // the bucket.
        let inputStream = Smithy.ByteStream.data(body.data(using: .utf8))

        // Store the text into an object in the Amazon S3 bucket.
        let putObjectRequest = PutObjectInput(
            body: inputStream,
            bucket: bucketName,
            key: objectName
        )
        let _ = try await client.putObject(input: putObjectRequest)

        // Return the name of the file.
        return objectName
    }
    // snippet-end:[lambda.swift.function.handler.putobject]

    // snippet-start:[lambda.swift.function.handler.handle]
    /// The Lambda function's entry point. Called by the Lambda runtime.
    ///
    /// - Parameters:
    ///   - event: The `Request` describing the request made by the
    ///     client.
    ///   - context: A `LambdaContext` describing the context in
    ///     which the lambda function is running.
    ///
    /// - Returns: A `Response` object that will be encoded to JSON and sent
    ///   to the client by the Lambda runtime.
    func handle(_ event: Request, context: LambdaContext) async throws -> Response {
        // Get the bucket name from the environment.
        guard let bucketName = ProcessInfo.processInfo.environment["BUCKET_NAME"] else {
            throw S3ExampleLambdaErrors.noEnvironmentVariable("BUCKET_NAME")
        }

        // Make sure the `S3Client` is valid.
        guard let s3Client else {
            throw S3ExampleLambdaErrors.noS3Client
        }

        // Call the `putObject` function to store the object on Amazon S3.
        var responseMessage: String
        do {
            let filename = try await putObject(
                            client: s3Client,
                            bucketName: bucketName,
                            body: event.body)

            // Generate the response text.
            responseMessage = "The Lambda function has successfully stored your data in S3 with name \(filename)'"

            // Send the success notification to the logger.
            context.logger.info("Data successfully stored in S3.")
        } catch let error as AWSServiceError {
            // Generate the error message.
            responseMessage = "The Lambda function encountered an error and your data was not saved. Root cause: \(error.errorCode ?? "") - \(error.message ?? "")"

            // Send the error message to the logger.
            context.logger.error("Failed to upload data to Amazon S3.")
        }

        // Return the response message. The AWS Lambda runtime will send it to the
        // client.
        return Response(
            req_id: context.requestID,
            body: responseMessage)
    }
    // snippet-end:[lambda.swift.function.handler.handle]
}
// snippet-end:[lambda.swift.function.handler]
// snippet-end:[lambda.swift.function.complete]
