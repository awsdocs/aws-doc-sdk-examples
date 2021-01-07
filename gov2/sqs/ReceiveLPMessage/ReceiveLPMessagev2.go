// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[sqs.go-v2.ReceiveLPMessage]
package main

import (
    "context"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/sqs"
    "github.com/aws/aws-sdk-go-v2/service/sqs/types"
)

// SQSGetLPMsgAPI defines the interface for the GetQueueUrl and ReceiveMessage functions.
// We use this interface to test the functions using a mocked service.
type SQSGetLPMsgAPI interface {
    GetQueueUrl(ctx context.Context,
        params *sqs.GetQueueUrlInput,
        optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error)

    ReceiveMessage(ctx context.Context,
        params *sqs.ReceiveMessageInput,
        optFns ...func(*sqs.Options)) (*sqs.ReceiveMessageOutput, error)
}

// GetQueueURL gets the URL of an Amazon SQS queue.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a GetQueueUrlOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to GetQueueUrl.
func GetQueueURL(c context.Context, api SQSGetLPMsgAPI, input *sqs.GetQueueUrlInput) (*sqs.GetQueueUrlOutput, error) {
    result, err := api.GetQueueUrl(c, input)

    return result, err
}

// GetLPMessages gets the messages from an Amazon SQS long polling queue.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a ReceiveMessageOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to ReceiveMessage.
func GetLPMessages(c context.Context, api SQSGetLPMsgAPI, input *sqs.ReceiveMessageInput) (*sqs.ReceiveMessageOutput, error) {
    result, err := api.ReceiveMessage(c, input)

    return result, err
}

func main() {
    queue := flag.String("q", "", "The name of the queue")
    waitTime := flag.Int("w", 10, "How long the queue waits for messages")
    flag.Parse()

    if *queue == "" {
        fmt.Println("You must supply a queue name (-q QUEUE")
        return
    }

    if *waitTime < 0 {
        *waitTime = 0
    }

    if *waitTime > 20 {
        *waitTime = 20
    }

    cfg, err := config.LoadDefaultConfig(context.TODO())
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := sqs.NewFromConfig(cfg)

    qInput := &sqs.GetQueueUrlInput{
        QueueName: queue,
    }

    result, err := GetQueueURL(context.Background(), client, qInput)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    queueURL := result.QueueUrl

    mInput := &sqs.ReceiveMessageInput{
        QueueUrl: queueURL,
        AttributeNames: ([]types.QueueAttributeName{
            "SentTimestamp",
        }),
        MaxNumberOfMessages: aws.Int32(1),
        MessageAttributeNames: aws.StringSlice([]string{
            "All",
        }),
        WaitTimeSeconds: aws.Int32(int32(*waitTime)),
    }

    resp, err := GetLPMessages(context.Background(), client, mInput)
    if err != nil {
        fmt.Println("Got an error receiving messages:")
        fmt.Println(err)
        return
    }

    fmt.Println("Message IDs:")

    for _, msg := range resp.Messages {
        fmt.Println("    " + *msg.MessageId)
    }
}
// snippet-end:[sqs.go-v2.ReceiveLPMessage]
