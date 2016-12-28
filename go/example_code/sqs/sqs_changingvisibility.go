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
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

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

	// 10 hour timeout
	duration := int64(36000)
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
