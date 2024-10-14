// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// A simple example that shows how to use the AWS SDK for Swift to
/// authenticate using an AWS IAM role ARN.

// snippet-start:[swift.static-resolver.imports]
import ArgumentParser
import AWSClientRuntime
import AWSS3
import AWSSDKIdentity
import AWSSTS
import Foundation
import SmithyIdentity
// snippet-end:[swift.static-resolver.imports]

struct ExampleCommand: ParsableCommand {
    @Option(help: "AWS access key ID")
    var accessKey: String
    @Option(help: "AWS secret access key")
    var secretKey: String
    @Option(help: "Session token")
    var sessionToken: String? = nil
    @Argument(help: "ARN of the role to assume")
    var roleArn: String

    static var configuration = CommandConfiguration(
        commandName: "static-resolver",
        abstract: """
        Authenticate using the access key, secret access key, and role provided.
        Then list the available buckets.
        """,
        discussion: """
        This program uses the specified credentials when assuming the specified
        role, then uses the credentials returned by the role to list the user's
        buckets. This shows a couple of ways to use a
        StaticAWSCredentialIdentityResolver object.
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[swift.static-resolver.command.runasync]
    func runAsync() async throws {
        // Authenticate using the command line inputs.
        var identityResolver: StaticAWSCredentialIdentityResolver? = nil
        /*
        do {
            identityResolver = try getIdentityResolver(accessKey: accessKey, 
                        secretKey: secretKey, sessionToken: sessionToken)
        } catch {
            print("ERROR: Unable to get identity resolver in runAsync:",
                    dump(error))
            throw error
        }
        */

        // Assume the role.

        do {
            // snippet-start: [swift.static-resolver.use-role-credentials]
            let credentials = try await assumeRole(identityResolver: identityResolver,
                                        roleArn: roleArn)
            do {
                identityResolver = try getIdentityResolver(
                    accessKey: credentials.accessKey, 
                    secretKey: credentials.secret,
                    sessionToken: credentials.sessionToken
                )
            } catch {
                print("ERROR: Unable to authenticate using provided options:",
                        dump(error))
                throw error
            }
            // snippet-end: [swift.static-resolver.use-role-credentials]
        } catch {
            print("ERROR: Error assuming role in runAsync:", dump(error))
            throw AssumeRoleExampleError.assumeRoleFailed
        }

        // Use the credential identity resolver to access AWS S3.

        do {
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
    // snippet-end:[swift.static-resolver.command.runasync]
}

enum AssumeRoleExampleError: Error {
    case assumeRoleFailed
    case incompleteCredentials
    case missingCredentials

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

// snippet-start:[swift.static-resolver.assumeRole-function]
func assumeRole(identityResolver: (any AWSCredentialIdentityResolver)?,
                roleArn: String) async throws -> AWSCredentialIdentity {
    let stsConfiguration = try await STSClient.STSClientConfiguration(
        awsCredentialIdentityResolver: identityResolver
    )
    let stsClient = STSClient(config: stsConfiguration)

    let input = AssumeRoleInput(
        roleArn: roleArn,
        roleSessionName: "Static-Resolver-Example"
    )

    // Assume the role and return the assigned credentials.

    do {
        let output = try await stsClient.assumeRole(input: input)

        guard let credentials = output.credentials else {
            throw AssumeRoleExampleError.missingCredentials
        }

        guard let accessKey = credentials.accessKeyId,
              let secretKey = credentials.secretAccessKey,
              let sessionToken = credentials.sessionToken else {
            throw AssumeRoleExampleError.incompleteCredentials
        }

        // Return an `AWSCredentialIdentity` object with the temporary
        // credentials.

        let awsCredentials = AWSCredentialIdentity(
            accessKey: accessKey,
            secret: secretKey,
            sessionToken: sessionToken
        )
        return awsCredentials
    }
}
// snippet-end:[swift.static-resolver.assumeRole-function]

// snippet-start:[s3.swift.intro.getbucketnames]
// Return an array containing the names of all available buckets.
//
// - Returns: An array of strings listing the buckets.
func getBucketNames(identityResolver: (any AWSCredentialIdentityResolver)?)
                    async throws -> [String] {
    do {
        // Get an S3Client with which to access Amazon S3.
        // snippet-start:[s3.swift.intro.client-init]
        let configuration = try await S3Client.S3ClientConfiguration(
            awsCredentialIdentityResolver: identityResolver
        )
        //   configuration.region = "us-east-2" // Uncomment this to set the region programmatically.
        let client = S3Client(config: configuration)
        // snippet-end:[s3.swift.intro.client-init]

        // snippet-start:[s3.swift.intro.listbuckets]
        // Use "Paginated" to get all the buckets.
        // This lets the SDK handle the 'continuationToken' in "ListBucketsOutput".
        let pages = client.listBucketsPaginated(
            input: ListBucketsInput( maxBuckets: 10)
        )
        // snippet-end:[s3.swift.intro.listbuckets]

        // Get the bucket names.
        var bucketNames: [String] = []

        do {
            for try await page in pages {
                guard let buckets = page.buckets else {
                    print("Error: no buckets returned.")
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

/// Create a credential identity resolver using access key and secret access
/// key.
///
/// - Parameters:
///   - accessKey: A string containing the AWS access key ID.
///   - secretKey: A string containing the AWS secret access key.
///   - sessionToken: An optional string containing the session token.
/// - Throws: Re-throws errors from AWSSDKIdentity.
/// - Returns: A `StaticAWSCredentialIdentityResolver` that can be used when
///   configuring service clients.
func getIdentityResolver(accessKey: String, secretKey: String,
                         sessionToken: String?)
            throws -> StaticAWSCredentialIdentityResolver {
    let credentials = AWSCredentialIdentity(
        accessKey: accessKey,
        secret: secretKey,
        //expiration: cognitoCredentials.expiration,
        sessionToken: sessionToken
    )

    return try StaticAWSCredentialIdentityResolver(credentials)
}

// snippet-end:[s3.swift.intro.getbucketnames]

// snippet-start:[s3.swift.intro.main]
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

// snippet-end:[s3.swift.intro.main]
