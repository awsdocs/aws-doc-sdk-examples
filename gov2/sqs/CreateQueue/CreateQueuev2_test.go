// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package main

import (
	"context"
	_ "embed"
	"encoding/json"
	"errors"
	"fmt"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
)

type SQSCreateQueueImpl struct{}

func (dt SQSCreateQueueImpl) CreateQueue(ctx context.Context,
	params *sqs.CreateQueueInput,
	optFns ...func(*sqs.Options)) (*sqs.CreateQueueOutput, error) {

	output := &sqs.CreateQueueOutput{
		QueueUrl: aws.String("aws-docs-example-queue-url"),
	}

	return output, nil
}

type Config struct {
	QueueName string `json:"QueueName"`
}

//go:embed config.json
var configjson []byte

var globalConfig Config

func populateConfiguration(t *testing.T) error {

	err := json.Unmarshal(configjson, &globalConfig)
	if err != nil {
		return err
	}

	if globalConfig.QueueName == "" {
		msg := "You must supply a value for QueueName in config.json"
		return errors.New(msg)
	}

	return nil
}

func TestCreateQueue(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	api := &SQSCreateQueueImpl{}

	input := &sqs.CreateQueueInput{
		QueueName: &globalConfig.QueueName,
		Attributes: map[string]string{
			"DelaySeconds":           "60",
			"MessageRetentionPeriod": "86400",
		},
	}

	result, err := CreateQueue(context.Background(), api, input)
	if err != nil {
		fmt.Println("Got an error creating the queue:")
		fmt.Println(err)
		return
	}

	t.Log("URL: " + *result.QueueUrl)
}
