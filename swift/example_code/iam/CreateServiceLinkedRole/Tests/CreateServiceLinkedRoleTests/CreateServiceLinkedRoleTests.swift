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

/// Perform tests on the S3Basics program. Call Amazon S3 service functions
/// using the global `CreateServiceLinkedRoleTests.serviceHandler` property, and manage
/// the demo cleanup handler object using the global
/// `CreateServiceLinkedRoleTests.demoCleanup` property.
final class CreateServiceLinkedRoleTests: XCTestCase {
    static var serviceHandler: ServiceHandler? = nil

    /// Class-wide setup function for the test case, which is run *once*
    /// before any tests are run.
    ///
    /// This function sets up the following:
    ///
    ///     Configures AWS SDK log system to only log errors.
    ///     Initializes the service handler, which is used to call
    ///     Amazon S3 functions.
    ///     Initializes the demo cleanup handler, which is used to
    ///     track the names of the files and buckets created by the tests
    ///     in order to remove them after testing is complete.
    override class func setUp() {
        let tdSem = TestWaiter(name: "Setup")
        super.setUp()
        SDKLoggingSystem.initialize(logLevel: .error)

        Task() {
            CreateServiceLinkedRoleTests.serviceHandler = await ServiceHandler()
            tdSem.signal()
        }
        tdSem.wait()
    }

    /// Returns an alphanumeric string of random characters up to the given
    /// length.
    ///
    /// - Parameter length: Length of desired random string.
    ///
    /// - Returns: A random string of the given number of characters.
    func randomString(length: Int) -> String {
        let letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return String((0..<length).map{ _ in letters.randomElement()! })
    }

    func testCreateServiceLinkedRole() async throws {
        do {
            let suffix = self.randomString(length: 4)
            let createdRole = try await CreateServiceLinkedRoleTests.serviceHandler!.createServiceLinkedRole(
                                    service: "autoscaling.amazonaws.com",
                                    suffix: suffix,
                                    description: "Created to test the Swift example for CreateServiceLinkedRole"
            )
            guard let roleName: String = createdRole.roleName else {
                XCTFail("Returned role does not have a name, but should.")
                return
            }
            
            let getID = try await CreateServiceLinkedRoleTests.serviceHandler!.getRoleID(name: roleName)

            XCTAssertTrue(createdRole.roleId == getID, "Created role's ID (\(createdRole.roleId!)) doesn't match retrieved role ID (\(getID))")

            // Delete the created role.
            _ = try await CreateServiceLinkedRoleTests.serviceHandler!.deleteServiceLinkedRole(name: roleName)
        } catch {
            throw error
        }
    }
}
