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
final class ListUsersTests: XCTestCase {
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
        SDKLoggingSystem.initialize(logLevel: .error)

        Task() {
            ListUsersTests.serviceHandler = await ServiceHandler()
            tdSem.signal()
        }
        tdSem.wait()
    }

    /// Called after **each** `testX()` function that follows, in order
    /// to clean up after each test is run.
    override func tearDown() async throws {
        let tdSem = TestWaiter(name: "Teardown")

        Task() {
            tdSem.signal()
        }
        tdSem.wait()
    }

    func testListUsers() async throws {
        let testNames = [
            "JamesJameson",
            "MariaMarino",
            "XavierHabanero",
            "SashaSchiff"
        ]

        do {
            var createdNames: [String] = []

            // Get a list of the pre-existing users.
            let previousUsers = try await ListUsersTests.serviceHandler!.listUsers()

            // Create the test users.
            for name in testNames {
                _ = try await ListUsersTests.serviceHandler!.createUser(name: name)
                createdNames.append(name)
            }

            // Ask AWS for a list of users.
            let users = try await ListUsersTests.serviceHandler!.listUsers()

            // Ensure that we got back the expected number of users: the
            // number we created plus the number that previously existed.
            XCTAssertTrue(users.count == testNames.count + previousUsers.count, "Incorrect number of users created. Should be \(testNames.count) but is instead \(users.count).")

            // For each user AWS reported, remove it from the list of names we
            // created. When done, there should be no entries left in the list
            // of names we created.
            for user in users {
                createdNames = createdNames.filter { $0 != user.name }
            }
            XCTAssertTrue(createdNames.count == 0, "Created user list doesn't match expected list.")

            // Remove the test users.
            for name in testNames {
                _ = try await ListUsersTests.serviceHandler!.deleteUser(name: name)
            }
        } catch {
            throw error
        }
    }
}
