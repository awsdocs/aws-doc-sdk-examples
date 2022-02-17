// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "encoding/json"
    "errors"
    "io/ioutil"
    "strconv"
    "testing"
    "time"

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
    Table  string  `json:"Table"`
    Year   int     `json:"Year"`
    Title  string  `json:"Title"`
    Plot   string  `json:"Plot"`
    Rating float64 `json:"float"`
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

    t.Log("Table:  " + globalConfig.Table)
    t.Log("Year:   " + strconv.Itoa(globalConfig.Year))
    t.Log("Title:  " + globalConfig.Title)
    t.Log("Plot:   " + globalConfig.Plot)
    t.Log("Rating: " + strconv.Itoa(int(globalConfig.Rating)))

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

    if globalConfig.Year == 0 {
        globalConfig.Year = 1999
    }

    if globalConfig.Plot == "" {
        globalConfig.Plot = "Not much happening"
    }

    if globalConfig.Rating == 0.0 {
        globalConfig.Rating = 5.0
    }

    if globalConfig.Title == "" {
        globalConfig.Title = "The Big Nada"
    }

    if globalConfig.Table == "" {
        // mock resources
        id := uuid.New()
        globalConfig.Table = "test-table-" + id.String()

        mockSvc := &mockDynamodbClient{}

        err = AddTableItem(mockSvc, &globalConfig.Year, &globalConfig.Table, &globalConfig.Title, &globalConfig.Plot, &globalConfig.Rating)
        if err != nil {
            t.Fatal(err)
        }
    } else {
        sess := session.Must(session.NewSessionWithOptions(session.Options{
            SharedConfigState: session.SharedConfigEnable,
        }))

        svc := dynamodb.New(sess)

        err = AddTableItem(svc, &globalConfig.Year, &globalConfig.Table, &globalConfig.Title, &globalConfig.Plot, &globalConfig.Rating)
        if err != nil {
            t.Fatal(err)
        }
    }

    t.Log("Successfully added '"+globalConfig.Title+"' ("+strconv.Itoa(globalConfig.Year)+") to table "+globalConfig.Table+" with rating", globalConfig.Rating)
}
