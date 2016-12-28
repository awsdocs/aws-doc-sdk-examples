package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"
)

// Usage:
// go run sqs_deletequeue.go
func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create a SQS service client.
	svc := sqs.New(sess)

	result, err := svc.DeleteQueue(&sqs.DeleteQueueInput{
		QueueUrl: aws.String("SQS_QUEUE_URL"),
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Success", result)
}
