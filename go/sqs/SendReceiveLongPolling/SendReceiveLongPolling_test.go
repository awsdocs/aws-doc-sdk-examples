// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"encoding/json"
	"io/ioutil"
	"strconv"
	"testing"

	"github.com/google/uuid"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"
)

// Config defines a set of configuration values
type Config struct {
	Queue    string `json:"Queue"`
	WaitTime int    `json:"WaitTime"`
}

// configFile defines the name of the file containing configuration values
var configFileName = "config.json"

// globalConfig contains the configuration values
var globalConfig Config

func populateConfiguration(t *testing.T) error {
	// Get configuration from config.json

	// Get entire file as a JSON string
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		return err
	}

	// Convert []byte to string
	text := string(content)

	// Marshall JSON string in text into global struct
	err = json.Unmarshal([]byte(text), &globalConfig)
	if err != nil {
		return err
	}

	t.Log("Queue:    " + globalConfig.Queue)
	t.Log("WaitTime: " + strconv.Itoa(globalConfig.WaitTime))

	return nil
}

func createLPQueue(sess *session.Session, queue *string, waitTime *int) (string, error) {
	// Create an SQS service client
	svc := sqs.New(sess)

	result, err := svc.CreateQueue(&sqs.CreateQueueInput{
		QueueName: queue,
		Attributes: aws.StringMap(map[string]string{
			"ReceiveMessageWaitTimeSeconds": strconv.Itoa(*waitTime),
		}),
	})
	if err != nil {
		return "", err
	}

	return *result.QueueUrl, nil
}

func deleteQueue(sess *session.Session, queueURL *string) error {
	// Create an SQS service client
	svc := sqs.New(sess)

	_, err := svc.DeleteQueue(&sqs.DeleteQueueInput{
		QueueUrl: queueURL,
	})
	if err != nil {
		return err
	}

	return nil
}

func TestQueue(t *testing.T) {
	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	// Create a session using credentials from ~/.aws/credentials
	// and the Region from ~/.aws/config
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	queueCreated := false
	queueURL := ""

	if globalConfig.Queue == "" {
		// Create a unique, random queue name
		id := uuid.New()
		globalConfig.Queue = "myqueue-" + id.String()

		queueURL, err = createLPQueue(sess, &globalConfig.Queue, &globalConfig.WaitTime)
		if err != nil {
			t.Fatal(err)
		}

		t.Log("Created queue " + globalConfig.Queue)
		queueCreated = true
	}

	err = SendMsg(sess, &queueURL)
	if err != nil {
		t.Fatal(err)
	}

	t.Log("Sent message to queue " + globalConfig.Queue)

	if queueCreated {
		err = deleteQueue(sess, &queueURL)
		if err != nil {
			t.Log("You'll have to delete queue " + globalConfig.Queue + " yourself")
			t.Fatal(err)
		}

		t.Log("Deleted queue " + globalConfig.Queue)
	}
}
