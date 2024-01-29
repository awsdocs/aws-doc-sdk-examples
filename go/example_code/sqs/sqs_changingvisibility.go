// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"
)

// Usage:
// go run sqs_changingvisibility.go
func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create a SQS service client.
	svc := sqs.New(sess)

	// URL to our queue
	qURL := "QueueURL"

	result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
		AttributeNames: []*string{
			aws.String(sqs.MessageSystemAttributeNameSentTimestamp),
		},
		MaxNumberOfMessages: aws.Int64(1),
		MessageAttributeNames: []*string{
			aws.String(sqs.QueueAttributeNameAll),
		},
		QueueUrl: &qURL,
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	// Check if we have any messages
	if len(result.Messages) == 0 {
		fmt.Println("Received no messages")
		return
	}

	// 30 seconds timeout
	duration := int64(30)
	resultVisibility, err := svc.ChangeMessageVisibility(&sqs.ChangeMessageVisibilityInput{
		ReceiptHandle:     result.Messages[0].ReceiptHandle,
		QueueUrl:          &qURL,
		VisibilityTimeout: &duration,
	})

	if err != nil {
		fmt.Println("Visibility Error", err)
		return
	}

	fmt.Println("Time Changed", resultVisibility)
}
