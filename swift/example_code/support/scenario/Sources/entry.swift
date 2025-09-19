// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// snippet-start:[swift.support.scenario.entry]
// An example that shows how to use the AWS SDK for Swift to perform a series
// of operations using AWS Support
//
// NOTE: You must have a business class AWS account to use AWS Support
// features. See https://aws.amazon.com/premiumsupport/plans/.
//

import ArgumentParser
import AWSClientRuntime
import AWSSupport
import Foundation

struct ExampleCommand: ParsableCommand {
    @Option(help: "The AWS Region to run AWS API calls in.")
    var awsRegion = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "support-scenario",
        abstract: """
        Demonstrates various operations using Amazon Support.
        """,
        discussion: """
        """
    )

    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        let scenario = try await Scenario(region: awsRegion)
        try await scenario.run()
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
// snippet-end:[swift.support.scenario.entry]
