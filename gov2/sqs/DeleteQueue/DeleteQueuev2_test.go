// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
package main

import (
    "context"
    "encoding/json"
    "errors"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/sqs"
)

type SQSDeleteQueueImpl struct{}

func (dt SQSDeleteQueueImpl) GetQueueUrl(ctx context.Context,
    params *sqs.GetQueueUrlInput,
    optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error) {

    output := &sqs.GetQueueUrlOutput{
        QueueUrl: aws.String("aws-docs-example-queue-url"),
    }

    return output, nil
}

func (dt SQSDeleteQueueImpl) DeleteQueue(ctx context.Context,
    params *sqs.DeleteQueueInput,
    optFns ...func(*sqs.Options)) (*sqs.DeleteQueueOutput, error) {
    output := &sqs.DeleteQueueOutput{}

    return output, nil
}

type Config struct {
    QueueName string `json:"QueueName"`
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

    if globalConfig.QueueName == "" {
        msg := "You musts supply a value for QueueName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestDeleteQueue(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &SQSDeleteQueueImpl{}

    qInput := &sqs.GetQueueUrlInput{
        QueueName: &globalConfig.QueueName,
    }

    // Get the URL for the queue
    result, err := GetQueueURL(context.TODO(), api, qInput)
    if err != nil {
        t.Log("Got an error getting the queue URL:")
        t.Log(err)
        return
    }

    queueURL := result.QueueUrl

    dqInput := &sqs.DeleteQueueInput{
        QueueUrl: queueURL,
    }

    _, err = DeleteQueue(context.TODO(), api, dqInput)
    if err != nil {
        t.Log("Got an error deleting the queue:")
        t.Log(err)
        return
    }

    t.Log("Deleted queue with URL " + *queueURL)
}
