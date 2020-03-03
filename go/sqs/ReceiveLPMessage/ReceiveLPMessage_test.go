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
    Message   string `json:"Message"`
    QueueName string `json:"QueueName"`
    Timeout   int64  `json:"Timeout"`
    WaitTime  int64  `json:"WaitTime"`
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

    if globalConfig.Timeout < 0 {
        globalConfig.Timeout = 0
    }

    if globalConfig.Timeout > 20 {
        globalConfig.Timeout = 20
    }

    if globalConfig.WaitTime < 0 {
        globalConfig.WaitTime = 0
    }

    if globalConfig.WaitTime > 20 {
        globalConfig.WaitTime = 20
    }

    t.Log("Message:            " + globalConfig.Message)
    t.Log("Queue name:         " + globalConfig.QueueName)
    t.Log("Timeout (seconds):  " + strconv.Itoa(int(globalConfig.Timeout)))
    t.Log("WaitTime (seconds): " + strconv.Itoa(int(globalConfig.WaitTime)))

    return nil
}

func createQueue(sess *session.Session, queueName string) (string, error) {
    // Create a SQS service client
    svc := sqs.New(sess)

    result, err := svc.CreateQueue(&sqs.CreateQueueInput{
        QueueName: aws.String(queueName),
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

func getQueueURL(sess *session.Session, queueName string) (string, error) {
    // Create a SQS service client
    svc := sqs.New(sess)

    result, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
        QueueName: aws.String(queueName),
    })
    if err != nil {
        return "", err
    }

    return *result.QueueUrl, nil
}

func configureLPQueue(sess *session.Session, queueURL string, timeout int) error {
    // Create a SQS service client
    svc := sqs.New(sess)

    _, err := svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
        QueueUrl: &queueURL,
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": strconv.Itoa(timeout),
        }),
    })
    if err != nil {
        return err
    }

    return nil
}

func sendMessage(sess *session.Session, queueURL string, message string) (string, error) {
    // Create a SQS service client
    svc := sqs.New(sess)

    result, err := svc.SendMessage(&sqs.SendMessageInput{
        DelaySeconds: aws.Int64(10),
        MessageBody:  aws.String(message),
        QueueUrl:     &queueURL,
    })
    if err != nil {
        return "", err
    }

    return *result.MessageId, nil
}

func deleteQueue(sess *session.Session, queueURL string) error {
    // Create a SQS service client
    svc := sqs.New(sess)

    _, err := svc.DeleteQueue(&sqs.DeleteQueueInput{
        QueueUrl: aws.String(queueURL),
    })
    if err != nil {
        return err
    }

    return nil
}

func TestReceiveLPMessages(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("20060102150405")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    // Create a session using credentials from ~/.aws/credentials
    // and the region from ~/.aws/config
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    if globalConfig.Message == "" {
        globalConfig.Message = "Sent " + thisTime.Format("2006-01-02 15:04:05 Monday")
    }

    // If we create a queue, we also need to delete it
    queueCreated := false
    url := ""

    if globalConfig.QueueName == "" {
        // Create unique, random queue name
        id := uuid.New()
        globalConfig.QueueName = "myqueue-" + id.String()

        url, err = createQueue(sess, globalConfig.QueueName)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Created queue " + globalConfig.QueueName)
        queueCreated = true
    } else {
        url, err = getQueueURL(sess, globalConfig.QueueName)
        if err != nil {
            t.Fatal(err)
        }
    }

    // Make sure it's an LP queue
    err = configureLPQueue(sess, url, int(globalConfig.WaitTime))
    if err != nil {
        t.Fatal(err)
    }

    msgID, err := sendMessage(sess, url, globalConfig.Message)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Sent message to queue " + globalConfig.QueueName)

    msgs, err := GetLPMessages(sess, url, globalConfig.WaitTime)
    if err != nil {
        t.Fatal(err)
    }

    numMsgs := len(msgs)

    if numMsgs > 0 {
        t.Log("Got " + strconv.Itoa(numMsgs) + " messages")
        foundMsg := false

        t.Log("Message IDs:")
        for _, msg := range msgs {
            t.Log("    " + *msg.MessageId)
            if msgID == *msg.MessageId {
                t.Log("Got message: " + *msg.Body)
                foundMsg = true
                break
            }
        }

        if foundMsg {
            t.Log("Found sent message")
        } else {
            t.Log("Did NOT find message")
        }
    } else {
        t.Log("Did not get any messages")
    }

    if queueCreated {
        err = deleteQueue(sess, url)
        if err != nil {
            t.Log("You'll have to delete queue " + globalConfig.QueueName + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted queue " + globalConfig.QueueName)
    }
}
