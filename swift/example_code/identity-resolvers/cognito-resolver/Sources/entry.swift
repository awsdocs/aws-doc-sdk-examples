// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// A simple example that shows how to use the AWS SDK for Swift to
/// authenticate using Amazon Cognito.

// snippet-start:[swift.identity.cognito.imports]
import ArgumentParser
import AWSCognitoIdentity
import AWSIAM
import AWSS3
import AWSSDKIdentity
import Foundation
import SmithyIdentity
// snippet-end:[swift.identity.cognito.imports]

struct ExampleCommand: ParsableCommand {
    @Option(help: "AWS Region name")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "cognito-resolver",
        abstract: """
        Demonstrates how to use a Cognito credential identity resolver with the
        AWS SDK for Swift.
        """,
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    func runAsync() async throws {
        let example = try Example(region: region)

        try await example.run()
    }
}

/// The program's asynchronous entry point.
@main
struct Main {
    /// The function that serves as the main asynchronous entry point for the
    /// example. It parses the command line using the Swift Argument Parser,
    /// then calls the `runAsync()` function to run the example itself.
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
