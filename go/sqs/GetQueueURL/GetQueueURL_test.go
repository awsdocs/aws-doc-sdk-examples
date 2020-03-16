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
    "github.com/aws/aws-sdk-go/service/sts"
)

// Config defines a set of configuration values
type Config struct {
    QueueName string `json:"QueueName"`
}

// configFile defines the name of the file containing configuration values
var configFileName = "config.json"

// globalConfig contains the configuration values
var globalConfig Config

func populateConfiguration() error {
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

    return nil
}

func createQueue(sess *session.Session, queueName string) (string, error) {
    // Create an SQS service client
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

func getFakeURL(sess *session.Session, queueName string) (string, error) {
    // Construct URL based on known pattern
    // For example, for the queue MyGroovyQueue:
    //     https://sqs.REGION.amazonaws.com/ACCOUNT-ID/MyGroovyQueue

    region := sess.Config.Region

    svc := sts.New(sess)
    input := &sts.GetCallerIdentityInput{}

    result, err := svc.GetCallerIdentity(input)
    if err != nil {
        return "", err
    }

    accountID := aws.StringValue(result.Account)

    return "https://sqs." + *region + ".amazonaws.com/" + accountID + "/" + queueName, nil
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

func TestQueue(t *testing.T) {
    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    // Create a session using credentials from ~/.aws/credentials
    // and the Region from ~/.aws/config
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    queueCreated := false

    if globalConfig.QueueName == "" {
        // Create a unique, random queue name
        id := uuid.New()
        globalConfig.QueueName = "myqueue-" + id.String()

        _, err = createQueue(sess, globalConfig.QueueName)
        if err != nil {
            t.Fatal(err)
        }

        queueCreated = true
    }

    newURL, err := GetQueueURL(sess, &globalConfig.QueueName)
    if err != nil {
        t.Fatal(err)
    }

    fakeURL, err := getFakeURL(sess, globalConfig.QueueName)
    if err != nil {
        t.Fatal(err)
    }

    if newURL != fakeURL {
        msg := "The URL returned by GetQueueUrl: " + newURL + " does not match the expected URL: " + fakeURL
        t.Fatal(msg)
    }

    t.Log("The URL created matched the URL retrieved: " + newURL)

    if queueCreated {
        err = deleteQueue(sess, newURL)
        if err != nil {
            t.Log("You'll have to delete queue " + globalConfig.QueueName + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted queue " + globalConfig.QueueName)
    }
}
