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

// snippet-start:[sqs.go.imports]
import (
    "log"
    "strconv"
    "testing"
    "time"

    "github.com/google/uuid"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.imports]

func getQueueURLs(sess *session.Session) ([]*string, error) {
    // Create an SQS service client
    svc := sqs.New(sess)

    result, err := svc.ListQueues(nil)
    if err != nil {
        return nil, err
    }

    return result.QueueUrls, nil
}

func listQueueURLs(sess *session.Session, t *testing.T) error {
    // Get and list the URLs of the queues in the default region
    queueURLs, err := getQueueURLs(sess)
    if err != nil {
        t.Log("Could not get queue URLs")
        return err
    }

    t.Log("Queue URLs:")

    for _, urls := range queueURLs {
        if urls == nil {
            continue
        }

        t.Log(*urls)
    }

    return nil
}

func multiplyDuration(factor int64, d time.Duration) time.Duration {
    return time.Duration(factor) * d
}

func isQueueInList(list []*string, queue string) bool {
    for _, q := range list {
        if *q == queue {
            return true
        }
    }

    return false
}

func TestBucketCrudOps(t *testing.T) {
    err := PopulateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    // snippet-start:[sqs.go.session]
    // Create a session using credentials from ~/.aws/credentials
    // and the region from ~/.aws/config
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.session]

    // Create a random, unique queue name
    id := uuid.New()
    queueName := "MyQueue-" + id.String()

    // Do the same for a dead-letter queue (for the previous queue)
    id = uuid.New()
    deadLetterQueueName := "MyDLQueue-" + id.String()

    // Do the same for a long-polling queue
    id = uuid.New()
    lPQueueName := "MyLPQueue-" + id.String()

    err = listQueueURLs(sess, t)
    if err != nil {
        t.Fatal(err)
    }

    // Create a queue
    queueURL, err := CreateQueue(sess, queueName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created queue with URL " + queueURL)

    // Make sure we got the correct URL
    uRL, err := GetQueueURL(sess, queueName)
    if err != nil {
        t.Fatal(err)
    }

    if uRL != queueURL {
        t.Fatal("URLs did not agree: " + uRL + " !+ " + queueURL)
    }

    t.Log("Confirmed queue URL")

    // Create dead-letter queue for queueName
    deadLetterQueueURL, err := CreateQueue(sess, deadLetterQueueName)
    if err != nil {
        log.Fatal(err)
    }

    // Now make it a dead-letter queue for queueName
    err = ConfigureDeadLetterQueue(sess, deadLetterQueueURL, queueURL, 10)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created dead-letter queue " + deadLetterQueueName)

    // Send a message to the queue
    msgID, err := SendMessage(sess, queueURL)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Sent a message with ID " + msgID + " to the queue " + queueName)
    t.Log("Waiting " + strconv.Itoa(GlobalConfig.RetrySeconds) + " seconds before trying to read messages")

    // Spin our wheels for a bit
    ts := multiplyDuration(int64(GlobalConfig.RetrySeconds), time.Second)
    time.Sleep(ts)

    // Get the messages from the queue
    msgs, err := ReceiveMessages(sess, queueURL)
    if err != nil {
        t.Fatal(err)
    }

    numMsgs := len(msgs)

    t.Log("Got " + strconv.Itoa(numMsgs) + " message(s)")

    // Get receipt handle for message with ID msgID
    receiptHandle := ""
    for _, m := range msgs {
        if *m.MessageId == msgID {
            receiptHandle = *m.ReceiptHandle
            break
        }
    }

    if receiptHandle == "" {
        msg := "Did not get a message with ID " + msgID + " from queue " + queueName
        t.Fatal(msg)
    }

    // Change the timeout for the message with the receipt handle in the queue
    err = ChangeVisibility(sess, queueURL, receiptHandle, int64(GlobalConfig.Timeout))
    if err != nil {
        t.Fatal(err)
    }

    err = DeleteMessage(sess, queueURL, receiptHandle)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted message with ID " + msgID)

    // Long polling operations
    err = ChangeQueue(sess, queueURL, GlobalConfig.Timeout)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Converted queue " + queueName + " to long-polling")

    // Create long-polling queue
    lpQueueURL, err := CreateLongPollingQueue(sess, lPQueueName, GlobalConfig.Timeout)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created long polling queue with URL " + lpQueueURL)

    // Send a message to a long-polling queue
    result, err := SendMessage(sess, lpQueueURL)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Sent a message with ID " + result + " to the long-polling queue " + lPQueueName)

    lpMessages, err := ReceiveLongPollingMessages(sess, lpQueueURL, int64(GlobalConfig.Timeout))
    if err != nil {
        t.Fatal(err)
    }

    numMsgs = len(lpMessages)

    t.Log("Got " + strconv.Itoa(numMsgs) + " long-polling message(s)")

    // Now delete the queues
    err = DeleteQueue(sess, queueURL)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted queue with URL " + queueURL)

    err = DeleteQueue(sess, deadLetterQueueURL)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted queue with URL " + deadLetterQueueURL)

    err = DeleteQueue(sess, lpQueueURL)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted queue with URL" + lpQueueURL)

    list, err := getQueueURLs(sess)
    if err != nil {
        t.Fatal(err)
    }

    isInList := isQueueInList(list, queueURL)
    if isInList {
        t.Error("Queue " + queueName + " is still in list")
    } else {
        t.Log("Confirmed " + queueName + " is deleted")
    }

    isInList = isQueueInList(list, deadLetterQueueURL)
    if isInList {
        t.Error("Queue " + deadLetterQueueName + " is still in list")
    } else {
        t.Log("Confirmed " + deadLetterQueueName + " is deleted")
    }

    isInList = isQueueInList(list, lpQueueURL)
    if isInList {
        t.Error("Queue " + lPQueueName + " is still in list")
    } else {
        t.Log("Confirmed " + lPQueueName + " is deleted")
    }
}
