// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"

    "github.com/google/uuid"
)

type mockDynamodbClient struct {
    dynamodbiface.DynamoDBAPI
}

var title = "this is a test"
var year = 1999

func (m *mockDynamodbClient) GetItem(input *dynamodb.GetItemInput) (*dynamodb.GetItemOutput, error) {
    result := dynamodb.GetItemOutput{}

    if input.TableName == nil || *input.TableName == "" {
        return &result, errors.New("You must supply a table name")
    }

    item := Item{
        Year:  year,
        Title: title,
    }

    av, err := dynamodbattribute.MarshalMap(item)
    if err != nil {
        return &result, err
    }

    result = dynamodb.GetItemOutput{
        Item: av,
    }

    return &result, nil
}

func TestGetItem(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    id := uuid.New()
    table := "test-table-" + id.String()

    mockSvc := &mockDynamodbClient{}

    item, err := GetTableItem(mockSvc, &table, &title, &year)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Retrieved movie '" + item.Title + "' from table ")
}
