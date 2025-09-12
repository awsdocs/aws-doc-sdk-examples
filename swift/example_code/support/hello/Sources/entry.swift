// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// snippet-start:[swift.support.hello]
// An example that shows how to use the AWS SDK for Swift to perform a simple
// operation using AWS Support.
//

import ArgumentParser
import AWSClientRuntime
import Foundation

// snippet-start:[swift.support.import]
import AWSSupport
// snippet-end:[swift.support.import]

struct ExampleCommand: ParsableCommand {
    @Option(help: "The AWS Region to run AWS API calls in.")
    var awsRegion = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "hello-support",
        abstract: """
        Demonstrates a simple operation using Amazon Support.
        """,
        discussion: """
        An example showing how to make a call to Amazon Support using the AWS
        SDK for Swift.         
        """
    )

    /// Return an array of the user's services.
    ///
    /// - Parameter supportClient: The `SupportClient` to use when calling
    ///   `describeServices()`.
    ///
    /// - Returns: An array of services.
    func getSupportServices(supportClient: SupportClient) async -> [SupportClientTypes.Service] {
        do {
            let output = try await supportClient.describeServices(
                input: DescribeServicesInput()
            )

            guard let services = output.services else {
                return []
            }

            return services
        } catch let error as AWSServiceError {
            // SubscriptionRequiredException isn't a modeled error, so we
            // have to catch AWSServiceError and then look at its errorCode to
            // see if it's SubscriptionRequiredException.
            if error.errorCode == "SubscriptionRequiredException" {
                print("*** You need a subscription to use AWS Support.")
                return []
            } else {
                print("*** An unknown error occurred getting support information.")
                return []
            }
        } catch {
            print("*** Error getting service information: \(error.localizedDescription)")
            return []
        }
    }

    /// Called by ``main()`` to run the bulk of the example.
    func runAsync() async throws {
        let supportConfig = try await SupportClient.SupportClientConfiguration(region: awsRegion)
        let supportClient = SupportClient(config: supportConfig)

        let services = await getSupportServices(supportClient: supportClient)

        print("Found \(services.count) security services")
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
// snippet-end:[swift.support.hello]
