// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"
	"github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"

	"fmt"
	"log"
)

// ItemInfo holds info to update
type ItemInfo struct {
	Rating float64 `json:"rating"`
}

// Item identifies the item in the table
type Item struct {
	Year  int    `json:"year"`
	Title string `json:"title"`
}

func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create DynamoDB client
	svc := dynamodb.New(sess)

	info := ItemInfo{
		Rating: 0.5,
	}

	item := Item{
		Year:  2015,
		Title: "The Big New Movie",
	}

	expr, err := dynamodbattribute.MarshalMap(info)
	if err != nil {
		log.Fatalf("Got error marshalling info: %s", err)
	}

	key, err := dynamodbattribute.MarshalMap(item)
	if err != nil {
		log.Fatalf("Got error marshalling item: %s", err)
	}

	// Update item in table Movies
	input := &dynamodb.UpdateItemInput{
		ExpressionAttributeValues: expr,
		TableName:                 aws.String("Movies"),
		Key:                       key,
		ReturnValues:              aws.String("UPDATED_NEW"),
		UpdateExpression:          aws.String("set info.rating = :r"),
	}

	_, err = svc.UpdateItem(input)
	if err != nil {
		log.Fatalf("Got error calling UpdateItem: %s", err)
	}

	fmt.Println("Successfully updated 'The Big New Movie' (2015) rating to 0.5")
}
