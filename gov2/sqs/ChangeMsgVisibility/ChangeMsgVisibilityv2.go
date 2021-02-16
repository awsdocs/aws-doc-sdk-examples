// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[sqs.go-v2.ChangeMessageVisibility]
package main

import (
	"context"
	"flag"
	"fmt"
	"strconv"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
)

// SQSSetMsgVisibilityAPI defines the interface for the GetQueueUrl and ChangeMessageVisibility functions.
// We use this interface to test the functions using a mocked service.
type SQSSetMsgVisibilityAPI interface {
	GetQueueUrl(ctx context.Context,
		params *sqs.GetQueueUrlInput,
		optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error)

	ChangeMessageVisibility(ctx context.Context,
		params *sqs.ChangeMessageVisibilityInput,
		optFns ...func(*sqs.Options)) (*sqs.ChangeMessageVisibilityOutput, error)
}

// GetQueueURL gets the URL of an Amazon SQS queue.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a GetQueueUrlOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to GetQueueUrl.
func GetQueueURL(c context.Context, api SQSSetMsgVisibilityAPI, input *sqs.GetQueueUrlInput) (*sqs.GetQueueUrlOutput, error) {
	return api.GetQueueUrl(c, input)


}

// SetMsgVisibility sets the visibility timeout for a message in an Amazon SQS queue.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a ChangeMessageVisibilityOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to ChangeMessageVisibility.
func SetMsgVisibility(c context.Context, api SQSSetMsgVisibilityAPI, input *sqs.ChangeMessageVisibilityInput) (*sqs.ChangeMessageVisibilityOutput, error) {
	return api.ChangeMessageVisibility(c, input)
}

func main() {
	queue := flag.String("q", "", "The name of the queue")
	handle := flag.String("h", "", "The receipt handle of the message")
	visibility := flag.Int("v", 30, "The duration, in seconds, that the message is not visible to other consumers")
	flag.Parse()

	if *queue == "" || *handle == "" {
		fmt.Println("You must supply a queue name (-q QUEUE) and message receipt handle (-h HANDLE)")
		return
	}

	if *visibility < 0 {
		*visibility = 0
	}

	if *visibility > 12*60*60 {
		*visibility = 12 * 60 * 60
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := sqs.NewFromConfig(cfg)

	gQInput := &sqs.GetQueueUrlInput{
		QueueName: queue,
	}

	// Get URL of queue
	urlResult, err := GetQueueURL(context.TODO(), client, gQInput)
	if err != nil {
		fmt.Println("Got an error getting the queue URL:")
		fmt.Println(err)
		return
	}

	queueURL := urlResult.QueueUrl

	sVInput := &sqs.ChangeMessageVisibilityInput{
		ReceiptHandle:     handle,
		QueueUrl:          queueURL,
		VisibilityTimeout: int32(*visibility),
	}

	_, err = SetMsgVisibility(context.TODO(), client, sVInput)
	if err != nil {
		fmt.Println("Got an error setting the visibility of the message:")
		fmt.Println(err)
		return
	}

	fmt.Println("Changed the visibility of the message with the handle " + *handle + " in the " + *queue + " to " + strconv.Itoa(*visibility))
}

// snippet-end:[sqs.go-v2.ChangeMessageVisibility]
