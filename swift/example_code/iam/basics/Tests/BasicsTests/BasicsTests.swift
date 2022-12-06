/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSIAM
import AWSClientRuntime
import ClientRuntime
import SwiftUtilities

@testable import ServiceHandler

/// Perform tests on the sample program. Call Amazon service functions
/// using the global `ListUsersTests.serviceHandler` property. Also, manage
/// the demo cleanup handler object using the global
/// `ListUsersTests.demoCleanup` property.
final class BasicsTests: XCTestCase {
    static var serviceHandler: ServiceHandler? = nil

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

        Task() {
            BasicsTests.serviceHandler = await ServiceHandler(logLevel: "error")
            tdSem.signal()
        }
        tdSem.wait()
    }

    func testCreateAndDeleteUser() async throws {
        let name = String.uniqueName()

        do {
            let user = try await BasicsTests.serviceHandler!.createUser(name: name)

            guard   let userID = user.userId,
                    let userName = user.userName else {
                XCTFail("Did not create a valid user")
                return
            }

            let getID = try await BasicsTests.serviceHandler!.getUserID(name: userName)

            XCTAssertTrue(userID == getID, "Created user's ID (\(userID)) doesn't match retrieved user's ID (\(getID))")

            // Delete the created user.
            _ = try await BasicsTests.serviceHandler!.deleteUser(user: user)

            do {
                _ = try await BasicsTests.serviceHandler!.getUserID(name: userName)
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
            let user = try await BasicsTests.serviceHandler!.getUser()

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

            let role = try await BasicsTests.serviceHandler!.createRole(name: name, policyDocument: policyDocument)
            let getID = try await BasicsTests.serviceHandler!.getRoleID(name: name)

            guard let createdID = role.roleId else {
                XCTFail("Created role has no valid ID or failed to create role")
                return
            }

            XCTAssertTrue(createdID == getID, "Created role's ID (\(createdID)) doesn't match retrieved role ID (\(getID))")

            // Delete the created user.
            _ = try await BasicsTests.serviceHandler!.deleteRole(role: role)
        } catch {
            throw error
        }
    }

    func testCreateAndDeleteAccessKey() async throws {
        do {
            let user = try await BasicsTests.serviceHandler!.getUser()

            guard let name = user.userName else {
                XCTFail("Unable to get the AWS user.")
                return
            }
            
            let accessKey = try await BasicsTests.serviceHandler!.createAccessKey(userName: name)

            guard let _ = accessKey.accessKeyId else {
                XCTFail("Unable to get the new access key's ID.")
                return
            }

            let account = try await BasicsTests.serviceHandler!.getAccessKeyAccountNumber(key: accessKey)
            XCTAssertTrue(Int(account) != nil, "Invalid account number returned for the generated access key.")

            try await BasicsTests.serviceHandler!.deleteAccessKey(key: accessKey)
            //try await BasicsTests.serviceHandler!.deleteUser(user: user)
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
            let policy = try await BasicsTests.serviceHandler!.createPolicy(
                name: policyName,
                policyDocument: policyDocument
            )

            guard let policyARN = policy.arn else {
                XCTFail("Invalid policy ARN in created policy.")
                return
            }

            let getID = try await BasicsTests.serviceHandler!.getPolicyID(arn: policyARN)
            XCTAssertEqual(getID, policy.policyId, "The returned policy ID and the retrieved ID do not match.")

            try await BasicsTests.serviceHandler!.deletePolicy(policy: policy)
        } catch {
            throw error
        }
    }

    func testAttachAndDetachRolePolicy() async throws {
        let roleName = String.uniqueName()
        let policyName = String.uniqueName()
        let testUserName = String.uniqueName()

        print("""
        
        *********************************************************
        *********************************************************
        *********************************************************

        """
        )

        do {
            // Get the current user so we can build a policy document.
    
            let user = try await BasicsTests.serviceHandler!.createUser(name: testUserName)
            //let user = try await BasicsTests.serviceHandler!.getUser()

            // Create an access key for the user

            let accessKey = try await BasicsTests.serviceHandler!.createAccessKey(userName: testUserName)

            // Create the role.
            await waitFor(seconds: 10)

            let startUser = try await BasicsTests.serviceHandler!.getUser()
            guard let userARN = startUser.arn else {
                XCTFail("Invalid user ARN.")
                return
            }

            let role = try await BasicsTests.serviceHandler!.createRole(
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

            print("Role \(roleName) created.")

            // Create a policy.

            let policyDocument = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                    "Action": [
                        "iam:AttachRolePolicy",
                        "iam:CreatePolicy",
                        "iam:CreateRole",
                        "iam:DeletePolicy",
                        "iam:DeleteRole",
                        "iam:DeleteRolePolicy",
                        "iam:DetachRolePolicy",
                        "iam:GetPolicy",
                        "iam:GetRolePolicy",
                        "iam:GetUser"
                    ],
                    "Effect": "Allow",
                    "Resource": "arn:aws:iam:::*"
                    }
                ]
            }
            """
            let policy = try await BasicsTests.serviceHandler!.createPolicy(
                name: policyName,
                policyDocument: policyDocument
            )
            print("Policy \(policyName) created.")

            // Attach the policy to the role.

            try await BasicsTests.serviceHandler!.attachRolePolicy(policy: policy, role: role)

            await waitFor(seconds: 10)

            // Assume the role to get the permissions we need for the rest of
            // the test.
            
            let credentials = try await BasicsTests.serviceHandler!.assumeRole(
                role: role,
                sessionName: "test-role-policy"
            )

            guard   let roleAccessKey = credentials.accessKeyId,
                    let roleSecretAccessKey = credentials.secretAccessKey,
                    let roleSessionToken = credentials.sessionToken else {
                        throw ServiceHandlerError.authError
            }

            try await BasicsTests.serviceHandler!.setCredentials(
                accessKeyId: roleAccessKey,
                secretAccessKey: roleSecretAccessKey,
                sessionToken: roleSessionToken
            )
            defer {
                Task {
                    do {
                        //try await BasicsTests.serviceHandler!.resetCredentials()
                    } catch {
                        throw error
                    }
                }
            }

            // Retrieve the role policy to confirm that it's attached.

            print("Getting the policy document we just attached to the role...")
            let getPolicyDocument = try await BasicsTests.serviceHandler!.getRolePolicyDocument(
                policyName: policyName,
                roleName: roleName
            )
            XCTAssertEqual(getPolicyDocument, policyDocument, "Retrieved role policy document does not match the one created by the test.")

            // Detach the role policy

            print("Detaching the policy from the role...")
            try await BasicsTests.serviceHandler!.detachRolePolicy(policy: policy, role: role)

            // Try getting the policy document again. Now it should fail.

            print("Trying to get the policy document for the role to be sure it's detached...")
            do {
                _ = try await BasicsTests.serviceHandler!.getRolePolicyDocument(
                    policyName: policyName,
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

            try await BasicsTests.serviceHandler!.resetCredentials()

            // Delete the policy.

            try await BasicsTests.serviceHandler!.deletePolicy(policy: policy)

            // Delete the role.

            try await BasicsTests.serviceHandler!.deleteRole(role: role)

            // Delete the access key.

            try await BasicsTests.serviceHandler!.deleteAccessKey(key: accessKey)

            // Delete the user.

            try await BasicsTests.serviceHandler!.deleteUser(user: user)

        } catch {
            try await BasicsTests.serviceHandler!.resetCredentials()
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
