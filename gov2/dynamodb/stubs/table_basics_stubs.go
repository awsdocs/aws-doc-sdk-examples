// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package stubs defines service action stubs that are used by both the action and
// scenario unit tests.
//
// Each stub expects specific data as input and returns specific data as an output.
// If an error is specified, it is raised by the stubber.
package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubDescribeTable(tableName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeTable",
		Input:         &dynamodb.DescribeTableInput{TableName: aws.String(tableName)},
		Output: &dynamodb.DescribeTableOutput{
			Table: &types.TableDescription{TableName: aws.String(tableName), TableStatus: types.TableStatusActive}},
		SkipErrorTest: true, // Because this action is used by a waiter, skip it for error testing.
		Error:         raiseErr,
	}
}

func StubCreateTable(tableName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateTable",
		Input: &dynamodb.CreateTableInput{
			TableName: aws.String(tableName),
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
			ProvisionedThroughput: &types.ProvisionedThroughput{
				ReadCapacityUnits:  aws.Int64(10),
				WriteCapacityUnits: aws.Int64(10),
			},
		},
		Output: &dynamodb.CreateTableOutput{TableDescription: &types.TableDescription{
			TableName: aws.String(tableName)}},
		Error: raiseErr,
	}
}

func StubAddMovie(tableName string, item map[string]types.AttributeValue, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "PutItem",
		Input:         &dynamodb.PutItemInput{TableName: aws.String(tableName), Item: item},
		Output:        &dynamodb.PutItemOutput{},
		Error:         raiseErr,
	}
}

func StubUpdateMovie(
	tableName string, key map[string]types.AttributeValue, rating string, plot string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "UpdateItem",
		Input: &dynamodb.UpdateItemInput{
			Key:                      key,
			TableName:                aws.String(tableName),
			ExpressionAttributeNames: map[string]string{"#0": "info", "#1": "rating", "#2": "plot"},
			ExpressionAttributeValues: map[string]types.AttributeValue{
				":0": &types.AttributeValueMemberN{Value: rating}, ":1": &types.AttributeValueMemberS{Value: plot},
			},
			UpdateExpression: aws.String("SET #0.#1 = :0, #0.#2 = :1\n"),
			ReturnValues:     types.ReturnValueUpdatedNew,
		},
		Output: &dynamodb.UpdateItemOutput{
			Attributes: map[string]types.AttributeValue{
				"info": &types.AttributeValueMemberM{Value: map[string]types.AttributeValue{
					"rating": &types.AttributeValueMemberN{Value: rating},
					"plot":   &types.AttributeValueMemberS{Value: plot},
				}},
			},
		},
		Error: raiseErr,
	}
}

func StubAddMovieBatch(tableName string, inputRequests []types.WriteRequest, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "BatchWriteItem",
		Input: &dynamodb.BatchWriteItemInput{RequestItems: map[string][]types.WriteRequest{
			tableName: inputRequests,
		}},
		Output: &dynamodb.BatchWriteItemOutput{},
		Error:  raiseErr,
	}
}

func StubGetMovie(
	tableName string, key map[string]types.AttributeValue, title string, year string, rating string, plot string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetItem",
		Input:         &dynamodb.GetItemInput{TableName: aws.String(tableName), Key: key},
		Output: &dynamodb.GetItemOutput{Item: map[string]types.AttributeValue{
			"title": &types.AttributeValueMemberS{Value: title},
			"year":  &types.AttributeValueMemberN{Value: year},
			"info": &types.AttributeValueMemberM{Value: map[string]types.AttributeValue{
				"rating": &types.AttributeValueMemberN{Value: rating},
				"plot":   &types.AttributeValueMemberS{Value: plot},
			}},
		}},
		Error: raiseErr,
	}
}

func StubQuery(tableName string, title string, year string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "Query",
		Input: &dynamodb.QueryInput{
			TableName:                 aws.String(tableName),
			ExpressionAttributeNames:  map[string]string{"#0": "year"},
			ExpressionAttributeValues: map[string]types.AttributeValue{":0": &types.AttributeValueMemberN{Value: year}},
			KeyConditionExpression:    aws.String("#0 = :0"),
		},
		Output: &dynamodb.QueryOutput{Items: []map[string]types.AttributeValue{{
			"title": &types.AttributeValueMemberS{Value: title},
			"year":  &types.AttributeValueMemberN{Value: year},
			"info": &types.AttributeValueMemberM{Value: map[string]types.AttributeValue{
				"rating": &types.AttributeValueMemberN{Value: "3.5"},
				"plot":   &types.AttributeValueMemberS{Value: "Test plot"},
			}},
		}}},
		Error: raiseErr,
	}
}

func StubScan(tableName string, title string, startYear string, endYear string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "Scan",
		Input: &dynamodb.ScanInput{
			TableName:                aws.String(tableName),
			ExpressionAttributeNames: map[string]string{"#0": "year", "#1": "title", "#2": "info", "#3": "rating"},
			ExpressionAttributeValues: map[string]types.AttributeValue{
				":0": &types.AttributeValueMemberN{Value: startYear},
				":1": &types.AttributeValueMemberN{Value: endYear},
			},
			FilterExpression:     aws.String("#0 BETWEEN :0 AND :1"),
			ProjectionExpression: aws.String("#0, #1, #2.#3"),
		},
		Output: &dynamodb.ScanOutput{Items: []map[string]types.AttributeValue{{
			"title": &types.AttributeValueMemberS{Value: title},
			"year":  &types.AttributeValueMemberN{Value: startYear},
			"info": &types.AttributeValueMemberM{Value: map[string]types.AttributeValue{
				"rating": &types.AttributeValueMemberN{Value: "3.5"},
				"plot":   &types.AttributeValueMemberS{Value: "Test plot"},
			}},
		}}},
		Error: raiseErr,
	}
}

func StubDeleteItem(tableName string, key map[string]types.AttributeValue, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteItem",
		Input:         &dynamodb.DeleteItemInput{TableName: aws.String(tableName), Key: key},
		Output:        &dynamodb.DeleteItemOutput{},
		Error:         raiseErr,
	}
}

func StubDeleteTable(tableName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteTable",
		Input:         &dynamodb.DeleteTableInput{TableName: aws.String(tableName)},
		Output:        &dynamodb.DeleteTableOutput{},
		Error:         raiseErr,
	}
}

func StubListTables(tableNames []string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ListTables",
		Input:         &dynamodb.ListTablesInput{},
		Output:        &dynamodb.ListTablesOutput{TableNames: tableNames},
		Error:         raiseErr,
	}
}
