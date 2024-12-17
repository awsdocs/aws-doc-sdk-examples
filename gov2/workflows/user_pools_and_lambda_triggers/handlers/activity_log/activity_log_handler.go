// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

// snippet-start:[gov2.lambda.ActivityLogHandler]

import (
	"context"
	"fmt"
	"log"
	"os"
	"time"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	dynamodbtypes "github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

const TABLE_NAME = "TABLE_NAME"

// LoginInfo defines structured login data that can be marshalled to a DynamoDB format.
type LoginInfo struct {
	UserPoolId string `dynamodbav:"UserPoolId"`
	ClientId   string `dynamodbav:"ClientId"`
	Time       string `dynamodbav:"Time"`
}

// UserInfo defines structured user data that can be marshalled to a DynamoDB format.
type UserInfo struct {
	UserName  string    `dynamodbav:"UserName"`
	UserEmail string    `dynamodbav:"UserEmail"`
	LastLogin LoginInfo `dynamodbav:"LastLogin"`
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

// HandleRequest handles the PostAuthentication event by writing custom data to the logs and
// to an Amazon DynamoDB table.
func (h *handler) HandleRequest(ctx context.Context, event events.CognitoEventUserPoolsPostAuthentication) (events.CognitoEventUserPoolsPostAuthentication, error) {
	log.Printf("Received post authentication trigger from %v for user '%v'", event.TriggerSource, event.UserName)
	tableName := os.Getenv(TABLE_NAME)
	user := UserInfo{
		UserName:  event.UserName,
		UserEmail: event.Request.UserAttributes["email"],
		LastLogin: LoginInfo{
			UserPoolId: event.UserPoolID,
			ClientId:   event.CallerContext.ClientID,
			Time:       time.Now().Format(time.UnixDate),
		},
	}
	// Write to CloudWatch Logs.
	fmt.Printf("%#v", user)

	// Also write to an external system. This examples uses DynamoDB to demonstrate.
	userMap, err := attributevalue.MarshalMap(user)
	if err != nil {
		log.Printf("Couldn't marshal to DynamoDB map. Here's why: %v\n", err)
	} else if len(userMap) == 0 {
		log.Printf("User info marshaled to an empty map.")
	} else {
		_, err := h.dynamoClient.PutItem(ctx, &dynamodb.PutItemInput{
			Item:      userMap,
			TableName: aws.String(tableName),
		})
		if err != nil {
			log.Printf("Couldn't write to DynamoDB. Here's why: %v\n", err)
		} else {
			log.Printf("Wrote user info to DynamoDB table %v.\n", tableName)
		}
	}

	return event, nil
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

// snippet-end:[gov2.lambda.ActivityLogHandler]
