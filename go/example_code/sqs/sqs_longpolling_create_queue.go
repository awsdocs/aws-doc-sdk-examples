// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[sqs.go.longpolling_create_queue.complete]
package main

// snippet-start:[sqs.go.longpolling_create_queue.imports]
import (
	"flag"
	"fmt"
	"os"
	"strconv"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.longpolling_create_queue.imports]

// Creates a new SQS queue with long polling enabled. If the Queue already exists
// no error will be returned.
//
// Usage:
//
//	go run sqs_longpolling_create_queue.go -n queue_name -t timeout
func main() {
	// snippet-start:[sqs.go.longpolling_create_queue.vars]
	namePtr := flag.String("n", "", "Queue name")
	timeoutPtr := flag.Int("t", 20, "(Optional) Timeout in seconds for long polling")

	flag.Parse()

	if *namePtr == "" {
		flag.PrintDefaults()
		exitErrorf("Queue name required")
	}
	// snippet-end:[sqs.go.longpolling_create_queue.vars]

	// Initialize a session that the SDK will use to load
	// credentials from the shared credentials file. (~/.aws/credentials).
	// snippet-start:[sqs.go.longpolling_create_queue.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[sqs.go.longpolling_create_queue.session]

	// snippet-start:[sqs.go.longpolling_create_queue.create]
	svc := sqs.New(sess)

	// Create the Queue with long polling enabled
	result, err := svc.CreateQueue(&sqs.CreateQueueInput{
		QueueName: namePtr,
		Attributes: aws.StringMap(map[string]string{
			"ReceiveMessageWaitTimeSeconds": strconv.Itoa(*timeoutPtr),
		}),
	})
	if err != nil {
		exitErrorf("Unable to create queue %q, %v.", *namePtr, err)
	}

	fmt.Printf("Successfully created queue %q. URL: %s\n", *namePtr,
		aws.StringValue(result.QueueUrl))

	// snippet-end:[sqs.go.longpolling_create_queue.create]
}

// snippet-start:[sqs.go.longpolling_create_queue.exit]
func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}

// snippet-end:[sqs.go.longpolling_create_queue.exit]
// snippet-end:[sqs.go.longpolling_create_queue.complete]
