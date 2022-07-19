/*
   A class to record the Amazon S3 buckets and files created by a code example,
   so that they can easily be cleaned up after the example is finished.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation
import AWSS3
import ClientRuntime
import AWSClientRuntime
import SwiftUtilities

/// Errors thrown by the `S3DemoCleanup` class.
enum S3DemoCleanupError: Error {
    case duplicate          /// Attempted to create an item that already exists.
    case notFound           /// Attempted to access an item that doesn't exist.
}

/// A structure identifying a file given its bucket name and the
/// file's name within the bucket.
public struct S3DemoFileInfo: Equatable {
    /// The name of the bucket containing the file.
    var bucket: String
    /// The file's name.
    var file: String
    /// The contents of the file.
    var contents: Data?

    init(bucket: String, file: String, contents: Data? = nil) {
        self.bucket = bucket
        self.file = file
        self.contents = contents
    }

    /// Determine if the `S3DemoFileInfo` matches the given bucket
    /// name and file name.
    ///
    /// - Parameters:
    ///   - bucket: Bucket name to compare to.
    ///   - file: File name to compare to.
    func matches(bucket: String, file: String) -> Bool {
        return bucket == self.bucket && file == self.file
    }
}

public class S3DemoCleanup {
    /// Returns a single shared instance of `S3DemoCleanup` that should
    /// always be used to access the cleanup manager.
    var client: S3Client? = nil
    var buckets: [String]
    var files: [S3DemoFileInfo]

    init() async {
        do {
            self.client = try await S3Client()
        } catch {
            print("Error initializing S3 client for test cleanup processor:")
            dump(error)
            exit(1)
        }
        self.buckets = []
        self.files = []
    }

    /// Return the index into the tracked file list at which the specified
    /// `S3DemoFileInfo` object is found.
    ///
    /// - Parameter fileInfo: The `S3DemoFileInfo` object to find in the tracked
    ///   files list.
    /// - Returns: The index into the tracked files list at which the file is
    ///   located. If the file isn't in the list, the result is -1.
    func getIndex(fileInfo: S3DemoFileInfo) -> Int {
        var index = 0

        for file in self.files {
            if fileInfo.matches(bucket: file.bucket, file: file.file) {
                return index
            }
            index += 1
        }
        return -1
    }

    /// Clean up all of the files and buckets previously added to the
    /// demo cleanup manager.
    func cleanup() async {
        do {
            try await self.disposeAllFiles()
            try await self.disposeAllBuckets()
        } catch {
            print("Error during purge of test buckets:")
            dump(error)
        }
    }

    /// Add a bucket to the list of tracked buckets. Tracked buckets are
    /// automatically deleted when the tracker is shut down.
    ///
    /// - Parameter name: The name of the bucket to track.
    ///
    /// Throws an exception if the bucket name is already in the list.
    public func addBucket(name: String) throws {
        if !self.buckets.contains(name) {
            self.buckets.append(name)
        } else {
            throw S3DemoCleanupError.duplicate
        }
    }

    /// Add a file identified by the given bucket and file names to the list
    /// of tracked files. These files will automatically get deleted when
    /// the tracker shuts down.
    ///
    /// - Parameters:
    ///   - bucket: Name of the bucket to track.
    ///   - file: Name of the file to delete upon shutdown.
    ///   - contents: The file's contents, or `nil` if none.
    public func addFile(bucket: String, filename: String, contents: Data?) throws -> S3DemoFileInfo {
        let fileInfo = S3DemoFileInfo(bucket: bucket, file: filename, contents: contents)
        if !self.files.contains(fileInfo) {
            self.files.append(fileInfo)
        } else {
            throw S3DemoCleanupError.duplicate
        }
        return fileInfo
    }

    /// Add a file identified by an existing `S3DemoFileInfo` structure
    /// to the list of tracked files. These files are automatically deleted when
    /// the `shutdown()` method is called.
    ///
    /// - Parameter fileInfo: A file information record describing the file's
    /// location and contents.
    public func addFile(withFileInfo fileInfo: S3DemoFileInfo) throws {
        if !self.files.contains(fileInfo) {
            self.files.append(fileInfo)
        } else {
            throw S3DemoCleanupError.duplicate
        }
    }

    /// Dispose of the bucket with the given name. The bucket must be empty.
    ///
    /// - Parameter name: The bucket to dispose of.
    ///
    /// Throws any errors that can be thrown by `S3Client.deleteBucket()`.
    public func disposeBucket(name: String) async throws {
        if !buckets.contains(name) {
            throw S3DemoCleanupError.notFound
        }

        do {
            let input = DeleteBucketInput(
                bucket: name
            )
            _ = try await client?.deleteBucket(input: input)

            self.buckets = self.buckets.filter { $0 != name }
        } catch {
            throw error
        }
    }

    /// Remove a bucket from the list **without** deleting the bucket.
    /// - Parameter name: The bucket to remove from the list.
    public func removeBucket(name: String) throws {
        if !buckets.contains(name) {
            throw S3DemoCleanupError.notFound
        }

        self.buckets = self.buckets.filter { $0 != name }
    }

    /// Dispose of all buckets previously added to the demo cleanup manager.
    public func disposeAllBuckets() async throws {
        for bucket in self.buckets {
            do {
                try await disposeBucket(name: bucket)
            } catch {
                throw error
            }
        }
    }

    /// Dispose of a file specified by `S3DemoFileInfo`.
    ///
    /// - Parameter file: An `S3DemoFileInfo` object indicating which file
    ///   should be disposed of.
    ///
    /// Passes along any exceptions thrown by the function
    /// `S3Client.deleteObject()`.
    public func disposeFile(file: S3DemoFileInfo) async throws {
        let input = DeleteObjectInput(
            bucket: file.bucket,
            key: file.file
        )
        do {
            _ = try await client?.deleteObject(input: input)
            self.files = self.files.filter { $0 != file }
        } catch {
            throw error
        }
    }

    /// Remove an `S3DemoFileInfo` object from the list of files, *without*
    /// deleting the corresponding file.
    ///
    /// - Parameter file: The file to remove from the tracked files list.
    public func removeFileInfo(file: S3DemoFileInfo) {
        self.files = self.files.filter { $0 != file }
    }

    /// Dispose of every file in the tracked files list.
    public func disposeAllFiles() async throws {
        for file in self.files {
            do {
                try await disposeFile(file: file)
            } catch {
                throw error
            }
        }
    }
}