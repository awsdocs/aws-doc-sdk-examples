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

type SQSGetLPMsgImpl struct{}

func (dt SQSGetLPMsgImpl) GetQueueUrl(ctx context.Context,
    params *sqs.GetQueueUrlInput,
    optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error) {

    output := &sqs.GetQueueUrlOutput{
        QueueUrl: aws.String("aws-docs-example-queue-url"),
    }

    return output, nil
}

func (dt SQSGetLPMsgImpl) ReceiveMessage(ctx context.Context,
    params *sqs.ReceiveMessageInput,
    optFns ...func(*sqs.Options)) (*sqs.ReceiveMessageOutput, error) {

    messages := make([]*types.Message, 2)
    messages[0] = &types.Message{MessageId: aws.String("aws-docs-example-message1-id")}
    messages[1] = &types.Message{MessageId: aws.String("aws-docs-example-message2-id")}

    output := &sqs.ReceiveMessageOutput{
        Messages: messages,
    }

    return output, nil
}

type Config struct {
    QueueName      string `json:"QueueName"`
    WaitTimeString string `json:"WaitTime"`
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

    if globalConfig.QueueName == "" || globalConfig.WaitTimeString == "" {
        msg := "You musts supply a value for QueueName and WaitTime in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestGetLPMsg(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    waitTime, err := strconv.Atoi(globalConfig.WaitTimeString)
    if err != nil {
        t.Log("WaitTime value (" + globalConfig.WaitTimeString + ") is not an integer")
        return
    }

    api := &SQSGetLPMsgImpl{}

    qInput := &sqs.GetQueueUrlInput{
        QueueName: &globalConfig.QueueName,
    }

    result, err := GetQueueURL(context.Background(), api, qInput)
    if err != nil {
        t.Log("Got an error getting the queue URL:")
        t.Log(err)
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
        WaitTimeSeconds: aws.Int32(int32(waitTime)),
    }

    resp, err := GetLPMessages(context.Background(), api, mInput)
    if err != nil {
        t.Log("Got an error receiving messages:")
        t.Log(err)
        return
    }

    t.Log("Message IDs:")

    for _, msg := range resp.Messages {
        t.Log("    " + *msg.MessageId)
    }
}
