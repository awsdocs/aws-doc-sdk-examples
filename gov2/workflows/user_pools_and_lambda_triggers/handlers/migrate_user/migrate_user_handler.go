// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

// snippet-start:[gov2.lambda.MigrateUserHandler]

import (
	"context"
	"log"
	"os"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/expression"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
)

const TABLE_NAME = "TABLE_NAME"

// UserInfo defines structured user data that can be marshalled to a DynamoDB format.
type UserInfo struct {
	UserName  string `dynamodbav:"UserName"`
	UserEmail string `dynamodbav:"UserEmail"`
}

type handler struct {
	dynamoClient *dynamodb.Client
}

// HandleRequest handles the MigrateUser event by looking up a user in an Amazon DynamoDB table and
// specifying whether they should be migrated to the user pool.
func (h *handler) HandleRequest(ctx context.Context, event events.CognitoEventUserPoolsMigrateUser) (events.CognitoEventUserPoolsMigrateUser, error) {
	log.Printf("Received migrate trigger from %v for user '%v'", event.TriggerSource, event.UserName)
	if event.TriggerSource != "UserMigration_Authentication" {
		return event, nil
	}
	tableName := os.Getenv(TABLE_NAME)
	user := UserInfo{
		UserName: event.UserName,
	}
	log.Printf("Looking up user '%v' in table %v.\n", user.UserName, tableName)
	filterEx := expression.Name("UserName").Equal(expression.Value(user.UserName))
	expr, err := expression.NewBuilder().WithFilter(filterEx).Build()
	if err != nil {
		log.Printf("Error building expression to query for user '%v'.\n", user.UserName)
		return event, err
	}
	output, err := h.dynamoClient.Scan(ctx, &dynamodb.ScanInput{
		TableName:                 aws.String(tableName),
		FilterExpression:          expr.Filter(),
		ExpressionAttributeNames:  expr.Names(),
		ExpressionAttributeValues: expr.Values(),
	})
	if err != nil {
		log.Printf("Error looking up user '%v'.\n", user.UserName)
		return event, err
	}
	if len(output.Items) == 0 {
		log.Printf("User '%v' not found, not migrating user.\n", user.UserName)
		return event, err
	}

	var users []UserInfo
	err = attributevalue.UnmarshalListOfMaps(output.Items, &users)
	if err != nil {
		log.Printf("Couldn't unmarshal DynamoDB items. Here's why: %v\n", err)
		return event, err
	}

	user = users[0]
	log.Printf("UserName '%v' found with email %v. User is migrated and must reset password.\n", user.UserName, user.UserEmail)
	event.CognitoEventUserPoolsMigrateUserResponse.UserAttributes = map[string]string{
		"email":          user.UserEmail,
		"email_verified": "true", // email_verified is required for the forgot password flow.
	}
	event.CognitoEventUserPoolsMigrateUserResponse.FinalUserStatus = "RESET_REQUIRED"
	event.CognitoEventUserPoolsMigrateUserResponse.MessageAction = "SUPPRESS"

	return event, err
}

func main() {
	ctx := context.Background()
	sdkConfig, err := config.LoadDefaultConfig(ctx)
	if err != nil {
		log.Panicln(err)
	}
	h := handler{
		dynamoClient: dynamodb.NewFromConfig(sdkConfig),
	}
	lambda.Start(h.HandleRequest)
}

// snippet-end:[gov2.lambda.MigrateUserHandler]
