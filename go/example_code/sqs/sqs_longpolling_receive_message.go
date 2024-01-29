// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[sqs.go.longpolling_receive_message.complete]
package main

// snippet-start:[sqs.go.longpolling_receive_message.imports]
import (
	"flag"
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.longpolling_receive_message.imports]

// Receive message from Queue with long polling enabled.
//
// Usage:
//
//	go run sqs_longpolling_receive_message.go -n queue_name -t timeout
func main() {
	// snippet-start:[sqs.go.longpolling_receive_message.vars]
	namePtr := flag.String("n", "", "Queue name")
	timeoutPtr := flag.Int64("t", 20, "(Optional) Timeout in seconds for long polling")

	flag.Parse()

	if *namePtr == "" {
		flag.PrintDefaults()
		exitErrorf("Queue name required")
	}
	// snippet-end:[sqs.go.longpolling_receive_message.vars]

	// Initialize a session that the SDK will use to load
	// credentials from the shared credentials file. (~/.aws/credentials).
	// snippet-start:[sqs.go.longpolling_receive_message.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := sqs.New(sess)
	// snippet-end:[sqs.go.longpolling_receive_message.session]

	// Need to convert the queue name into a URL. Make the GetQueueUrl
	// API call to retrieve the URL. This is needed for receiving messages
	// from the queue.
	// snippet-start:[sqs.go.longpolling_receive_message.url]
	resultURL, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
		QueueName: namePtr,
	})
	if err != nil {
		if aerr, ok := err.(awserr.Error); ok && aerr.Code() == sqs.ErrCodeQueueDoesNotExist {
			exitErrorf("Unable to find queue %q.", *namePtr)
		}
		exitErrorf("Unable to queue %q, %v.", *namePtr, err)
	}
	// snippet-end:[sqs.go.longpolling_receive_message.url]

	// Receive a message from the SQS queue with long polling enabled.
	// snippet-start:[sqs.go.longpolling_receive_message.receive]
	result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
		QueueUrl: resultURL.QueueUrl,
		AttributeNames: aws.StringSlice([]string{
			"SentTimestamp",
		}),
		MaxNumberOfMessages: aws.Int64(1),
		MessageAttributeNames: aws.StringSlice([]string{
			"All",
		}),
		WaitTimeSeconds: timeoutPtr,
	})
	if err != nil {
		exitErrorf("Unable to receive message from queue %q, %v.", *namePtr, err)
	}

	fmt.Printf("Received %d messages.\n", len(result.Messages))
	if len(result.Messages) > 0 {
		fmt.Println(result.Messages)
	}
	// snippet-end:[sqs.go.longpolling_receive_message.receive]
}

// snippet-start:[sqs.go.longpolling_receive_message.exit]
func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}

// snippet-end:[sqs.go.longpolling_receive_message.exit]
// snippet-end:[sqs.go.longpolling_receive_message.complete]
