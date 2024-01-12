// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[dynamodb.go.read_item]
package main

// snippet-start:[dynamodb.go.read_item.imports]
import (
	"errors"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"
	"github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"

	"fmt"
	"log"
)

// snippet-end:[dynamodb.go.read_item.imports]

// snippet-start:[dynamodb.go.read_item.struct]
// Create struct to hold info about new item
type Item struct {
	Year   int
	Title  string
	Plot   string
	Rating float64
}

// snippet-end:[dynamodb.go.read_item.struct]

func main() {
	// snippet-start:[dynamodb.go.read_item.session]
	// Initialize a session that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and region from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create DynamoDB client
	svc := dynamodb.New(sess)
	// snippet-end:[dynamodb.go.read_item.session]

	// snippet-start:[dynamodb.go.read_item.call]
	tableName := "Movies"
	movieName := "The Big New Movie"
	movieYear := "2015"

	result, err := svc.GetItem(&dynamodb.GetItemInput{
		TableName: aws.String(tableName),
		Key: map[string]*dynamodb.AttributeValue{
			"Year": {
				N: aws.String(movieYear),
			},
			"Title": {
				S: aws.String(movieName),
			},
		},
	})
	if err != nil {
		log.Fatalf("Got error calling GetItem: %s", err)
	}
	// snippet-end:[dynamodb.go.read_item.call]

	// snippet-start:[dynamodb.go.read_item.unmarshall]
	if result.Item == nil {
		msg := "Could not find '" + *title + "'"
		return nil, errors.New(msg)
	}

	item := Item{}

	err = dynamodbattribute.UnmarshalMap(result.Item, &item)
	if err != nil {
		panic(fmt.Sprintf("Failed to unmarshal Record, %v", err))
	}

	fmt.Println("Found item:")
	fmt.Println("Year:  ", item.Year)
	fmt.Println("Title: ", item.Title)
	fmt.Println("Plot:  ", item.Plot)
	fmt.Println("Rating:", item.Rating)
	// snippet-end:[dynamodb.go.read_item.unmarshall]
}

// snippet-end:[dynamodb.go.read_item]
