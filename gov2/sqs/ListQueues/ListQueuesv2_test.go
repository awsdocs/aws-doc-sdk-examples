// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
package main

import (
    "context"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/service/sqs"
    "github.com/aws/aws-sdk-go/aws"
)

type SQSListQueuesImpl struct{}

func (dt SQSListQueuesImpl) ListQueues(ctx context.Context,
    params *sqs.ListQueuesInput,
    optFns ...func(*sqs.Options)) (*sqs.ListQueuesOutput, error) {

    // URLs look like:
    //    https://sqs.REGION.amazonaws.com/ACCOUNT#/QUEUE-NAME
    prefix := "https://sqs.REGION.amazonaws.com/ACCOUNT#/"

    urls := []*string{aws.String(prefix+"aws-docs-example-queue-url1"), aws.String(prefix+"aws-docs-example-queue-url2")}

    output := &sqs.ListQueuesOutput{
        QueueUrls: urls,
    }

    return output, nil
}

func TestListQueues(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    api := &SQSListQueuesImpl{}

    input := &sqs.ListQueuesInput{}

    resp, err := GetQueues(context.Background(), *api, input)
    if err != nil {
        t.Log("Got an error ...:")
        t.Log(err)
        return
    }

    for _, url := range resp.QueueUrls {
        t.Log("Queue URL: ", *url)
    }
}
