//
// Swift Example: Basics
//
// An example demonstrating the use of multiple AWS Identity and Access
// Management (IAM) functions to:
//
// 1. Create a user with no permissions.
// 2. Create a role with a policy granting the s3:ListAllMyBuckets permission.
// 3. Grant the created user permission to assume the role.
// 4. Create an S3Client object as the new user and try to list the buckets.
//    This should fail since the role hasn't been assumed yet.
// 5. Get temporarily credentials for the user by assuming the role.
// 6. Once again, list the buckets, this time with the temporary credentials.
//    This should succeed.
// 7. Delete all resources created by this example.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.basics.example]
// snippet-start:[iam.swift.basics.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
import AWSIAM
import AWSSTS
import AWSS3
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.basics.main.imports]

/// The command line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.basics.command]
struct ExampleCommand: ParsableCommand {
    @Option(help: "The AWS Region to run AWS API calls in.")
    var awsRegion: String = "us-east-2"

    static var configuration = CommandConfiguration(
        commandName: "basics",
        abstract: "Demonstrates a series of IAM actions using the AWS SDK for Swift.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.basics.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()
        let userName = String.uniqueName()
        let roleName = String.uniqueName()
        let policyName = String.uniqueName()

        do {
            let user: IAMClientTypes.User
            let role: IAMClientTypes.Role
            let policy: IAMClientTypes.Policy
            let accessKey: IAMClientTypes.AccessKey

            // Create the test user and create an access key for
            // authentication.
            
            print("Creating new user named \(userName)...")
            user = try await serviceHandler.createUser(name: userName)
            defer {
                Task {
                    do {
                        _ = try await serviceHandler.deleteUser(user: user)
                    } catch {
                        print("*** Error: Unable to delete the user \(userName)")
                    }
                }
            }

            // Pause a few seconds to let user percolate

            print("*** Pausing a few seconds to be sure the user is ready ***")
            Thread.sleep(forTimeInterval: 10)

            print("Creating access key for user...")
            accessKey = try await serviceHandler.createAccessKey(userName: userName)
            defer {
                Task {
                    do {
                        _ = try await serviceHandler.deleteAccessKey(key: accessKey)
                    } catch {
                        print("*** Error: Unable to delete the access key")
                    }
                }
            }

            print("Starting the test without access privileges...")

            guard   let accessKeyId = accessKey.accessKeyId,
                    let secretAccessKey = accessKey.secretAccessKey else {
                        throw ServiceHandlerError.authError
            }
            try await tryListBucketsWithoutPermission(accessKeyId: accessKeyId,
                    secretAccessKey: secretAccessKey)
            
            print("Back from test 1")

        } catch {
            print("*** ERROR ***")
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.command.runasync]

    func tryListBucketsWithoutPermission(accessKeyId: String, 
                secretAccessKey: String, sessionToken: String? = nil) async throws {

        // Use the given access key ID, secret access key, and session token
        // to generate a static credentials provider suitable for use when
        // initializing an AWS S3 client.
        let credentialsProvider = try AWSCredentialsProvider.fromStatic(
            AWSCredentialsProviderStaticConfig(
                accessKey: accessKeyId,
                secret: secretAccessKey,
                sessionToken: sessionToken
            )
        )

        // Create an AWS S3 configuration specifying the credentials provider
        // we just created, then use it to create a new `S3Client` that will
        // use the corresponding permissions.
        let s3Config = try await S3Client.S3ClientConfiguration(
            credentialsProvider: credentialsProvider,
            region: awsRegion
        )
        let s3Client = try S3Client(config: s3Config)

        // Now try to list the available buckets. This should fail.

        do {
            _ = try await s3Client.listBuckets(input: ListBucketsInput())
            print("*** ListBuckets call succeeded without authentication!")
        } catch {
            print("*** Unauthorized ListBuckets attempt failed as expected. Error:")
            print(error)
        }
    }
}
// snippet-end:[iam.swift.basics.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.basics.main]
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
// snippet-end:[iam.swift.basics.main]
// snippet-end:[iam.swift.basics.example]