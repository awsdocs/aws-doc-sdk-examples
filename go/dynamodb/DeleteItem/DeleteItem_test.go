// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"
)

// Define a mock struct to use in unit tests
type mockDynamodbClient struct {
    dynamodbiface.DynamoDBAPI
}

func (m *mockDynamodbClient) DeleteItem(input *dynamodb.DeleteItemInput) (*dynamodb.DeleteItemOutput, error) {
    //     Check that required inputs exist
    resp := dynamodb.DeleteItemOutput{}

    return &resp, nil
}

func TestDeleteItem(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    table := "test-table"
    movie := "test-movie"
    year := "1999"

    mockSvc := &mockDynamodbClient{}

    err := DeleteTableItem(mockSvc, &table, &movie, &year)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted movie " + movie + " from table " + table)
}
