// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[sqs.go-v2.ListQueues]
package main

import (
    "context"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/sqs"
)

// SQSListQueuesAPI defines the interface for the ListQueues function.
// We use this interface to test the function using a mocked service.
type SQSListQueuesAPI interface {
    ListQueues(ctx context.Context,
        params *sqs.ListQueuesInput,
        optFns ...func(*sqs.Options)) (*sqs.ListQueuesOutput, error)
}

// GetQueues retrieves a list of your Amazon Simple Queue Service (Amazon SQS) queues.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a ListQueuesOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to ListQueues.
func GetQueues(c context.Context, api SQSListQueuesAPI, input *sqs.ListQueuesInput) (*sqs.ListQueuesOutput, error) {
    result, err := api.ListQueues(c, input)

    return result, err
}

func main() {
    cfg, err := config.LoadDefaultConfig()
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := sqs.NewFromConfig(cfg)

    input := &sqs.ListQueuesInput{}

    result, err := GetQueues(context.Background(), client, input)
    if err != nil {
        fmt.Println("Got an error retrieving queue URLs:")
        fmt.Println(err)
        return
    }

    for i, url := range result.QueueUrls {
        fmt.Printf("%d: %s\n", i+1, *url)
    }
}
// snippet-end:[sqs.go-v2.ListQueues]
