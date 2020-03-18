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

// CreateLPQueue creates an Amazon SQS queue with long polling enabled
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueName is the name of the queue
//     waitTime is the wait time, in seconds, for long polling to wait for messages
// Output:
//     If success, the URL of the queue and nil
//     Otherwise, an empty string and an error from the call to CreateQueue
func CreateLPQueue(sess *session.Session, queueName *string, waitTime *int) (string, error) {
    // Create an SQS service client
    svc := sqs.New(sess)

    // snippet-start:[sqs.go.create_lp_queue.call]
    result, err := svc.CreateQueue(&sqs.CreateQueueInput{
        QueueName: queueName,
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": strconv.Itoa(*waitTime),
        }),
    })
    // snippet-end:[sqs.go.create_lp_queue.call]
    if err != nil {
        return "", err
    }

    return *result.QueueUrl, nil
}

func main() {
    queue := flag.String("q", "", "The name of the queue")
    waitTime := flag.Int("w", 10, "How long, in seconds, to wait for long polling")
    flag.Parse()

    if *queue == "" {
        fmt.Println("You must supply a queue name (-q QUEUE")
        return
    }

    if *waitTime < 1 {
        *waitTime = 1
    }

    if *waitTime > 20 {
        *waitTime = 20
    }

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.create_lp_queue.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.create_lp_queue.sess]

    url, err := CreateLPQueue(sess, queue, waitTime)
    if err != nil {
        fmt.Println("Got an error creating the long polling queue:")
        fmt.Println(err)
        return
    }

    // snippet-start:[sqs.go.create_lp_queue.url]
    fmt.Println("URL for long polling queue " + *queue + ": " + url)
    // snippet-end:[sqs.go.create_lp_queue.url]
}
// snippet-end:[sqs.go.create_lp_queue]
