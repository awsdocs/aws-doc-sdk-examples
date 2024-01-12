// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[dynamodb.go.load_items]
package main

// snippet-start:[dynamodb.go.load_items.imports]
import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"
	"github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"

	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"strconv"
)

// snippet-end:[dynamodb.go.load_items.imports]

// snippet-start:[dynamodb.go.load_items.struct]
// Create struct to hold info about new item
type Item struct {
	Year   int
	Title  string
	Plot   string
	Rating float64
}

// snippet-end:[dynamodb.go.load_items.struct]

// snippet-start:[dynamodb.go.load_items.func]
// Get table items from JSON file
func getItems() []Item {
	raw, err := ioutil.ReadFile("./.movie_data.json")
	if err != nil {
		log.Fatalf("Got error reading file: %s", err)
	}

	var items []Item
	json.Unmarshal(raw, &items)
	return items
}

// snippet-end:[dynamodb.go.load_items.func]

func main() {
	// snippet-start:[dynamodb.go.load_items.session]
	// Initialize a session that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and region from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create DynamoDB client
	svc := dynamodb.New(sess)
	// snippet-end:[dynamodb.go.load_items.session]

	// snippet-start:[dynamodb.go.load_items.call]
	// Get table items from .movie_data.json
	items := getItems()

	// Add each item to Movies table:
	tableName := "Movies"

	for _, item := range items {
		av, err := dynamodbattribute.MarshalMap(item)
		if err != nil {
			log.Fatalf("Got error marshalling map: %s", err)
		}

		// Create item in table Movies
		input := &dynamodb.PutItemInput{
			Item:      av,
			TableName: aws.String(tableName),
		}

		_, err = svc.PutItem(input)
		if err != nil {
			log.Fatalf("Got error calling PutItem: %s", err)
		}

		year := strconv.Itoa(item.Year)

		fmt.Println("Successfully added '" + item.Title + "' (" + year + ") to table " + tableName)
		// snippet-end:[dynamodb.go.load_items.call]
	}
}

// snippet-end:[dynamodb.go.load_items]
