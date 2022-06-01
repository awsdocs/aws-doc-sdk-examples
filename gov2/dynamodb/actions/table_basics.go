// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"errors"
	"log"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/expression"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

// snippet-start:[gov2.dynamodb.TableBasics.complete]
// snippet-start:[gov2.dynamodb.TableBasics.struct]

// TableBasics encapsulates the Amazon DynamoDB service actions used in the examples.
// It contains a DynamoDB service client that is used to act on the specified table.
type TableBasics struct {
	DynamoDbClient *dynamodb.Client
	TableName      string
}

// snippet-end:[gov2.dynamodb.TableBasics.struct]

// snippet-start:[gov2.dynamodb.DescribeTable]

// TableExists determines whether a DynamoDB table exists.
func (basics TableBasics) TableExists() (bool, error) {
	exists := true
	_, err := basics.DynamoDbClient.DescribeTable(
		context.TODO(), &dynamodb.DescribeTableInput{TableName: aws.String(basics.TableName)},
	)
	if err != nil {
		var notFoundEx *types.ResourceNotFoundException
		if errors.As(err, &notFoundEx) {
			log.Printf("Table %v does not exist.\n", basics.TableName)
			err = nil
		} else {
			log.Printf("Couldn't determine existence of table %v. Here's why: %v\n", basics.TableName, err)
		}
		exists = false
	}
	return exists, err
}

// snippet-end:[gov2.dynamodb.DescribeTable]

// snippet-start:[gov2.dynamodb.CreateTable]

// CreateMovieTable creates a DynamoDB table with a composite primary key defined as
// a string sort key named `title`, and a numeric partition key named `year`.
// This function uses NewTableExistsWaiter to wait for the table to be created by
// DynamoDB before it returns.
func (basics TableBasics) CreateMovieTable() (*types.TableDescription, error) {
	var tableDesc *types.TableDescription
	table, err := basics.DynamoDbClient.CreateTable(context.TODO(), &dynamodb.CreateTableInput{
		AttributeDefinitions: []types.AttributeDefinition{{
			AttributeName: aws.String("year"),
			AttributeType: types.ScalarAttributeTypeN,
		}, {
			AttributeName: aws.String("title"),
			AttributeType: types.ScalarAttributeTypeS,
		}},
		KeySchema: []types.KeySchemaElement{{
			AttributeName: aws.String("year"),
			KeyType:       types.KeyTypeHash,
		}, {
			AttributeName: aws.String("title"),
			KeyType:       types.KeyTypeRange,
		}},
		TableName: aws.String(basics.TableName),
		ProvisionedThroughput: &types.ProvisionedThroughput{
			ReadCapacityUnits:  aws.Int64(10),
			WriteCapacityUnits: aws.Int64(10),
		},
	})
	if err != nil {
		log.Printf("Couldn't create table %v. Here's why: %v\n", basics.TableName, err)
	} else {
		waiter := dynamodb.NewTableExistsWaiter(basics.DynamoDbClient)
		err = waiter.Wait(context.TODO(), &dynamodb.DescribeTableInput{
			TableName: aws.String(basics.TableName)}, 5*time.Minute)
		if err != nil {
			log.Printf("Wait for table exists failed. Here's why: %v\n", err)
		}
		tableDesc = table.TableDescription
	}
	return tableDesc, err
}

// snippet-end:[gov2.dynamodb.CreateTable]

// snippet-start:[gov2.dynamodb.ListTables]

// ListTables lists the DynamoDB table names for the current account.
func (basics TableBasics) ListTables() ([]string, error) {
	var tableNames []string
	tables, err := basics.DynamoDbClient.ListTables(
		context.TODO(), &dynamodb.ListTablesInput{})
	if err != nil {
		log.Printf("Couldn't list tables. Here's why: %v\n", err)
	} else {
		tableNames = tables.TableNames
	}
	return tableNames, err
}

// snippet-end:[gov2.dynamodb.ListTables]

// snippet-start:[gov2.dynamodb.PutItem]

// AddMovie adds a movie the DynamoDB table.
func (basics TableBasics) AddMovie(movie Movie) error {
	item, err := attributevalue.MarshalMap(movie)
	if err != nil {
		panic(err)
	}
	_, err = basics.DynamoDbClient.PutItem(context.TODO(), &dynamodb.PutItemInput{
		TableName: aws.String(basics.TableName), Item: item,
	})
	if err != nil {
		log.Printf("Couldn't add item to table. Here's why: %v\n", err)
	}
	return err
}

// snippet-end:[gov2.dynamodb.PutItem]

// snippet-start:[gov2.dynamodb.UpdateItem]

// UpdateMovie updates the rating and plot of a movie that already exists in the
// DynamoDB table. This function uses the `expression` package to build the update
// expression.
func (basics TableBasics) UpdateMovie(movie Movie) (map[string]map[string]interface{}, error) {
	var err error
	var response *dynamodb.UpdateItemOutput
	var attributeMap map[string]map[string]interface{}
	update := expression.Set(expression.Name("info.rating"), expression.Value(movie.Info["rating"]))
	update.Set(expression.Name("info.plot"), expression.Value(movie.Info["plot"]))
	expr, err := expression.NewBuilder().WithUpdate(update).Build()
	if err != nil {
		log.Printf("Couldn't build expression for update. Here's why: %v\n", err)
	} else {
		response, err = basics.DynamoDbClient.UpdateItem(context.TODO(), &dynamodb.UpdateItemInput{
			TableName:                 aws.String(basics.TableName),
			Key:                       movie.GetKey(),
			ExpressionAttributeNames:  expr.Names(),
			ExpressionAttributeValues: expr.Values(),
			UpdateExpression:          expr.Update(),
			ReturnValues:              types.ReturnValueUpdatedNew,
		})
		if err != nil {
			log.Printf("Couldn't update movie %v. Here's why: %v\n", movie.Title, err)
		} else {
			err = attributevalue.UnmarshalMap(response.Attributes, &attributeMap)
			if err != nil {
				log.Printf("Couldn't unmarshall update response. Here's why: %v\n", err)
			}
		}
	}
	return attributeMap, err
}

// snippet-end:[gov2.dynamodb.UpdateItem]

// snippet-start:[gov2.dynamodb.BatchWriteItem]

// AddMovieBatch adds a slice of movies to the DynamoDB table. The function sends
// batches of 25 movies to DynamoDB until all movies are added or it reaches the
// specified maximum.
func (basics TableBasics) AddMovieBatch(movies []Movie, maxMovies int) (int, error) {
	var err error
	var item map[string]types.AttributeValue
	written := 0
	batchSize := 25 // DynamoDB allows a maximum batch size of 25 items.
	start := 0
	end := start + batchSize
	for start < maxMovies && start < len(movies) {
		var writeReqs []types.WriteRequest
		if end > len(movies) {
			end = len(movies)
		}
		for _, movie := range movies[start:end] {
			item, err = attributevalue.MarshalMap(movie)
			if err != nil {
				log.Printf("Couldn't marshal movie %v for batch writing. Here's why: %v\n", movie.Title, err)
			} else {
				writeReqs = append(
					writeReqs,
					types.WriteRequest{PutRequest: &types.PutRequest{Item: item}},
				)
			}
		}
		_, err = basics.DynamoDbClient.BatchWriteItem(context.TODO(), &dynamodb.BatchWriteItemInput{
			RequestItems: map[string][]types.WriteRequest{basics.TableName: writeReqs}})
		if err != nil {
			log.Printf("Couldn't add a batch of movies to %v. Here's why: %v\n", basics.TableName, err)
		} else {
			written += len(writeReqs)
		}
		start = end
		end += batchSize
	}

	return written, err
}

// snippet-end:[gov2.dynamodb.BatchWriteItem]

// snippet-start:[gov2.dynamodb.GetItem]

// GetMovie gets movie data from the DynamoDB table by using the primary composite key
// made of title and year.
func (basics TableBasics) GetMovie(title string, year int) (Movie, error) {
	movie := Movie{Title: title, Year: year}
	response, err := basics.DynamoDbClient.GetItem(context.TODO(), &dynamodb.GetItemInput{
		Key: movie.GetKey(), TableName: aws.String(basics.TableName),
	})
	if err != nil {
		log.Printf("Couldn't get info about %v. Here's why: %v\n", title, err)
	} else {
		err = attributevalue.UnmarshalMap(response.Item, &movie)
		if err != nil {
			log.Printf("Couldn't unmarshal response. Here's why: %v\n", err)
		}
	}
	return movie, err
}

// snippet-end:[gov2.dynamodb.GetItem]

// snippet-start:[gov2.dynamodb.Query]

// Query gets all movies in the DynamoDB table that were released in the specified year.
// The function uses the `expression` package to build the key condition expression
// that is used in the query.
func (basics TableBasics) Query(releaseYear int) ([]Movie, error) {
	var err error
	var response *dynamodb.QueryOutput
	var movies []Movie
	keyEx := expression.Key("year").Equal(expression.Value(releaseYear))
	expr, err := expression.NewBuilder().WithKeyCondition(keyEx).Build()
	if err != nil {
		log.Printf("Couldn't build epxression for query. Here's why: %v\n", err)
	} else {
		response, err = basics.DynamoDbClient.Query(context.TODO(), &dynamodb.QueryInput{
			TableName:                 aws.String(basics.TableName),
			ExpressionAttributeNames:  expr.Names(),
			ExpressionAttributeValues: expr.Values(),
			KeyConditionExpression:    expr.KeyCondition(),
		})
		if err != nil {
			log.Printf("Couldn't query for movies released in %v. Here's why: %v\n", releaseYear, err)
		} else {
			err = attributevalue.UnmarshalListOfMaps(response.Items, &movies)
			if err != nil {
				log.Printf("Couldn't unmarshal query response. Here's why: %v\n", err)
			}
		}
	}
	return movies, err
}

// snippet-end:[gov2.dynamodb.Query]

// snippet-start:[gov2.dynamodb.Scan]

// Scan gets all movies in the DynamoDB table that were released in a range of years
// and projects them to return a reduced set of fields.
// The function uses the `expression` package to build the filter and projection
// expressions.
func (basics TableBasics) Scan(startYear int, endYear int) ([]Movie, error) {
	var movies []Movie
	var err error
	var response *dynamodb.ScanOutput
	filtEx := expression.Name("year").Between(expression.Value(startYear), expression.Value(endYear))
	projEx := expression.NamesList(
		expression.Name("year"), expression.Name("title"), expression.Name("info.rating"))
	expr, err := expression.NewBuilder().WithFilter(filtEx).WithProjection(projEx).Build()
	if err != nil {
		log.Printf("Couldn't build expressions for scan. Here's why: %v\n", err)
	} else {
		response, err = basics.DynamoDbClient.Scan(context.TODO(), &dynamodb.ScanInput{
			TableName:                 aws.String(basics.TableName),
			ExpressionAttributeNames:  expr.Names(),
			ExpressionAttributeValues: expr.Values(),
			FilterExpression:          expr.Filter(),
			ProjectionExpression:      expr.Projection(),
		})
		if err != nil {
			log.Printf("Couldn't scan for movies released between %v and %v. Here's why: %v\n",
				startYear, endYear, err)
		} else {
			err = attributevalue.UnmarshalListOfMaps(response.Items, &movies)
			if err != nil {
				log.Printf("Couldn't unmarshal query response. Here's why: %v\n", err)
			}
		}
	}
	return movies, err
}

// snippet-end:[gov2.dynamodb.Scan]

// snippet-start:[gov2.dynamodb.DeleteItem]

// DeleteMovie removes a movie from the DynamoDB table.
func (basics TableBasics) DeleteMovie(movie Movie) error {
	_, err := basics.DynamoDbClient.DeleteItem(context.TODO(), &dynamodb.DeleteItemInput{
		TableName: aws.String(basics.TableName), Key: movie.GetKey(),
	})
	if err != nil {
		log.Printf("Couldn't delete %v from the table. Here's why: %v\n", movie.Title, err)
	}
	return err
}

// snippet-end:[gov2.dynamodb.DeleteItem]

// snippet-start:[gov2.dynamodb.DeleteTable]

// DeleteTable deletes the DynamoDB table and all of its data.
func (basics TableBasics) DeleteTable() error {
	_, err := basics.DynamoDbClient.DeleteTable(context.TODO(), &dynamodb.DeleteTableInput{
		TableName: aws.String(basics.TableName)})
	if err != nil {
		log.Printf("Couldn't delete table %v. Here's why: %v\n", basics.TableName, err)
	}
	return err
}

// snippet-end:[gov2.dynamodb.DeleteTable]
// snippet-end:[gov2.dynamodb.TableBasics.complete]
