/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
Scenario demonstrating how to do the following in an Amazon DymamoDB table using PartiQL:
- Get an item (Select)
- Update an item (Update)
- Delete an item (Delete)
- Put an item (Insert)

Running the code:
1. Change directories to the same directory as this file
(javascriptv3/example_code/dynamodb/scenarios/partiQL_basics/src).
2. Import this file as a module and then call `main()`. Do this from
another file, or use the following command:
`node -e 'import("./pariQL_basics.js").then(({ main }) => main())`

// snippet-start:[javascript.dynamodb_scenarios.partiQL_basics]
*/
import fs from "fs";
import {splitEvery} from "ramda";
import {
    ExecuteStatementCommand,
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
 * existingMovieName: string,
 * existingMovieYear: number }} keyUpdate
 * @returns
 */

const getMovie = async (tableName, keyUpdate) => {
    const {Items} = await ddbDocClient.send(
        new ExecuteStatementCommand({
            Statement: "SELECT * FROM " + tableName + " where title=? and year=?",
            Parameters: [keyUpdate.existingMovieName, keyUpdate.existingMovieYear]
        })
    )
    return Items
};
/**
 *
 * @param {string} tableName
 * @param {{
 * existingMovieName: string,
 * existingMovieYear: number,
 * newProducer: string }} keyUpdate
 */
const updateMovie = async (
    tableName, keyUpdate
) => {
    await ddbClient.send(
        new ExecuteStatementCommand({
            Statement:
                "UPDATE " +
                tableName +
                " SET Producer=? where title=? and year=?",
            Parameters: [
                keyUpdate.newProducer,
                keyUpdate.existingMovieName,
                keyUpdate.existingMovieYear
            ],
        })
    );
};

/**
 *
 * @param {string} tableName
 * @param {{ existingMovieName: string, existingMovieYear: number }} key
 */
const deleteMovie = async (tableName, key) => {
    await ddbDocClient.send(
        new ExecuteStatementCommand({
            Statement: "DELETE FROM " + tableName + " where title=? and year=?",
            Parameters: [key.existingMovieName, key.existingMovieYear],
        })
    );
};

/**
 *
 * @param {string} tableName
 * @param {{ newMovieName: string, newMovieYear: number }} key
 */

const putItem = async (tableName, key) => {
    const command = new ExecuteStatementCommand({
        Statement:
            "INSERT INTO " + tableName + " value  {'title':?, 'year':?}",
        Parameters: [key.newMovieName, key.newMovieYear],
    })

    await ddbDocClient.send(command);
};

/**
 * @param {{ title: string, info: { plot: string, rank: number }, year: number }} movie
 */
const logMovie = (movie) => {
    console.log(` | Title: "${movie.title}".`);
    console.log(` | Plot: "${movie.info.plot}`);

    console.log(` | Year: ${movie.year}`);
    console.log(` | Rank: ${movie.info.rank}`);
};

/**
 *
 * @param {{ title: string, info: { plot: string, rank: number }, year: number }[]} movies
 */
const logMovies = (movies) => {
    console.log("\n");
    movies.forEach((movie, i) => {
        if (i > 0) {
            console.log("-".repeat(80));
        }
        logMovie(movie);
    });
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

export const runScenario = async ({tableName, newMovieName, newMovieYear, existingMovieName, existingMovieYear, newProducer, moviesPath}) => {
    console.log(`Creating table named: ${tableName}`);
    await createTable(tableName);
    console.log(`\nTable created.`);
    console.log("\nWriting hundreds of movies in batches.");
    const {movieCount} = await batchWriteMoviesFromFile(tableName, moviesPath);
    console.log(`\nWrote ${movieCount} movies to database.`);
    console.log(`\nGetting "${existingMovieName}."`);
    const originalMovie = await getMovie(
        tableName,
        {
            existingMovieName,
            existingMovieYear
        }
    );
    logMovies(originalMovie);
    console.log(`\nUpdating "${existingMovieName}" with a new producer.`);
    await updateMovie(tableName, {
        newProducer,
        existingMovieName,
        existingMovieYear
    });
    console.log(`\n"${existingMovieName}" updated.`);
    console.log(`\nDeleting "${existingMovieName}."`);
    await deleteMovie(tableName, {existingMovieName, existingMovieYear});
    console.log(`\n"${existingMovieName} deleted.`);
    console.log(`\nAdding "${newMovieName}" to ${tableName}.`);
    await putItem(tableName, {newMovieName, newMovieYear});
    console.log("\nSuccess - single movie added.");
    console.log(`Deleting ${tableName}.`);
    await deleteTable(tableName);
    console.log(`${tableName} deleted.`);
};

const main = async () => {
    const args = {
        tableName: "myNewTable",
        newMovieName: "myMovieName",
        newMovieYear: 2022,
        existingMovieName: "This Is the End",
        existingMovieYear: 2013,
        newProducer: "Amazon Movies",
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
// snippet-end:[javascript.dynamodb_scenarios.partiQL_basics]
