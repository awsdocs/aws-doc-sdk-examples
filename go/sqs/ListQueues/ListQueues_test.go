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
    "github.com/aws/aws-sdk-go/service/sts"
)

type Config struct {
    // Set to true to create a new bucket
    // and wait SleepSeconds seconds (60 seems to work) for it to appear in the list
    Confirm      bool  `json:"Confirm"`
    SleepSeconds int64 `json:"SleepSeconds"`
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

    if globalConfig.Confirm {
        t.Log("Confirming new bucket will show up in the list")
        t.Log("by waiting " + strconv.Itoa(int(globalConfig.SleepSeconds)))
    } else {
        t.Log("Skipping confirmation")
    }

    if globalConfig.SleepSeconds < 0 {
        globalConfig.SleepSeconds = 0
    }

    if globalConfig.SleepSeconds > 60 {
        globalConfig.SleepSeconds = 60
    }

    return nil
}

func createQueue(sess *session.Session, queueName string) (string, error) {
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
    svc := sqs.New(sess)

    _, err := svc.DeleteQueue(&sqs.DeleteQueueInput{
        QueueUrl: aws.String(queueURL),
    })
    if err != nil {
        return err
    }

    return nil
}

func multiplyDuration(factor int64, d time.Duration) time.Duration {
    return time.Duration(factor) * d
}

func TestListQueues60(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("20060102150405")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    queueName := ""
    expectedURL := ""

    if globalConfig.Confirm {
        id := uuid.New()
        queueName = "myqueue-" + id.String()

        expectedURL, err := getFakeURL(sess, queueName)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Queue should have URL: " + expectedURL)
    }

    queueURLs, err := GetQueues(sess)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Queue URLs:")

    for _, url := range queueURLs {
        t.Log("    " + *url)

        if globalConfig.Confirm {
            // Make sure name is NOT in the list
            if expectedURL == *url {
                msg := "Got unexpected URL"
                t.Fatal(msg)
            }
        }
    }

    if globalConfig.Confirm {
        // Now create the queue and make sure it IS in the list
        gotURL, err := createQueue(sess, queueName)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Created queue " + queueName)

        if gotURL == expectedURL {
            t.Log("URLs match")
        }

        // It takes a while for the queue to appear, so sleep Duration seconds
        t.Log("Slept " + strconv.Itoa(int(globalConfig.SleepSeconds)) + " seconds")

        ts := multiplyDuration(globalConfig.SleepSeconds, time.Second)
        time.Sleep(ts)

        queueURLs, err = GetQueues(sess)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Queue URLs:")

        for _, url := range queueURLs {
            if gotURL == *url {
                t.Log("    BINGO!")
            } else {
                t.Log("    " + *url)
            }
        }

        err = deleteQueue(sess, gotURL)
        if err != nil {
            t.Log("You'll have to delete queue " + queueName + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted queue " + queueName)
    }
}
