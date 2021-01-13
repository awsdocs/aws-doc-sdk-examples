// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"strconv"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/expression"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

type DynamoDBScanImpl struct{}

type Info struct {
	Rating float64
}

type Entry struct {
	Title string
	Info  Info
}

func (dt DynamoDBScanImpl) Scan(ctx context.Context,
	params *dynamodb.ScanInput,
	optFns ...func(*dynamodb.Options)) (*dynamodb.ScanOutput, error) {

	item1 := Item{
		Title: "Movie one",
		Info: Info{
			Rating: 6.0,
		},
	}

	item2 := Item{
		Title: "Movie two",
		Info: Info{
			Rating: 6.5,
		},
	}

	av1, err := attributevalue.MarshalMap(item1)
	if err != nil {
		return nil, errors.New("Could not items")
	}

	av2, err := attributevalue.MarshalMap(item2)
	if err != nil {
		return nil, errors.New("Could not items")
	}

	avs := make([]map[string]types.AttributeValue, 2)
	avs[0] = av1
	avs[1] = av2

	output := &dynamodb.ScanOutput{
		Items: avs,
	}

	return output, nil
}

type Config struct {
	Table   string `json:"Table"`
	Year    string `json:"Year"`
	Rating  string `json:"Rating"`
	Verbose string `json:"Verbose"`
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

func TestDescribeTable(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	if globalConfig.Table == "" || globalConfig.Rating == "" || globalConfig.Year == "" {
		t.Fatal("You must set a value for Table, Rating, and Year in " + configFileName)
	}

	// Make sure year is an int > 1900 and rating is a float > 0.0
	year, err := strconv.Atoi(globalConfig.Year)
	if err != nil {
		t.Fatal("You must supply an integer value for Year in " + configFileName)
	}

	if year < 1901 {
		t.Fatal("You must supply a value > 1900 for Year in " + configFileName)
	}

	rating, err := strconv.ParseFloat(globalConfig.Rating, 64)
	if err != nil {
		t.Fatal("You must supply a floating-point v value for Rating in " + configFileName)
	}

	if rating < 0.0 {
		t.Fatal("You must supply a value > 0.0 for Rating in " + configFileName)
	}

	// Get items in that year.
	filt1 := expression.Name("year").Equal(expression.Value(year))
	// Get items with a rating above the minimum.
	filt2 := expression.Name("info.rating").GreaterThan(expression.Value(rating))

	// Get back the title and rating (we know the year).
	proj := expression.NamesList(expression.Name("title"), expression.Name("info.rating"))

	expr, err := expression.NewBuilder().WithFilter(filt1).WithFilter(filt2).WithProjection(proj).Build()
	if err != nil {
		fmt.Println("Got error building expression:")
		fmt.Println(err.Error())
		return
	}

	input := &dynamodb.ScanInput{
		ExpressionAttributeNames:  expr.Names(),
		ExpressionAttributeValues: expr.Values(),
		FilterExpression:          expr.Filter(),
		ProjectionExpression:      expr.Projection(),
		TableName:                 &globalConfig.Table,
	}

	api := &DynamoDBScanImpl{}

	resp, err := GetItems(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error retrieving the table items:")
		t.Log(err)
		return
	}

	items := []Item{}

	err = attributevalue.UnmarshalListOfMaps(resp.Items, &items)
	if err != nil {
		panic(fmt.Sprintf("failed to unmarshal Dynamodb Scan Items, %v", err))
	}

	if globalConfig.Verbose == "true" {
		for _, item := range items {
			t.Log("Title: ", item.Title)
			t.Log("Rating:", item.Info.Rating)
			t.Log("")
		}
	}

	numItems := strconv.Itoa(len(items))

	t.Log("Found", numItems, "movie(s) with a rating above "+globalConfig.Rating+" in "+globalConfig.Year)
}
