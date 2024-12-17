// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// A simple example that shows how to use the AWS SDK for Swift to
/// authenticate using SSO credentials from AWS Identity Center.

// snippet-start:[swift.identity.sso.imports]
import ArgumentParser
import AWSS3
import AWSSDKIdentity
import AWSSTS
import Foundation
import SmithyIdentity
// snippet-end:[swift.identity.sso.imports]

struct ExampleCommand: ParsableCommand {
    @Option(help: "AWS profile name (default: 'default')")
    var profile: String? = nil
    @Option(help: "AWS configuration file path (default: '~/.aws/config')")
    var config: String? = nil
    @Option(help: "AWS credentials file path (default: '~/.aws/credentials')")
    var credentials: String? = nil

    static var configuration = CommandConfiguration(
        commandName: "sso-resolver",
        abstract: """
        Demonstrates how to use an SSO credential identity resolver with the
        AWS SDK for Swift.
        """,
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    func runAsync() async throws {
        do {
            // snippet-start:[swift.identity.sso.create-resolver]
            let identityResolver = try SSOAWSCredentialIdentityResolver(
                profileName: profile,
                configFilePath: config,
                credentialsFilePath: credentials
            )
            // snippet-end:[swift.identity.sso.create-resolver]

            // Call the function that fetches the Amazon S3 bucket names, then
            // output the names.

            let names = try await getBucketNames(identityResolver: identityResolver)

            print("Found \(names.count) buckets:")
            for name in names {
                print("  \(name)")
            }
        } catch {
            print("ERROR: Error getting bucket names in runAsync:", dump(error))
            throw error
        }
    }
}

/// Return an array containing the names of all available buckets using
/// the specified credential identity resolver to authenticate.
///
/// - Parameter identityResolver: Any type of `AWSCredentialIdentityResolver`,
///   used to authenticate and authorize the user for access to the bucket
///   names.
///
/// - Throws: Re-throws errors from `ListBucketsPaginated`.
///
/// - Returns: An array of strings listing the buckets.
func getBucketNames(identityResolver: (any AWSCredentialIdentityResolver)?)
                    async throws -> [String] {
    do {
        // snippet-start:[swift.identity.sso.use-resolver]
        // Get an S3Client with which to access Amazon S3.
        let configuration = try await S3Client.S3ClientConfiguration(
            awsCredentialIdentityResolver: identityResolver
        )
        let client = S3Client(config: configuration)

        // Use "Paginated" to get all the buckets. This lets the SDK handle
        // the 'continuationToken' in "ListBucketsOutput".
        let pages = client.listBucketsPaginated(
            input: ListBucketsInput(maxBuckets: 10)
        )
        // snippet-end:[swift.identity.sso.use-resolver]

        // Get the bucket names.
        var bucketNames: [String] = []

        do {
            for try await page in pages {
                guard let buckets = page.buckets else {
                    // For this example, if the bucket list reference for the
                    // page is `nil`, print an error and continue on with the
                    // next page.
                    print("ERROR: page is empty.")
                    continue
                }

                // Add the page's bucket names to the list.
                for bucket in buckets {
                    bucketNames.append(bucket.name ?? "<unknown>")
                }
            }

            return bucketNames
        } catch {
            throw error
        }
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
