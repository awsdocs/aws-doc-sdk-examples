// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[lambda.swift.function.complete]
// snippet-start:[lambda.swift.function.imports]
import Foundation
import AWSLambdaRuntime
@preconcurrency import AWSS3

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
}
// snippet-end:[lambda.swift.function.errors]
// snippet-end:[lambda.swift.function.types]

let currentRegion = ProcessInfo.processInfo.environment["AWS_REGION"] ?? "us-east-1"
let s3Client = try S3Client(region: currentRegion)

// snippet-start:[lambda.swift.function.putobject]
/// Create a new object on Amazon S3 whose name is based on the current
/// timestamp, containing the text specified.
/// 
/// - Parameters: 
///   - body: The text to store in the new S3 object.
///   - bucketName: The name of the Amazon S3 bucket to put the new object
///     into.
/// 
/// - Throws: Errors from `PutObject`.
/// 
/// - Returns: The name of the new Amazon S3 object that contains the
///   specified body text.
func putObject(body: String, bucketName: String) async throws -> String {
    // Generate an almost certainly unique object name based on the current
    // timestamp.
    
    let objectName = "\(Int(Date().timeIntervalSince1970*1_000_000)).txt"

    // Create a Smithy `ByteStream` that represents the string to write into
    // the bucket.
    
    let inputStream = Smithy.ByteStream.data(body.data(using: .utf8))

    // Store the text into an object in the Amazon S3 bucket.

    _ = try await s3Client.putObject(
        input: PutObjectInput(
            body: inputStream,
            bucket: bucketName,
            key: objectName
        )
    )

    // Return the name of the file

    return objectName
}
// snippet-end:[lambda.swift.function.putobject]

// snippet-start:[lambda.swift.function.runtime]
let runtime = LambdaRuntime {
    (event: Request, context: LambdaContext) async throws -> Response in

    var responseMessage: String

    // Get the name of the bucket to write the new object into from the
    // environment variable `BUCKET_NAME`.
    guard let bucketName = ProcessInfo.processInfo.environment["BUCKET_NAME"] else {
        context.logger.error("Set the environment variable BUCKET_NAME to the name of the S3 bucket to write files to.")
        throw S3ExampleLambdaErrors.noEnvironmentVariable("BUCKET_NAME")
    }

    do {
        let filename = try await putObject(body: event.body, bucketName: bucketName)

        // Generate the response text and update the log.
        responseMessage = "The Lambda function has successfully stored your data in S3 with name '\(filename)'"
        context.logger.info("Data successfully stored in S3.")
    } catch let error as AWSServiceError {
        // Generate the error message and update the log.
        responseMessage = "The Lambda function encountered an error and your data was not saved. Root cause: \(error.errorCode ?? "") - \(error.message ?? "")"
        context.logger.error("Failed to upload data to Amazon S3.")
    }

    return Response(req_id: context.requestID, body: responseMessage)
}
// snippet-end:[lambda.swift.function.runtime]

// Start up the runtime.

// snippet-start:[lambda.swift.function.start]
try await runtime.run()
// snippet-end:[lambda.swift.function.start]
// snippet-end:[lambda.swift.function.complete]
