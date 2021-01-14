// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"context"
	"flag"
	"fmt"
	"strconv"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/expression"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
)

// DynamoDBScanAPI defines the interface for the Scan function.
// We use this interface to test the function using a mocked service.
type DynamoDBScanAPI interface {
	Scan(ctx context.Context,
		params *dynamodb.ScanInput,
		optFns ...func(*dynamodb.Options)) (*dynamodb.ScanOutput, error)
}

// Item holds info about the items returned by Scan
type Item struct {
	Title string
	Info  struct {
		Rating float64
	}
}

// GetItems retrieves the Amazon DynamoDB items above a minimum rating in a specified year.
// Note that this example only works if the table has a schema with:
//   year as a number (int)
//   info.rating as a number (float)
//   title as a string
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a ScanOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to Scan.
func GetItems(c context.Context, api DynamoDBScanAPI, input *dynamodb.ScanInput) (*dynamodb.ScanOutput, error) {
	resp, err := api.Scan(c, input)

	return resp, err
}

// Get the items above a minimum rating in a specific year.
func main() {
	table := flag.String("t", "", "The name of the table to scan.")
	rating := flag.Float64("r", -1.0, "The minimum rating for a movie to retrieve.")
	year := flag.Int("y", 1899, "The year when the movie was released.")
	verbose := flag.Bool("v", false, "Whether to show info about the movie.")

	flag.Parse()

	if *table == "" || *rating < 0.0 || *year < 1900 {
		fmt.Println("You must supply the name of the table, a rating above zero, and a year after 1900:")
		fmt.Println("-t TABLE -r RATING -y YEAR")
		return
	}

	// Get items in that year.
	filt1 := expression.Name("year").Equal(expression.Value(*year))
	// Get items with a rating above the minimum.
	filt2 := expression.Name("info.rating").GreaterThan(expression.Value(*rating))

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
		TableName:                 table,
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("unable to load SDK config, " + err.Error())
	}

	client := dynamodb.NewFromConfig(cfg)

	resp, err := GetItems(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got an error scanning the table:")
		fmt.Println(err.Error())
		return
	}

	items := []Item{}

	err = attributevalue.UnmarshalListOfMaps(resp.Items, &items)
	if err != nil {
		panic(fmt.Sprintf("failed to unmarshal Dynamodb Scan Items, %v", err))
	}

	for _, item := range items {
		if *verbose {
			fmt.Println("Title: ", item.Title)
			fmt.Println("Rating:", item.Info.Rating)
			fmt.Println()
		}
	}

	numItems := strconv.Itoa(len(items))

	fmt.Println("Found", numItems, "movie(s) with a rating above", *rating, "in", *year)
}
