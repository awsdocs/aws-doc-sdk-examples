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

type SQSGetQueueUrlImpl struct{}

func (dt SQSGetQueueUrlImpl) GetQueueUrl(ctx context.Context,
    params *sqs.GetQueueUrlInput,
    optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error) {

    output := &sqs.GetQueueUrlOutput{
        QueueUrl: aws.String("aws-docs-example-queue-url"),
    }

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
        msg := "You must supply a value for QueueName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestGetQueueUrl(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &SQSGetQueueUrlImpl{}

    input := &sqs.GetQueueUrlInput{
        QueueName: &globalConfig.QueueName,
    }

    result, err := GetQueueURL(context.TODO(), api, input)
    if err != nil {
        t.Log("Got an error getting the queue URL:")
        t.Log(err)
        return
    }

    fmt.Println("URL: " + *result.QueueUrl)
}
