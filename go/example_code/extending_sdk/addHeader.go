// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/request"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"

	"fmt"
	"os"
)

func main() {
	// Initialize a session in us-west-2 that the SDK will use to load credentials
	// from the shared config file. (~/.aws/credentials).
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)
	if err != nil {
		fmt.Println("Error getting session:")
		fmt.Println(err)
		os.Exit(1)
	}

	// Create DynamoDB client
	// and expose HTTP requests/responses
	svc := dynamodb.New(sess, aws.NewConfig().WithLogLevel(aws.LogDebugWithHTTPBody))

	// Add "CustomHeader" header with value of 10
	svc.Handlers.Send.PushFront(func(r *request.Request) {
		r.HTTPRequest.Header.Set("CustomHeader", fmt.Sprintf("%d", 10))
	})

	// Call ListTables just to see HTTP request/response
	// The request should have the CustomHeader set to 10
	_, _ = svc.ListTables(&dynamodb.ListTablesInput{})
}
