/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

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
    QueueURL   string `json:"QueueURL"`
    DlQueueURL string `json:"DlQueueURL"`
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

    t.Log("QueueURL:   " + globalConfig.QueueURL)
    t.Log("DlQueueURL: " + globalConfig.DlQueueURL)

    return nil
}

func createQueue(sess *session.Session, queueName *string) (string, error) {
    // Create an SQS service client
    svc := sqs.New(sess)

    result, err := svc.CreateQueue(&sqs.CreateQueueInput{
        QueueName: queueName,
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

func deleteQueue(sess *session.Session, queueURL string) error {
    // Create an SQS service client
    svc := sqs.New(sess)

    _, err := svc.DeleteQueue(&sqs.DeleteQueueInput{
        QueueUrl: aws.String(queueURL),
    })
    if err != nil {
        return err
    }

    return nil
}

func TestDeadLetterQueue(t *testing.T) {
    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    // Create a session using credentials from ~/.aws/credentials
    // and the Region from ~/.aws/config
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    queueName := ""
    dlQueueName := ""
    dlARN := ""
    queueCreated := false
    dlQueueCreated := false

    id := uuid.New()

    if globalConfig.QueueURL == "" {
        // Create a unique, random queue name
        queueName = "myqueue-" + id.String()

        globalConfig.QueueURL, err = createQueue(sess, &queueName)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Created queue " + queueName)
        queueCreated = true
    }

    if globalConfig.DlQueueURL == "" {
        // Create a unique, random queue name
        dlQueueName = "mydlqueue-" + id.String()

        globalConfig.DlQueueURL, err = createQueue(sess, &dlQueueName)
        if err != nil {
            t.Fatal(err)
        }

        dlARN = GetQueueArn(&globalConfig.DlQueueURL)

        t.Log("Created dead-letter queue " + dlQueueName)
        dlQueueCreated = true
    }

    err = ConfigureDeadLetterQueue(sess, &dlARN, &globalConfig.QueueURL)
    if err != nil {
        t.Fatal(err)
    }

    if queueCreated {
        err = deleteQueue(sess, globalConfig.QueueURL)
        if err != nil {
            t.Log("You'll have to delete queue " + queueName + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted queue " + queueName)
    }

    if dlQueueCreated {
        err = deleteQueue(sess, globalConfig.DlQueueURL)
        if err != nil {
            t.Log("You'll have to delete queue " + dlQueueName + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted queue " + dlQueueName)
    }
}
