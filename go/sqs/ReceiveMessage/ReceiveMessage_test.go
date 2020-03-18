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
    Message      string `json:"Message"`
    Queue        string `json:"Queue"`
    SleepSeconds int64  `json:"SleepSeconds"`
    Timeout      int64  `json:"Timeout"`
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

    if globalConfig.Timeout > 12*60*60 {
        globalConfig.Timeout = 12 * 60 * 60
    }

    t.Log("Message:           " + globalConfig.Message)
    t.Log("Queue:             " + globalConfig.Queue)
    t.Log("SleepSeconds:      " + strconv.Itoa(int(globalConfig.SleepSeconds)))
    t.Log("Timeout (seconds): " + strconv.Itoa(int(globalConfig.Timeout)))

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

func sendMessage(sess *session.Session, queueURL *string, message *string) (string, error) {
    // Create an SQS service client
    svc := sqs.New(sess)

    result, err := svc.SendMessage(&sqs.SendMessageInput{
        DelaySeconds: aws.Int64(10),
        MessageBody:  message,
        QueueUrl:     queueURL,
    })
    if err != nil {
        return "", err
    }

    return *result.MessageId, nil
}

func multiplyDuration(factor int64, d time.Duration) time.Duration {
    return time.Duration(factor) * d
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

func TestReceiveMessages(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    // Create a session using credentials from ~/.aws/credentials
    // and the Region from ~/.aws/config
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    if globalConfig.Message == "" {
        globalConfig.Message = "Sent " + thisTime.Format("2006-01-02 15:04:05 Monday")
    }

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

        queueCreated = true
        t.Log("Created queue " + globalConfig.Queue)
    }

    msgID, err := sendMessage(sess, &queueURL, &globalConfig.Message)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Sent message with ID " + msgID + " to queue " + globalConfig.Queue)

    ts := multiplyDuration(globalConfig.SleepSeconds, time.Second)
    time.Sleep(ts)

    t.Log("Slept " + strconv.Itoa(int(globalConfig.SleepSeconds)) + " second(s)")

    msg, err := GetMessage(sess, &queueURL, &globalConfig.Timeout)
    if err != nil {
        t.Fatal(err)
    }

    if msgID == *msg.MessageId {
        t.Log("Found sent message")
    } else {
        t.Log("Did NOT find message")
    }

    if queueCreated {
        err = deleteQueue(sess, &queueURL)
        if err != nil {
            t.Log("You'll have to delete queue " + globalConfig.Queue + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted queue " + globalConfig.Queue)
    }
}
