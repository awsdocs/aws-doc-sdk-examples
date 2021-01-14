// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
package main

import (
    "context"
    "encoding/json"
    "errors"
    "io/ioutil"
    "strconv"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/sqs"
    "github.com/aws/aws-sdk-go-v2/service/sqs/types"
)

type SQSReceiveMessageImpl struct{}

func (dt SQSReceiveMessageImpl) GetQueueUrl(ctx context.Context,
    params *sqs.GetQueueUrlInput,
    optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error) {

    output := &sqs.GetQueueUrlOutput{
        QueueUrl: aws.String("aws-docs-example-queue-url"),
    }

    return output, nil
}

func (dt SQSReceiveMessageImpl) ReceiveMessage(ctx context.Context,
    params *sqs.ReceiveMessageInput,
    optFns ...func(*sqs.Options)) (*sqs.ReceiveMessageOutput, error) {

    messages := make([]*types.Message, 2)
    messages[0] = &types.Message{
        MessageId:     aws.String("aws-docs-example-message1-id"),
        ReceiptHandle: aws.String("aws-docs-example-message1-receipt-handle"),
    }
    messages[1] = &types.Message{
        MessageId:     aws.String("aws-docs-example-message2-id"),
        ReceiptHandle: aws.String("aws-docs-example-message2-receipt-handle"),
    }

    output := &sqs.ReceiveMessageOutput{
        Messages: messages,
    }

    return output, nil
}

type Config struct {
    QueueName     string `json:"QueueName"`
    TimeoutString string `json:"Timeout"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration() error {
    content, err := ioutil.ReadFile(configFileName)
    if err != nil {
        return err
    }

    text := string(content)

    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    if globalConfig.QueueName == "" || globalConfig.TimeoutString == "" {
        msg := "You musts supply a value for QueueName and Timeout in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestReceiveMessage(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    timeout, err := strconv.Atoi(globalConfig.TimeoutString)
    if err != nil {
        t.Log(globalConfig.TimeoutString + " is not an integer")
        return
    }

    api := &SQSReceiveMessageImpl{}

    gQInput := &sqs.GetQueueUrlInput{
        QueueName: &globalConfig.QueueName,
    }

    // Get URL of queue
    urlResult, err := GetQueueURL(context.TODO(), api, gQInput)
    if err != nil {
        t.Log("Got an error getting the queue URL:")
        t.Log(err)
        return
    }

    queueURL := urlResult.QueueUrl

    gMInput := &sqs.ReceiveMessageInput{
        MessageAttributeNames: []*string{
            aws.String(string(types.QueueAttributeNameAll)),
        },
        QueueUrl:            queueURL,
        MaxNumberOfMessages: aws.Int32(1),
        VisibilityTimeout:   aws.Int32(int32(timeout)),
    }

    msgResult, err := GetMessages(context.TODO(), api, gMInput)
    if err != nil {
        t.Log("Got an error receiving messages:")
        t.Log(err)
        return
    }

    t.Log("Message ID:     " + *msgResult.Messages[0].MessageId)
    t.Log("Message Handle: " + *msgResult.Messages[0].ReceiptHandle)
}
