// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// An example demonstrating how to mock AWS SDK for Swift functions using
// protocols.
//

import Foundation
import ClientRuntime
import AWSS3

/// The main entry point for the example is an asynchronous main function.
@main
struct MockingDemo {
    /// The static, asynchronous entry point for the program.
    static func main() async {
        // snippet-start:[mocking.swift.main-setup]
        /// An ``S3Session`` object that passes calls through to the SDK for
        /// Swift.
        let session: S3Session
        /// A ``BucketManager`` object that will be initialized to call the
        /// SDK using the session.
        let bucketMgr: BucketManager
        
        // Create the ``S3Session`` and a ``BucketManager`` that calls the SDK
        // using it.
        do {
            session = try S3Session(region: "us-east-1")
            bucketMgr = BucketManager(session: session)
        } catch {
            print("Unable to initialize access to Amazon S3.")
            return
        }
        // snippet-end:[mocking.swift.main-setup]

        // snippet-start:[mocking.swift.main-call]
        let bucketList: [String]

        do {
            bucketList = try await bucketMgr.getBucketNames()
        } catch {
            print("Unable to get the bucket list.")
            return
        }
        // snippet-end:[mocking.swift.main-call]

        // Print out a list of the bucket names.

        if bucketList.count != 0 {
            print("Found \(bucketList.count) buckets:")
            for name in bucketList {
                print("  \(name)")
            }
        } else {
            print("No buckets found.")
        }
    }
}
