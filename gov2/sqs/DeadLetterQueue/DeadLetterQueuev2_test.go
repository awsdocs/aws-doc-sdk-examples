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

type SQSDeadLetterQueueImpl struct{}

func (dt SQSDeadLetterQueueImpl) GetQueueUrl(ctx context.Context,
	params *sqs.GetQueueUrlInput,
	optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error) {

	// URLs look like:
	//    https://sqs.REGION.amazonaws.com/ACCOUNT#/QUEUE-NAME
	prefix := "https://sqs.REGION.amazonaws.com/ACCOUNT#/"

	output := &sqs.GetQueueUrlOutput{
		QueueUrl: aws.String(prefix + "aws-docs-example-queue-url1"),
	}

	return output, nil
}

func (dt SQSDeadLetterQueueImpl) SetQueueAttributes(ctx context.Context,
	params *sqs.SetQueueAttributesInput,
	optFns ...func(*sqs.Options)) (*sqs.SetQueueAttributesOutput, error) {

	output := &sqs.SetQueueAttributesOutput{}

	return output, nil
}

type Config struct {
	QueueName   string `json:"QueueName"`
	DLQueueName string `json:"DLQueueName"`
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

	if globalConfig.QueueName == "" || globalConfig.DLQueueName == "" {
		msg := "You must specify a value for QueueName and DLQueueName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestDeadLetterQueue(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	api := &SQSDeadLetterQueueImpl{}

	// Get URL for queue
	gQInput := &sqs.GetQueueUrlInput{
		QueueName: &globalConfig.QueueName,
	}

	qResult, err := GetQueueURL(context.TODO(), api, gQInput)
	if err != nil {
		fmt.Println("Got an error getting the queue URL:")
		fmt.Println(err)
		return
	}

	queueURL := qResult.QueueUrl

	// Get the URL for the dead-letter queue
	gDLQInput := &sqs.GetQueueUrlInput{
		QueueName: &globalConfig.DLQueueName,
	}

	dlResult, err := GetQueueURL(context.TODO(), api, gDLQInput)
	if err != nil {
		t.Log("Got an error retrieving URL for dead letter queue:")
		t.Log(err)
		return
	}

	dlQueueURL := dlResult.QueueUrl

	// Get the ARN for the dead-letter queue
	dlQueueArn := GetQueueArn(dlQueueURL)

	// Our redrive policy for our queue
	policy := map[string]string{
		"deadLetterTargetArn": *dlQueueArn,
		"maxReceiveCount":     "10",
	}

	// Marshal policy for SetQueueAttributes
	b, err := json.Marshal(policy)
	if err != nil {
		t.Log("Got an error marshalling the policy:")
		t.Log(err)
		return
	}

	cQInput := &sqs.SetQueueAttributesInput{
		QueueUrl: queueURL,
		Attributes: map[string]*string{
			"RedrivePolicy": aws.String(string(b)),
		},
	}

	_, err = ConfigureDeadLetterQueue(context.TODO(), api, cQInput)
	if err != nil {
		t.Log("Got an error configuring the dead-letter queue:")
		t.Log(err)
		return
	}

	t.Log("Created dead-letter queue")
}
