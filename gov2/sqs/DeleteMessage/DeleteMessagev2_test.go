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

type SQSDeleteMessageImpl struct{}

func (dt SQSDeleteMessageImpl) GetQueueUrl(ctx context.Context,
	params *sqs.GetQueueUrlInput,
	optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error) {

	output := &sqs.GetQueueUrlOutput{
		QueueUrl: aws.String("aws-docs-example-queue-url"),
	}

	return output, nil
}

func (dt SQSDeleteMessageImpl) DeleteMessage(ctx context.Context,
	params *sqs.DeleteMessageInput,
	optFns ...func(*sqs.Options)) (*sqs.DeleteMessageOutput, error) {

	output := &sqs.DeleteMessageOutput{}

	return output, nil
}

type Config struct {
	QueueName     string `json:"QueueName"`
	MessageHandle string `json:"MessageHandle"`
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

	if globalConfig.QueueName == "" || globalConfig.MessageHandle == "" {
		msg := "You must supply a value for QueueName and MessageHandle in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestDeleteMessage(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	api := &SQSDeleteMessageImpl{}

	qUInput := &sqs.GetQueueUrlInput{
		QueueName: &globalConfig.QueueName,
	}

	// Get URL of queue
	result, err := GetQueueURL(context.TODO(), api, qUInput)
	if err != nil {
		t.Log("Got an error getting the queue URL:")
		t.Log(err)
		return
	}

	queueURL := result.QueueUrl

	dMInput := &sqs.DeleteMessageInput{
		QueueUrl:      queueURL,
		ReceiptHandle: &globalConfig.MessageHandle,
	}

	_, err = RemoveMessage(context.TODO(), api, dMInput)
	if err != nil {
		t.Log("Got an error deleting the message:")
		t.Log(err)
		return
	}

	t.Log("Deleted message with message handle: " + globalConfig.MessageHandle + " from " + globalConfig.QueueName + " queue with URL " + *queueURL)
}
