// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

// snippet-start:[gov2.lambda.AutoConfirmHandler]

import (
	"context"
	"log"
	"os"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	dynamodbtypes "github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

const TABLE_NAME = "TABLE_NAME"

// UserInfo defines structured user data that can be marshalled to a DynamoDB format.
type UserInfo struct {
	UserName  string `dynamodbav:"UserName"`
	UserEmail string `dynamodbav:"UserEmail"`
}

// GetKey marshals the user email value to a DynamoDB key format.
func (user UserInfo) GetKey() map[string]dynamodbtypes.AttributeValue {
	userEmail, err := attributevalue.Marshal(user.UserEmail)
	if err != nil {
		panic(err)
	}
	return map[string]dynamodbtypes.AttributeValue{"UserEmail": userEmail}
}

type handler struct {
	dynamoClient *dynamodb.Client
}

// HandleRequest handles the PreSignUp event by looking up a user in an Amazon DynamoDB table and
// specifying whether they should be confirmed and verified.
func (h *handler) HandleRequest(ctx context.Context, event events.CognitoEventUserPoolsPreSignup) (events.CognitoEventUserPoolsPreSignup, error) {
	log.Printf("Received presignup from %v for user '%v'", event.TriggerSource, event.UserName)
	if event.TriggerSource != "PreSignUp_SignUp" {
		// Other trigger sources, such as PreSignUp_AdminInitiateAuth, ignore the response from this handler.
		return event, nil
	}
	tableName := os.Getenv(TABLE_NAME)
	user := UserInfo{
		UserEmail: event.Request.UserAttributes["email"],
	}
	log.Printf("Looking up email %v in table %v.\n", user.UserEmail, tableName)
	output, err := h.dynamoClient.GetItem(ctx, &dynamodb.GetItemInput{
		Key:       user.GetKey(),
		TableName: aws.String(tableName),
	})
	if err != nil {
		log.Printf("Error looking up email %v.\n", user.UserEmail)
		return event, err
	}
	if output.Item == nil {
		log.Printf("Email %v not found. Email verification is required.\n", user.UserEmail)
		return event, err
	}

	err = attributevalue.UnmarshalMap(output.Item, &user)
	if err != nil {
		log.Printf("Couldn't unmarshal DynamoDB item. Here's why: %v\n", err)
		return event, err
	}

	if user.UserName != event.UserName {
		log.Printf("UserEmail %v found, but stored UserName '%v' does not match supplied UserName '%v'. Verification is required.\n",
			user.UserEmail, user.UserName, event.UserName)
	} else {
		log.Printf("UserEmail %v found with matching UserName %v. User is confirmed.\n", user.UserEmail, user.UserName)
		event.Response.AutoConfirmUser = true
		event.Response.AutoVerifyEmail = true
	}

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

// snippet-end:[gov2.lambda.AutoConfirmHandler]
