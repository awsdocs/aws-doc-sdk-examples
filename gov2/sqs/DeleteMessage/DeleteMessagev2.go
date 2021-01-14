// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[sqs.go-v2.DeleteMessage]
package main

import (
    "context"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/sqs"
)

// SQSDeleteMessageAPI defines the interface for the GetQueueUrl and DeleteMessage functions.
// We use this interface to test the functions using a mocked service.
type SQSDeleteMessageAPI interface {
    GetQueueUrl(ctx context.Context,
        params *sqs.GetQueueUrlInput,
        optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error)

    DeleteMessage(ctx context.Context,
        params *sqs.DeleteMessageInput,
        optFns ...func(*sqs.Options)) (*sqs.DeleteMessageOutput, error)
}

// GetQueueURL gets the URL of an Amazon SQS queue.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a GetQueueUrlOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to GetQueueUrl.
func GetQueueURL(c context.Context, api SQSDeleteMessageAPI, input *sqs.GetQueueUrlInput) (*sqs.GetQueueUrlOutput, error) {
    return api.GetQueueUrl(c, input)
}

// RemoveMessage deletes a message from an Amazon SQS queue.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a DeleteMessageOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to DeleteMessage.
func RemoveMessage(c context.Context, api SQSDeleteMessageAPI, input *sqs.DeleteMessageInput) (*sqs.DeleteMessageOutput, error) {
    return api.DeleteMessage(c, input)
}

func main() {
    queue := flag.String("q", "", "The name of the queue")
    messageHandle := flag.String("m", "", "The receipt handle of the message")
    flag.Parse()

    if *queue == "" || *messageHandle == "" {
        fmt.Println("You must supply a queue name (-q QUEUE) and message receipt handle (-m MESSAGE-HANDLE)")
        return
    }

    cfg, err := config.LoadDefaultConfig(context.TODO())
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := sqs.NewFromConfig(cfg)

    qUInput := &sqs.GetQueueUrlInput{
        QueueName: queue,
    }

    // Get URL of queue
    result, err := GetQueueURL(context.Background(), client, qUInput)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    queueURL := result.QueueUrl

    dMInput := &sqs.DeleteMessageInput{
        QueueUrl:      queueURL,
        ReceiptHandle: messageHandle,
    }

    _, err = RemoveMessage(context.Background(), client, dMInput)
    if err != nil {
        fmt.Println("Got an error deleting the message:")
        fmt.Println(err)
        return
    }

    fmt.Println("Deleted message from queue with URL " + *queueURL)
}
// snippet-end:[sqs.go-v2.DeleteMessage]
