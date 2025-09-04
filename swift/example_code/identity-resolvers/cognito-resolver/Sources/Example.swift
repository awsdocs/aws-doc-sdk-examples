// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[swift.identity.cognito.imports]
import AWSCognitoIdentity
import AWSSDKIdentity
import AWSSTS
// snippet-end:[swift.identity.cognito.imports]
import AWSIAM
import AWSS3
import Foundation

/// Contains the data and code for the main body of the example.
class Example {
    let region: String

    var cognitoIdentityClient: CognitoIdentityClient!
    var iamClient: IAMClient!

    let identityPoolName: String
    var identityPoolID: String!
    var roleName: String

    let managedPolicyName: String
    var managedPolicyArn: String?

    /// Initialize the example.
    ///
    /// - Parameter region: The AWS Region to operate in.
    /// 
    /// - Throws: Any AWS errors thrown by IAM or Cognito.
    /// 
    /// ^ Note: IAM must always use `us-east-1`, so it doesn't use the value
    /// of the `region` parameter.
    init(region: String) throws {
        self.region = region

        self.identityPoolName = "cognito-resolver-example-\(UUID().uuidString.split(separator: "-").first!.lowercased())"
        cognitoIdentityClient = try CognitoIdentityClient(region: region)

        self.roleName = "cognito-unauth-\(identityPoolName)"
        iamClient = try IAMClient(region: "us-east-1")

        self.managedPolicyName = "cognito-policy-\(identityPoolName)"
    }

    /// The body of the example.
    /// 
    /// - Throws: Errors from IAM, STS, or Cognito.
    func run() async throws {
        // Create an identity pool to use for this example.

        print("Creating a Cognito identity pool named \(identityPoolName)...")
        identityPoolID = try await cognitoIdentityClient.createIdentityPool(
            input: CreateIdentityPoolInput(
                allowUnauthenticatedIdentities: true,
                identityPoolName: identityPoolName
            )
        ).identityPoolId

        // Create an IAM role for unauthenticated users.

        let trustPolicy = """
        {
            "Version": "2012-10-17",
            "Statement": [{
                "Effect": "Allow",
                "Principal": {"Federated": "cognito-identity.amazonaws.com"},
                "Action": "sts:AssumeRoleWithWebIdentity",
                "Condition": {
                    "StringEquals": {"cognito-identity.amazonaws.com:aud": "\(identityPoolID!)"},
                    "ForAnyValue:StringLike": {"cognito-identity.amazonaws.com:amr": "unauthenticated"}
                }
            }]
        }
        """

        print("Creating an IAM role named \(roleName)...")
        let createRoleInput = CreateRoleInput(
            assumeRolePolicyDocument: trustPolicy,
            roleName: roleName
        )
        let createRoleOutput = try await iamClient.createRole(input: createRoleInput)

        guard let role = createRoleOutput.role else {
            print("*** No role returned by CreateRole!")
            await cleanup()
            return
        }

        // Wait for the role to be available.

        print("Waiting for the role to be available...")
        try await Task.sleep(nanoseconds: 10_000_000_000)   // Wait 10 seconds

        // Assign the role to the identity pool.

        print("Setting the identity pool's roles...")
        _ = try await cognitoIdentityClient.setIdentityPoolRoles(
            input: SetIdentityPoolRolesInput(
                identityPoolId: identityPoolID,
                roles: ["unauthenticated": role.arn!]
            )
        )

        //======================================================================
        // Resolve an identity using the Cognito credential identity resolver
        // with the AWS STS function getCallerIdentity(input:). This is done
        // by configuring the STS client to use the Cognito credentials
        // resolver.
        //======================================================================

        // snippet-start:[swift.identity.cognito.resolve]
        let cognitoCredentialResolver = try CognitoAWSCredentialIdentityResolver(
            identityPoolId: identityPoolID,
            identityPoolRegion: region
        )

        let cognitoSTSConfig = try await STSClient.STSClientConfiguration(
            awsCredentialIdentityResolver: cognitoCredentialResolver,
            region: "us-east-1"
        )
        let cognitoSTSClient = STSClient(config: cognitoSTSConfig)

        let output = try await cognitoSTSClient.getCallerIdentity(
            input: GetCallerIdentityInput()
        )

        print("Authenticated with AWS using Cognito!")
        print("           ARN: \(output.arn ?? "<unknown>")")
        print("    Account ID: \(output.account ?? "<unknown>")")
        print("       User ID: \(output.userId ?? "<unknown>")")
        // snippet-end:[swift.identity.cognito.resolve]

        //======================================================================
        // Add a managed policy to the role to allow access to the AWS S3
        // function ListBuckets.
        //======================================================================

        print("Creating a managed policy to allow listing S3 buckets...")
        let createPolicyOutput = try await iamClient.createPolicy(
            input: CreatePolicyInput(
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
                """,
                policyName: managedPolicyName
            )
        )

        guard let managedPolicy = createPolicyOutput.policy else {
            print("No policy returned by CreatePolicy!")
            await cleanup()
            return
        }

        managedPolicyArn = managedPolicy.arn

        print("Attaching the policy to the IAM role...")
        _ = try await iamClient.attachRolePolicy(
            input: AttachRolePolicyInput(
                policyArn: managedPolicy.arn,
                roleName: roleName
            )
        )

        // Wait for the policy to attach.

        print("Waiting for the policy to attach to the role...")
        try await Task.sleep(nanoseconds: 10_000_000_000)   // Wait 10 seconds

        //======================================================================
        // This is where you can do tasks using the returned AWS credentials.
        // In this example, we list S3 buckets.
        //======================================================================

        let s3Config = try await S3Client.S3ClientConfiguration(
            awsCredentialIdentityResolver: cognitoCredentialResolver,
            region: region
        )
        let s3Client = S3Client(config: s3Config)

        let listBucketsOutput = try await s3Client.listBuckets(
            input: ListBucketsInput()
        )
        guard let buckets = listBucketsOutput.buckets else {
            print("No buckets returned by S3!")
            await cleanup()
            return
        }

        print("Found \(buckets.count) S3 buckets:")
        for bucket in buckets {
            print("    \(bucket.name ?? "<unnamed>")")
        }
        
        //======================================================================
        // Clean up before exiting.
        //======================================================================

        await cleanup()
    }

    /// Clean up by deleting AWS assets created by the example. Ignores
    /// errors since this is just simple cleanup work.
    func cleanup() async {
        print("Deleting the identity pool...")
        _ = try? await cognitoIdentityClient.deleteIdentityPool(
            input: DeleteIdentityPoolInput(identityPoolId: identityPoolID)
        )

        print("Deleting the policy...")
        if managedPolicyArn != nil {
            _ = try? await iamClient.deletePolicy(
                input: DeletePolicyInput(policyArn: managedPolicyArn)
            )
        }

        print ("Deleting the IAM role...")
        _ = try? await iamClient.deleteRole(
            input: DeleteRoleInput(roleName: roleName)
        )
    }
}