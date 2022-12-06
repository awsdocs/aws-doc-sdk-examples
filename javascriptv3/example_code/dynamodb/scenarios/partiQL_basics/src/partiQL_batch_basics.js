/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
Scenario demonstrating how to do the following batch jobs an Amazon DymamoDB table using PartiQL:
- Get an item (Select)
- Update an item (Update)
- Delete an item (Delete)
- Put an item (Insert)

Running the code:
1. Change directories to the same directory as this file
(javascriptv3/example_code/dynamodb/scenarios/partiQL_batch_basics/src).
2. Import this file as a module and then call `main()`. Do this from
another file, or use the following command:
`node -e 'import("./pariQL_basics.js").then(({ main }) => main())`

// snippet-start:[javascript.dynamodb_scenarios.partiQL_batch_basics]
*/
import fs from "fs";
import {splitEvery} from "ramda";
import {
    BatchExecuteStatementCommand,
    BatchWriteCommand
} from "@aws-sdk/lib-dynamodb";
import {
    CreateTableCommand,
    DeleteTableCommand,
    waitUntilTableExists,
    waitUntilTableNotExists,
} from "@aws-sdk/client-dynamodb";

import {ddbClient} from "../libs/ddbClient.js";
import {ddbDocClient} from "../libs/ddbDocClient.js";

/**
 * @param {string} tableName
 */
const createTable = async (tableName) => {
    await ddbClient.send(
        new CreateTableCommand({
            TableName: tableName,
            AttributeDefinitions: [
                {
                    AttributeName: "year",
                    AttributeType: "N",
                },
                {
                    AttributeName: "title",
                    AttributeType: "S",
                }
            ],
            KeySchema: [
                {
                    AttributeName: "year",
                    KeyType: "HASH",
                },
                {
                    AttributeName: "title",
                    KeyType: "RANGE",
                },
            ],
            // Enables "on-demand capacity mode".
            BillingMode: "PAY_PER_REQUEST"
        })
    );
    await waitUntilTableExists(
        {client: ddbClient, maxWaitTime: 15, maxDelay: 2, minDelay: 1},
        {TableName: tableName}
    );
};

/**
 *
 * @param {string} tableName
 * @param {string} filePath
 * @returns { { movieCount: number } } The number of movies written to the database.
 */
const batchWriteMoviesFromFile = async (tableName, filePath) => {
    const fileContents = fs.readFileSync(filePath);
    const movies = JSON.parse(fileContents, "utf8");

    // Map movies to RequestItems.
    const putMovieRequestItems = movies.map(({year, title, info}) => ({
        PutRequest: {Item: {year, title, info}},
    }));

    // Organize RequestItems into batches of 25. 25 is the max number of items in a batch request.
    const putMovieBatches = splitEvery(25, putMovieRequestItems);
    const batchCount = putMovieBatches.length;

    // Map batches to promises.
    const batchRequests = putMovieBatches.map(async (batch, i) => {
        const command = new BatchWriteCommand({
            RequestItems: {
                [tableName]: batch,
            },
        });

        await ddbDocClient.send(command).then(() => {
            console.log(
                `Wrote batch ${i + 1} of ${batchCount} with ${batch.length} items.`
            );
        });
    });

    // Wait for all batch requests to resolve.
    await Promise.all(batchRequests);

    return {movieCount: movies.length};
};

/**
 *
 * @param {string} tableName
 * @param {{
 * existingMovieName1: string,
 * existingMovieYear1: number }} keyUpdate1
 * @param {{
 * existingMovieName2: string,
 * existingMovieYear2: number }} keyUpdate2
 */

const batchGetMovies = async (tableName, keyUpdate1, keyUpdate2) => {
    const Items = await ddbDocClient.send(
        new BatchExecuteStatementCommand({
                Statements: [
                    {
                        Statement:
                            "SELECT * FROM " + tableName + " where title=? and year=?",
                        Parameters: [keyUpdate1.existingMovieName1, keyUpdate1.existingMovieYear1]
                    },
                    {
                        Statement:
                            "SELECT * FROM " + tableName + " where title=? and year=?",
                        Parameters: [keyUpdate2.existingMovieName2, keyUpdate2.existingMovieYear2]
                    }
                ]
            }
        )
    )
    return Items
};
/**
 *
 * @param {string} tableName
 * @param {{
 * existingMovieName1: string,
 * existingMovieYear1: number,
 * newProducer1: string }} keyUpdate1
 * @param {{
 * existingMovieName2: string,
 * existingMovieYear2: number,
 * newProducer2: string }} keyUpdate2
 */
const batchUpdateMovies = async (
    tableName,
    keyUpdate1, keyUpdate2
) => {
    await ddbDocClient.send(
        new BatchExecuteStatementCommand({
            Statements: [
                {
                    Statement:
                        "UPDATE " +
                        tableName +
                        " SET Producer=? where title=? and year=?",
                    Parameters: [keyUpdate1.newProducer1, keyUpdate1.existingMovieName1, keyUpdate1.existingMovieYear1
                    ],
                },
                {
                    Statement:
                        "UPDATE " +
                        tableName +
                        " SET Producer=? where title=? and year=?",
                    Parameters: [
                        keyUpdate2.newProducer2, keyUpdate2.existingMovieName2, keyUpdate2.existingMovieYear2
                    ],
                }
            ],
        })
    );
};

/**
 *
 * @param {string} tableName
 * @param {{ existingMovieName1: string, existingMovieYear1: number }} key1,
 * @param {{ existingMovieName2: string, existingMovieYear2: number}} key2
 */
const batchDeleteMovies = async (tableName, key1, key2) => {
    await ddbDocClient.send(
        new BatchExecuteStatementCommand({
            Statements: [
                {
                    Statement:
                        "DELETE FROM " + tableName + " where title=? and year=?",
                    Parameters: [key1.existingMovieName1, key1.existingMovieYear1],
                },
                {
                    Statement:
                        "DELETE FROM " + tableName + " where title=? and year=?",
                    Parameters: [key2.existingMovieName2, key2.existingMovieYear2],
                },
            ],
        }))
};

/**
 *
 * @param {string} tableName
 * @param {{ newMovieName1: string, newMovieYear1: number }} key1,
 * @param {{ newMovieName2: string, newMovieYear2: number }} key2
 */

const batchPutItems = async (tableName, key1, key2) => {
    const command = new BatchExecuteStatementCommand({
        Statements: [
            {
                Statement:
                    "INSERT INTO " + tableName + " value  {'title':?, 'year':?}",
                Parameters: [key1.newMovieName1, key1.newMovieYear1],
            },
            {
                Statement:
                    "INSERT INTO " + tableName + " value  {'title':?, 'year':?}",
                Parameters: [key2.newMovieName2, key2.newMovieYear2],
            }
        ]
    })

    await ddbDocClient.send(command);
};

/**
 *
 * @param {{ title: string, info: { plot: string, rank: number }, year: number }[]} movies
 */
const logMovies = (Items) => {
    console.log("Success. The query return the following data.");
    for (let i = 0; i < Items.Responses.length; i++) {
        console.log(Items.Responses[i].Item);
    }
};


/**
 *
 * @param {*} tableName
 */
const deleteTable = async (tableName) => {
    await ddbDocClient.send(new DeleteTableCommand({TableName: tableName}));
    await waitUntilTableNotExists(
        {
            client: ddbClient,
            maxWaitTime: 10,
            maxDelay: 2,
            minDelay: 1,
        },
        {TableName: tableName}
    );
};

export const runScenario = async ({
                                      tableName,
                                      newMovieName1,
                                      newMovieYear1,
                                      newMovieName2,
                                      newMovieYear2,
                                      existingMovieName1,
                                      existingMovieYear1,
                                      existingMovieName2,
                                      existingMovieYear2,
                                      newProducer1,
                                      newProducer2,
                                      moviesPath
                                  }) => {
    await createTable(tableName);
    console.log(`Creating table named: ${tableName}`);
    console.log(`\nTable created.`);
    console.log("\nWriting hundreds of movies in batches.");
    const {movieCount} = await batchWriteMoviesFromFile(tableName, moviesPath);
    console.log(`\nWrote ${movieCount} movies to database.`);
    console.log(`\nGetting "${existingMovieName1}" and "${existingMovieName2}"`);
    const originalMovies = await batchGetMovies(
        tableName,
        {
            existingMovieName1,
            existingMovieYear1
        },
        {
            existingMovieName2,
            existingMovieYear2
        }
    );
    logMovies(originalMovies);
    console.log(`\nUpdating "${existingMovieName1} and ${existingMovieName2} with new producers.`);
    await batchUpdateMovies(tableName,
        {
            existingMovieName1,
            existingMovieYear1,
            newProducer1
        },
        {
            existingMovieName2,
            existingMovieYear2,
            newProducer2
        }
    );
    //console.log(`\n"${existingMovieName1}" and ${existingMovieName2}" updated.`);
    console.log(`\nDeleting "${existingMovieName1}."`);
    await batchDeleteMovies(tableName, {
            existingMovieName1,
            existingMovieYear1
        },
        {
            existingMovieName2,
            existingMovieYear2
        }
    );
    console.log(`\n"${existingMovieName1} and ${existingMovieName2} deleted.`);
    console.log(`\nAdding "${newMovieName1}" and "${newMovieName2}" to ${tableName}.`);
    await batchPutItems(tableName, {newMovieName1, newMovieYear1}, {newMovieName2, newMovieYear2});
    console.log("\nSuccess - single movie added.");
    console.log(`Deleting ${tableName}.`);
    await deleteTable(tableName);
    console.log(`${tableName} deleted.`);
};

const main = async () => {
    const args = {
        tableName: "myNewTable",
        newMovieName1: "myMovieName1",
        newMovieYear1: 2022,
        newMovieName2: "myMovieName2",
        newMovieYear2: 2023,
        existingMovieName1: "This Is the End",
        existingMovieYear1: 2013,
        existingMovieName2: "Deep Impact",
        existingMovieYear2: 1998,
        newProducer1: "Amazon Movies",
        newProducer2: "Amazon Movies2",
        moviesPath: "../../../../../../resources/sample_files/movies.json",
    };

    try {
        await runScenario(args);
    } catch (err) {
        // Some extra error handling here to be sure the table is cleaned up if something
        // goes wrong during the scenario run.

        console.error(err);

        const tableName = args.tableName;

        if (tableName) {
            console.log(`Attempting to delete ${tableName}`);
            await ddbClient
                .send(new DeleteTableCommand({TableName: tableName}))
                .then(() => console.log(`\n${tableName} deleted.`))
                .catch((err) => console.error(`\nFailed to delete ${tableName}.`, err));
        }
    }
};

export {main};
// snippet-end:[javascript.dynamodb_scenarios.partiQL_batch_basics]
