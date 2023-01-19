/*
   A class containing functions that interact with AWS services.
   
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[s3.swift.deleteobjects.handler]
// snippet-start:[s3.swift.deleteobjects.handler.imports]
import Foundation
import AWSS3
import ClientRuntime
import AWSClientRuntime
// snippet-end:[s3.swift.deleteobjects.handler.imports]

/// Errors returned by `ServiceHandler` functions.
// snippet-start:[s3.swift.deleteobjects.enum.service-error]
public enum ServiceHandlerError: Error {
    case deleteObjectsError
}
// snippet-end:[s3.swift.deleteobjects.enum.service-error]

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class ServiceHandler {
    public let client: S3Client
    public var region: S3ClientTypes.BucketLocationConstraint?
    
    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls
    /// used for the example.
    /// 
    /// - Parameters:
    ///   - region: The AWS Region to access.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[s3.swift.deleteobjects.handler.init]
    public init(region: String = "us-east-2") async {
        self.region = S3ClientTypes.BucketLocationConstraint(rawValue: region)
        do {
            client = try S3Client(region: region)
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon S3 client"))
            exit(1)
        }
    }
    // snippet-end:[s3.swift.deleteobjects.handler.init]

    /// Deletes the specified objects from Amazon S3.
    ///
    /// - Parameters:
    ///   - bucket: Name of the bucket containing the object to delete.
    ///   - keys: Names of the objects to delete from the bucket.
    ///
    // snippet-start:[s3.swift.deleteobjects.handler.deleteobjects]
    public func deleteObjects(bucket: String, keys: [String]) async throws {
        let input = DeleteObjectsInput(
            bucket: bucket,
            delete: S3ClientTypes.Delete(
                objects: keys.map({ S3ClientTypes.ObjectIdentifier(key: $0) }),
                quiet: true
            )
        )

        do {
            let output = try await client.deleteObjects(input: input)

            // As of the last update to this example, any errors are returned
            // in the `output` object's `errors` property. If there are any
            // errors in this array, throw an exception. Once the error
            // handling is finalized in later updates to the AWS SDK for
            // Swift, this example will be updated to handle errors better.

            guard let errors = output.errors else {
                return  // No errors.
            }
            if errors.count != 0 {
                throw ServiceHandlerError.deleteObjectsError
            }
        } catch {
            throw error
        }
    }
    // snippet-end:[s3.swift.deleteobjects.handler.deleteobjects]
}
// snippet-end:[s3.swift.deleteobjects.handler]
