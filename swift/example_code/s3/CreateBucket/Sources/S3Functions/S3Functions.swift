//
// Created by ericsh on 2/23/22.
//

import Foundation
import AWSS3
import AWSClientRuntime

class S3Functions {
    let s3Client: S3Client

    public init() async {
        do {
            s3Client = try await S3Client()
        } catch {
            print("ERROR: ", dump(error, name: "Initializing S3 Client"))
            exit(1)
        }
    }

    public func createBucket(name: String) async throws {
        do {
            let output = try await s3Client.createBucket(input: CreateBucketInput(bucket: name))
        } catch {
            print("ERROR: ", dump(error, name: "Creating S3 bucket"))
            exit(2)
        }
    }
}
