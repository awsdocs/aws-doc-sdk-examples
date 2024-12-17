// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// A simple example that shows how to use the AWS SDK for Swift to
/// authenticate using optional static credentials and an AWS IAM role ARN.

// snippet-start:[swift.sts.AssumeRole.imports]
import ArgumentParser
import AWSClientRuntime
import AWSS3
import AWSSDKIdentity
import AWSSTS
import Foundation
import SmithyIdentity
// snippet-end:[swift.sts.AssumeRole.imports]

struct ExampleCommand: ParsableCommand {
    @Option(help: "AWS access key ID")
    var accessKey: String?
    @Option(help: "AWS secret access key")
    var secretKey: String?
    @Option(help: "Session token")
    var sessionToken: String?
    @Option(help: "Amazon S3 Region to use")
    var region: String = "us-east-1"
    @Argument(help: "ARN of the role to assume")
    var roleArn: String

    static var configuration = CommandConfiguration(
        commandName: "AssumeRole",
        abstract: """
        Authenticate using the specified role, optionally using specified
        access key, secret access key, and session token first.
        """,
        discussion: """
        This program uses the specified access key, secret access key, and
        optional session token, to request temporary credentials for the
        specified role Then it uses the credentials to list the user's
        Amazon S3 buckets. This shows a couple of ways to use a
        StaticAWSCredentialIdentityResolver object.
        """
    )

    /// Called by ``main()`` to run the main example code.
    // snippet-start:[swift.sts.AssumeRole.command.runasync]
    func runAsync() async throws {
        // If credentials are specified, create a credential identity
        // resolver that uses them to authenticate. This identity will be used
        // to ask for permission to use the specified role.

        var identityResolver: StaticAWSCredentialIdentityResolver? = nil
        
        if accessKey != nil && secretKey != nil {
            do {
                identityResolver = try getIdentityResolver(accessKey: accessKey, 
                            secretKey: secretKey, sessionToken: sessionToken)
            } catch {
                print("ERROR: Unable to get identity resolver in runAsync:",
                        dump(error))
                throw error
            }
        }

        // Assume the role using the credentials provided on the command line,
        // or using the default credentials if none were specified.

        do {
            // snippet-start: [swift.sts.AssumeRole]
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
            // snippet-end: [swift.sts.AssumeRole]
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
    // snippet-end:[swift.sts.AssumeRole.command.runasync]

    // snippet-start:[swift.sts.AssumeRole.assumeRole-function]
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
        let stsConfiguration = try await STSClient.STSClientConfiguration(
            awsCredentialIdentityResolver: identityResolver,
            region: region
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
    // snippet-end:[swift.sts.AssumeRole.assumeRole-function]

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
            // snippet-start:[swift.sts.AssumeRole.use-resolver]
            let s3Configuration = try await S3Client.S3ClientConfiguration(
                awsCredentialIdentityResolver: identityResolver,
                region: region
            )
            let client = S3Client(config: s3Configuration)

            // Use "Paginated" to get all the buckets. This lets the SDK handle
            // the 'continuationToken' in "ListBucketsOutput".
            let pages = client.listBucketsPaginated(
                input: ListBucketsInput( maxBuckets: 10)
            )
            // snippet-end:[swift.sts.AssumeRole.use-resolver]

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
    func getIdentityResolver(accessKey: String?, secretKey: String?,
                            sessionToken: String?)
                throws -> StaticAWSCredentialIdentityResolver? {
        
        if accessKey == nil || secretKey == nil {
            return nil
        }  

        guard let accessKey = accessKey,
            let secretKey = secretKey else {
            return nil
        }

        // snippet-start:[swift.sts.AssumeRole.create-static-resolver]
        let credentials = AWSCredentialIdentity(
            accessKey: accessKey,
            secret: secretKey,
            sessionToken: sessionToken
        )

        let identityResolver = try StaticAWSCredentialIdentityResolver(credentials)
        // snippet-end:[swift.sts.AssumeRole.create-static-resolver]
        return identityResolver
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
