// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"
	"github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
)

// Create structs to hold info about new item
type ItemInfo struct {
	Plot   string  `json:"plot"`
	Rating float64 `json:"rating"`
}

type Item struct {
	Year  int      `json:"year"`
	Title string   `json:"title"`
	Info  ItemInfo `json:"info"`
}

// Get table items from JSON file
func getItems() []Item {
	raw, err := ioutil.ReadFile("./.movie_data.json")

	if err != nil {
		fmt.Println(err.Error())
		os.Exit(1)
	}

	var items []Item
	json.Unmarshal(raw, &items)
	return items
}

func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	if err != nil {
		log.Fatalf("Error creating session: %s", err)
	}

	// Create DynamoDB client
	svc := dynamodb.New(sess)

	// Get table items from .movie_data.json
	items := getItems()

	// Add each item to Movies table:
	for _, item := range items {
		av, err := dynamodbattribute.MarshalMap(item)

		if err != nil {
			log.Fatalf("Got error marshalling map: %s", err)
		}

		// Create item in table Movies
		input := &dynamodb.PutItemInput{
			Item:      av,
			TableName: aws.String("Movies"),
		}

		_, err = svc.PutItem(input)

		if err != nil {
			log.Fatalf("Got error calling PutItem: %s", err)
		}

		fmt.Println("Successfully added '", item.Title, "' (", item.Year, ") to Movies table")
	}
}
