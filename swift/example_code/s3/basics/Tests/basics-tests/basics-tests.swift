import XCTest
import class Foundation.Bundle
import Dispatch
import ClientRuntime
import Fakery
@testable import ServiceHandler

let DEBUG_SEM = false
struct TestWaiter {
    let sem: DispatchSemaphore
    var name: String = "Unnamed"
    var timeout: Double = 2.0

    init() {
        sem = DispatchSemaphore(value: 0)
        if DEBUG_SEM == true {
            print("Sem init: \(name)")
        }
    }

    init(name: String, timeout: Double = 2.0) {
        self.name = name
        self.timeout = timeout

        sem = DispatchSemaphore(value: 0)
        if DEBUG_SEM == true {
            print("Sem init: \(name)")
        }
    }

    /// Signal the DispatchSemaphore, ending the lock.
    public func signal() {
        if DEBUG_SEM == true {
            print("Sem signal: \(name)")
        }
        sem.signal()
    }

    /// Wait until the semaphore is unlocked, or until
    /// the timeout period has elapsed, whichever comes
    /// first.
    ///
    /// - Returns: true if the timeout elapsed; false if
    ///            the semaphore was signaled. Ignorable.
    @discardableResult public func wait() -> Bool {
        var timedOut: Bool = false

        if DEBUG_SEM == true {
            print("Sem wait: \(name) start")
        }
        
        let timeoutResult = sem.wait(timeout: .now() + self.timeout)
        if (timeoutResult == .timedOut) {
            timedOut = true
        }

        if DEBUG_SEM == true {
            print("Sem wait: \(name) complete")
        }
        return timedOut
    }
}

/// Returns a unique filename for use in testing. The filename
/// is just a UUID, optionally with a period and an extension
/// added. The returned name is invalid for S3 use if isValid is
/// true.
///
/// - Parameters:
///   - ext: String - The file extension to add. If nil, no extension is added.
///   - isValid: Bool - If true, the returned filename is invalid for S3 use.
/// - Returns: A string containing a unique filename for S3 testing.
public func uniqueName(withExtension ext: String? = nil, isValid: Bool = true) -> String {
    var name = UUID().uuidString

    if ext != nil {
        name += ".\(ext!)"
    }

    if isValid {
        return name.lowercased()
    } else {
        return ",12%\(name)"
    }
}

class File {
    var name: String
    var bucket: Bucket
    var contents: Data
    var handler: ServiceHandler
    let faker = Faker(locale: "en-US")

    init(handler: ServiceHandler, bucket: Bucket, name: String, contents: Data) async throws {
        self.name = name
        self.handler = handler
        self.bucket = bucket
        self.contents = contents
        try await handler.createFile(bucket:bucket.name, key: name, withData: contents)
    }

    init(handler: ServiceHandler, bucket: Bucket, name: String, withParagraphs paragraphs: Int = 1) async throws {
        self.name = name
        self.handler = handler
        self.bucket = bucket

        let text = faker.lorem.paragraphs(amount: paragraphs)
        self.contents = text.data(using: .utf8)!
        do {
            try await handler.createFile(bucket:bucket.name, key: name, withData: contents)
        } catch {
            throw error
        }
    }

    /// Compare the contents of the file on S3 with the cached contents
    /// kept in memory.
    ///
    /// - Returns: true if the file validates correctly
    public func verify() async throws -> Bool {
        do {
            let fileData = try await self.read()
            return fileData.elementsEqual(self.contents)
        } catch {
            throw error
        }
    }

    public func read() async throws -> Data {
        do {
            return try await handler.readFile(bucket: self.bucket.name, key: self.name)
        } catch {
            throw error
        }

    }
}

class Bucket {
    var handler: ServiceHandler
    var name: String
    var files: [File]

    init(handler: ServiceHandler, name: String) async throws {
        self.handler = handler
        self.name = name
        self.files = []

        _ = try await self.handler.createBucket(name: name)
    }

    /* deinit {
        let testSem = DispatchSemaphore(value: 0)

        print("deinit: start")
        Task() {
            _ = try await self.handler.deleteBucket(name: name)
            testSem.signal()
        }
        _ = testSem.wait(timeout: .now() + 5)
        print("deinit: exit")
    } */

    /// Find the index into the test file list that matches the given
    /// test file name.
    /// - Parameter name: name: File name to look for
    /// - Returns: An integer offset into the test file list, or -1
    ///            if the file isn't found
    public func indexOf(name: String) -> Int {
        var index = 0

        for file in self.files {
            if file.name == name {
                return index
            }
            index += 1
        }
        return -1
    }

    public func getFile(name: String) -> File? {
        for file in self.files {
            if file.name == name {
                return file
            }
        }
        return nil
    }

    @discardableResult public func createFile(name: String, withParagraphs: Int = 1) async throws  -> File {
        do {
            let file = try await File(handler: self.handler, bucket: self, name: name, withParagraphs: 5)
            self.files.append(file)
            return file
        } catch {
            throw error
        }
    }

    public func copyFile(name: String, to bucket: String) async throws {
        do {
            try await self.handler.copyFile(from: self.name, name: name, to: bucket)
        } catch {
            throw error
        }
    }

    public func deleteFile(name: String) async throws {
        let index = indexOf(name: name)

        // Don't check the index before trying to delete the file --
        // we need to allow the attempt to be made in order to test
        // this properly.
        do {
            try await self.handler.deleteFile(bucket: self.name, key: name)
        } catch {
            throw error
        }

        // Remove the file from the file list if it's there
        if index >= 0 {
            files.remove(at: index)
        }
    }

    public func deleteAll() async throws {
        for file in self.files {
            do {
                try await self.handler.deleteFile(bucket: self.name, key: file.name)
            } catch {
                throw error
            }
        }
        self.files = []
    }
}

class BucketList {
    var handler: ServiceHandler
    var buckets: [Bucket]

    init(handler: ServiceHandler) {
        self.handler = handler
        buckets = []
    }

    /// Find the index into the bucket list that matches the given
    /// bucket name.
    /// - Parameter name: name: Bucket name to look for
    /// - Returns: An integer offset into the bucket list, or -1
    ///            if the bucket isn't found
    public func indexOf(name: String) -> Int {
        var index = 0

        for bucket in self.buckets {
            if bucket.name == name {
                return index
            }
            index += 1
        }
        return -1
    }

    public func getBucket(name: String) -> Bucket? {
        for bucket in self.buckets {
            if bucket.name == name {
                return bucket
            }
        }
        return nil
    }

    @discardableResult public func newBucket(name: String? = nil)
                                             async throws -> Bucket {
        let testSem = TestWaiter(name: "newBucket")
        
        let handler = handler

        let aTask = Task { () -> Bucket in
            var newName: String
    
            if name == nil {
                newName = uniqueName()
            } else {
                newName = name!
            }

            do {
                let bucket = try await Bucket(handler: handler, name: newName)
                self.buckets.append(bucket)
                testSem.signal()
                return bucket
            } catch {
                testSem.signal()
                throw error
            }
        }

        do {
            let bucket = try await aTask.value
            testSem.wait()
            return bucket
        } catch {
            throw error
        }
    }

    public func deleteBucket(bucket: Bucket) async throws {
        let testSem = TestWaiter(name: "deleteBucket")

        Task() {
            do {
                try await bucket.deleteAll()
                let index = self.indexOf(name: bucket.name)
                if (index >= 0) {
                    self.buckets.remove(at: index)
                }
            } catch {
                testSem.signal()
                throw error
            }
            testSem.signal()
        }
        testSem.wait()
    }

    /// Delete the bucket with the given name.
    /// - Parameter name: String - Bucket name
    public func deleteBucket(name: String) async throws {
        guard let bucket = self.getBucket(name: name) else {
            return
        }
        
        do {
            try await self.deleteBucket(bucket: bucket)
        } catch {
            throw error
        }
    }

    /// Compare a file in one bucket to a file in another. Returns true if
    /// their contents match; otherwise false.
    ///
    /// - Parameters:
    ///   - bucket1: String - The first file's bucket
    ///   - file1: String - Name of the first file
    ///   - bucket2: String - The second file's bucket
    ///   - file2: String - Name of the second file
    /// - Returns: Bool -  true if the files' contents match; otherwise false
    public func filesMatch(bucket1: String, file1: String,
                    bucket2: String, file2: String) async throws -> Bool {
        do {
            let sourceBucket = self.getBucket(name: bucket1)
            XCTAssertNotNil(sourceBucket, "Did not find the source bucket \(bucket1)")
            let sourceFile = sourceBucket!.getFile(name: file1)
            XCTAssertNotNil(sourceFile, "Did not find the source file \(file1)")
            let sourceData = try await sourceFile!.read()

            let destBucket = self.getBucket(name: bucket2)
            XCTAssertNotNil(destBucket, "Did not find the source bucket \(bucket2)")
            let destFile = destBucket!.getFile(name: file2)
            XCTAssertNotNil(destFile, "Did not find the source file \(file2)")
            let destData = try await destFile!.read()

            return sourceData.elementsEqual(destData)
        } catch {
            throw error
        }
    }
}

private var logInitialized = false

final class BasicsTests: XCTestCase {
    let invalidBucketName: String = uniqueName(isValid: false)
    var bucketList: BucketList? = nil
    var serviceHandler: ServiceHandler? = nil

    /// Perform state setup prior to test. Called before every single
    /// test below.
    override func setUp() async{
        if logInitialized == false {
            SDKLoggingSystem.initialize(logLevel: .error)
            logInitialized = true
        }
        serviceHandler = await ServiceHandler()
        self.bucketList = BucketList(handler: serviceHandler!)
    }

    /// Cleans up after a test. Called once after each test below.
    override func tearDown() async throws {
        for bucket in (self.bucketList!.buckets) {
            do {
                try await bucket.deleteAll()
            } catch {}
            do {
                try await self.bucketList!.deleteBucket(bucket: bucket)
            } catch {}
        }
    }

    func testCreateBucket() async throws {
        let testSem = TestWaiter(name: "testCreateBucket")

        Task() {
            do {
                try await self.bucketList!.newBucket()
                testSem.signal()
            } catch {
                testSem.signal()
                throw error
            }
        }
        testSem.wait()
    }

     func testCreateExistingBucket() async throws {
        let testSem = TestWaiter(name: "testCreateExistingBucket")

        Task() {
            do {
                let bucket = try await self.bucketList!.newBucket()

                do {
                    try await self.bucketList!.newBucket(name: bucket.name)
                    XCTFail("CreateBucket should fail when given the name of an existing bucket")
                } catch {
                    return
                }
            } catch {
                testSem.signal()
                throw error
            }
            testSem.signal()
        }
        testSem.wait()
    }
 
    func testCreateInvalidBucketName() async throws {
        let invalidName = uniqueName(isValid: false)

        let testSem = TestWaiter(name: "testCreateInvalidBucketName")

        Task() {
            do {
                try await self.bucketList!.newBucket(name: invalidName)
                XCTFail("CreateBucket should throw an exception when given an invalid bucket name")
            } catch {
                return
            }
            testSem.signal()
        }
        testSem.wait()
    }

    func testDeleteBucket() async throws {
        let testSem = TestWaiter(name: "testDeleteBucket")

        Task() {
            var testBucket: Bucket

            do {
                testBucket = try await self.bucketList!.newBucket()
            } catch {
                testSem.signal()
                throw error
            }
            do {
                try await self.bucketList!.deleteBucket(bucket: testBucket)
            } catch {
                testSem.signal()
                throw error
            }
            testSem.signal()
        }
        testSem.wait()
    }

    func testDeleteNonexistentBucket() async throws {
        let testSem = TestWaiter(name: "testDeleteNonexistentBucket")

        Task() {
            do {
                // Use the service handler directly since the bucket doesn't exist
                try await self.serviceHandler?.deleteBucket(name: uniqueName())
                XCTFail("DeleteBucket should throw an exception when given the name of a nonexistent bucket")
            } catch {
                return
            }
            testSem.signal()
        }
        testSem.wait()
    }

    func testCreateFile() async throws {
        let testSem = TestWaiter(name: "testCreateFile")

        Task() {
            let bucketName = uniqueName()
            let fileName = uniqueName(withExtension: "txt", isValid: true)

            do {
                let bucket = try await self.bucketList!.newBucket(name: bucketName)
                let file = try await bucket.createFile(name: fileName)
                let verified = try await file.verify()
                XCTAssertTrue(verified, "Created file does not contain correct data")
                testSem.signal()
            } catch {
                testSem.signal()
                throw error
            }
        }
        testSem.wait()
    }

    func testDeleteFile() async throws {
        let testSem = TestWaiter(name: "testDeleteFile")

        Task() {
            do {
                let bucket = try await self.bucketList!.newBucket()
                let file = try await bucket.createFile(name: uniqueName(withExtension: "txt"), withParagraphs: 5)
                try await bucket.deleteFile(name: file.name)
                testSem.signal()
            } catch {
                testSem.signal()
                throw error
            }
        }
        testSem.wait()
    }

    func testDeleteNonexistentFile() async throws {
        let testSem = TestWaiter(name: "testDeleteNonexistentFile")

        Task() {
            do {
                let bucket = try await self.bucketList!.newBucket()
                let fileName = uniqueName()
                do {
                    try await bucket.deleteFile(name: fileName)
                    XCTFail("Deleting a nonexistent file succeeded but should not")
                } catch {
                    // Errors mean a successful test
                }
            } catch {
                testSem.signal()
                throw error
            }
            testSem.signal()
        }
        testSem.wait()
    }

    func testReadFile() async throws {
        let testSem = TestWaiter(name: "testReadFile")

        Task() {
            do {
                let bucket = try await self.bucketList!.newBucket()
                let fileName = uniqueName(withExtension: "txt")
                let file: File = try await bucket.createFile(name: fileName, withParagraphs: 5)
                let fileData = try await file.read()

                XCTAssertTrue(fileData.elementsEqual(file.contents), "Read file doesn't match initialized content")
            } catch {
                testSem.signal()
                throw error
            }
            testSem.signal()
        }
        testSem.wait()
    }

    func testCopyFile() async throws {
        let testSem = TestWaiter(name: "testCopyFile")

        Task() {
            let sourceBucketName = uniqueName()
            let destBucketName = uniqueName()
            let fileName = uniqueName(withExtension: "txt", isValid: true)

            do {
                let sourceBucket = try await self.bucketList!.newBucket(name: sourceBucketName)
                let destBucket = try await self.bucketList!.newBucket(name: destBucketName)
                _ = try await sourceBucket.createFile(name: fileName)

                try await sourceBucket.copyFile(name: fileName, to: destBucketName)
                let file = destBucket.getFile(name: fileName)
                XCTAssertNotNil(file, "File does not exist on destination bucket after copy")

                let verified1 = try await file!.verify()
                XCTAssertTrue(verified1, "Copied file exists but doesn't contain expected data")

                let verified2 = try await self.bucketList!.filesMatch(bucket1: sourceBucketName, file1: fileName,
                                    bucket2: destBucketName, file2: fileName)
                XCTAssertTrue(verified2, "Copied file doesn't match source file")
            } catch {
                testSem.signal()
                throw error
            }
            testSem.signal()
        }
        testSem.wait()
    }

    func testListFiles() async throws {
        let testSem = TestWaiter(name: "testListFiles")

        Task() {
            do {
                let bucket = try await self.bucketList!.newBucket()

                for _ in 1...15 {
                    try await bucket.createFile(name: uniqueName(), withParagraphs: Int.random(in: 1...15))
                }
                let list = try await bucket.handler.listBucketFiles(bucket: bucket.name)

                XCTAssertEqual(15, list.count, "Should be 15 files in the list; actual number is \(list.count)")

                for index in 1...15 {
                    XCTAssertEqual(list[index], bucket.files[index].name, "File names should match but are \(list[index]) and \(bucket.files[index].name)")
                }

            } catch {
                testSem.signal()
                throw error
            }
            testSem.signal()
        }
        testSem.wait()
    }

    /// Returns path to the built products directory.
    var productsDirectory: URL {
      #if os(macOS)
        for bundle in Bundle.allBundles where bundle.bundlePath.hasSuffix(".xctest") {
            return bundle.bundleURL.deletingLastPathComponent()
        }
        fatalError("couldn't find the products directory")
      #else
        return Bundle.main.bundleURL
      #endif
    }
}
