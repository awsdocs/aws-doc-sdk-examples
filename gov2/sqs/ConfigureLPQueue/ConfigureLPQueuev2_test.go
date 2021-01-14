// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
package main

import (
    "context"
    "encoding/json"
    "errors"
    "fmt"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/sqs"
)

type SQSConfigureLPQueueImpl struct{}

func (dt SQSConfigureLPQueueImpl) GetQueueUrl(ctx context.Context,
    params *sqs.GetQueueUrlInput,
    optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error) {

    output := &sqs.GetQueueUrlOutput{
        QueueUrl: aws.String("aws-docs-example-queue-url"),
    }

    return output, nil
}

func (dt SQSConfigureLPQueueImpl) SetQueueAttributes(ctx context.Context,
    params *sqs.SetQueueAttributesInput,
    optFns ...func(*sqs.Options)) (*sqs.SetQueueAttributesOutput, error) {

    output := &sqs.SetQueueAttributesOutput{}

    return output, nil
}

type Config struct {
    QueueName      string `json:"QueueName"`
    WaitTimeString string `json:"WaitTime"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration(t *testing.T) error {
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
        msg := "You must supply a value for QueueName and WaitTime in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestConfigureLPQueue(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    api := &SQSConfigureLPQueueImpl{}

    gQInput := &sqs.GetQueueUrlInput{
        QueueName: &globalConfig.QueueName,
    }

    result, err := GetQueueURL(context.TODO(), api, gQInput)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    queueURL := result.QueueUrl

    cQInput := &sqs.SetQueueAttributesInput{
        QueueUrl: queueURL,
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": globalConfig.WaitTimeString,
        }),
    }

    _, err = ConfigureLPQueue(context.TODO(), api, cQInput)
    if err != nil {
        fmt.Println("Got an error configuring the queue:")
        fmt.Println(err)
        return
    }

    t.Log("Configured queue with URL " + *queueURL + " to use long polling")
}
