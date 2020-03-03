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
// snippet-start:[sqs.go.create_lp_queue]
package main

// snippet-start:[sqs.go.create_lp_queue.imports]
import (
    "flag"
    "fmt"
    "strconv"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.create_lp_queue.imports]

// CreateLPQueue creates an Amazon SQS queue with long-polling enabled
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueName is the name of the queue
//     timeout is the duration, in seconds, for long polling to wait
// Output:
//     If success, the URL of the queue and nil
//     Otherwise, an empty string and an error from the call to CreateQueue
func CreateLPQueue(sess *session.Session, queueName string, timeout int) (string, error) {
    // Create a SQS service client
    svc := sqs.New(sess)

    // snippet-start:[sqs.go.create_lp_queue.call]
    result, err := svc.CreateQueue(&sqs.CreateQueueInput{
        QueueName: aws.String(queueName),
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": strconv.Itoa(timeout),
        }),
    })
    // snippet-end:[sqs.go.create_lp_queue]
    if err != nil {
        return "", err
    }

    return *result.QueueUrl, nil
}

func main() {
    queueNamePtr := flag.String("n", "", "The name of the queue")
    timeoutPtr := flag.Int("t", 20, "How long, in seconds, to wait for long polling")
    flag.Parse()

    if *queueNamePtr == "" {
        fmt.Println("You must supply a queue name (-n QUEUE-NAME")
        return
    }

    if *timeoutPtr < 0 {
        *timeoutPtr = 0
    }

    // Create a session that get credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.create_lp_queue.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.create_lp_queue.sess]

    url, err := CreateLPQueue(sess, *queueNamePtr, *timeoutPtr)
    if err != nil {
        fmt.Println("Got an error creating the long-polling queue:")
        fmt.Println(err)
        return
    }

    fmt.Println("URL for long-polling queue " + *queueNamePtr + ": " + url)
}

// snippet-end:[sqs.go.create_lp_queue]
