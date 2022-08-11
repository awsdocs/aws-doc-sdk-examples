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
        // First build a list of `ObjectIdentifier`s representing each object
        // we want to delete.
        var objectList: [S3ClientTypes.ObjectIdentifier] = []
        for key in keys {
            let objId = S3ClientTypes.ObjectIdentifier(
                key: key
            )
            objectList.append(objId)
        }

        // Next build a `Delete` object with the list of objects.
        let deleteObjectList: S3ClientTypes.Delete = S3ClientTypes.Delete(objects: objectList)
        let input = DeleteObjectsInput(
            bucket: bucket,
            delete: deleteObjectList
        )

        // Call the SDK for Swift to delete the objects from Amazon S3.
        do {
            _ = try await client.deleteObjects(input: input)
        } catch {
            throw error
        }
    }
    // snippet-end:[s3.swift.deleteobjects.handler.deleteobjects]
}
// snippet-end:[s3.swift.deleteobjects.handler]