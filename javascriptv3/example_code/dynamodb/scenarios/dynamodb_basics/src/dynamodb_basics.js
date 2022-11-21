/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
This scenario demonstrates how to:
    - Create a table that can hold movie data.
    - Write movie data to the table from a sample JSON file.
    - Put, get, update, and delete a single movie in the table.
    - Scan for movies that were released in a range of years.
    - Query for movies that were released in a given year.
    - Delete the table.


Running the code:
1. Change directories to the same directory as this file
(javascriptv3/example_code/dynamodb/scenarios/dynamodb_basics/src).
2. Import this file as a module and then call `main()`. Do this from 
another file, or use the following command:
`node -e 'import("./dynamodb_basics.js").then(({ main }) => main())`
*/

// snippet-start:[javascript.dynamodb_scenarios.dynamodb_basics]
import fs from "fs";
import { splitEvery } from "ramda";
import {
  PutCommand,
  GetCommand,
  UpdateCommand,
  BatchWriteCommand,
  DeleteCommand,
  ScanCommand,
  QueryCommand,
} from "@aws-sdk/lib-dynamodb";
import {
  CreateTableCommand,
  DeleteTableCommand,
  waitUntilTableExists,
  waitUntilTableNotExists,
} from "@aws-sdk/client-dynamodb";

import { ddbClient } from "../libs/ddbClient.js";
import { ddbDocClient } from "../libs/ddbDocClient.js";

/**
 * @param {string} tableName
 */
const createTable = async (tableName) => {
  await ddbClient.send(
    new CreateTableCommand({
      AttributeDefinitions: [
        {
          AttributeName: "year",
          AttributeType: "N",
        },
        {
          AttributeName: "title",
          AttributeType: "S",
        },
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
      BillingMode: "PAY_PER_REQUEST",
      TableName: tableName,
    })
  );
  await waitUntilTableExists(
    { client: ddbClient, maxWaitTime: 15, maxDelay: 2, minDelay: 1 },
    { TableName: tableName }
  );
};

/**
 *
 * @param {string} tableName
 * @param {Record<string, any> | undefined} attributes
 */
const putItem = async (tableName, attributes) => {
  const command = new PutCommand({
    TableName: tableName,
    Item: attributes,
  });

  await ddbDocClient.send(command);
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
  const putMovieRequestItems = movies.map(({ year, title, info }) => ({
    PutRequest: { Item: { year, title, info } },
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

  return { movieCount: movies.length };
};

/**
 *
 * @param {string} tableName
 * @param {{
 * existingMovieName: string,
 * existingMovieYear: string,
 * newMoviePlot: string,
 * newMovieRank: string}} keyUpdate
 */
const updateMovie = async (
  tableName,
  { existingMovieName, existingMovieYear, newMoviePlot, newMovieRank }
) => {
  await ddbClient.send(
    new UpdateCommand({
      TableName: tableName,
      Key: {
        title: existingMovieName,
        year: existingMovieYear,
      },
      // Define expressions for the new or updated attributes.
      ExpressionAttributeNames: { "#r": "rank" },
      UpdateExpression: "set info.plot = :p, info.#r = :r",
      ExpressionAttributeValues: {
        ":p": newMoviePlot,
        ":r": newMovieRank,
      },
      ReturnValues: "ALL_NEW",
    })
  );
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
 * @param {string} tableName
 * @param {string} title
 * @param {number} year
 * @returns
 */
const getMovie = async (tableName, title, year) => {
  const { Item } = await ddbDocClient.send(
    new GetCommand({
      TableName: tableName,
      Key: {
        title,
        year,
      },
      // By default, reads are eventually consistent. "ConsistentRead: true" represents
      // a strongly consistent read. This guarantees that the most up-to-date data is returned. It
      // can also result in higher latency and a potential for server errors.
      ConsistentRead: true,
    })
  );

  return Item;
};

/**
 *
 * @param {string} tableName
 * @param {{ title: string, year: number }} key
 */
const deleteMovie = async (tableName, key) => {
  await ddbDocClient.send(
    new DeleteCommand({
      TableName: tableName,
      Key: key,
    })
  );
};

/**
 *
 * @param {string} tableName
 * @param {number} startYear
 * @param {number} endYear
 * @param {Record<string, any>} startKey
 * @returns {Promise<{}[]>}
 */
const findMoviesBetweenYears = async (
  tableName,
  startYear,
  endYear,
  startKey = undefined
) => {
  const { Items, LastEvaluatedKey } = await ddbClient.send(
    new ScanCommand({
      ConsistentRead: true,
      TableName: tableName,
      ExpressionAttributeNames: { "#y": "year" },
      FilterExpression: "#y BETWEEN :y1 AND :y2",
      ExpressionAttributeValues: { ":y1": startYear, ":y2": endYear },
      ExclusiveStartKey: startKey,
    })
  );

  if (LastEvaluatedKey) {
    return Items.concat(
      await findMoviesBetweenYears(
        tableName,
        startYear,
        endYear,
        LastEvaluatedKey
      )
    );
  } else {
    return Items;
  }
};

/**
 *
 * @param {string} tableName
 * @param {number} year
 * @returns
 */
const queryMoviesByYear = async (tableName, year) => {
  const command = new QueryCommand({
    ConsistentRead: true,
    ExpressionAttributeNames: { "#y": "year" },
    TableName: tableName,
    ExpressionAttributeValues: {
      ":y": year,
    },
    KeyConditionExpression: "#y = :y",
  });

  const { Items } = await ddbDocClient.send(command);

  return Items;
};

/**
 *
 * @param {*} tableName
 */
const deleteTable = async (tableName) => {
  await ddbDocClient.send(new DeleteTableCommand({ TableName: tableName }));
  await waitUntilTableNotExists(
    {
      client: ddbClient,
      maxWaitTime: 10,
      maxDelay: 2,
      minDelay: 1,
    },
    { TableName: tableName }
  );
};

export const runScenario = async ({
  tableName,
  newMovieName,
  newMovieYear,
  existingMovieName,
  existingMovieYear,
  newMovieRank,
  newMoviePlot,
  moviesPath,
}) => {
  console.log(`Creating table named: ${tableName}`);
  await createTable(tableName);
  console.log(`\nTable created.`);

  console.log(`\nAdding "${newMovieName}" to ${tableName}.`);
  await putItem(tableName, { title: newMovieName, year: newMovieYear });
  console.log("\nSuccess - single movie added.");

  console.log("\nWriting hundreds of movies in batches.");
  const { movieCount } = await batchWriteMoviesFromFile(tableName, moviesPath);
  console.log(`\nWrote ${movieCount} movies to database.`);

  console.log(`\nGetting "${existingMovieName}."`);
  const originalMovie = await getMovie(
    tableName,
    existingMovieName,
    existingMovieYear
  );
  logMovie(originalMovie);

  console.log(`\nUpdating "${existingMovieName}" with a new plot and rank.`);
  await updateMovie(tableName, {
    existingMovieName,
    existingMovieYear,
    newMoviePlot,
    newMovieRank,
  });
  console.log(`\n"${existingMovieName}" updated.`);

  console.log(`\nGetting latest info for "${existingMovieName}"`);
  const updatedMovie = await getMovie(
    tableName,
    existingMovieName,
    existingMovieYear
  );
  logMovie(updatedMovie);

  console.log(`\nDeleting "${newMovieName}."`);
  await deleteMovie(tableName, { title: newMovieName, year: newMovieYear });
  console.log(`\n"${newMovieName} deleted.`);

  const [scanY1, scanY2] = [1985, 2003];
  console.log(
    `\nScanning ${tableName} for movies that premiered between ${scanY1} and ${scanY2}.`
  );
  const scannedMovies = await findMoviesBetweenYears(tableName, scanY1, scanY1);
  logMovies(scannedMovies);

  const queryY = 2003;
  console.log(`Querying ${tableName} for movies that premiered in ${queryY}.`);
  const queriedMovies = await queryMoviesByYear(tableName, queryY);
  logMovies(queriedMovies);

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
    newMovieRank: 200,
    newMoviePlot: "A coder cracks code...",
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
        .send(new DeleteTableCommand({ TableName: tableName }))
        .then(() => console.log(`\n${tableName} deleted.`))
        .catch((err) => console.error(`\nFailed to delete ${tableName}.`, err));
    }
  }
};

export { main };
// snippet-end:[javascript.dynamodb_scenarios.dynamodb_basics]
