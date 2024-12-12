// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.dynamodb.DynamoActions.complete]

import (
	"context"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

// DynamoActions encapsulates the Amazon Simple Notification Service (Amazon SNS) actions
// used in the examples.
type DynamoActions struct {
	DynamoClient *dynamodb.Client
}

// User defines structured user data.
type User struct {
	UserName  string
	UserEmail string
	LastLogin *LoginInfo `dynamodbav:",omitempty"`
}

// LoginInfo defines structured custom login data.
type LoginInfo struct {
	UserPoolId string
	ClientId   string
	Time       string
}

// UserList defines a list of users.
type UserList struct {
	Users []User
}

// UserNameList returns the usernames contained in a UserList as a list of strings.
func (users *UserList) UserNameList() []string {
	names := make([]string, len(users.Users))
	for i := 0; i < len(users.Users); i++ {
		names[i] = users.Users[i].UserName
	}
	return names
}

// PopulateTable adds a set of test users to the table.
func (actor DynamoActions) PopulateTable(ctx context.Context, tableName string) error {
	var err error
	var item map[string]types.AttributeValue
	var writeReqs []types.WriteRequest
	for i := 1; i < 4; i++ {
		item, err = attributevalue.MarshalMap(User{UserName: fmt.Sprintf("test_user_%v", i), UserEmail: fmt.Sprintf("test_email_%v@example.com", i)})
		if err != nil {
			log.Printf("Couldn't marshall user into DynamoDB format. Here's why: %v\n", err)
			return err
		}
		writeReqs = append(writeReqs, types.WriteRequest{PutRequest: &types.PutRequest{Item: item}})
	}
	_, err = actor.DynamoClient.BatchWriteItem(ctx, &dynamodb.BatchWriteItemInput{
		RequestItems: map[string][]types.WriteRequest{tableName: writeReqs},
	})
	if err != nil {
		log.Printf("Couldn't populate table %v with users. Here's why: %v\n", tableName, err)
	}
	return err
}

// Scan scans the table for all items.
func (actor DynamoActions) Scan(ctx context.Context, tableName string) (UserList, error) {
	var userList UserList
	output, err := actor.DynamoClient.Scan(ctx, &dynamodb.ScanInput{
		TableName: aws.String(tableName),
	})
	if err != nil {
		log.Printf("Couldn't scan table %v for items. Here's why: %v\n", tableName, err)
	} else {
		err = attributevalue.UnmarshalListOfMaps(output.Items, &userList.Users)
		if err != nil {
			log.Printf("Couldn't unmarshal items into users. Here's why: %v\n", err)
		}
	}
	return userList, err
}

// AddUser adds a user item to a table.
func (actor DynamoActions) AddUser(ctx context.Context, tableName string, user User) error {
	userItem, err := attributevalue.MarshalMap(user)
	if err != nil {
		log.Printf("Couldn't marshall user to item. Here's why: %v\n", err)
	}
	_, err = actor.DynamoClient.PutItem(ctx, &dynamodb.PutItemInput{
		Item:      userItem,
		TableName: aws.String(tableName),
	})
	if err != nil {
		log.Printf("Couldn't put item in table %v. Here's why: %v", tableName, err)
	}
	return err
}

// snippet-end:[gov2.dynamodb.DynamoActions.complete]
