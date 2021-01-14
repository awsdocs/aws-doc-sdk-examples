// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[sqs.go-v2.GetQueueUrl]
package main

import (
    "context"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/sqs"
)

// SQSGetQueueUrlAPI defines the interface for the GetQueueUrl function.
// We use this interface to test the function using a mocked service.
type SQSGetQueueUrlAPI interface {
    GetQueueUrl(ctx context.Context,
        params *sqs.GetQueueUrlInput,
        optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error)
}

// GetQueueURL gets the URL of an Amazon SQS queue.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a GetQueueUrlOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to GetQueueUrl.
func GetQueueURL(c context.Context, api SQSGetQueueUrlAPI, input *sqs.GetQueueUrlInput) (*sqs.GetQueueUrlOutput, error) {
    result, err := api.GetQueueUrl(c, input)

    return result, err
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

    input := &sqs.GetQueueUrlInput{
        QueueName: queue,
    }

    result, err := GetQueueURL(context.TODO(), client, input)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    fmt.Println("URL: " + *result.QueueUrl)
}
// snippet-end:[sqs.go-v2.GetQueueUrl]
