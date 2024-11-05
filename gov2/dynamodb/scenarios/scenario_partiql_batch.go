// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

// snippet-start:[gov2.dynamodb.Scenario_PartiQLBatch]

import (
	"context"
	"fmt"
	"log"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/dynamodb/actions"
)

// RunPartiQLBatchScenario shows you how to use the AWS SDK for Go
// to run batches of PartiQL statements to query a table that stores data about movies.
//
//   - Use batches of PartiQL statements to add, get, update, and delete data for
//     individual movies.
//
// This example creates an Amazon DynamoDB service client from the specified sdkConfig so that
// you can replace it with a mocked or stubbed config for unit testing.
//
// This example creates and deletes a DynamoDB table to use during the scenario.
func RunPartiQLBatchScenario(ctx context.Context, sdkConfig aws.Config, tableName string) {
	defer func() {
		if r := recover(); r != nil {
			fmt.Printf("Something went wrong with the demo.")
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Println("Welcome to the Amazon DynamoDB PartiQL batch demo.")
	log.Println(strings.Repeat("-", 88))

	tableBasics := actions.TableBasics{
		DynamoDbClient: dynamodb.NewFromConfig(sdkConfig),
		TableName:      tableName,
	}
	runner := actions.PartiQLRunner{
		DynamoDbClient: dynamodb.NewFromConfig(sdkConfig),
		TableName:      tableName,
	}

	exists, err := tableBasics.TableExists(ctx)
	if err != nil {
		panic(err)
	}
	if !exists {
		log.Printf("Creating table %v...\n", tableName)
		_, err = tableBasics.CreateMovieTable(ctx)
		if err != nil {
			panic(err)
		} else {
			log.Printf("Created table %v.\n", tableName)
		}
	} else {
		log.Printf("Table %v already exists.\n", tableName)
	}
	log.Println(strings.Repeat("-", 88))

	currentYear, _, _ := time.Now().Date()
	customMovies := []actions.Movie{{
		Title: "House PartiQL",
		Year:  currentYear - 5,
		Info: map[string]interface{}{
			"plot":   "Wacky high jinks result from querying a mysterious database.",
			"rating": 8.5}}, {
		Title: "House PartiQL 2",
		Year:  currentYear - 3,
		Info: map[string]interface{}{
			"plot":   "Moderate high jinks result from querying another mysterious database.",
			"rating": 6.5}}, {
		Title: "House PartiQL 3",
		Year:  currentYear - 1,
		Info: map[string]interface{}{
			"plot":   "Tepid high jinks result from querying yet another mysterious database.",
			"rating": 2.5},
	},
	}

	log.Printf("Inserting a batch of movies into table '%v'.\n", tableName)
	err = runner.AddMovieBatch(ctx, customMovies)
	if err == nil {
		log.Printf("Added %v movies to the table.\n", len(customMovies))
	}
	log.Println(strings.Repeat("-", 88))

	log.Println("Getting data for a batch of movies.")
	movies, err := runner.GetMovieBatch(ctx, customMovies)
	if err == nil {
		for _, movie := range movies {
			log.Println(movie)
		}
	}
	log.Println(strings.Repeat("-", 88))

	newRatings := []float64{7.7, 4.4, 1.1}
	log.Println("Updating a batch of movies with new ratings.")
	err = runner.UpdateMovieBatch(ctx, customMovies, newRatings)
	if err == nil {
		log.Printf("Updated %v movies with new ratings.\n", len(customMovies))
	}
	log.Println(strings.Repeat("-", 88))

	log.Println("Getting projected data from the table to verify our update.")
	log.Println("Using a page size of 2 to demonstrate paging.")
	projections, err := runner.GetAllMovies(ctx, 2)
	if err == nil {
		log.Println("All movies:")
		for _, projection := range projections {
			log.Println(projection)
		}
	}
	log.Println(strings.Repeat("-", 88))

	log.Println("Deleting a batch of movies.")
	err = runner.DeleteMovieBatch(ctx, customMovies)
	if err == nil {
		log.Printf("Deleted %v movies.\n", len(customMovies))
	}

	err = tableBasics.DeleteTable(ctx)
	if err == nil {
		log.Printf("Deleted table %v.\n", tableBasics.TableName)
	}

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// snippet-end:[gov2.dynamodb.Scenario_PartiQLBatch]
