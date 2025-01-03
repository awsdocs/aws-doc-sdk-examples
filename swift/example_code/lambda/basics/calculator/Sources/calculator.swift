// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[lambda.swift.calculator.complete]
// snippet-start:[lambda.swift.calculator.imports]
import Foundation
import AWSLambdaRuntime
// snippet-end:[lambda.swift.calculator.imports]

// snippet-start:[lambda.swift.calculator.types]
// snippet-start:[lambda.swift.calculator.struct.request]
/// Represents the contents of the requests being received from the client.
/// This structure must be `Decodable` to indicate that its initializer
/// converts an external representation into this type.
struct Request: Decodable, Sendable {
    /// The action to perform.
    let action: String
    /// The first number to act upon.
    let x: Int
    /// The second number to act upon.
    let y: Int
}
// snippet-end:[lambda.swift.calculator.struct.request]

// snippet-start:[lambda.swift.calculator.actions]
/// A dictionary mapping operation names to closures that perform that
/// operation and return the result.
let actions = [
    "plus": { (x: Int, y: Int) -> Int in
        return x + y
    },
    "minus": { (x: Int, y: Int) -> Int in
        return x - y
    },
    "times": { (x: Int, y: Int) -> Int in
        return x * y
    },
    "divided-by": { (x: Int, y: Int) -> Int in
        return x / y
    }
]
// snippet-end:[lambda.swift.calculator.actions]

// snippet-start:[lambda.swift.calculator.struct.response]
/// The contents of the response sent back to the client. This must be
/// `Encodable`.
struct Response: Encodable, Sendable {
    /// The resulting value after performing the action.
    let answer: Int?
}
// snippet-end:[lambda.swift.calculator.struct.response]

// snippet-end:[lambda.swift.calculator.types]

// snippet-start:[lambda.swift.calculator.handler]
/// A Swift AWS Lambda Runtime `LambdaHandler` lets you both perform needed
/// initialization and handle AWS Lambda requests. There are other handler
/// protocols available for other use cases.
@main
struct CalculatorLambda: LambdaHandler {

    // snippet-start:[lambda.swift.calculator.handler.init]
    /// Initialize the AWS Lambda runtime.
    ///
    /// ^ The logger is a standard Swift logger. You can control the verbosity
    /// by setting the `LOG_LEVEL` environment variable.
    init(context: LambdaInitializationContext) async throws {
        // Display the `LOG_LEVEL` configuration for this process.
        context.logger.info(
            "Log Level env var : \(ProcessInfo.processInfo.environment["LOG_LEVEL"] ?? "info" )"
        )
    }
    // snippet-end:[lambda.swift.calculator.handler.init]

    // snippet-start:[lambda.swift.calculator.handler.handle]
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
        let action = event.action
        var answer: Int?
        var actionFunc: ((Int, Int) -> Int)?

        // Get the closure to run to perform the calculation.

        actionFunc = actions[action]

        guard let actionFunc else {
            context.logger.error("Unrecognized operation '\(action)\'")
            return Response(answer: nil)
        }

        // Perform the calculation and return the answer.

        answer = actionFunc(event.x, event.y)

        guard let answer else {
            context.logger.error("Error computing \(event.x) \(action) \(event.y)")
        }
        context.logger.info("\(event.x) \(action) \(event.y) = \(answer)")

        return Response(answer: answer)
    }
    // snippet-end:[lambda.swift.calculator.handler.handle]
}
// snippet-end:[lambda.swift.calculator.handler]
// snippet-end:[lambda.swift.calculator.complete]
