// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[sqs.go.longpolling_existing_queue.complete]
package main

// snippet-start:[sqs.go.longpolling_existing_queue.imports]
import (
	"flag"
	"fmt"
	"os"
	"strconv"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.longpolling_existing_queue.imports]

// Updates the existing queue to use the long polling option.
//
// Usage:
//
//	go run sqs_longpolling_existing_queue.go -n queue_name -t timeout
func main() {
	// snippet-start:[sqs.go.longpolling_existing_queue.vars]
	namePtr := flag.String("n", "", "Queue name")
	timeoutPtr := flag.Int("t", 20, "(Optional) Timeout in seconds for long polling")

	flag.Parse()

	if *namePtr == "" {
		flag.PrintDefaults()
		exitErrorf("Queue name required")
	}
	// snippet-end:[sqs.go.longpolling_existing_queue.vars]

	// Initialize a session that the SDK will use to load
	// credentials from the shared credentials file. (~/.aws/credentials).
	// snippet-start:[sqs.go.longpolling_existing_queue.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := sqs.New(sess)
	// snippet-end:[sqs.go.longpolling_existing_queue.session]

	// Need to convert the queue name into a URL. Make the GetQueueUrl
	// API call to retrieve the URL. This is needed for setting attributes
	// on the queue.
	// snippet-start:[sqs.go.longpolling_existing_queue.url]
	resultURL, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
		QueueName: namePtr,
	})
	if err != nil {
		if aerr, ok := err.(awserr.Error); ok && aerr.Code() == sqs.ErrCodeQueueDoesNotExist {
			exitErrorf("Unable to find queue %q.", *namePtr)
		}
		exitErrorf("Unable to get queue %q, %v.", *namePtr, err)
	}
	// snippet-end:[sqs.go.longpolling_existing_queue.url]

	// Update the queue enabling long polling.
	// snippet-start:[sqs.go.longpolling_existing_queue.enable]
	_, err = svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
		QueueUrl: resultURL.QueueUrl,
		Attributes: aws.StringMap(map[string]string{
			"ReceiveMessageWaitTimeSeconds": strconv.Itoa(*timeoutPtr),
		}),
	})
	if err != nil {
		exitErrorf("Unable to update queue %q, %v.", *namePtr, err)
	}

	fmt.Printf("Successfully updated queue %q.\n", *namePtr)
	// snippet-end:[sqs.go.longpolling_existing_queue.enable]
}

// snippet-start:[sqs.go.longpolling_existing_queue.error]
func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}

// snippet-end:[sqs.go.longpolling_existing_queue.error]
// snippet-end:[sqs.go.longpolling_existing_queue.complete]
