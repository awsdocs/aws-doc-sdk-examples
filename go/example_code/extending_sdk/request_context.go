// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"

	"context"
	"fmt"
	"time"
)

func main() {
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := sqs.New(sess)

	// URL to our queue
	qURL := "QueueURL"

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	// SQS ReceiveMessage
	params := &sqs.ReceiveMessageInput{
		AttributeNames: []*string{
			aws.String(sqs.MessageSystemAttributeNameSentTimestamp),
		},
		MessageAttributeNames: []*string{
			aws.String(sqs.QueueAttributeNameAll),
		},
		QueueUrl:            &qURL,
		MaxNumberOfMessages: aws.Int64(1),
		VisibilityTimeout:   aws.Int64(20), // 20 seconds
		WaitTimeSeconds:     aws.Int64(0),
	}

	// snippet-start:[extending.go.receive_message_request]
	req, resp := svc.ReceiveMessageRequest(params)
	req.HTTPRequest = req.HTTPRequest.WithContext(ctx)

	err := req.Send()
	if err != nil {
		fmt.Println("Got error receiving message:")
		fmt.Println(err.Error())
	} else {
		fmt.Println(resp)
	}
	// snippet-end:[extending.go.receive_message_request]
}
