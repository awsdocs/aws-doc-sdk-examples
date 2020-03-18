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
// snippet-start:[sqs.go.receive_lp_message]
package main

// snippet-start:[sqs.go.receive_lp_message.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)
// snippet-end:[sqs.go.receive_lp_message.imports]

// GetQueueURL gets the URL of an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueName is the name of the queue
// Output:
//     If success, the URL of the queue and nil
//     Otherwise, an empty string and an error from the call to
func GetQueueURL(sess *session.Session, queue *string) (*sqs.GetQueueUrlOutput, error) {
    // Create an SQS service client
    // snippet-start:[sqs.go.get_queue_url.call]
    svc := sqs.New(sess)

    result, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
        QueueName: queue,
    })
    // snippet-end:[sqs.go.get_queue_url.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

// GetLPMessages gets the messages from an Amazon SQS long polling queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
// Output:
//     If success, nil
//     Otherwise, an error from the call to ReceiveMessage
func GetLPMessages(sess *session.Session, queueURL *string, waitTime *int64) ([]*sqs.Message, error) {
    var msgs []*sqs.Message

    // Create an SQS service client
    // snippet-start:[sqs.go.receive_lp_message.call]
    svc := sqs.New(sess)

    result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
        QueueUrl: queueURL,
        AttributeNames: aws.StringSlice([]string{
            "SentTimestamp",
        }),
        MaxNumberOfMessages: aws.Int64(1),
        MessageAttributeNames: aws.StringSlice([]string{
            "All",
        }),
        WaitTimeSeconds: waitTime,
    })
    // snippet-end:[sqs.go.receive_lp_message.call]
    if err != nil {
        return msgs, err
    }

    return result.Messages, nil
}

func main() {
    // snippet-start:[sqs.go.receive_lp_message.args]
    queue := flag.String("q", "", "The name of the queue")
    visibility := flag.Int64("v", 5, "How long, in seconds, that messages are hidden from other consumers")
    waitTime := flag.Int64("w", 10, "How long the queue waits for messages")
    flag.Parse()

    if *queue == "" {
        fmt.Println("You must supply a queue name (-q QUEUE")
        return
    }

    if *visibility < 0 {
        *visibility = 0
    }

    if *visibility > 12*60*60 { // 12 hours
        *visibility = 12 * 60 * 60
    }

    if *waitTime < 0 {
        *waitTime = 0
    }

    if *waitTime > 20 {
        *waitTime = 20
    }
    // snippet-end:[sqs.go.receive_lp_message.args]

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.receive_lp_message.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.receive_lp_message.sess]

    result, err := GetQueueURL(sess, queue)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    queueURL := result.QueueUrl

    msgs, err := GetLPMessages(sess, queueURL, waitTime)
    if err != nil {
        fmt.Println("Got an error receiving messages:")
        fmt.Println(err)
        return
    }

    fmt.Println("Message IDs:")

    for _, msg := range msgs {
        fmt.Println("    " + *msg.MessageId)
    }
}
// snippet-end:[sqs.go.receive_lp_message]
