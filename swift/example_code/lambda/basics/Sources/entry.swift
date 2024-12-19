// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//

// snippet-start:[swift.transcribe-streaming.all]
/// An example that demonstrates how to watch an transcribe event stream to
/// transcribe audio from a file to the console.

// snippet-start:[swift.lambda-basics.imports]
import ArgumentParser
import AWSClientRuntime
import AWSIAM
import AWSLambda
import Foundation
// snippet-end:[swift.lambda-basics.imports]

let exampleName = "SwiftLambdaRoleExample"

/// The ARN of the standard IAM policy for execution of Lambda functions.
let policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"

struct ExampleCommand: ParsableCommand {
    // -MARK: Command arguments
    @Option(help: "Name of the IAM Role to use for the Lambda functions")
    var role = exampleName
    @Option(help: "Name of the Amazon S3 Region to use (default: us-east-1)")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "lambda-basics",
        abstract: """
        This example demonstrates several common operations using AWS Lambda.
        """,
        discussion: """
        """
    )

    func doesRoleExist(iamClient: IAMClient, roleName: String) async -> Bool {
        do {
            let roleOutput = try await iamClient.getRole(
                input: GetRoleInput(
                    roleName: roleName
                )
            )

            if roleOutput.role != nil {
                return true
            }
        } catch {
            return false
        }

        return true
    }

    func basics() async throws {
        let iamClient = try await IAMClient(
            config: IAMClient.IAMClientConfiguration(region: region)
        )
        let lambdaClient = try await LambdaClient(
            config: LambdaClient.LambdaClientConfiguration(region: region)
        )

        if (await doesRoleExist(iamClient: iamClient, roleName: role)) {
            print("The role \(role) exists")
            throw ExampleError.roleAlreadyExists
        }
        print("The role \(role) doesn't exist.")
    }
}
// -MARK: - Entry point

/// The program's asynchronous entry point.
@main
struct Main {
    static func main() async {
        let args = Array(CommandLine.arguments.dropFirst())

        do {
            let command = try ExampleCommand.parse(args)
            try await command.basics()
        } catch {
            ExampleCommand.exit(withError: error)
        }
    }    
}
