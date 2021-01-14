// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[sqs.go-v2.DeadLetterQueue]
package main

import (
	"context"
	"encoding/json"
	"flag"
	"fmt"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
)

// SQSDeadLetterQueueAPI defines the interface for the GetQueueUrl and SetQueueAttributes functions.
// We use this interface to test the function using a mocked service.
type SQSDeadLetterQueueAPI interface {
	GetQueueUrl(ctx context.Context,
		params *sqs.GetQueueUrlInput,
		optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error)

	SetQueueAttributes(ctx context.Context,
		params *sqs.SetQueueAttributesInput,
		optFns ...func(*sqs.Options)) (*sqs.SetQueueAttributesOutput, error)
}

// GetQueueURL gets the URL of an Amazon SQS queue.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a GetQueueUrlOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to GetQueueUrl.
func GetQueueURL(c context.Context, api SQSDeadLetterQueueAPI, input *sqs.GetQueueUrlInput) (*sqs.GetQueueUrlOutput, error) {
	result, err := api.GetQueueUrl(c, input)

	return result, err
}

// GetQueueArn gets the ARN of a queue based on its URL
func GetQueueArn(queueURL *string) *string {
	parts := strings.Split(*queueURL, "/")
	subParts := strings.Split(parts[2], ".")

	arn := "arn:aws:" + subParts[0] + ":" + subParts[1] + ":" + parts[3] + ":" + parts[4]

	return &arn
}

// ConfigureDeadLetterQueue configures an Amazon SQS queue for messages that could not be delivered to another queue.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a SetQueueAttributesOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to SetQueueAttributes.
func ConfigureDeadLetterQueue(c context.Context, api SQSDeadLetterQueueAPI, input *sqs.SetQueueAttributesInput) (*sqs.SetQueueAttributesOutput, error) {
	result, err := api.SetQueueAttributes(c, input)

	return result, err
}

func main() {
	queue := flag.String("q", "", "The name of the queue")
	dlQueue := flag.String("d", "", "The name of the dead-letter queue")
	flag.Parse()

	if *queue == "" || *dlQueue == "" {
		fmt.Println("You must supply the names of the queue (-q QUEUE) and the dead-letter queue (-d DLQUEUE)")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := sqs.NewFromConfig(cfg)

	gQInput := &sqs.GetQueueUrlInput{
		QueueName: queue,
	}

	result, err := GetQueueURL(context.TODO(), client, gQInput)
	if err != nil {
		fmt.Println("Got an error getting the queue URL:")
		fmt.Println(err)
		return
	}

	dlQueueURL := result.QueueUrl

	// Get the ARN for the dead-letter queue
	arn := GetQueueArn(dlQueueURL)

	// Our redrive policy for our queue
	policy := map[string]string{
		"deadLetterTargetArn": *arn,
		"maxReceiveCount":     "10",
	}

	// Marshal policy for SetQueueAttributes
	b, err := json.Marshal(policy)
	if err != nil {
		return
	}

	cQInput := &sqs.SetQueueAttributesInput{
		QueueUrl: dlQueueURL,
		Attributes: map[string]*string{
			"RedrivePolicy": aws.String(string(b)),
		},
	}

	_, err = ConfigureDeadLetterQueue(context.TODO(), client, cQInput)
	if err != nil {
		fmt.Println("Got an error configuring the dead-letter queue:")
		fmt.Println(err)
		return
	}

	fmt.Println("Created dead-letter queue")
}
// snippet-end:[sqs.go-v2.DeadLetterQueue]
