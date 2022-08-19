/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSS3
import AWSClientRuntime
import ClientRuntime
import SwiftUtilities

@testable import ServiceHandler

/// Perform tests on the S3Basics program. Call Amazon S3 service functions
/// using the global `DeleteObjectsTests.serviceHandler` property, and manage
/// the demo cleanup handler object using the global
/// `DeleteObjectsTests.demoCleanup` property.
final class DeleteObjectsTests: XCTestCase {
    static var serviceHandler: ServiceHandler? = nil
    static var demoCleanup: S3DemoCleanup? = nil

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
            DeleteObjectsTests.serviceHandler = await ServiceHandler()
            DeleteObjectsTests.demoCleanup = await S3DemoCleanup()
            tdSem.signal()
        }
        tdSem.wait()
    }

    /// Called after **each** `testX()` function that follows, in order
    /// to clean up after each test is run.
    override func tearDown() async throws {
        let tdSem = TestWaiter(name: "Teardown")

        Task() {
            await DeleteObjectsTests.demoCleanup?.cleanup()
            tdSem.signal()
        }
        tdSem.wait()
    }

    //*********** UTILITY FUNCTIONS *********************************

    /// Create an S3 bucket and add it to the list of buckets to be removed
    /// when the `demoCleanup.cleanup()` function is called.
    ///
    /// - Parameter name: The name of the bucket to create. If not specified
    ///   (or nil), a random bucket name is used.
    /// - Returns: The name of the created bucket.
    func createTestBucket(name: String? = nil) async throws -> String {
        let bucketName = name ?? String.uniqueName(withPrefix: "test")

        do {
            try await DeleteObjectsTests.serviceHandler?.createBucket(name: bucketName)
            try DeleteObjectsTests.demoCleanup?.addBucket(name: bucketName)
        } catch {
            throw error
        }
        return bucketName
    }

    /// Create a test file.
    ///
    /// - Parameters:
    ///   - bucket: The name of the bucket to create the file in.
    ///   - file: The name of the file to create. If `nil` or unspecified, a
    ///     random name is generated.
    ///   - paragraphs: The number of paragraphs to write into the file.
    ///     Default is 1.
    /// - Returns: An `S3DemoFileInfo` object describing the file and its
    ///   expected contents.
    func createTestFile(bucket: String, file: String? = nil, withParagraphs paragraphs: Int = 1)
            async throws -> S3DemoFileInfo {
        let fileName = file ?? String.uniqueName(withExtension: "txt")

        do {
            let contents = String.withLoremText(paragraphs: paragraphs).data(using: .utf8)!

            try await DeleteObjectsTests.serviceHandler?.createFile(bucket: bucket, key: fileName, withData: contents)
            let fileInfo = try DeleteObjectsTests.demoCleanup?.addFile(bucket: bucket, filename: fileName, contents: contents)
            return fileInfo!
        } catch {
            throw error
        }
    }

    /// Get a list of the files contained in a bucket.
    ///
    /// - Parameter bucket: The name of the bucket to get the file names from.
    /// - Returns: An array of strings, each identifying a single file in the
    ///   bucket.
     func listTestFiles(bucket: String) async throws -> [String] {
        do {
            let fileNames = try await DeleteObjectsTests.serviceHandler!.listBucketFiles(bucket: bucket)
            return fileNames
        } catch {
            throw error
        }
    }
 
    //*********** TEST FUNCTIONS ************************************

    func testDeleteObjects() async throws {
        do {
            // Create a test bucket and some files.

            let bucketName = try await createTestBucket()
            for _ in 1...10 {
                _ = try await createTestFile(bucket: bucketName)
            }

            let fileNames = try await listTestFiles(bucket: bucketName)

            // Delete the files.
            try await DeleteObjectsTests.serviceHandler?.deleteObjects(bucket: bucketName, keys: fileNames)
            // Remove the files from the demo cleanup handler's list.
            DeleteObjectsTests.demoCleanup?.reset(dropBuckets: false, dropFiles: true)
        } catch {
            throw error
        }
    }

    func testDeleteMissingObjects() async throws {
        do {
            let bucketName = try await createTestBucket()

            let fileNames = [
                "NoSuchFile.txt",
                "NothingToSeeHere.png"
            ]

            try await DeleteObjectsTests.serviceHandler?.deleteObjects(bucket: bucketName, keys: fileNames)
        } catch {
            XCTFail("An exception occurred trying to delete files that don't exist, but this should fail silently")
        }
    }

    func testBogusTest() async throws {
        _ = try await createTestBucket(name: ",35225")
    }
}
