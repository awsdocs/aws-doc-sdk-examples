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
    "strconv"
    "testing"
    "time"

    "github.com/google/uuid"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// Config defines a set of configuration values
type Config struct {
    Queue      string `json:"Queue"`
    Visibility int64  `json:"Visibility"`
    WaitTime   int    `json:"WaitTime"`
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

    if globalConfig.Visibility < 0 {
        globalConfig.Visibility = 0
    }

    if globalConfig.Visibility > 12*60*60 { // 12 hours
        globalConfig.Visibility = 12 * 60 * 60
    }

    if globalConfig.WaitTime < 0 {
        globalConfig.WaitTime = 0
    }

    if globalConfig.WaitTime > 20 {
        globalConfig.WaitTime = 20
    }

    t.Log("Queue:   " + globalConfig.Queue)
    t.Log("Visibility: " + strconv.Itoa(int(globalConfig.Visibility)))
    t.Log("WaitTime:   " + strconv.Itoa(globalConfig.WaitTime))

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

func configureLPQueue(sess *session.Session, queueURL *string, timeout int) error {
    // Create an SQS service client
    svc := sqs.New(sess)

    _, err := svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
        QueueUrl: queueURL,
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": strconv.Itoa(timeout),
        }),
    })
    if err != nil {
        return err
    }

    return nil
}

func sendMessage(sess *session.Session, queueURL string) error {
    // Create an SQS service client
    svc := sqs.New(sess)

    currentTime := time.Now()

    _, err := svc.SendMessage(&sqs.SendMessageInput{
        DelaySeconds: aws.Int64(0),
        MessageBody:  aws.String(currentTime.Format("2006-01-02 15:04:05 Monday")),
        QueueUrl:     &queueURL,
    })
    if err != nil {
        return err
    }

    return nil
}

func receiveMessage(sess *session.Session, queueURL string) (string, error) {
    // Create an SQS service client
    svc := sqs.New(sess)

    result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
        AttributeNames: []*string{
            aws.String(sqs.MessageSystemAttributeNameSentTimestamp),
        },
        MessageAttributeNames: []*string{
            aws.String(sqs.QueueAttributeNameAll),
        },
        QueueUrl:            &queueURL,
        MaxNumberOfMessages: aws.Int64(1),
    })
    if err != nil {
        return "", err
    }

    if len(result.Messages) > 0 {
        return *result.Messages[0].ReceiptHandle, nil
    }

    return "", nil
}

func deleteMsg(sess *session.Session, url string, handle string) error {
    svc := sqs.New(sess)

    _, err := svc.DeleteMessage(&sqs.DeleteMessageInput{
        QueueUrl:      &url,
        ReceiptHandle: &handle,
    })
    if err != nil {
        return err
    }

    return nil
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

func TestChangeVisibility(t *testing.T) {
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

        queueURL, err = createQueue(sess, &globalConfig.Queue)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Created queue " + globalConfig.Queue)
    }

    if globalConfig.WaitTime > 0 {
        err := configureLPQueue(sess, &queueURL, globalConfig.WaitTime)
        if err != nil {
            t.Log("You'll have to delete queue " + globalConfig.Queue + " yourself")
            t.Fatal(err)
        }
    }

    err = sendMessage(sess, queueURL)
    if err != nil {
        t.Log("You'll have to delete queue " + globalConfig.Queue + " yourself")
        t.Fatal(err)
    }

    t.Log("Sent message to queue " + globalConfig.Queue)

    msgHandle, err := receiveMessage(sess, queueURL)
    if err != nil {
        t.Log("You'll have to delete queue " + globalConfig.Queue + " yourself")
        t.Fatal(err)
    }

    if msgHandle != "" {
        t.Log("Received message")

        err = SetMsgVisibility(sess, &msgHandle, &queueURL, &globalConfig.Visibility)
        if err != nil {
            t.Log("You'll have to delete queue " + globalConfig.Queue + " yourself")
            t.Fatal(err)
        }

        t.Log("Changed message's visibility to " + strconv.Itoa(int(globalConfig.Visibility)))

        // Delete message
        err = deleteMsg(sess, queueURL, msgHandle)
        if err != nil {
            t.Log("Got an error deleting msg:")
            t.Fatal(err)
        }

        t.Log("Deleted message")
    } else {
        t.Log("Did not receive message")
    }

    if queueCreated {
        err = deleteQueue(sess, queueURL)
        if err != nil {
            t.Log("You'll have to delete queue " + globalConfig.Queue + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted queue " + globalConfig.Queue)
    }
}
