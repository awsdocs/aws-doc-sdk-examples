// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"encoding/json"
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"
)

// Usage:
// go run sqs_deadletterqueue.go
func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create a SQS service client.
	svc := sqs.New(sess)

	// Our redrive policy for our queue
	policy := map[string]string{
		"deadLetterTargetArn": "SQS_QUEUE_ARN",
		"maxReceiveCount":     "10",
	}

	// Marshal our policy to be used as input for our SetQueueAttributes
	// call.
	b, err := json.Marshal(policy)
	if err != nil {
		fmt.Println("Failed to marshal policy:", err)
		return
	}

	result, err := svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
		QueueUrl: aws.String("SQS_QUEUE_URL"),
		Attributes: map[string]*string{
			sqs.QueueAttributeNameRedrivePolicy: aws.String(string(b)),
		},
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Success", result)
}
