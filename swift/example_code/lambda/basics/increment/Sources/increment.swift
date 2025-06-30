// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[lambda.swift.increment.complete]
// snippet-start:[lambda.swift.increment.imports]
import Foundation
import AWSLambdaRuntime
// snippet-end:[lambda.swift.increment.imports]

// snippet-start:[lambda.swift.increment.types]
// snippet-start:[lambda.swift.increment.struct.request]
/// Represents the contents of the requests being received from the client.
/// This structure must be `Decodable` to indicate that its initializer
/// converts an external representation into this type.
struct Request: Decodable, Sendable {
    /// The action to perform.
    let action: String
    /// The number to act upon.
    let number: Int
}
// snippet-end:[lambda.swift.increment.struct.request]

// snippet-start:[lambda.swift.increment.struct.response]
/// The contents of the response sent back to the client. This must be
/// `Encodable`.
struct Response: Encodable, Sendable {
    /// The resulting value after performing the action.
    let answer: Int?
}
// snippet-end:[lambda.swift.increment.struct.response]

// snippet-end:[lambda.swift.increment.types]

// snippet-start:[lambda.swift.increment.runtime]
/// The Lambda function body.
///
/// - Parameters:
///   - event: The `Request` describing the request made by the
///     client.
///   - context: A `LambdaContext` describing the context in
///     which the lambda function is running.
///
/// - Returns: A `Response` object that will be encoded to JSON and sent
///   to the client by the Lambda runtime.
let incrementLambdaRuntime = LambdaRuntime {
        (event: Request, context: LambdaContext) -> Response in
    let action = event.action
    var answer: Int?

    if action != "increment" {
        context.logger.error("Unrecognized operation: \"\(action)\". The only supported action is \"increment\".")
    } else {
        answer = event.number + 1
        context.logger.info("The calculated answer is \(answer!).")
    }

    let response = Response(answer: answer)
    return response
}
// snippet-end:[lambda.swift.increment.runtime]

// Run the Lambda runtime code.

// snippet-start:[lambda.swift.increment.run]
try await incrementLambdaRuntime.run()
// snippet-end:[lambda.swift.increment.run]
// snippet-end:[lambda.swift.increment.complete]