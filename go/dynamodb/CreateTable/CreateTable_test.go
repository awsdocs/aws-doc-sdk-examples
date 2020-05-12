// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "encoding/json"
    "errors"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"

    // For mocking
    "github.com/google/uuid" // For creating a random, unique resource name
)

// Define a mock struct to use in unit tests
type mockDynamodbClient struct {
    dynamodbiface.DynamoDBAPI
}

func (m *mockDynamodbClient) CreateTable(input *dynamodb.CreateTableInput) (*dynamodb.CreateTableOutput, error) {
    result := dynamodb.CreateTableOutput{}

    if input.AttributeDefinitions == nil {
        return &result, errors.New("Missing required field CreateTableInput.AttributeDefinitions")
    }

    if input.KeySchema == nil {
        return &result, errors.New("Missing required field CreateTableInput.KeySchema")
    }

    if input.TableName == nil || *input.TableName == "" {
        return &result, errors.New("Missing required field CreateTableInput.TableName")
    }

    return &result, nil
}

type Config struct {
    Table string `json:"Table"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration(t *testing.T) error {
    content, err := ioutil.ReadFile(configFileName)
    if err != nil {
        return err
    }

    text := string(content)

    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    t.Log("Table: " + globalConfig.Table)

    return nil
}

func TestCreateTable(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    attributeDefinitions := []*dynamodb.AttributeDefinition{
        {
            AttributeName: aws.String("Year"),
            AttributeType: aws.String("N"),
        },
        {
            AttributeName: aws.String("Title"),
            AttributeType: aws.String("S"),
        },
    }

    keySchema := []*dynamodb.KeySchemaElement{
        {
            AttributeName: aws.String("Year"),
            KeyType:       aws.String("HASH"),
        },
        {
            AttributeName: aws.String("Title"),
            KeyType:       aws.String("RANGE"),
        },
    }

    provisionedThroughput := &dynamodb.ProvisionedThroughput{
        ReadCapacityUnits:  aws.Int64(10),
        WriteCapacityUnits: aws.Int64(10),
    }

    if globalConfig.Table == "" {
        // mock resources
        id := uuid.New()
        globalConfig.Table = "test-table-" + id.String()

        mockSvc := &mockDynamodbClient{}

        err = MakeTable(mockSvc, attributeDefinitions, keySchema, provisionedThroughput, &globalConfig.Table)
        if err != nil {
            t.Fatal(err)
        }
    } else {
        sess := session.Must(session.NewSessionWithOptions(session.Options{
            SharedConfigState: session.SharedConfigEnable,
        }))

        svc := dynamodb.New(sess)

        err = MakeTable(svc, attributeDefinitions, keySchema, provisionedThroughput, &globalConfig.Table)
        if err != nil {
            t.Fatal(err)
        }
    }

    t.Log("Created table " + globalConfig.Table)
}
