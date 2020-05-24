// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
package main

import (
    "encoding/json"
    "errors"
    "io/ioutil"
    "strconv"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"
)

// Define a mock struct to use in unit tests
type mockDynamodbClient struct {
    dynamodbiface.DynamoDBAPI
}

func (m *mockDynamodbClient) UpdateItem(input *dynamodb.UpdateItemInput) (*dynamodb.UpdateItemOutput, error) {
    result := dynamodb.UpdateItemOutput{}

    if input.TableName == nil || *input.TableName == "" {
        return &result, errors.New("Missing required field UpdateItemInput.TableName")
    }

    return &result, nil
}

type Config struct {
    Table  string `json:"Table"`
    Movie  string `json:"Movie"`
    Year   string `json:"Year"`
    Rating string `json:"Rating"`
}

/*
type Item struct {
    Year   int
    Title  string
    Plot   string
    Rating float64
}
*/
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

    t.Log("Table:   " + globalConfig.Table)
    t.Log("Movie:   " + globalConfig.Movie)
    t.Log("Year:    " + globalConfig.Year)
    t.Log("Rating:  " + globalConfig.Rating)

    return nil
}

func TestUpdateItem(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    if globalConfig.Movie == "" || globalConfig.Rating == "" || globalConfig.Year == "" || globalConfig.Table == "" {
        t.Fatal("You must specify values for Table, Movie, Year, and Rating in config.json")
    }

    // Make sure rating is in the range 0 to 1, and year is in the range 1900 - 2020
    yearInt, err := strconv.Atoi(globalConfig.Year)
    if err != nil {
        t.Fatal("Year is not an integer value")
    }

    if yearInt < 1900 {
        globalConfig.Year = "1900"
    } else if yearInt > 2020 {
        globalConfig.Year = "2020"
    }

    ratingFloat, err := strconv.ParseFloat(globalConfig.Rating, 64)
    if err != nil {
        t.Fatal("Rating is not a floating-point value")
    }

    if ratingFloat < 0.0 {
        globalConfig.Rating = "0.0"
    } else if ratingFloat > 1.0 {
        globalConfig.Rating = "1.0"
    }

    mockSvc := &mockDynamodbClient{}

    err = UpdateMovie(mockSvc, &globalConfig.Table, &globalConfig.Movie, &globalConfig.Year, &globalConfig.Rating)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Updated movie '"+globalConfig.Movie+"' in table "+globalConfig.Table+" to rating", globalConfig.Rating)
}
