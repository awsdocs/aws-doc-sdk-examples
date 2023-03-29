/// A class to manage access to the movie database and its entries.
///
/// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
/// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ddb.swift.batchgetitem.moviedatabase]
import Foundation
import AWSDynamoDB

// snippet-start:[ddb.swift.batchgetitem.movieerror]
/// Errors that can be thrown by the `Movie` struct and the `MovieDatabase`
/// class.
enum MovieError: Error {
    /// The DynamoDB client has not been initialized.
    case ClientUninitialized
    /// The DynamoDB table could not be created.
    case CreateTableFailed
    /// The DynamoDB table could not be found.
    case TableNotFound
    /// The requested item was not found in the movie table.
    case ItemNotFound
    /// The table status returned by `DynamoDBClient.describeTable()` was not
    /// recognized.
    case StatusUnknown
    /// One or more attributes are invalid.
    case InvalidAttributes
    /// The response to the DynamoDB client's `batchGetItem()` function was not
    /// valid.
    case InvalidResponse
}
// snippet-end:[ddb.swift.batchgetitem.movieerror]

/// A class used to access the movies in a DynamoDB database.
public class MovieDatabase {
    let tableName: String
    var ddbClient: DynamoDBClient? = nil

    // snippet-start:[ddb.swift.batchgetitem.moviedatabase.init]
    /// Create a new `MovieDatabase`. This includes creating the DynamoDB
    /// table and optionally preloading it with the contents of a specified
    /// JSON file.
    ///
    /// - Parameters:
    ///   - region: The AWS Region in which to create the table.
    ///   - jsonPath: The path name of a JSON file containing movie data with
    ///     which to populate the table.
    ///
    /// - Throws:
    ///     - `MovieError.ClientUninitialized` if the DynamoDB client is not
    ///       initialized successfully.
    ///     - `MovieError.CreateTableFailed` if the DynamoDB table could not
    ///       be created.
    ///     - Appropriate DynamoDB errors might be thrown also.
    init(region: String = "us-east-2", jsonPath: String) async throws {
        ddbClient = try DynamoDBClient(region: region)

        tableName = "ddb-batchgetitem-sample-\(Int.random(in: 1...Int.max))"

        let input = CreateTableInput(
            attributeDefinitions: [
                DynamoDBClientTypes.AttributeDefinition(attributeName: "year", attributeType: .n),
                DynamoDBClientTypes.AttributeDefinition(attributeName: "title", attributeType: .s),
            ],
            keySchema: [
                DynamoDBClientTypes.KeySchemaElement(attributeName: "year", keyType: .hash),
                DynamoDBClientTypes.KeySchemaElement(attributeName: "title", keyType: .range)
            ],
            provisionedThroughput: DynamoDBClientTypes.ProvisionedThroughput(
                readCapacityUnits: 10,
                writeCapacityUnits: 10
            ),
            tableName: self.tableName
        )

        // Get safe access to the `DynamoDBClient`.

        guard let client = self.ddbClient else {
            throw MovieError.ClientUninitialized
        }

        // Create the table. If the table description in the response is
        // `nil`, throw an exception.

        let output = try await client.createTable(input: input)
        if output.tableDescription == nil {
            throw MovieError.CreateTableFailed
        }

        /// Wait until the table has been created and is active, then populate
        /// the database from the file.

        try await awaitTableActive()
        try await self.populate(jsonPath: jsonPath)
    }
    // snippet-end:[ddb.swift.batchgetitem.moviedatabase.init]

    // snippet-start:[ddb.swift.batchgetitem.deletetable]
    /// Delete the DynamoDB table containing the movie data.
    ///
    /// - Throws:
    ///     - `MovieError.ClientUninitialized` if the DynamoDB client has not
    ///     been initialized.
    ///     - DynamoDB errors are thrown without change.
    func deleteTable() async throws {
        guard let client = self.ddbClient else {
            throw MovieError.ClientUninitialized
        }

        let input = DeleteTableInput(
            tableName: self.tableName
        )
        _ = try await client.deleteTable(input: input)
    }
    // snippet-end:[ddb.swift.batchgetitem.deletetable]

    // snippet-start:[ddb.swift.batchgetitem.tableexists]
    /// Check to see if the table exists online yet.
    ///
    /// - Returns: `true` if the table exists, or `false` if not.
    ///
    /// - Throws:
    ///     - `MovieError.ClientUninitialized` if the DynamoDB client has not
    ///     been initialized.
    ///     - DynamoDB errors are thrown without change.
    func tableExists() async throws -> Bool {
        guard let client = self.ddbClient else {
            throw MovieError.ClientUninitialized
        }

        let input = DescribeTableInput(
            tableName: tableName
        )
        let output = try await client.describeTable(input: input)
        guard let description = output.table else {
            return false
        }
        
        return (description.tableName == self.tableName)
    }
    // snippet-end:[ddb.swift.batchgetitem.tableexists]

    // snippet-start:[ddb.swift.batchgetitem.gettablestatus]
    /// Get the table's status.
    ///
    /// - Returns: The table status, as defined by the
    ///   `DynamoDBClientTypes.TableStatus` enum.
    ///
    /// - Throws:
    ///     - `MovieError.ClientUninitialized` if the DynamoDB client has not
    ///     been initialized.
    ///     - `MovieError.TableNotFound` if the table doesn't exist.
    ///     - `MovieError.StatusUnknown` if the table status couldn't be
    ///     determined.
    ///     - DynamoDB errors are thrown without change.
    func getTableStatus() async throws -> DynamoDBClientTypes.TableStatus {
        guard let client = self.ddbClient else {
            throw MovieError.ClientUninitialized
        }

        let input = DescribeTableInput(
            tableName: self.tableName
        )
        let output = try await client.describeTable(input: input)
        guard let description = output.table else {
            throw MovieError.TableNotFound
        }
        guard let status = description.tableStatus else {
            throw MovieError.StatusUnknown
        }
        return status
    }
    // snippet-end:[ddb.swift.batchgetitem.gettablestatus]

    // snippet-start:[ddb.swift.batchgetitem.awaittableactive]
    ///
    /// Waits for the table to exist and for its status to be active.
    ///
    /// - Throws:
    ///     - `MovieError.ClientUninitialized` if the DynamoDB client has not
    ///     been initialized.
    ///     - `MovieError.StatusUnknown` if the table status couldn't be determined.
    ///     - DynamoDB errors are thrown without change.
    func awaitTableActive() async throws {
        while (try await tableExists() == false) {
            Thread.sleep(forTimeInterval: 0.25)
        }

        while (try await getTableStatus() != .active) {
            Thread.sleep(forTimeInterval: 0.25)
        }
    }
    // snippet-end:[ddb.swift.batchgetitem.awaittableactive]

    // snippet-start:[ddb.swift.batchgetitem.populate]
    /// Populate the movie database from the specified JSON file. Called only
    /// by the `init()` function.
    ///
    /// - Parameters:
    ///     - jsonPath: Path to a JSON file containing movie data.
    ///
    /// - Throws:
    ///     - `MovieError.ClientUninitialized` if the DynamoDB client has not
    ///     been initialized.
    ///     - DynamoDB errors are thrown without change.
    fileprivate func populate(jsonPath: String) async throws {
        guard let client = self.ddbClient else {
            throw MovieError.ClientUninitialized
        }

        // Create a Swift `URL` and use it to load the file into a `Data`
        // object. Then decode the JSON into an array of `Movie` objects.

        let fileUrl = URL(fileURLWithPath: jsonPath)
        let jsonData = try Data(contentsOf: fileUrl)

        var movieList = try JSONDecoder().decode([Movie].self, from: jsonData)

        // Truncate the list to the first 200 entries or so for this example.

        if movieList.count > 200 {
            movieList = Array(movieList[...199])
        }

        // Before sending records to the database, break the movie list into
        // 25-entry chunks, which is the maximum size of a batch item request.

        let count = movieList.count
        let chunks = stride(from: 0, to: count, by: 25).map {
            Array(movieList[$0 ..< Swift.min($0 + 25, count)])
        }

        // For each chunk, create a list of write request records and populate
        // them with `PutRequest` requests, each specifying one movie from the
        // chunk. After the chunk's items are all in the `PutRequest` list,
        // send them to Amazon DynamoDB using the
        // `DynamoDBClient.batchWriteItem()` function.

        for chunk in chunks {
            var requestList: [DynamoDBClientTypes.WriteRequest] = []
            for movie in chunk {
                let item: [String : DynamoDBClientTypes.AttributeValue] = try await movie.getAsItem()
                let request = DynamoDBClientTypes.WriteRequest(
                    putRequest: .init(
                        item: item
                    )
                )
                requestList.append(request)
            }

            let input = BatchWriteItemInput(requestItems: [self.tableName: requestList])
            _ = try await client.batchWriteItem(input: input)
        }
    }
    // snippet-end:[ddb.swift.batchgetitem.populate]

    // snippet-start:[ddb.swift.batchgetitem.batchget]
    /// Gets an array of `Movie` objects describing all the movies in the
    /// specified list. Any movies that aren't found in the list have no
    /// corresponding entry in the resulting array.
    ///
    /// - Parameters
    ///     - keys: An array of tuples, each of which specifies the title and
    ///       release year of a movie to fetch from the table.
    ///
    /// - Returns:
    ///     - An array of `Movie` objects describing each match found in the
    ///     table.
    ///
    /// - Throws:
    ///     - `MovieError.ClientUninitialized` if the DynamoDB client has not
    ///     been initialized.
    ///     - DynamoDB errors are thrown without change.
    func batchGet(keys: [(title: String, year: Int)]) async throws -> [Movie] {
        guard let client = self.ddbClient else {
            throw MovieError.ClientUninitialized
        }

        var movieList: [Movie] = []
        var keyItems: [[Swift.String:DynamoDBClientTypes.AttributeValue]] = []

        // Convert the list of keys into the form used by DynamoDB.

        for key in keys {
            let item: [Swift.String:DynamoDBClientTypes.AttributeValue] = [
                "title": .s(key.title),
                "year": .n(String(key.year))
            ]
            keyItems.append(item)
        }

        // Create the input record for `batchGetItem()`. The list of requested
        // items is in the `requestItems` property. This array contains one
        // entry for each table from which items are to be fetched. In this
        // example, there's only one table containing the movie data.
        //
        // If we wanted this program to also support searching for matches
        // in a table of book data, we could add a second `requestItem`
        // mapping the name of the book table to the list of items we want to
        // find in it.
        let input = BatchGetItemInput(
            requestItems: [
                self.tableName: .init(
                    consistentRead: true,
                    keys: keyItems
                )
            ]
        )

        // Fetch the matching movies from the table.

        let output = try await client.batchGetItem(input: input)

        // Get the set of responses. If there aren't any, return the empty
        // movie list.

        guard let responses = output.responses else {
            return movieList
        }

        // Get the list of matching items for the table with the name
        // `tableName`.

        guard let responseList = responses[self.tableName] else {
            return movieList
        }

        // Create `Movie` items for each of the matching movies in the table
        // and add them to the `MovieList` array.

        for response in responseList {
            movieList.append(try Movie(withItem: response))
        }

        return movieList
    }
    // snippet-end:[ddb.swift.batchgetitem.batchget]
}
// snippet-end:[ddb.swift.batchgetitem.moviedatabase]
