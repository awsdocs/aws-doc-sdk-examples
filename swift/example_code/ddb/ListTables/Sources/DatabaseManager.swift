/// A class containing functions to access Amazon DynamoDB.
///
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ddb.swift.databasemanager-all]
import Foundation
import AWSDynamoDB
import ClientRuntime

/// A protocol describing the implementation of functions that allow either
/// calling through to Amazon DynamoDB or mocking DynamoDB functions.
public protocol DatabaseSession {
    /// A mockable entry point for the Amazon DynamoDB function
    /// `listTables()`. A DynamoDB implementation of `DatabaseSession` should
    /// call through to `DynamoDBClient.listTables()`, while a mocked
    /// implementation should generate and return a `ListTablesOutput`
    /// object with the desired results for testing purposes.
    ///
    /// - Parameter input: A `ListTablesInput` object specifying the input
    ///   parameters for the call to `listTables()`.
    ///
    /// - Returns: A `ListTablesOutput` structure with the results.
    func listTables(input: ListTablesInput) async throws -> ListTablesOutput
}

// snippet-start:[ddb.swift.dynamodbsession]
/// An implementation of the `DatabaseSession` protocol that calls through to
/// DynamoDB for its operations.
public struct DynamoDBSession: DatabaseSession {
    let awsRegion: String
    let client: DynamoDBClient

    // snippet-start:[ddb.swift.dynamodbsession.init]
    /// Initialize the `DatabaseSession`.
    ///
    /// - Parameter region: The AWS Region to use for DynamoDB.
    ///
    init(region: String = "us-east-2") throws {
        self.awsRegion = region
        self.client = try DynamoDBClient(region: awsRegion)
    }
    // snippet-end:[ddb.swift.dynamodbsession.init]

    // snippet-start:[ddb.swift.dynamodbsession.listtables]
    /// Return a list of all the tables available by calling through to the
    /// DynamoDB function `listTables()`.
    ///
    /// - Parameter input: The `input` parameter for `listTables()` as a
    ///   `ListTablesInput` object.
    ///
    /// - Returns: The `ListTablesOutput` returned by `listTables()`.
    /// 
    /// - Throws: Errors from DynamoDB are thrown as usual.
    public func listTables(input: ListTablesInput) async throws -> ListTablesOutput {
        return try await client.listTables(input: input)
    }
    // snippet-end:[ddb.swift.dynamodbsession.listtables]
}
// snippet-end:[ddb.swift.dynamodbsession]

// snippet-start:[ddb.swift.databasemanager]
/// A class that uses a `DynamoDBSession` to perform database operations.
public class DatabaseManager {
    private let session: DatabaseSession

    // snippet-start:[ddb.swift.databasemanager.init]
    /// Initializes the database manager.
    ///
    /// The `session` parameter specifies an object that implements the
    /// `DatabaseSession` protocol. All calls to access the database are done
    /// using the `session` object. The `DynamoDBSession` struct sends
    /// requests to Amazon DynamoDB. You can test by instead providing a
    /// struct that mocks the DynamoDB functions.
    init(session: DatabaseSession) {
        self.session = session
    }
    // snippet-end:[ddb.swift.databasemanager.init]

    // snippet-start:[ddb.swift.databasemanager.gettablelist]
    /// Get a list of the DynamoDB tables available in the specified Region.
    ///
    /// - Returns: An array of strings listing all of the tables available
    ///   in the Region specified when the session was created.
    public func getTableList() async throws -> [String] {
        var tableList: [String] = []
        var lastEvaluated: String? = nil

        // Iterate over the list of tables, 25 at a time, until we have the
        // names of every table. Add each group to the `tableList` array.
        // Iteration is complete when `output.lastEvaluatedTableName` is `nil`.

        repeat {
            let input = ListTablesInput(
                exclusiveStartTableName: lastEvaluated,
                limit: 25
            )
            let output = try await self.session.listTables(input: input)
            guard let tableNames = output.tableNames else {
                return tableList
            }
            tableList.append(contentsOf: tableNames)
            lastEvaluated = output.lastEvaluatedTableName
        } while lastEvaluated != nil

        return tableList
    }
    // snippet-end:[ddb.swift.databasemanager.gettablelist]
}
// snippet-end:[ddb.swift.databasemanager]
// snippet-end:[ddb.swift.databasemanager-all]
