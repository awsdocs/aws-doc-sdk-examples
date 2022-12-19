/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSIAM
import AWSS3
import AWSSTS
import AWSClientRuntime
import ClientRuntime
import SwiftUtilities

@testable import ServiceHandler

/// Perform tests on the sample program. Call Amazon service functions
/// using the global `ListUsersTests.serviceHandler` property. Also, manage
/// the demo cleanup handler object using the global
/// `ListUsersTests.demoCleanup` property.
final class BasicsTests: XCTestCase {
    static var iamHandler: ServiceHandlerIAM? = nil
    static var stsHandler: ServiceHandlerSTS? = nil
    static var s3Handler: ServiceHandlerS3? = nil
    static let region = "us-east-2"

    /// Class-wide setup function for the test case, which is run *once*
    /// before any tests are run.
    ///
    /// This function sets up the following:
    ///
    ///     Configures the AWS SDK log system to only log errors.
    ///     Initializes the service handler, which is used to call
    ///     Amazon Identity and Access Management (IAM) functions.
    ///     Initializes the demo cleanup handler, which is used to
    ///     track the names of the files and buckets created by the tests
    ///     in order to remove them after testing is complete.
    override class func setUp() {
        let tdSem = TestWaiter(name: "Setup")
        super.setUp()

        SDKLoggingSystem.initialize(logLevel: .error)

        Task() {
            self.iamHandler = await ServiceHandlerIAM()
            self.stsHandler = await ServiceHandlerSTS(region: self.region)
            self.s3Handler = await ServiceHandlerS3(region: self.region)
            tdSem.signal()
        }
        tdSem.wait()
    }

    func testCreateAndDeleteUser() async throws {
        let name = String.uniqueName()

        do {
            let user = try await BasicsTests.iamHandler!.createUser(name: name)

            guard   let userID = user.userId,
                    let _ = user.userName else {
                XCTFail("Did not create a valid user")
                return
            }

            let getID = try await BasicsTests.iamHandler!.getUserID(name: name)

            XCTAssertTrue(userID == getID, "Created user's ID (\(userID)) doesn't match retrieved user's ID (\(getID))")

            // Delete the created user.
            _ = try await BasicsTests.iamHandler!.deleteUser(user: user)

            do {
                _ = try await BasicsTests.iamHandler!.getUserID(name: name)
                XCTFail("User still exists after being deleted.")
            } catch {
                // This is a success condition -- the getUserID() function is
                // expected to throw here.
            }
        } catch {
            throw error
        }
    }

    func testCreateAndDeleteRole() async throws {
        let name = String.uniqueName()

        do {
            //let user = try await BasicsTests.serviceHandler!.createUser(name: name)
            let user = try await BasicsTests.iamHandler!.getUser()

            guard let userARN = user.arn else {
                throw ServiceHandlerError.noSuchUser 
            }

            // Policy document for the new role.
            let policyDocument = """
            {
                "Version": "2012-10-17",
                "Statement": [{
                    "Effect": "Allow",
                    "Principal": {"AWS": "\(userARN)"},
                    "Action": "sts:AssumeRole"
                }]
            }
            """

            let role = try await BasicsTests.iamHandler!.createRole(name: name, policyDocument: policyDocument)
            let getID = try await BasicsTests.iamHandler!.getRoleID(name: name)

            guard let createdID = role.roleId else {
                XCTFail("Created role has no valid ID or failed to create role")
                return
            }

            XCTAssertTrue(createdID == getID, "Created role's ID (\(createdID)) doesn't match retrieved role ID (\(getID))")

            // Delete the created user.
            _ = try await BasicsTests.iamHandler!.deleteRole(role: role)
        } catch {
            throw error
        }
    }

    func testCreateAndDeleteAccessKey() async throws {
        do {
            let user = try await BasicsTests.iamHandler!.getUser()

            guard let name = user.userName else {
                XCTFail("Unable to get the AWS user.")
                return
            }
            
            let accessKey = try await BasicsTests.iamHandler!.createAccessKey(userName: name)

            guard let _ = accessKey.accessKeyId else {
                XCTFail("Unable to get the new access key's ID.")
                return
            }

            let account = try await BasicsTests.stsHandler!.getAccessKeyAccountNumber(key: accessKey)
            XCTAssertTrue(Int(account) != nil, "Invalid account number returned for the generated access key.")

            try await BasicsTests.iamHandler!.deleteAccessKey(key: accessKey)
            //try await BasicsTests.iamHandler!.deleteUser(user: user)
        } catch {
            throw error
        }
    }

    func testCreateAndDeletePolicy() async throws {
        let policyName = String.uniqueName()

        let policyDocument = """
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

        do {
            let policy = try await BasicsTests.iamHandler!.createPolicy(
                name: policyName,
                policyDocument: policyDocument
            )

            guard let policyARN = policy.arn else {
                XCTFail("Invalid policy ARN in created policy.")
                return
            }

            let getID = try await BasicsTests.iamHandler!.getPolicyID(arn: policyARN)
            XCTAssertEqual(getID, policy.policyId, "The returned policy ID and the retrieved ID do not match.")

            try await BasicsTests.iamHandler!.deletePolicy(policy: policy)
        } catch {
            throw error
        }
    }

    func testAttachAndDetachRolePolicy() async throws {
        let roleName = String.uniqueName()
        let rolePolicyName = String.uniqueName()
        let userPolicyName = String.uniqueName()
        let testUserName = String.uniqueName()

        print("""
        
        *********************************************************
        *********************************************************
        *********************************************************

        """
        )

        do {
            // Get the current user so we can build a policy document.
    
            let user = try await BasicsTests.iamHandler!.createUser(name: testUserName)

            // Create an access key for the user.

            let accessKey = try await BasicsTests.iamHandler!.createAccessKey(userName: testUserName)

            // Wait for a few moments for the new user to propagate.

            await waitFor(seconds: 10)

            // Create the role using the user's ARN so we can specify the user
            // as the principal in the role's inline policy. This policy
            // permits the user to use the STS action `AssumeRole`.

            guard let userARN = user.arn else {
                XCTFail("Invalid user ARN.")
                return
            }

            let role = try await BasicsTests.iamHandler!.createRole(
                name: roleName,
                policyDocument: """
                {
                    "Version": "2012-10-17",
                    "Statement": [{
                        "Effect": "Allow",
                        "Principal": {"AWS": "\(userARN)"},
                        "Action": [
                            "sts:AssumeRole"
                        ]
                    }]
                }
                """
            )

            print("Role \(roleName) created.")

            guard let roleARN = role.arn else {
                XCTFail("Invalid role ARN.")
                return
            }

            // Wait 10 seconds to let the changes propagate out.

            await waitFor(seconds: 10)

            // Add the policy to the role.

            let rolePolicyDocument = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                    "Action": [
                        "iam:AttachRolePolicy",
                        "iam:GetRolePolicy",
                        "iam:DeleteRolePolicy",
                        "iam:DetachRolePolicy",
                        "iam:CreatePolicy",
                        "iam:CreateRole",
                        "iam:DeletePolicy",
                        "iam:DeleteRole",
                        "iam:GetPolicy",
                        "iam:GetUser"
                    ],
                    "Effect": "Allow",
                    "Resource": "arn:aws:iam:::*"
                    },
                    {
                    "Action": [
                        "iam:GetRolePolicy"
                    ],
                    "Effect": "Allow",
                    "Resource": "\(roleARN)"
                    }
                ]
            }
            """
            let rolePolicy = try await BasicsTests.iamHandler!.createPolicy(
                name: rolePolicyName,
                policyDocument: rolePolicyDocument
            )
            print("Role policy \(rolePolicyName) created.")
            try await BasicsTests.iamHandler!.attachRolePolicy(policy: rolePolicy, role: role)

            // Create an inline user policy that lets it assume the new role.

            let userPolicyDocument = """
            {
                "Version": "2012-10-17",
                "Statement": [{
                    "Effect": "Allow",
                    "Action": [
                        "sts:AssumeRole"
                    ],
                    "Resource": "\(roleARN)"
                }]
            }
            """

            try await BasicsTests.iamHandler!.putUserPolicy(
                policyDocument: userPolicyDocument,
                policyName: userPolicyName,
                user: user
            )

            // END OF FUNCTION "SETUP" IN PYTHON EXAMPLE

            // Use the credentials provided by the temporary user's access key.

            guard   let accessKeyId = accessKey.accessKeyId,
                    let secretAccessKey = accessKey.secretAccessKey else {
                        throw ServiceHandlerError.authError
            }

            try await BasicsTests.stsHandler!.setCredentials(
                accessKeyId: accessKeyId,
                secretAccessKey: secretAccessKey
            )

            // Use those credentials to assume the role we created.
            
            let credentials = try await BasicsTests.stsHandler!.assumeRole(
                role: role,
                sessionName: "test-role-policy"
            )

            guard   let roleAccessKey = credentials.accessKeyId,
                    let roleSecretAccessKey = credentials.secretAccessKey,
                    let roleSessionToken = credentials.sessionToken else {
                        XCTFail("Invalid credentials returned by assumeRole() function.")
                        throw ServiceHandlerError.authError
            }

            print("Setting credentials; access key is \(roleAccessKey)")

            // Use the new credentials for IAM operations.

            try await BasicsTests.iamHandler!.setCredentials(
                accessKeyId: roleAccessKey,
                secretAccessKey: roleSecretAccessKey,
                sessionToken: roleSessionToken
            )
            defer {
                Task {
                    do {
                        try await BasicsTests.iamHandler!.resetCredentials()
                    } catch {
                        throw error
                    }
                }
            }

            // Retrieve the role policy to confirm that it's attached.

            print("Getting the policy document we just attached to the role...")
            let getPolicyDocument = try await BasicsTests.iamHandler!.getRolePolicyDocument(
                policyName: rolePolicyName,
                roleName: roleName
            )
            XCTAssertEqual(getPolicyDocument, rolePolicyDocument, "Retrieved role policy document does not match the one created by the test.")

            // Detach the role policy

            print("Detaching the policy from the role...")
            try await BasicsTests.iamHandler!.detachRolePolicy(policy: rolePolicy, role: role)

            // Try getting the policy document again. Now it should fail.

            print("Trying to get the policy document for the role to be sure it's detached...")
            do {
                _ = try await BasicsTests.iamHandler!.getRolePolicyDocument(
                    policyName: rolePolicyName,
                    roleName: roleName
                )
                XCTFail("Getting a deleted policy document succeeded but should have failed.")
            } catch {
                // This is a success condition. Getting a detached policy
                // document should fail.
                print("Deleted policy not available -- as expected!")
            }

            print("Cleaning up...")

            // Restore original credentials.

            try await BasicsTests.iamHandler!.resetCredentials()
            try await BasicsTests.stsHandler!.resetCredentials()
            try await BasicsTests.s3Handler!.resetCredentials()

            // Delete the role.

            try await BasicsTests.iamHandler!.deleteRole(role: role)

            // Delete the access key.

            try await BasicsTests.iamHandler!.deleteAccessKey(key: accessKey)

            // Delete the user.

            try await BasicsTests.iamHandler!.deleteUser(user: user)

        } catch {
            try await BasicsTests.iamHandler!.resetCredentials()
            try await BasicsTests.stsHandler!.resetCredentials()
            try await BasicsTests.s3Handler!.resetCredentials()
            throw error
        }

        print("""
        
        *********************************************************
        *********************************************************
        *********************************************************

        """
        )
    }

    /// Display a message and wait for a few seconds to pass.
    func waitFor(seconds: Double, message: String? = nil) async {
        if message != nil {
            print("\n*** \(message!) ***") 
        }
        Thread.sleep(forTimeInterval: seconds)
        print("\n")
    }
}
