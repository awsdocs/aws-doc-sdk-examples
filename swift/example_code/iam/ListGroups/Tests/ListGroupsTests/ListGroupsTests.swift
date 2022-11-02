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
/// using the global `ListGroupsTests.serviceHandler` property. Also, manage
/// the demo cleanup handler object using the global
/// `ListGroupsTests.demoCleanup` property.
final class ListGroupsTests: XCTestCase {
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
            ListGroupsTests.serviceHandler = await ServiceHandler()
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

    /// Test the `listGroups()` function.
    func testListGroups() async throws {
        do {
            var createdGroups: [String] = []

            let previousGroups = try await ListGroupsTests.serviceHandler!.listGroups()

            for _ in 1...5 {
                let newName = String.uniqueName()
                _ = try await ListGroupsTests.serviceHandler!.createGroup(name: newName)
                createdGroups.append(newName)
            }

            // Get the list of groups including the new ones we just created.
            var groups = try await ListGroupsTests.serviceHandler!.listGroups()
            XCTAssertTrue(groups.count == createdGroups.count + previousGroups.count, "Incorrect number of groups created. Should be \(createdGroups.count + previousGroups.count) but is instead \(groups.count).")

            // Removed the previously existing groups from the retrieved group
            // list.
            for group in previousGroups {
                groups = groups.filter { $0 != group }
            }
            XCTAssertEqual(createdGroups.count, groups.count, "Created group list doesn't match expected list.")

            // Delete the groups created by the test.

            for group in createdGroups {
                _ = try await ListGroupsTests.serviceHandler!.deleteGroup(name: group)
            }
        } catch {
            throw error
        }
    }
}
