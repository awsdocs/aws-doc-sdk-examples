<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

/**
 * Purpose
 * Shows how to use the AWS SDK for PHP with Amazon DynamoDB and PartiQL to
 * create and use a table that stores data about movies.
 * 1. Create the table and load it with data from a JSON file.
 * 2. Perform basic operations like adding, getting, and updating data for individual movies.
 * 3. Use conditional expressions to update movie data only when it meets certain criteria.
 * 4. Query and scan the table to retrieve movie data that meets varying criteria.
 *
 * Run the following command to install dependencies:
 * composer install
 *
 * After your composer dependencies are installed, you can run the interactive getting started file directly with the
 * following from the `aws-doc-sdk-examples\php\dynamodb\partiql_basics` directory:
 * php Runner.php
 *
 * Alternatively, you can have the choices automatically selected by running the file as part of a PHPUnit test with the
 * following:
 * vendor\bin\phpunit PartiQLBasicsTests.php
 *
 **/

# snippet-start:[php.example_code.dynamodb.partiql_basics.scenario]
namespace DynamoDb\PartiQL_Basics;

use Aws\DynamoDb\Marshaler;
use DynamoDb;
use DynamoDb\DynamoDBAttribute;

class GettingStartedWithPartiQL
{
    public function run()
    {
        echo("--------------------------------------\n");
        print("Welcome to the Amazon DynamoDB - PartiQL getting started demo using PHP!\n");
        echo("--------------------------------------\n");

# snippet-start:[php.example_code.dynamodb.partiql_basics.startService]
        $uuid = uniqid();
        $service = new DynamoDb\DynamoDBService();
# snippet-end:[php.example_code.dynamodb.partiql_basics.startService]

# snippet-start:[php.example_code.dynamodb.partiql_basics.createTable]
        $tableName = "partiql_demo_table_$uuid";
        $service->createTable(
            $tableName,
            [
                new DynamoDBAttribute('year', 'N', 'HASH'),
                new DynamoDBAttribute('title', 'S', 'RANGE')
            ]
        );
# snippet-end:[php.example_code.dynamodb.partiql_basics.createTable]

        echo "Waiting for table...";
        $service->dynamoDbClient->waitUntil("TableExists", ['TableName' => $tableName]);
        echo "table $tableName found!\n";

# snippet-start:[php.example_code.dynamodb.partiql_basics.putItem]
        echo "What's the name of the last movie you watched?\n";
        while (empty($movieName)) {
            $movieName = testable_readline("Movie name: ");
        }
        echo "And what year was it released?\n";
        $movieYear = "year";
        while (!is_numeric($movieYear) || intval($movieYear) != $movieYear) {
            $movieYear = testable_readline("Year released: ");
        }
# snippet-start:[php.example_code.dynamodb.partiql_basics.key]
        $key = [
            'Item' => [
                'year' => [
                    'N' => "$movieYear",
                ],
                'title' => [
                    'S' => $movieName,
                ],
            ],
        ];
# snippet-end:[php.example_code.dynamodb.partiql_basics.key]
        list($statement, $parameters) = $service->buildStatementAndParameters("INSERT", $tableName, $key);
        $service->insertItemByPartiQL($statement, $parameters);
# snippet-end:[php.example_code.dynamodb.partiql_basics.putItem]

        echo "How would you rate the movie from 1-10?\n";
        $rating = 0;
        while (!is_numeric($rating) || intval($rating) != $rating || $rating < 1 || $rating > 10) {
            $rating = testable_readline("Rating (1-10): ");
        }
        echo "What was the movie about?\n";
        while (empty($plot)) {
            $plot = testable_readline("Plot summary: ");
        }
        $attributes = [
            new DynamoDBAttribute('rating', 'N', 'HASH', $rating),
            new DynamoDBAttribute('plot', 'S', 'RANGE', $plot),
        ];

        list($statement, $parameters) = $service->buildStatementAndParameters("UPDATE", $tableName, $key, $attributes);
        $service->updateItemByPartiQL($statement, $parameters);
        echo "Movie added and updated.\n";



        $batch = json_decode(loadMovieData());

        $service->writeBatch($tableName, $batch);

# snippet-start:[php.example_code.dynamodb.partiql_basics.getItem]
        $movie = $service->getItemByPartiQL($tableName, $key);
        echo "\nThe movie {$movie['Items'][0]['title']['S']} was released in {$movie['Items'][0]['year']['N']}.\n";
# snippet-end:[php.example_code.dynamodb.partiql_basics.getItem]
# snippet-start:[php.example_code.dynamodb.partiql_basics.updateItem]
        echo "What rating would you like to give {$movie['Items'][0]['title']['S']}?\n";
        $rating = 0;
        while (!is_numeric($rating) || intval($rating) != $rating || $rating < 1 || $rating > 10) {
            $rating = testable_readline("Rating (1-10): ");
        }
        $attributes = [
            new DynamoDBAttribute('rating', 'N', 'HASH', $rating),
            new DynamoDBAttribute('plot', 'S', 'RANGE', $plot)
        ];
        list($statement, $parameters) = $service->buildStatementAndParameters("UPDATE", $tableName, $key, $attributes);
        $service->updateItemByPartiQL($statement, $parameters);
# snippet-end:[php.example_code.dynamodb.partiql_basics.updateItem]

        $movie = $service->getItemByPartiQL($tableName, $key);
        echo "Okay, you have rated {$movie['Items'][0]['title']['S']} as a {$movie['Items'][0]['rating']['N']}\n";

# snippet-start:[php.example_code.dynamodb.partiql_basics.deleteItem]
        $service->deleteItemByPartiQL($statement, $parameters);
        echo "But, bad news, this was a trap. That movie has now been deleted because of your rating...harsh.\n";
# snippet-end:[php.example_code.dynamodb.partiql_basics.deleteItem]

        echo "That's okay though. The book was better. Now, for something lighter, in what year were you born?\n";
        $birthYear = "not a number";
        while (!is_numeric($birthYear) || $birthYear >= date("Y")) {
            $birthYear = testable_readline("Birth year: ");
        }
# snippet-start:[php.example_code.dynamodb.partiql_basics.query]
        $birthKey = [
            'Key' => [
                'year' => [
                    'N' => "$birthYear",
                ],
            ],
        ];
        $result = $service->query($tableName, $birthKey);
# snippet-end:[php.example_code.dynamodb.partiql_basics.query]
        $marshal = new Marshaler();
        echo "Here are the movies in our collection released the year you were born:\n";
        $oops = "Oops! There were no movies released in that year (that we know of).\n";
        $display = "";
        foreach ($result['Items'] as $movie) {
            $movie = $marshal->unmarshalItem($movie);
            $display .= $movie['title'] . "\n";
        }
        echo ($display) ?: $oops;

# snippet-start:[php.example_code.dynamodb.partiql_basics.scan]
        $yearsKey = [
            'Key' => [
                'year' => [
                    'N' => [
                        'minRange' => 1990,
                        'maxRange' => 1999,
                    ],
                ],
            ],
        ];
        $filter = "year between 1990 and 1999";
        echo "\nHere's a list of all the movies released in the 90s:\n";
        $result = $service->scan($tableName, $yearsKey, $filter);
        foreach ($result['Items'] as $movie) {
            $movie = $marshal->unmarshalItem($movie);
            echo $movie['title'] . "\n";
        }
# snippet-end:[php.example_code.dynamodb.partiql_basics.scan]

        echo "\nCleaning up this demo by deleting table $tableName...\n";
        $service->deleteTable($tableName);
    }
}
# snippet-end:[php.example_code.dynamodb.partiql_basics.scenario]
