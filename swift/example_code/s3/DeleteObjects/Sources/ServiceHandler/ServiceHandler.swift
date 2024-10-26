// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 A class containing functions that interact with AWS services.
 */

// snippet-start:[s3.swift.deleteobjects.handler]
// snippet-start:[s3.swift.deleteobjects.handler.imports]
import AWSS3
import ClientRuntime
import Foundation

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

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls
    /// used for the example.
    ///
    /// - Parameters:
    ///   - region: The optional AWS Region to access.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[s3.swift.deleteobjects.handler.init]
    public init(region: String? = nil) async throws {
        do {
            let config = try await S3Client.S3ClientConfiguration()
            if let region = region {
                config.region = region
            }
            client = S3Client(config: config)
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon S3 client"))
            throw error
        }
    }

    // snippet-end:[s3.swift.deleteobjects.handler.init]

    /// Deletes the specified objects from Amazon S3.
    ///
    /// - Parameters:
    ///   - bucket: Name of the bucket containing the object to delete.
    ///   - keys: Names of the objects to delete from the bucket.
    ///
    // snippet-start:[s3.swift.deleteobjects.handler.DeleteObjects]
    public func deleteObjects(bucket: String, keys: [String]) async throws {
        let input = DeleteObjectsInput(
            bucket: bucket,
            delete: S3ClientTypes.Delete(
                objects: keys.map { S3ClientTypes.ObjectIdentifier(key: $0) },
                quiet: true
            )
        )

        do {
            _ = try await client.deleteObjects(input: input)
        } catch {
            print("ERROR: deleteObjects:", dump(error))
            throw error
        }
    }
    // snippet-end:[s3.swift.deleteobjects.handler.DeleteObjects]
}

// snippet-end:[s3.swift.deleteobjects.handler]
