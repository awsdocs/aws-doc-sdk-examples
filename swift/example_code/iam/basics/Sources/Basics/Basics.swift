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
// The example shows how IAM and the AWS Security Token Service work together
// to authorize use of AWS services.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.basics.example]
// snippet-start:[iam.swift.basics.main.imports]
import Foundation
import ArgumentParser
import AWSIAM
import AWSSTS
import AWSS3
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.basics.main.imports]

@testable import ServiceHandler

/// The command line arguments and options available for this example command.
// snippet-start:[iam.swift.basics.command]
struct ExampleCommand: ParsableCommand {
    @Option(help: "The AWS Region to run AWS API calls in.")
    var awsRegion = "us-east-2"

    @Option(
        help: ArgumentHelp("The level of logging for the Swift SDK to perform."),
        completion: .list([
            "critical",
            "debug",
            "error",
            "info",
            "notice",
            "trace",
            "warning"
        ])
    )
    var logLevel: String = "error"

    static var configuration = CommandConfiguration(
        commandName: "basics",
        abstract: "Demonstrates a series of IAM actions using the AWS SDK for Swift.",
        discussion: """
        This example uses a variety of AWS Identity and Access Management (IAM)
        and ATS Security Token Service functions to demonstrate how to create a
        user and set up a permission profile using policies and a role, then uses
        that role to access another service.
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.basics.command.runasync]
    func runAsync() async throws {
        SDKLoggingSystem.initialize(logLevel: .error)
        let iamHandler = await ServiceHandlerIAM()
        let stsHandler = await ServiceHandlerSTS(region: awsRegion)
        let s3Handler = await ServiceHandlerS3(region: awsRegion)
        let userName = String.uniqueName()
        let roleName = String.uniqueName()
        let rolePolicyName = String.uniqueName()

        // In this example, every AWS SDK for Swift function call that creates
        // or changes data on the server is paired with a `defer` block that
        // undoes that action. This may not be the best approach for your
        // real-world use, so consider how to handle errors properly for your
        // use case.
        //
        // The `defer` blocks work well for this example because we simply
        // want to avoid leaving bits of sample data on the server if when the
        // example exits, whether it exits normally or fails for any reason.

        do {
            let user: IAMClientTypes.User
            let role: IAMClientTypes.Role
            let rolePolicy: IAMClientTypes.Policy
            let accessKey: IAMClientTypes.AccessKey

            // Create the test user and create an access key for
            // authentication.
            
            print("Creating a new example user...")
            user = try await iamHandler.createUser(name: userName)
            defer {
                Task {
                    do {
                        print("DELETING USER")
                        //_ = try await iamHandler.deleteUser(user: user)
                    } catch {
                        print("*** Error: Unable to delete the user \(userName)")
                    }
                }
            }

            // Create an access key for the new user.

            print("Creating an access key for the example user...")
            accessKey = try await iamHandler.createAccessKey(userName: userName)
            defer {
                Task {
                    do {
                        print("DELETING ACCESS KEY")
                        _ = try await iamHandler.deleteAccessKey(key: accessKey)
                    } catch {
                        print("*** Error: Unable to delete the access key")
                    }
                }
            }

            // Pause a few seconds to give IAM time to let the new user
            // propagate.

            await waitFor(seconds: 10, message: "Pausing a few seconds to let the new user propagate")

            // Create a new role and give the user permission to assume it.

            guard let userARN = user.arn else {
                print("*** Invalid user ARN.")
                return
            }

            print("Creating a new role to give the user permission to assume roles...")
            role = try await iamHandler.createRole(
                name: roleName,
                policyDocument: """
                {
                    "Version": "2012-10-17",
                    "Statement": [{
                        "Effect": "Allow",
                        "Principal": {"AWS": "\(userARN)"},
                        "Action": "sts:AssumeRole"
                    }]
                }
                """
            )
            defer {
                Task {
                    do {
                        print("DELETING ROLE")
                        _ = try await iamHandler.deleteRole(role: role)
                    } catch {
                        print("*** Error: Unable to delete the role \(roleName)")
                    }
                }
            }

            // Create a new policy that allows the AWS S3 ListAllMyBuckets
            // action.

            print("Creating a role policy that gives the user permission to list S3 buckets...")
            rolePolicy = try await iamHandler.createPolicy(
                name: rolePolicyName,
                policyDocument: """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Action": "s3:ListAllMyBuckets",
                            "Resource": "arn:aws:s3:::*"
                        }
                    ]
                }
                """
            )
            defer {
                Task {
                    do {
                        print("DELETING ROLE POLICY")
                        _ = try await iamHandler.deletePolicy(policy: rolePolicy)
                    } catch {
                        print("*** Error: Unable to delete the role policy \(rolePolicyName)")
                    }
                }
            }

            // Attach the policy to the role so that assuming the role grants
            // the needed permissions.

            print("Attaching the role policy to the role...")
            _ = try await iamHandler.attachRolePolicy(policy: rolePolicy, role: role)
            defer {
                Task {
                    do {
                        print("DETACHING ROLE POLICY!")
                        _ = try await iamHandler.detachRolePolicy(policy: rolePolicy, role: role)
                    } catch {
                        print("*** Error: Unable to delete the user \(userName)")
                    }
                }
            }

            // Give the changes time to propagate.

            await waitFor(seconds: 10, message: "Pausing a few seconds to allow user and role changes to propagate")

            // Try to list the buckets without first getting permission to do
            // so.

            guard   let accessKeyId = accessKey.accessKeyId,
                    let secretAccessKey = accessKey.secretAccessKey else {
                        throw ServiceHandlerError.authError
            }

            print("Attempting to list buckets without first assuming the role that\ngrants permission to do so...")
            do {
                try await listBucketsWithCredentials(
                    accessKeyId: accessKeyId,
                    secretAccessKey: secretAccessKey
                )
                print("*** Oh no! ListBuckets call succeeded without authentication!")
                throw ServiceHandlerError.authError
            } catch {
                print("--- As expected, the ListBuckets call failed because the user\ndoesn't yet have permission.\n")
            }

            // Assume the role to get permission to list the buckets.

            print("Assuming the role to get credentials with ListBuckets permissions...")
            let credentials = try await stsHandler.assumeRole(
                role: role,
                sessionName: "listing-buckets"
            )

            guard   let roleAccessKey = credentials.accessKeyId,
                    let roleSecretAccessKey = credentials.secretAccessKey,
                    let roleSessionToken = credentials.sessionToken else {
                        throw ServiceHandlerError.authError
            }

            // List the buckets. This time, it should succeed.

            print("Attempting to list the S3 buckets using the role credentials...")
            let buckets = try await listBucketsWithCredentials(
                accessKeyId: roleAccessKey,
                secretAccessKey: roleSecretAccessKey,
                sessionToken: roleSessionToken
            )

            print("Found \(buckets.count) bucket(s)")
            for bucket in buckets {
                print("  \(bucket.name)")
            }

            print("--- Successfully got the bucket list. Example complete!")
            print("EXITING runAsync() WITH BUCKET LIST")

        } catch {
            print("*** ERROR ***")
            throw error
        }

        func listBucketsWithCredentials(accessKeyId: String, 
                    secretAccessKey: String, sessionToken: String? = nil)
                    async throws -> [S3ClientTypes.Bucket] {
            do {
                try await iamHandler.setCredentials(accessKeyId: accessKeyId,
                            secretAccessKey: secretAccessKey,
                            sessionToken: sessionToken)
                try await stsHandler.setCredentials(accessKeyId: accessKeyId,
                            secretAccessKey: secretAccessKey,
                            sessionToken: sessionToken)
                try await s3Handler.setCredentials(accessKeyId: accessKeyId,
                            secretAccessKey: secretAccessKey,
                            sessionToken: sessionToken)

                // Get a list of the buckets.

                return try await s3Handler.listBuckets()
            } catch {
                throw error
            }
        }
    }
    // snippet-end:[iam.swift.basics.command.runasync]

    /// Display a message and wait for a few seconds to pass.
    func waitFor(seconds: Double, message: String? = nil) async {
        if message != nil {
            print("\n*** \(message!) ***") 
        }
        Thread.sleep(forTimeInterval: seconds)
        print("\n")
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
            print("BACK FROM runAsync()")
        } catch {
            ExampleCommand.exit(withError: error)
        }
        print("EXITING main()")
    }    
}
// snippet-end:[iam.swift.basics.main]
// snippet-end:[iam.swift.basics.example]