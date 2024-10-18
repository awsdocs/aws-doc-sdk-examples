// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// A simple example that shows how to use the AWS SDK for Swift to
/// authenticate using optional static credentials and an AWS IAM role ARN.

// snippet-start:[swift.identity.sso.imports]
import ArgumentParser
import AWSClientRuntime
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
    // snippet-start:[swift.identity.sso.command.runasync]
    func runAsync() async throws {
        // If credentials are specified, create a credential identity
        // resolver that uses them to authenticate. This identity will be used
        // to ask for permission to use the specified role.

        print("Creating resolver...")
        do {
            let identityResolver = try SSOAWSCredentialIdentityResolver(
                profileName: profile,
                configFilePath: config,
                credentialsFilePath: credentials
            )
            dump(identityResolver, name: "Identity resolver:")

            // Use the credential identity resolver to access AWS S3.

            print("Listing bucket names...")
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
    // snippet-end:[swift.identity.sso.command.runasync]
}

/// An `Error` type used to return errors from the
/// `assumeRole(identityResolver: roleArn:)` function.
enum AssumeRoleExampleError: Error {
    /// An error indicating that the STS `AssumeRole` request failed.
    case assumeRoleFailed
    /// An error indicating that the returned credentials were missing
    /// required information.
    case incompleteCredentials
    /// An error indicating that no credentials were returned by `AssumeRole`.
    case missingCredentials

    /// Return a human-readable explanation of the error.
    var errorDescription: String? {
        switch self {
        case .assumeRoleFailed:
            return "Unable to assume the specified role."
        case .incompleteCredentials:
            return "AWS STS returned incomplete credentials."
        case .missingCredentials:
            return "AWS STS did not return any credentials for the specified role."
        }
    }
}

// snippet-start:[swift.identity.sso.assumeRole-function]
/// Assume the specified role. If any kind of credential identity resolver is
/// specified, that identity is adopted before assuming the role.
/// 
/// - Parameters:
///   - identityResolver: Any kind of `AWSCredentialIdentityResolver`. If
///     provided, this identity is adopted before attempting to assume the
///     specified role.
///   - roleArn: The ARN of the AWS role to assume.
///
/// - Throws: Re-throws STS errors. Also can throw any
///   `AssumeRoleExampleError`. 
/// - Returns: An `AWSCredentialIdentity` containing the temporary credentials
///   assigned.
func assumeRole(identityResolver: (any AWSCredentialIdentityResolver)?,
                roleArn: String) async throws -> AWSCredentialIdentity {
    print("ASSUMEROLE")
    let stsConfiguration = try await STSClient.STSClientConfiguration(
        awsCredentialIdentityResolver: identityResolver
    )
    let stsClient = STSClient(config: stsConfiguration)

    // Assume the role and return the assigned credentials.

    // snippet-start: [swift.sts.sts.AssumeRole]
    let input = AssumeRoleInput(
        roleArn: roleArn,
        roleSessionName: "AssumeRole-Example"
    )

    let output = try await stsClient.assumeRole(input: input)

    guard let credentials = output.credentials else {
        throw AssumeRoleExampleError.missingCredentials
    }

    guard let accessKey = credentials.accessKeyId,
            let secretKey = credentials.secretAccessKey,
            let sessionToken = credentials.sessionToken else {
        throw AssumeRoleExampleError.incompleteCredentials
    }
    // snippet-end: [swift.sts.sts.AssumeRole]

    // Return an `AWSCredentialIdentity` object with the temporary
    // credentials.

    let awsCredentials = AWSCredentialIdentity(
        accessKey: accessKey,
        secret: secretKey,
        sessionToken: sessionToken
    )
    return awsCredentials
}
// snippet-end:[swift.identity.sso.assumeRole-function]

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
        // Get an S3Client with which to access Amazon S3.
        // snippet-start:[swift.identity.sso.use-resolver]
        let configuration = try await S3Client.S3ClientConfiguration(
            awsCredentialIdentityResolver: identityResolver
        )
        let client = S3Client(config: configuration)

        // Use "Paginated" to get all the buckets. This lets the SDK handle
        // the 'continuationToken' in "ListBucketsOutput".
        let pages = client.listBucketsPaginated(
            input: ListBucketsInput( maxBuckets: 10)
        )
        // snippet-end:[swift.identity.sso.use-resolver]

        // Get the bucket names.
        var bucketNames: [String] = []

        do {
            for try await page in pages {
                guard let buckets = page.buckets else {
                    print("Error: page is empty.")
                    continue
                }

                for bucket in buckets {
                    bucketNames.append(bucket.name ?? "<unknown>")
                }
            }

            return bucketNames
        } catch {
            print("ERROR: listBuckets:", dump(error))
            throw error
        }
    }
}

/// The program's asynchronous entry point.
@main
struct Main {
    static func main() async {
        print("STARTING")
        let args = Array(CommandLine.arguments.dropFirst())

        do {
            await SDKLoggingSystem().initialize(logLevel: .trace)
            SDKDefaultIO.shared.setLogLevel(level: .trace)
            let command = try ExampleCommand.parse(args)
            print("Calling runAsync")
            //try await command.runAsync()
        } catch {
            ExampleCommand.exit(withError: error)
        }
    }    
}
