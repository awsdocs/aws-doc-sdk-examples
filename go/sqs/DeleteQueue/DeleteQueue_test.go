// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"encoding/json"
	"io/ioutil"
	"testing"

	"github.com/google/uuid"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"
)

// Config defines a set of configuration values
type Config struct {
	Queue string `json:"Queue"`
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

	t.Log("Queue: " + globalConfig.Queue)

	return nil
}

func createQueue(sess *session.Session, queue *string) (string, error) {
	// Create an SQS service client
	svc := sqs.New(sess)

	result, err := svc.CreateQueue(&sqs.CreateQueueInput{
		QueueName: queue,
		Attributes: map[string]*string{
			"DelaySeconds":           aws.String("60"),
			"MessageRetentionPeriod": aws.String("86400"),
		},
	})
	if err != nil {
		return "", err
	}

	return *result.QueueUrl, nil
}

func TestDeleteQueue(t *testing.T) {
	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	// Create a session using credentials from ~/.aws/credentials
	// and the Region from ~/.aws/config
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	queueURL := ""

	if globalConfig.Queue == "" {
		// Create a unique, random queue name
		id := uuid.New()
		globalConfig.Queue = "myqueue-" + id.String()

		queueURL, err = createQueue(sess, &globalConfig.Queue)
		if err != nil {
			t.Fatal(err)
		}

		t.Log("Created queue " + globalConfig.Queue)
	}

	err = DeleteQueue(sess, &queueURL)
	if err != nil {
		t.Log("You'll have to delete queue " + globalConfig.Queue + " yourself")
		t.Fatal(err)
	}

	t.Log("Deleted queue " + globalConfig.Queue)
}
