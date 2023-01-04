//
// Swift example: Basics
//
// An example demonstrating the use of multiple AWS Identity and Access
// Management (IAM) functions to:
//
// 1. Create a user with no permissions.
// 2. Create access keys for the new user.
// 3. Create an S3Client with the new user's access keys and try to list its
//    buckets. This should fail because the user has no permission to do so.
// 4. Create a managed policy allowing users to assume roles and list buckets.
// 5. Create a role with an inline policy allowing users to assume the role,
//    then attach the managed policy to the new role.
// 6. Create a user policy allowing the user to change roles.
// 7. Assume the role so the user has its permissions, then try listing
//    buckets again. This time, it will succeed.
// 8. Delete the resources created by this example.
//
// The example shows how IAM and the AWS Security Token Service (AWS STS) work
// together to authorize use of AWS services.
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
        This example IAM and AWS STS functions to demonstrate how to create a
        user and set up a permission profile using policies and a role. Then,
        the role is used to access another service.
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.basics.command.runasync]
    func runAsync() async throws {
        SDKLoggingSystem.initialize(logLevel: .error)

        // Create handlers for the AWS services to use.

        let iamHandler = await ServiceHandlerIAM()
        let stsHandler = await ServiceHandlerSTS(region: awsRegion)
        let s3Handler = await ServiceHandlerS3(region: awsRegion)

        // Create unique names for the user, role, and policy to create.
        let userName = String.uniqueName(withPrefix: "basics-user", maxDigits: 8)
        let roleName = String.uniqueName(withPrefix: "basics-role", maxDigits: 8)
        let userPolicyName = String.uniqueName(withPrefix: "basics-userpolicy", maxDigits: 8)
        let managedPolicyName = String.uniqueName(withPrefix: "basics-policy", maxDigits: 8)

        // Constants that will contain the AWS objects to create. Declaring
        // them here makes sure they're available in the scope of all the
        // blocks below.

        let user: IAMClientTypes.User
        let role: IAMClientTypes.Role
        let managedPolicy: IAMClientTypes.Policy
        let accessKey: IAMClientTypes.AccessKey

        //=====================================================================
        // 1. Create a user for this example.
        //=====================================================================

        do {
            print("Creating a new user named \"\(userName)\"...")
            user = try await iamHandler.createUser(name: userName)
        } catch {
            print("*** Error creating the user.")
            throw error
        }

        // Abort if the returned user has no Amazon Resource Name (ARN).

        guard let userARN: String = user.arn else {
            print("*** Invalid user ARN returned by createUser().")
            return
        }

        //=====================================================================
        // 2. Create the access key and secret key for the newly created user.
        //=====================================================================

        do {
            print("Creating access keys for user \"\(userName)\"...")
            accessKey = try await iamHandler.createAccessKey(userName: userName)
        } catch {
            print("*** Unable to create the access key.")
            throw error
        }

        // Confirm that the returned access key is valid, then extract the access key
        // ID and secret access key. They will be needed later.

        guard   let accessKeyId: String = accessKey.accessKeyId,
                let secretAccessKey = accessKey.secretAccessKey else {
            print("*** Invalid access key returned by createAccessKey().")
            return
        }
        print("   ----> Created access key \"\(accessKeyId)\"")

        // Pause a few seconds to give IAM time for the new user to propagate.

        await waitFor(seconds: 10,
                message: "Pausing a few seconds so the new user can propagate.")

        //=====================================================================
        // 3. Attempt to list the S3 buckets without first getting permission
        //    to do so.
        //=====================================================================

        do {
            print("Attempting to list buckets without permission to do so.")
            
            // Use the user's access key to attempt to get the bucket list.

            try await s3Handler.setCredentials(accessKeyId: accessKeyId,
                    secretAccessKey: secretAccessKey)
            _ = try await s3Handler.listBuckets()

            print("*** Successfully listed S3 buckets without permission. Bad program!")
            return
        } catch {
            print("   ----> Failed as expected.")
        }

        //=====================================================================
        // 4. Create a policy allowing the user to assume roles and list
        //    buckets.
        //=====================================================================

        do {
            print("Creating a policy allowing users to list buckets and change roles.")

            managedPolicy = try await iamHandler.createPolicy(
                name: managedPolicyName,
                policyDocument: """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Action": "s3:ListAllMyBuckets",
                            "Resource": "arn:aws:s3:::*"
                        },
                        {
                            "Effect": "Allow",
                            "Action": "sts:AssumeRole",
                            "Resource": "arn:aws:sts:::*"
                        }
                    ]
                }
                """
            )

        } catch {
            print("*** Error creating the managed policy.")
            throw error
        }

        //=====================================================================
        // 5. Create a role with a trust policy allowing the user to assume
        //    the role, then attach the policy to it.
        //=====================================================================

        do {
            print("Creating the role allowing the user to assume the role.")

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

            try await iamHandler.attachRolePolicy(policy: managedPolicy, role: role)
        } catch {
            print("*** Error setting up the role to let the user assume the role.")
            throw error
        }

        guard let roleARN: String = role.arn else {
            print("*** Invalid role ARN returned by createRole().")
            return
        }

        await waitFor(seconds: 10,
                message: "Pausing a few seconds to allow user and role changes to propagate.")

        //=====================================================================
        // 6. Create a user policy letting the user change roles.
        //=====================================================================

        do {
            print("Creating a user policy to let the user change roles.")

            try await iamHandler.putUserPolicy(policyDocument: """
            {
                "Version": "2012-10-17",
                "Statement": [{
                    "Effect": "Allow",
                    "Action": "sts:AssumeRole",
                    "Resource": "\(roleARN)"
                }]
            }
            """, policyName: userPolicyName, user: user)
        } catch {
            print("*** Error creating the user policy.")
            throw error
        }

        await waitFor(seconds: 10, message: "Waiting for user policy to propagate.")

        //=====================================================================
        // 7. Assume the role so the user has the permissions associated with
        //    the role, then try listing buckets again. This should succeed.
        //=====================================================================

        do {
            // Make sure our calls to the AWS STS will use the user's
            // credentials.
            try await stsHandler.setCredentials(accessKeyId: accessKeyId,
                    secretAccessKey: secretAccessKey)

            // Assume the role you created and get the credentials that grant the
            // permissions offered by the role.
            let credentials = try await stsHandler.assumeRole(
                role: role, 
                sessionName: "listing-buckets"
            )

            // Extract the components of the credentials and confirm that all
            // three parts have values.
            guard let roleAccessKeyId = credentials.accessKeyId,
                  let roleSecretAccessKey = credentials.secretAccessKey,
                  let roleSessionToken = credentials.sessionToken else {
                    print("*** Incomplete access keys returned by AssumeRole.")
                    return
            }

            await waitFor(seconds: 10, message: "Waiting to ensure user has assumed the role.")

            // Set our Amazon S3 handler to use the credentials returned by
            // assumeRole().
            try await s3Handler.setCredentials(accessKeyId: roleAccessKeyId,
                    secretAccessKey: roleSecretAccessKey, sessionToken: roleSessionToken)
        } catch {
            print("*** Unable to assume the role and update Amazon S3 credentials.")
            throw error
        }

        // List the buckets. This should now succeed.

        do {
            print("Trying again to list the S3 buckets. This time, it should succeed.")
            _ = try await s3Handler.listBuckets()

            print("   ----> And it did succeed!")
        } catch {
            print("*** Failed to list the buckets even though it should have worked.")
            throw error
        }

        //=====================================================================
        // 8. Clean up by removing the policies, role, access key, and user you
        //    created.
        
        do {
            print("Deleting resources created by this example.")
            try await iamHandler.detachRolePolicy(policy: managedPolicy, role: role)
            try await iamHandler.deletePolicy(policy: managedPolicy)
            try await iamHandler.deleteRole(role: role)
            try await iamHandler.deleteAccessKey(user: user, key: accessKey)
            try await iamHandler.deleteUserPolicy(user: user, policyName: userPolicyName)
            try await iamHandler.deleteUser(user: user)
            print("   ----> All done!")
        } catch {
            print("*** Error cleaning up.")
            throw error
        }
    }
    // snippet-end:[iam.swift.basics.command.runasync]

    /// Display a message and wait for a few seconds to pass.
    /// 
    /// - Parameters:
    ///   - seconds: The number of seconds to wait as a `Double`.
    ///   - message: Optional `String` to display before the pause begins.
    func waitFor(seconds: Double, message: String? = nil) async {
        if message != nil {
            print("*** \(message!) ***") 
        }
        Thread.sleep(forTimeInterval: seconds)
    }
}
// snippet-end:[iam.swift.basics.command]

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