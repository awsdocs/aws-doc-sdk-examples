/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
This scenario demonstrates how to:
    - Create a table that can hold movie data.
    - Write movie data to the table from a sample JSON file.
    - Put, get, and update a single movie in the table.
    - Update a movie.
    - Query for movies that were released in a given year.
    - Scan for movies that were released in a range of years.
    - Delete a movie from the table.
    - Delete the table.


Running the code:
1. Update the AWS Region in '../libs/ddbClient.js'.
2. Run the following at the command line:

   node dynamodb_basics.js <table_name> <newMovieName> <newMovieYear> <existingMovieName> <existingMovieYear> <newMovieRank> <newMoviePlot>

   For example, node dynamodb_basics.js myNewTable myMovieName 2022 'This Is the End' 2013 200 'A coder cracks code...'

// snippet-start:[javascript.dynamodb_scenarios.dynamodb_basics]
*/
import fs from "fs";
// A practical functional library used to split the data into segments.
import * as R from "ramda";
import {ddbClient} from "../libs/ddbClient.js";
import {ddbDocClient} from "../libs/ddbDocClient.js";
import {
    PutCommand,
    GetCommand,
    UpdateCommand,
    BatchWriteCommand,
    DeleteCommand,
    ScanCommand,
    QueryCommand
} from "@aws-sdk/lib-dynamodb";
import {
    DeleteTableCommand,
    CreateTableCommand
} from "@aws-sdk/client-dynamodb";
import {to} from "../utilities/to.js";

if (process.argv.length < 6) {
    console.log(
        "Usage: node dynamodb_basics.js <tableName> <newMovieName> <newMovieYear> <existingMovieName> <existingMovieYear> <newMovieRank> <newMoviePlot>\n" +
        "Example: node dynamodb_basics.js newmoviesbrmur newmoviename 2025 200 'MOVIE PLOT DETAILS'"
    );
}

// Helper function to delay running the code while the AWS service calls wait for responses.
function wait(ms) {
    var start = Date.now();
    var end = start;
    while (end < start + ms) {
        end = Date.now();
    }
}

// Set the parameters.
const tableName = process.argv[2];
const newMovieName = process.argv[3];
const newMovieYear = parseInt(process.argv[4]); // parseInt() converts the string into a number.
const existingMovieName = process.argv[5];
const existingMovieYear = parseInt(process.argv[6]);
const newMovieRank = parseInt(process.argv[7]);
const newMoviePlot = process.argv[8];

export const run = async (
    tableName,
    newMovieName,
    newMovieYear,
    existingMovieName,
    existingMovieYear,
    newMovieRank,
    newMoviePlot
) => {
    console.log("Creating table ...");
    // Set the parameters.
    const params = {
        AttributeDefinitions: [
            {
                AttributeName: "title",
                AttributeType: "S",
            },
            {
                AttributeName: "year",
                AttributeType: "N",
            },
        ],
        KeySchema: [
            {
                AttributeName: "title",
                KeyType: "HASH",
            },
            {
                AttributeName: "year",
                KeyType: "RANGE",
            },
        ],
        ProvisionedThroughput: {
            ReadCapacityUnits: 5,
            WriteCapacityUnits: 5,
        },
        TableName: tableName,
    };
    const [error, result] = await to(ddbClient.send(new CreateTableCommand(params)));
    if (error) {
        console.log(error)
    }
    console.log("Waiting for table to be created...");
    wait(10000);
    console.log("Table created. Table name is ", result.TableDescription.TableName);
    const newTableParams = {
        TableName: tableName,
        Item: {
            title: newMovieName,
            year: newMovieYear,
        },
    };
    console.log("Adding movie...");
    const [error1, result1] = await to(ddbDocClient.send(new PutCommand(newTableParams)));
    console.log("Success - single movie added.");
    if (error1) {
        console.log(error1)
    }
    // Get the movie data parse to convert into a JSON object.
    const allMovies = JSON.parse(fs.readFileSync("../../../../../../resources/sample_files/movies.json", "utf8"));
    // Split the table into segments of 25.
    const dataSegments = R.splitEvery(25, allMovies);
    // Loop batch write operation 10 times to upload 250 items.
    console.log("Writing movies in batch to table...");
    for (let i = 0; i < 10; i++) {
        const segment = dataSegments[i];
        for (let j = 0; j < 25; j++) {
            const batchWriteParams = {
                RequestItems: {
                    [tableName]: [
                        {
                            // Destination Amazon DynamoDB table name.
                            PutRequest: {
                                Item: {
                                    year: segment[j].year,
                                    title: segment[j].title,
                                    info: segment[j].info,
                                },
                            },
                        },
                    ],
                },
            };
            const [error2, result2] = await to(ddbDocClient.send(new BatchWriteCommand(batchWriteParams)));
            if (error2) {
                console.log(error2)
            }
        }
    }

    wait(20000);
    console.log("Success, movies written to table.");
    const updateMovieParams = {
        TableName: tableName,
        Key: {
            title: existingMovieName,
            year: existingMovieYear
        },
        // Define expressions for the new or updated attributes.
        ProjectionExpression: "#r",
        ExpressionAttributeNames: {"#r": "rank"},
        UpdateExpression: "set info.plot = :p, info.#r = :r",
        ExpressionAttributeValues: {
            ":p": newMoviePlot,
            ":r": newMovieRank
        },
        ReturnValues: "ALL_NEW",
    };
    console.log("Updating a single movie...");
    const [error3, result3] = await to(ddbClient.send(new UpdateCommand(updateMovieParams)));
    if (error3) {
        console.log(error3)
    }
    console.log("Success - movie updated.");
    console.log("Getting movie....");
    const getMovieParams = {
        TableName: tableName,
        Key: {
            title: existingMovieName,
            year: existingMovieYear,
        },
    };
    const [error4, result4] = await to(ddbDocClient.send(new GetCommand(getMovieParams)));
    if (error4) {
        console.log(error4)
    }
    console.log("Success getting item. Item details :", result4.Item);
    console.log("Scanning table....");
    const scanTableCommands = {
        TableName: tableName,
        ProjectionExpression: "#r, #y, title",
        ExpressionAttributeNames: {"#r": "rank", "#y": "year"},
        FilterExpression: "title = :t and #y = :y and info.#r = :r",
        ExpressionAttributeValues: {
            ":r": newMovieRank,
            ":y": existingMovieYear,
            ":t": existingMovieName,
        },
    };
    const [error5, result5] = await to(ddbClient.send(new ScanCommand(scanTableCommands)));
    if (error5) {
        console.log(error5)
    }
    // Loop through and parse the response.
    for (let i = 0; i < result5.Items.length; i++) {
        console.log(
            "Scan successful. Items with rank of " +
            newMovieRank +
            " include:\n" +
            result5.Items[i].title +
            ", released in " +
            result5.Items[i].year
        );
    }
    const queryMovieParams = {
        ExpressionAttributeNames: {"#r": "rank", "#y": "year"},
        ProjectionExpression: "#r, #y, title",
        TableName: tableName,
        UpdateExpression: "set #r = :r, title = :t, #y = :y",
        ExpressionAttributeValues: {
            ":t": existingMovieName,
            ":y": existingMovieYear,
            ":r": newMovieRank,
        },
        KeyConditionExpression: "title = :t and #y = :y",
        FilterExpression: "info.#r = :r",
    };

    console.log("Querying table...");
    const [error6, result6] = await to(ddbDocClient.send(
        new QueryCommand(queryMovieParams))
    );
    if (error6) {
        console.log(error6)
    }
    // Loop through and parse the response.
    for (let i = 0; i < result6.Items.length; i++) {
        console.log(
            "Query successful. Items with rank of " +
            newMovieRank +
            " include:\n" +
            result6.Items[i].title +
            ", released in " +
            result6.Items[i].year
        );
    }
    console.log("Deleting a movie...");
    const deleteParams = {
        TableName: tableName,
        Key: {
            title: existingMovieName,
            year: existingMovieYear,
        },
    };
    const [error7, result7] = await to(ddbDocClient.send(
        new DeleteCommand(deleteParams)
    ));
    if (error7) {
        console.log(error7)
    }
    console.log("Success - item deleted");
    console.log("Deleting the table...");
    const deleteTableParams = {
        TableName: tableName,
    };
    wait(5000);
    const [error8, result8] = await to(ddbDocClient.send(
        new DeleteTableCommand(deleteTableParams)
    ));
    if (error8) {
        console.log(error8)
    }
    return "Run successfully"; // For unit tests.
    console.log("Success, table deleted.");

};
run(
    tableName,
    newMovieName,
    newMovieYear,
    existingMovieName,
    existingMovieYear,
    newMovieRank,
    newMoviePlot
);
// snippet-end:[javascript.dynamodb_scenarios.dynamodb_basics]
