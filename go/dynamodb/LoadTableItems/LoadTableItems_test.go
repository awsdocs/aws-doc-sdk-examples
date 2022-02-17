// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "encoding/json"
    "errors"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"

    "github.com/google/uuid"
)

// Define a mock struct to use in unit tests
type mockDynamodbClient struct {
    dynamodbiface.DynamoDBAPI
}

func (m *mockDynamodbClient) PutItem(input *dynamodb.PutItemInput) (*dynamodb.PutItemOutput, error) {
    result := dynamodb.PutItemOutput{}

    if input.Item == nil {
        return &result, errors.New("Missing required field PutItemInput.Item")
    }

    if input.TableName == nil || *input.TableName == "" {
        return &result, errors.New("Missing required field CreateTableInput.TableName")
    }

    return &result, nil
}

type Config struct {
    JSONFile string `json:"JsonFile"`
    Table    string `json:"Table"`
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

    t.Log("JsonFile: " + globalConfig.JSONFile)
    t.Log("Table:    " + globalConfig.Table)

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

    if globalConfig.JSONFile == "" {
        t.Fatal("You must supply the name of the JSON file (JsonFile) in config.json")
    }

    if globalConfig.Table == "" {
        // mock resources
        id := uuid.New()
        globalConfig.Table = "test-table-" + id.String()

        mockSvc := &mockDynamodbClient{}

        items, err := GetItems(&globalConfig.JSONFile)
        if err != nil {
            t.Fatal(err)
        }

        for _, item := range items {
            av, err := dynamodbattribute.MarshalMap(item)
            if err != nil {
                t.Fatal(err)
            }

            err = AddTableItem(mockSvc, av, &globalConfig.Table)
            if err != nil {
                t.Fatal(err)
            }

            title, err := GetMovieName(av)
            if err != nil {
                t.Fatal(err)
            }

            t.Log("Added movie " + title + " to table " + globalConfig.Table)
        }
    } else {
        sess := session.Must(session.NewSessionWithOptions(session.Options{
            SharedConfigState: session.SharedConfigEnable,
        }))

        svc := dynamodb.New(sess)

        items, err := GetItems(&globalConfig.JSONFile)
        if err != nil {
            t.Fatal(err)
        }

        for _, item := range items {
            av, err := dynamodbattribute.MarshalMap(item)
            if err != nil {
                t.Fatal(err)
            }

            err = AddTableItem(svc, av, &globalConfig.Table)
            if err != nil {
                t.Fatal(err)
            }

            title, err := GetMovieName(av)
            if err != nil {
                t.Fatal(err)
            }

            t.Log("Added movie " + title + " to table " + globalConfig.Table)
        }
    }

}
