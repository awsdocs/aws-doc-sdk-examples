// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[sqs.go-v2.ConfigureLPQueue]
package main

import (
    "context"
    "flag"
    "fmt"
    "strconv"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/sqs"
)

// SQSConfigureLPQueueAPI defines the interface for the GetQueueUrl and SetQueueAttributes functions.
// We use this interface to test the function using a mocked service.
type SQSConfigureLPQueueAPI interface {
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
func GetQueueURL(c context.Context, api SQSConfigureLPQueueAPI, input *sqs.GetQueueUrlInput) (*sqs.GetQueueUrlOutput, error) {
    result, err := api.GetQueueUrl(c, input)

    return result, err
}

// ConfigureLPQueue configures an Amazon SQS queue to use long polling.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a SetQueueAttributesOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to MSetQueueAttributesETHOD.
func ConfigureLPQueue(c context.Context, api SQSConfigureLPQueueAPI, input *sqs.SetQueueAttributesInput) (*sqs.SetQueueAttributesOutput, error) {
    results, err := api.SetQueueAttributes(c, input)

    return results, err
}

func main() {
    queue := flag.String("q", "", "The name of the queue")
    waitTimeString := flag.String("w", "10", "The wait time, in seconds, for long polling")
    flag.Parse()

    if *queue == "" || *waitTimeString == "" {
        fmt.Println("You must supply a queue name (-q QUEUE) and wait time (-w WAIT-TIME)")
        return
    }

    waitTime, err := strconv.Atoi(*waitTimeString)
    if err != nil {
        fmt.Println(*waitTimeString + " is not an integer")
    }

    if waitTime < 1 {
        waitTime = 1
    }

    if waitTime > 20 {
        waitTime = 20
    }

    *waitTimeString = strconv.Itoa(waitTime)

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

    queueURL := result.QueueUrl

    cQInput := &sqs.SetQueueAttributesInput{
        QueueUrl: queueURL,
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": *waitTimeString,
        }),
    }

    _, err = ConfigureLPQueue(context.TODO(), client, cQInput)
    if err != nil {
        fmt.Println("Got an error configuring the queue:")
        fmt.Println(err)
        return
    }

    fmt.Println("Configured queue with URL " + *queueURL + " to use long polling")
}
// snippet-end:[sqs.go-v2.ConfigureLPQueue]
