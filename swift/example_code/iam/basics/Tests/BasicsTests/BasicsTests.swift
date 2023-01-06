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

/// Perform tests on the sample program. Call AWS service functions
/// using the global service handler properties.
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
    /// * Configures the AWS SDK log system to only log errors.
    /// * Initializes the service handler, which is used to call AWS
    ///   Identity and Access Management (IAM) functions.
    /// * Initializes the demo cleanup handler, which is used to track the
    ///   names of the files and buckets created by the tests in order to
    ///   remove them after testing is complete.
    override class func setUp() {
        let tdSem = TestWaiter(name: "Setup")
        super.setUp()

        SDKLoggingSystem.initialize(logLevel: .error)

        // A `Task` is used to allow us to call asynchronous setup functions
        // from within this synchronous function. A `TestWaiter` object is
        // used to wait until the setup task is complete before returning from
        // the `setUp()` function.

        Task() {
            self.iamHandler = await ServiceHandlerIAM()
            self.stsHandler = await ServiceHandlerSTS(region: self.region)
            self.s3Handler = await ServiceHandlerS3(region: self.region)
            tdSem.signal()
        }
        tdSem.wait()
    }

    /// Test creating and deleting a user.
    func testCreateAndDeleteUser() async throws {
        let name = String.uniqueName()

        do {
            let user = try await BasicsTests.iamHandler!.createUser(name: name)

            guard   let userID = user.userId,
                    let _ = user.userName else {
                XCTFail("Did not create a valid user")
                return
            }

            // Try to fetch the new user's ID from AWS.

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

    /// Test creating and deleting a role.
    func testCreateAndDeleteRole() async throws {
        let name = String.uniqueName()

        do {
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

            // Try creating the role and then fetching its ID from AWS.

            let role = try await BasicsTests.iamHandler!.createRole(name: name, policyDocument: policyDocument)
            let getID = try await BasicsTests.iamHandler!.getRoleID(name: name)

            // If the ID is nil, it's a failure.

            guard let createdID = role.roleId else {
                XCTFail("Created role has no valid ID or failed to create role")
                return
            }

            // If the returned ID doesn't match what we asked for, it's a
            // failure.

            XCTAssertEqual(createdID, getID, "Created role's ID (\(createdID)) doesn't match retrieved role ID (\(getID))")

            // Delete the created role.

            _ = try await BasicsTests.iamHandler!.deleteRole(role: role)
        } catch {
            throw error
        }
    }

    /// Test creating and deleting a user access key.
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
            XCTAssertNotNil(Int(account), "Invalid account number returned for the generated access key.")

            try await BasicsTests.iamHandler!.deleteAccessKey(key: accessKey)
            //try await BasicsTests.iamHandler!.deleteUser(user: user)
        } catch {
            throw error
        }
    }

    /// Test creating and deleting a policy.
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

    /// Test attaching and detaching a role policy.
    func testAttachAndDetachRolePolicy() async throws {
        typealias CleanupClosure = () async throws -> ()

        var cleanupClosures: [CleanupClosure] = []

        let userName: String = String.uniqueName(withPrefix: "basicstest-user", maxDigits: 8)
        let roleName = String.uniqueName(withPrefix: "basicstest-role", maxDigits: 8)
        let managedPolicyName = String.uniqueName(withPrefix: "basicstest-policy", maxDigits: 8)

        // Constants that will contain the AWS objects we will be creating.
        // Declaring them here to ensure they're available in the scope of all
        // of the blocks below.

        let user: IAMClientTypes.User
        let role: IAMClientTypes.Role
        let managedPolicy: IAMClientTypes.Policy
        let accessKey: IAMClientTypes.AccessKey

        do {
            // Create the user.

            user = try await BasicsTests.iamHandler!.createUser(name: userName)
            cleanupClosures.append({ try await BasicsTests.iamHandler!.deleteUser(user: user) })

            guard let userARN: String = user.arn else {
                throw ServiceHandlerError.invalidArn
            }

            // Wait a few seconds for the user to propagate.

            await waitFor(seconds: 10)

            // Create the access key and get its details.

            accessKey = try await BasicsTests.iamHandler!.createAccessKey(userName: userName)
            cleanupClosures.append({ try await BasicsTests.iamHandler!.deleteAccessKey(user: user, key: accessKey) })

            guard let _ = accessKey.accessKeyId,
                    let _ = accessKey.secretAccessKey else {
                throw ServiceHandlerError.keyError
            }

            // Create the managed role policy.

            managedPolicy = try await BasicsTests.iamHandler!.createPolicy(
                name: managedPolicyName,
                policyDocument: """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Action": "sts:AssumeRole",
                            "Resource": "arn:aws:sts:::*"
                        }
                    ]
                }
                """
            )
            cleanupClosures.append({ try await BasicsTests.iamHandler!.deletePolicy(policy: managedPolicy) })

            // Wait for the policy to propagate. 

            await waitFor(seconds: 10)

            // Create the role to which we'll attach the policy.

            role = try await BasicsTests.iamHandler!.createRole(
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
            cleanupClosures.append({ try await BasicsTests.iamHandler!.deleteRole(role: role)})

            // Attach the managed policy to the role.

            try await BasicsTests.iamHandler!.attachRolePolicy(policy: managedPolicy, role: role)

            // Check to be sure the policy is attached to the role.

            let attachedPolicies1: [IAMClientTypes.AttachedPolicy] = try await BasicsTests.iamHandler!.getAttachedPolicies(forRole: role)
            var policyFound = false
            for attachedPolicy in attachedPolicies1 {
                if attachedPolicy.policyArn == managedPolicy.arn &&
                        attachedPolicy.policyName == managedPolicy.policyName {
                    policyFound = true
                }
            }
            XCTAssertTrue(policyFound, "Attached policy was not found in the policy list.")

            // Detach the policy from the role.
            try await BasicsTests.iamHandler!.detachRolePolicy(policy: managedPolicy, role: role)

            // Check to be sure the policy is no longer attached to the role.

            let attachedPolicies2: [IAMClientTypes.AttachedPolicy] = try await BasicsTests.iamHandler!.getAttachedPolicies(forRole: role)
            policyFound = false
            for attachedPolicy in attachedPolicies2 {
                if attachedPolicy.policyArn == managedPolicy.arn &&
                        attachedPolicy.policyName == managedPolicy.policyName {
                    policyFound = true
                }
            }
            XCTAssertFalse(policyFound, "Attached policy was found after being deleted!")

            // Call the function to perform cleanup of the items created.

            try await performCleanup()
        } catch {
            // Clean up any items successfully created before the error
            // occurred.
            try await performCleanup()
            throw error
        }

        /// Clean up after the test by calling the closures stored in the
        /// `cleanupClosures` list in the opposite order in which they were
        /// added. The list is left empty when the cleanup is complete.    
        func performCleanup() async throws {
            while(cleanupClosures.count != 0) {
                try await cleanupClosures.removeLast()()
            }
        }

    }
    
    /// Display a message and wait for a few seconds to pass.
    /// - Parameters:
    ///   - seconds: The number of seconds to wait.
    ///   - message: An (optional) message to display before waiting. If not
    ///     specified, no message is displayed.
    func waitFor(seconds: Double, message: String? = nil) async {
        if message != nil {
            print("\n*** \(message!) ***") 
        }
        Thread.sleep(forTimeInterval: seconds)
        print("\n")
    }
}
