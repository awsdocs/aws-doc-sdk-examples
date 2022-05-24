// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

// snippet-start:[gov2.dynamodb.PartiQLRunner.complete]
// snippet-start:[gov2.dynamodb.PartiQLRunner.struct]

// PartiQLRunner encapsulates the Amazon DynamoDB service actions used in the
// PartiQL examples. It contains a DynamoDB service client that is used to act on the
// specified table.
type PartiQLRunner struct {
	DynamoDbClient *dynamodb.Client
	TableName      string
}

// snippet-end:[gov2.dynamodb.PartiQLRunner.struct]

// snippet-start:[gov2.dynamodb.ExecuteStatement.Insert]

// AddMovie runs a PartiQL INSERT statement to add a movie to the DynamoDB table.
func (runner PartiQLRunner) AddMovie(movie Movie) error {
	params, err := attributevalue.MarshalList([]interface{}{movie.Title, movie.Year, movie.Info})
	if err != nil {
		panic(err)
	}
	_, err = runner.DynamoDbClient.ExecuteStatement(context.TODO(), &dynamodb.ExecuteStatementInput{
		Statement: aws.String(
			fmt.Sprintf("INSERT INTO \"%v\" VALUE {'title': ?, 'year': ?, 'info': ?}",
				runner.TableName)),
		Parameters: params,
	})
	if err != nil {
		log.Printf("Couldn't insert an item with PartiQL. Here's why: %v\n", err)
	}
	return err
}

// snippet-end:[gov2.dynamodb.ExecuteStatement.Insert]

// snippet-start:[gov2.dynamodb.ExecuteStatement.Select]

// GetMovie runs a PartiQL SELECT statement to get a movie from the DynamoDB table by
// title and year.
func (runner PartiQLRunner) GetMovie(title string, year int) (Movie, error) {
	var movie Movie
	params, err := attributevalue.MarshalList([]interface{}{title, year})
	if err != nil {
		panic(err)
	}
	response, err := runner.DynamoDbClient.ExecuteStatement(context.TODO(), &dynamodb.ExecuteStatementInput{
		Statement: aws.String(
			fmt.Sprintf("SELECT * FROM \"%v\" WHERE title=? AND year=?",
				runner.TableName)),
		Parameters: params,
	})
	if err != nil {
		log.Printf("Couldn't get info about %v. Here's why: %v\n", title, err)
	} else {
		err = attributevalue.UnmarshalMap(response.Items[0], &movie)
		if err != nil {
			log.Printf("Couldn't unmarshal response. Here's why: %v\n", err)
		}
	}
	return movie, err
}

// snippet-end:[gov2.dynamodb.ExecuteStatement.Select]

// snippet-start:[gov2.dynamodb.ExecuteStatement.Select.Projected]

// GetAllMovies runs a PartiQL SELECT statement to get all movies from the DynamoDB table.
// The results are projected to return only the title and rating of each movie.
func (runner PartiQLRunner) GetAllMovies() ([]map[string]interface{}, error) {
	var output []map[string]interface{}
	response, err := runner.DynamoDbClient.ExecuteStatement(context.TODO(), &dynamodb.ExecuteStatementInput{
		Statement: aws.String(
			fmt.Sprintf("SELECT title, info.rating FROM \"%v\"", runner.TableName)),
	})
	if err != nil {
		log.Printf("Couldn't get movies. Here's why: %v\n", err)
	} else {
		err = attributevalue.UnmarshalListOfMaps(response.Items, &output)
		if err != nil {
			log.Printf("Couldn't unmarshal response. Here's why: %v\n", err)
		}
	}
	return output, err
}

// snippet-end:[gov2.dynamodb.ExecuteStatement.Select.Projected]

// snippet-start:[gov2.dynamodb.ExecuteStatement.Update]

// UpdateMovie runs a PartiQL UPDATE statement to update the rating of a movie that
// already exists in the DynamoDB table.
func (runner PartiQLRunner) UpdateMovie(movie Movie, rating float64) error {
	params, err := attributevalue.MarshalList([]interface{}{rating, movie.Title, movie.Year})
	if err != nil {
		panic(err)
	}
	_, err = runner.DynamoDbClient.ExecuteStatement(context.TODO(), &dynamodb.ExecuteStatementInput{
		Statement: aws.String(
			fmt.Sprintf("UPDATE \"%v\" SET info.rating=? WHERE title=? AND year=?",
				runner.TableName)),
		Parameters: params,
	})
	if err != nil {
		log.Printf("Couldn't update movie %v. Here's why: %v\n", movie.Title, err)
	}
	return err
}

// snippet-end:[gov2.dynamodb.ExecuteStatement.Update]

// snippet-start:[gov2.dynamodb.ExecuteStatement.Delete]

// DeleteMovie runs a PartiQL DELETE statement to remove a movie from the DynamoDB table.
func (runner PartiQLRunner) DeleteMovie(movie Movie) error {
	params, err := attributevalue.MarshalList([]interface{}{movie.Title, movie.Year})
	if err != nil {
		panic(err)
	}
	_, err = runner.DynamoDbClient.ExecuteStatement(context.TODO(), &dynamodb.ExecuteStatementInput{
		Statement: aws.String(
			fmt.Sprintf("DELETE FROM \"%v\" WHERE title=? AND year=?",
				runner.TableName)),
		Parameters: params,
	})
	if err != nil {
		log.Printf("Couldn't delete %v from the table. Here's why: %v\n", movie.Title, err)
	}
	return err
}

// snippet-end:[gov2.dynamodb.ExecuteStatement.Delete]

// snippet-start:[gov2.dynamodb.BatchExecuteStatement.Insert]

// AddMovieBatch runs a batch of PartiQL INSERT statements to add multiple movies to the
// DynamoDB table.
func (runner PartiQLRunner) AddMovieBatch(movies []Movie) error {
	statementRequests := make([]types.BatchStatementRequest, len(movies))
	for index, movie := range movies {
		params, err := attributevalue.MarshalList([]interface{}{movie.Title, movie.Year, movie.Info})
		if err != nil {
			panic(err)
		}
		statementRequests[index] = types.BatchStatementRequest{
			Statement: aws.String(fmt.Sprintf(
				"INSERT INTO \"%v\" VALUE {'title': ?, 'year': ?, 'info': ?}", runner.TableName)),
			Parameters: params,
		}
	}

	_, err := runner.DynamoDbClient.BatchExecuteStatement(context.TODO(), &dynamodb.BatchExecuteStatementInput{
		Statements: statementRequests,
	})
	if err != nil {
		log.Printf("Couldn't insert a batch of items with PartiQL. Here's why: %v\n", err)
	}
	return err
}

// snippet-end:[gov2.dynamodb.BatchExecuteStatement.Insert]

// snippet-start:[gov2.dynamodb.BatchExecuteStatement.Select]

// GetMovieBatch runs a batch of PartiQL SELECT statements to get multiple movies from
// the DynamoDB table by title and year.
func (runner PartiQLRunner) GetMovieBatch(movies []Movie) ([]Movie, error) {
	statementRequests := make([]types.BatchStatementRequest, len(movies))
	for index, movie := range movies {
		params, err := attributevalue.MarshalList([]interface{}{movie.Title, movie.Year})
		if err != nil {
			panic(err)
		}
		statementRequests[index] = types.BatchStatementRequest{
			Statement: aws.String(
				fmt.Sprintf("SELECT * FROM \"%v\" WHERE title=? AND year=?", runner.TableName)),
			Parameters: params,
		}
	}

	output, err := runner.DynamoDbClient.BatchExecuteStatement(context.TODO(), &dynamodb.BatchExecuteStatementInput{
		Statements: statementRequests,
	})
	var outMovies []Movie
	if err != nil {
		log.Printf("Couldn't get a batch of items with PartiQL. Here's why: %v\n", err)
	} else {
		for _, response := range output.Responses {
			var movie Movie
			err = attributevalue.UnmarshalMap(response.Item, &movie)
			if err != nil {
				log.Printf("Couldn't unmarshal response. Here's why: %v\n", err)
			} else {
				outMovies = append(outMovies, movie)
			}
		}
	}
	return outMovies, err
}

// snippet-end:[gov2.dynamodb.BatchExecuteStatement.Select]

// snippet-start:[gov2.dynamodb.BatchExecuteStatement.Update]

// UpdateMovieBatch runs a batch of PartiQL UPDATE statements to update the rating of
// multiple movies that already exist in the DynamoDB table.
func (runner PartiQLRunner) UpdateMovieBatch(movies []Movie, ratings []float64) error {
	statementRequests := make([]types.BatchStatementRequest, len(movies))
	for index, movie := range movies {
		params, err := attributevalue.MarshalList([]interface{}{ratings[index], movie.Title, movie.Year})
		if err != nil {
			panic(err)
		}
		statementRequests[index] = types.BatchStatementRequest{
			Statement: aws.String(
				fmt.Sprintf("UPDATE \"%v\" SET info.rating=? WHERE title=? AND year=?", runner.TableName)),
			Parameters: params,
		}
	}

	_, err := runner.DynamoDbClient.BatchExecuteStatement(context.TODO(), &dynamodb.BatchExecuteStatementInput{
		Statements: statementRequests,
	})
	if err != nil {
		log.Printf("Couldn't update the batch of movies. Here's why: %v\n", err)
	}
	return err
}

// snippet-end:[gov2.dynamodb.BatchExecuteStatement.Update]

// snippet-start:[gov2.dynamodb.BatchExecuteStatement.Delete]

// DeleteMovieBatch runs a batch of PartiQL DELETE statements to remove multiple movies
// from the DynamoDB table.
func (runner PartiQLRunner) DeleteMovieBatch(movies []Movie) error {
	statementRequests := make([]types.BatchStatementRequest, len(movies))
	for index, movie := range movies {
		params, err := attributevalue.MarshalList([]interface{}{movie.Title, movie.Year})
		if err != nil {
			panic(err)
		}
		statementRequests[index] = types.BatchStatementRequest{
			Statement: aws.String(
				fmt.Sprintf("DELETE FROM \"%v\" WHERE title=? AND year=?", runner.TableName)),
			Parameters: params,
		}
	}

	_, err := runner.DynamoDbClient.BatchExecuteStatement(context.TODO(), &dynamodb.BatchExecuteStatementInput{
		Statements: statementRequests,
	})
	if err != nil {
		log.Printf("Couldn't delete the batch of movies. Here's why: %v\n", err)
	}
	return err
}

// snippet-end:[gov2.dynamodb.BatchExecuteStatement.Delete]
// snippet-end:[gov2.dynamodb.PartiQLRunner.complete]
