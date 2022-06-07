// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"fmt"
	"log"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/picante-io/aws-doc-sdk-examples/gov2/dynamodb/actions"
)

// snippet-start:[gov2.dynamodb.Scenario_PartiQLSingle]

// RunPartiQLSingleScenario shows you how to use the AWS SDK for Go
// to use PartiQL to query a table that stores data about movies.
//
// * Use PartiQL statements to add, get, update, and delete data for individual movies.
//
// This example creates an Amazon DynamoDB service client from the specified sdkConfig so that
// you can replace it with a mocked or stubbed config for unit testing.
//
// This example creates and deletes a DynamoDB table to use during the scenario.
func RunPartiQLSingleScenario(sdkConfig aws.Config, tableName string) {
	defer func() {
		if r := recover(); r != nil {
			fmt.Printf("Something went wrong with the demo.")
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Println("Welcome to the Amazon DynamoDB PartiQL single action demo.")
	log.Println(strings.Repeat("-", 88))

	tableBasics := actions.TableBasics{
		DynamoDbClient: dynamodb.NewFromConfig(sdkConfig),
		TableName:      tableName,
	}
	runner := actions.PartiQLRunner{
		DynamoDbClient: dynamodb.NewFromConfig(sdkConfig),
		TableName:      tableName,
	}

	exists, err := tableBasics.TableExists()
	if err != nil {
		panic(err)
	}
	if !exists {
		log.Printf("Creating table %v...\n", tableName)
		_, err = tableBasics.CreateMovieTable()
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
	customMovie := actions.Movie{
		Title: "24 Hour PartiQL People",
		Year:  currentYear,
		Info: map[string]interface{}{
			"plot":   "A group of data developers discover a new query language they can't stop using.",
			"rating": 9.9,
		},
	}

	log.Printf("Inserting movie '%v' released in %v.", customMovie.Title, customMovie.Year)
	err = runner.AddMovie(customMovie)
	if err == nil {
		log.Printf("Added %v to the movie table.\n", customMovie.Title)
	}
	log.Println(strings.Repeat("-", 88))

	log.Printf("Getting data for movie '%v' released in %v.", customMovie.Title, customMovie.Year)
	movie, err := runner.GetMovie(customMovie.Title, customMovie.Year)
	if err == nil {
		log.Println(movie)
	}
	log.Println(strings.Repeat("-", 88))

	newRating := 6.6
	log.Printf("Updating movie '%v' with a rating of %v.", customMovie.Title, newRating)
	err = runner.UpdateMovie(customMovie, newRating)
	if err == nil {
		log.Printf("Updated %v with a new rating.\n", customMovie.Title)
	}
	log.Println(strings.Repeat("-", 88))

	log.Printf("Getting data again to verify the update.")
	movie, err = runner.GetMovie(customMovie.Title, customMovie.Year)
	if err == nil {
		log.Println(movie)
	}
	log.Println(strings.Repeat("-", 88))

	log.Printf("Deleting movie '%v'.\n", customMovie.Title)
	err = runner.DeleteMovie(customMovie)
	if err == nil {
		log.Printf("Deleted %v.\n", customMovie.Title)
	}

	err = tableBasics.DeleteTable()
	if err == nil {
		log.Printf("Deleted table %v.\n", tableBasics.TableName)
	}

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// snippet-end:[gov2.dynamodb.Scenario_PartiQLSingle]
