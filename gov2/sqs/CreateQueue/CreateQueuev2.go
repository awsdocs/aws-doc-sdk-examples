// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[sqs.go-v2.CreateQueue]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
)

// SQSCreateQueueAPI defines the interface for the CreateQueue function.
// We use this interface to test the function using a mocked service.
type SQSCreateQueueAPI interface {
	CreateQueue(ctx context.Context,
		params *sqs.CreateQueueInput,
		optFns ...func(*sqs.Options)) (*sqs.CreateQueueOutput, error)
}

// CreateQueue creates an Amazon SQS queue.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a CreateQueueOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to CreateQueue.
func CreateQueue(c context.Context, api SQSCreateQueueAPI, input *sqs.CreateQueueInput) (*sqs.CreateQueueOutput, error) {
	return api.CreateQueue(c, input)
}

func main() {
	queue := flag.String("q", "", "The name of the queue")
	flag.Parse()

	if *queue == "" {
		fmt.Println("You must supply a queue name (-q QUEUE")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := sqs.NewFromConfig(cfg)

	input := &sqs.CreateQueueInput{
		QueueName: queue,
		Attributes: map[string]string{
			"DelaySeconds":           "60",
			"MessageRetentionPeriod": "86400",
		},
	}

	result, err := CreateQueue(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got an error creating the queue:")
		fmt.Println(err)
		return
	}

	fmt.Println("URL: " + *result.QueueUrl)
}

// snippet-end:[sqs.go-v2.CreateQueue]
