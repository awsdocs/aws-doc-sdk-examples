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
)

type SQSSetMsgVisibilityImpl struct{}

func (dt SQSSetMsgVisibilityImpl) GetQueueUrl(ctx context.Context,
	params *sqs.GetQueueUrlInput,
	optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error) {

	output := &sqs.GetQueueUrlOutput{
		QueueUrl: aws.String("aws-docs-example-queue-url"),
	}

	return output, nil
}

func (dt SQSSetMsgVisibilityImpl) ChangeMessageVisibility(ctx context.Context,
	params *sqs.ChangeMessageVisibilityInput,
	optFns ...func(*sqs.Options)) (*sqs.ChangeMessageVisibilityOutput, error) {

	output := &sqs.ChangeMessageVisibilityOutput{}

	return output, nil
}

type Config struct {
	QueueName        string `json:"QueueName"`
	Handle           string `json:"Handle"`
	VisibilityString string `json:"Visibility"`
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

	if globalConfig.QueueName == "" || globalConfig.Handle == "" || globalConfig.VisibilityString == "" {
		msg := "You musts supply a value for QueueName, Handle, and Visibility in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestChangeMsgVisibility(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	visibility, err := strconv.Atoi(globalConfig.VisibilityString)
	if err != nil {
		t.Log("Configuration value Visibility (" + globalConfig.VisibilityString + ") is not an integer")
		return
	}

	api := &SQSSetMsgVisibilityImpl{}

	gQInput := &sqs.GetQueueUrlInput{
		QueueName: &globalConfig.QueueName,
	}

	// Get URL of queue
	urlResult, err := GetQueueURL(context.Background(), api, gQInput)
	if err != nil {
		t.Log("Got an error getting the queue URL:")
		t.Log(err)
		return
	}

	queueURL := urlResult.QueueUrl

	sVInput := &sqs.ChangeMessageVisibilityInput{
		ReceiptHandle:     &globalConfig.Handle,
		QueueUrl:          queueURL,
		VisibilityTimeout: aws.Int32(int32(visibility)),
	}

	_, err = SetMsgVisibility(context.Background(), api, sVInput)
	if err != nil {
		t.Log("Got an error setting the visibility of the message:")
		t.Log(err)
		return
	}

	t.Log("Changed the visibility of the message with the handle " + globalConfig.Handle + " in the " + globalConfig.QueueName + " to " + globalConfig.VisibilityString)
}
