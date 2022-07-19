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

/// Performs tests on the S3Basics program. Call Amazon S3 service functions
/// using the global `BasicsTests.serviceHandler` property, and manage the demo
/// cleanup handler object using the global `BasicsTests.demoCleanup` property.
final class BasicsTests: XCTestCase {
    static var serviceHandler: ServiceHandler? = nil
    static var demoCleanup: S3DemoCleanup? = nil

    /// Class-wide setup function for the test case, which is run *once*, before
    /// any tests are run.
    /// 
    /// This function sets up the following:
    ///
    ///     Configures AWS SDK log system to only log errors.
    ///     Instantiates the service handler, which is used to call
    ///     Amazon S3 functions.
    ///     Instantiates the demo cleanup handler, which is used to
    ///     track the names of the files and buckets created by the tests
    ///     in order to remove them once testing is complete.
    override class func setUp() {
        let tdSem = TestWaiter(name: "Setup")
        super.setUp()
        SDKLoggingSystem.initialize(logLevel: .error)

        Task() {
            BasicsTests.serviceHandler = await ServiceHandler()
            BasicsTests.demoCleanup = await S3DemoCleanup()
            tdSem.signal()
        }
        tdSem.wait()
    }

    /// Perform state setup prior to test. `setUp()` is called before
    /// calling each `testX()` function that follows.
    override func setUp() async {
    }

    /// Called after **each** `testX()` function that follows, in order
    /// to clean up after each test is run.
    override func tearDown() async throws {
        let tdSem = TestWaiter(name: "Teardown")

        Task() {
            await BasicsTests.demoCleanup?.cleanup()
            tdSem.signal()
        }
        tdSem.wait()
    }

    //*********** UTILITY FUNCTIONS *********************************

    /// Create an Amazon S3 bucket and add it to the list of buckets to be
    /// removed when the `demoCleanup.cleanup()` function is called.
    ///
    /// - Parameter name: The name of the bucket to create. If not specified (or
    ///   nil), a random bucket name is used.
    /// - Returns: The name of the created bucket.
    func createTestBucket(name: String? = nil) async throws -> String {
        let bucketName = name ?? String.uniqueName(withPrefix: "test")

        do {
            try await BasicsTests.serviceHandler?.createBucket(name: bucketName)
            try BasicsTests.demoCleanup?.addBucket(name: bucketName)
        } catch {
            throw error
        }
        return bucketName
    }

    /// Delete an Amazon S3 bucket and removes it from the list of test
    /// buckets.
    /// - Parameter name: The name of the bucket to delete
    func deleteTestBucket(name: String) async throws {
        do {
            try await BasicsTests.serviceHandler?.deleteBucket(name: name)
            try BasicsTests.demoCleanup?.removeBucket(name: name)
        } catch {
            throw error
        }
    }

    /// Create a test file.
    ///
    /// - Parameters:
    ///   - bucket: The name of the bucket to create the file in.
    ///   - file: The name of the file to create; if `nil` or unspecified, a
    ///     random name is generated.
    ///   - paragraphs: The number of paragraphs to write into the file; default
    ///     is 1.
    /// - Returns: An `S3DemoFileInfo` object describing the file and its
    ///   expected contents.
    func createTestFile(bucket: String, file: String? = nil, withParagraphs paragraphs: Int = 1)
            async throws -> S3DemoFileInfo {
        let fileName = file ?? String.uniqueName(withExtension: "txt")

        do {
            let contents = String.withLoremText(paragraphs: paragraphs).data(using: .utf8)!

            try await BasicsTests.serviceHandler?.createFile(bucket: bucket, key: fileName, withData: contents)
            let fileInfo = try BasicsTests.demoCleanup?.addFile(bucket: bucket, filename: fileName, contents: contents)
            return fileInfo!
        } catch {
            throw error
        }
    }

    /// Read a file from Amazon S3 given its `S3DemoFileInfo` object. This does
    /// *not* return the object's `contents` property.
    ///
    /// - Parameter fileInfo: The `S3DemoFileInfo` object identifying the file
    ///   to read.
    /// - Returns: A `Data` object containing the entire contents of the file.
    func readTestFile(fileInfo: S3DemoFileInfo) async throws -> Data? {
        do {
            return try await BasicsTests.serviceHandler?.readFile(
                                bucket: fileInfo.bucket, key: fileInfo.file)
        } catch {
            throw error
        }
    }

    /// Verify that the contents of a file on Amazon S3 match the file's
    /// `contents` property.
    /// - Parameter fileInfo: An `S3DemoFileInfo` object indicating which file
    ///   to verify
    /// - Returns: A `Bool` which is `true` if the file matches the expected
    ///   contents, or `false` if the contents don't match.
    func verifyTestFileContents(fileInfo: S3DemoFileInfo) async throws -> Bool {
        do {
            let fileData = try await readTestFile(fileInfo: fileInfo)

            // Handle empty file cases first.

            if fileData == nil && fileInfo.contents == nil {
                return true     // Two empty files match
            }
            if fileData == nil || fileInfo.contents == nil {
                return false    // If one is empty and the other not, no match
            }

            // Compare the contents.

            return fileData!.elementsEqual(fileInfo.contents!)
        } catch {
            throw error
        }
    }

    /// Delete a file from Amazon S3 and remove it from the list of tracked
    /// files.
    ///
    /// - Parameter fileInfo: An `S3DemoFileInfo` object identifying the file to
    ///   delete.
    func deleteTestFile(fileInfo: S3DemoFileInfo) async throws {
        do {
            try await BasicsTests.serviceHandler?.deleteFile(bucket: fileInfo.bucket, key: fileInfo.file)
            BasicsTests.demoCleanup?.removeFileInfo(file: fileInfo)
        } catch {
            throw error
        }
    }

    /// Copy a file from its original bucket to another bucket.
    ///
    /// - Parameters:
    ///   - fileInfo: An `S3DemoFileInfo` object indicating the file to copy
    ///   - destBucket: A string indicating the name of the bucket to copy the
    ///     file into
    /// - Returns: An `S3D3moFileInfo` object representing the copy of the file.
    func copyTestFile(fileInfo: S3DemoFileInfo, to destBucket: String) async throws -> S3DemoFileInfo {
        do {
            try await BasicsTests.serviceHandler?.copyFile(from: fileInfo.bucket, name: fileInfo.file, to: destBucket)
            let destFileInfo = try BasicsTests.demoCleanup?.addFile(bucket: destBucket, filename: fileInfo.file, contents: fileInfo.contents)
            return destFileInfo!
        } catch {
            throw error
        }
    }

    /// Get a list of the files contained in a bucket.
    ///
    /// - Parameter bucket: The name of the bucket to get the filenames from.
    /// - Returns: An array of strings, each identifying a single file in the
    ///   bucket.
    func listTestFiles(bucket: String) async throws -> [String] {
        do {
            let fileNames = try await BasicsTests.serviceHandler!.listBucketFiles(bucket: bucket)
            return fileNames
        } catch {
            throw error
        }
    }

    //*********** TEST FUNCTIONS ************************************

    /// Test that creating a bucket works using `createBucket(name:)`.
    func testCreateBucket() async throws {
        do {
            _ = try await createTestBucket()
        } catch {
            XCTFail("Error creating the test bucket: \(error)")
        }
    }

    /// Test that trying to create a bucket whose name is already in use fails
    /// as expected.
     func testCreateExistingBucket() async throws {
        var bucketName: String?

        // Create a bucket.

        let createTask = Task { () -> String? in
            do {
                return try await createTestBucket()
            } catch {
                XCTFail("Error creating test bucket: \(error)")
            }
            return nil
        }
        bucketName = await createTask.value

        // Try creating the same bucket again.

        do {
            _ = try await createTestBucket(name: bucketName)
            XCTFail("CreateBucket should fail when given the name of an existing bucket")
        } catch {
            return      // This is an expected error
        }
    }

    /// Test that trying to create a bucket with an invalid name fails as
    /// expected.
    func testCreateInvalidBucketName() async throws {
        do {
            _ = try await createTestBucket(name: String.uniqueName(isValid: false))
            XCTFail("CreateBucket should throw an exception when given an invalid bucket name")
        } catch {
            return      // This is an expected error
        }
    }

    // Test that deleting a bucket with `deleteBucket(name:)` works.
    func testDeleteBucket() async throws {
        let bucketName = String.uniqueName()

        do {
            _ = try await createTestBucket(name: bucketName)
            try await deleteTestBucket(name: bucketName)
        } catch {
            throw error
        }
    }

    /// Test that using `deleteBucket(name:)` to try to delete a bucket that
    /// doesn't exist silently does nothing, as expected.
    func testDeleteNonexistentBucket() async throws {
        do {
            try await deleteTestBucket(name: String.uniqueName())
            XCTFail("DeleteBucket should throw an exception when given the name of a nonexistent bucket")
        } catch {
            return
        }
    }

    /// Test that creating a file with `createFile(bucket: key: data)` works and
    /// that the created file's contents are as expected.
    func testCreateFile() async throws {
        do {
            let bucketName = try await createTestBucket()

            let fileInfo = try await createTestFile(bucket: bucketName)
            
            if try await verifyTestFileContents(fileInfo: fileInfo) == false {
                XCTFail("Created file doesn't contain expected contents")
            }
        } catch {
            throw error
        }
    }

    /// Test that deleting a file with `deleteFile(bucket: key:)` works.
    func testDeleteFile() async throws {
        do {
            let bucketName = try await createTestBucket()
            let fileInfo = try await createTestFile(bucket: bucketName)

            try await deleteTestFile(fileInfo: fileInfo)
        } catch {
            throw error
        }
    }

    /// Test that attempting to delete a nonexistent file doesn't throw an
    /// exception, which is the expected behavior.
    func testDeleteNonexistentFile() async throws {
        do {
            let bucketName = try await createTestBucket()
            let fileName = String.uniqueName(withExtension: "txt")

            try await BasicsTests.serviceHandler?.deleteFile(bucket: bucketName, key: fileName)
        } catch {
            throw error
        }
    }

    /// Test that a file can be read and that its contents match the expected
    /// content. This tests `readFile(bucket: key:)`.
    func testReadFile() async throws {
        do {
            let bucketName = try await createTestBucket()
            let fileInfo = try await createTestFile(bucket: bucketName)

            let fileData = try await readTestFile(fileInfo: fileInfo)
            XCTAssertTrue(fileData!.elementsEqual(fileInfo.contents!), "Read file doesn't match the initialized contents")
        } catch {
            throw error
        }
    }

    /// Test that copying a file from one bucket to another works using the
    /// service handler's `copyFile(from: name: to:)` function.
    func testCopyFile() async throws {
        do {
            let srcBucketName = try await createTestBucket()
            let destBucketName = try await createTestBucket()
        
            // Create a file.

            let srcFileInfo = try await createTestFile(bucket: srcBucketName)

            // Copy the file.

            let destFileInfo = try await copyTestFile(fileInfo: srcFileInfo, to: destBucketName)
            if try await verifyTestFileContents(fileInfo: destFileInfo) == false {
                XCTFail("Copied file does not match the original")
            }
        } catch {
            throw error
        }
    }

    /// Test that trying to copy a file into a bucket that doesn't exist fails
    /// as expected.
    func testCopyFileInvalidDestination() async throws {
        do {
            let srcBucketName = try await createTestBucket()
            let destBucketName = String.uniqueName()

            // Create the test file.

            let srcFileInfo = try await createTestFile(bucket: srcBucketName)

            do {
                // Attempt to copy the file.

                let destFileInfo = try await copyTestFile(fileInfo: srcFileInfo, to: destBucketName)
                XCTFail("Copying file to a non-existent bucket didn't fail like it should")
            } catch {
                return      // An error is a success case here
            }
        } catch {
            throw error
        }
    }

    /// Test listing files using the `ServiceHandler` function
    /// `listBucketFiles(bucket:)`.
    func testListFiles() async throws {
        do {
            let bucketName = try await createTestBucket()

            // Create a bunch of files in the bucket, with random names and
            // contents.
            for _ in 0...14 {
                let fileInfo = try await createTestFile(bucket: bucketName,
                                withParagraphs: Int.random(in: 1...15))
            }

            // Get a list of the contents of the bucket

            let fileNames = try await listTestFiles(bucket: bucketName)
            let listedFileCount = fileNames.count

            XCTAssertEqual(15, listedFileCount, "Should be 15 files in the list but actual number is \(listedFileCount)")

            // Build a list of the names we expect to see.

            var expectedNames: [String] = []

            for file in BasicsTests.demoCleanup!.files {
                if file.bucket == bucketName {
                    expectedNames.append(file.file)
                }
            }

            let expectedFileCount = expectedNames.count

            XCTAssertEqual(listedFileCount, expectedFileCount, "Number of files found in the bucket (\(listedFileCount)) doesn't equal the expected number (\(expectedFileCount))")

            // Make sure that both every file that exists is one we expect, and
            // that every file we expect to see is present.

            for name in fileNames {
                if expectedNames.contains(name) {
                    expectedNames.remove(at: expectedNames.firstIndex(of: name)!)
                } else {
                    XCTFail("File \(name) is not expected")
                }
            }

            XCTAssertTrue(expectedNames.count == 0, "\(expectedNames.count) files were missing from the S3 bucket")
        } catch {
            throw error
        }
    }
}
