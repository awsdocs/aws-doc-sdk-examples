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
// snippet-start:[sqs.go.send_receive_long_polling]
package main

// snippet-start:[sqs.go.send_receive_long_polling.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)
// snippet-end:[sqs.go.send_receive_long_polling.imports]

// GetQueueURL gets the URL of an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueName is the name of the queue
// Output:
//     If success, the URL of the queue and nil
//     Otherwise, an empty string and an error from the call to
func GetQueueURL(sess *session.Session, queue *string) (*sqs.GetQueueUrlOutput, error) {
    // Create an SQS service client
    svc := sqs.New(sess)

    result, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
        QueueName: queue,
    })
    if err != nil {
        return nil, err
    }

    return result, nil
}

// SendMsg sends a message to an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteQueue
func SendMsg(sess *session.Session, queueURL *string) error {
    // Create an SQS service client
    // snippet-start:[sqs.go.send_receive_long_polling.call]
    svc := sqs.New(sess)

    _, err := svc.SendMessage(&sqs.SendMessageInput{
        DelaySeconds: aws.Int64(10),
        MessageAttributes: map[string]*sqs.MessageAttributeValue{
            "Title": &sqs.MessageAttributeValue{
                DataType:    aws.String("String"),
                StringValue: aws.String("The Whistler"),
            },
            "Author": &sqs.MessageAttributeValue{
                DataType:    aws.String("String"),
                StringValue: aws.String("John Grisham"),
            },
            "WeeksOn": &sqs.MessageAttributeValue{
                DataType:    aws.String("Number"),
                StringValue: aws.String("6"),
            },
        },
        MessageBody: aws.String("Information about current NY Times fiction bestseller for week of 12/11/2016."),
        QueueUrl:    queueURL,
    })
    // snippet-end:[sqs.go.send_receive_long_polling.call]
    if err != nil {
        return err
    }

    return nil
}

// GetLPMessages gets the messages from an Amazon SQS long polling queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
// Output:
//     If success, nil
//     Otherwise, an error from the call to ReceiveMessage
func GetLPMessages(sess *session.Session, queueURL *string, waitTime *int64) (*sqs.ReceiveMessageOutput, error) {
    // Create an SQS service client
    // snippet-start:[sqs.go.receive_lp_message.call]
    svc := sqs.New(sess)

    results, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
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
        return nil, err
    }

    return results, nil
}

func main() {
    // snippet-start:[sqs.go.send_receive_long_polling.args]
    queue := flag.String("q", "", "The name of the queue")
    waitTime := flag.Int64("w", 0, "How long to wait for messages")
    flag.Parse()

    if *queue == "" {
        fmt.Println("You must supply a queue name (-q QUEUE")
        return
    }

    if *waitTime < 0 {
        *waitTime = 0
    }

    if *waitTime > 20 {
        *waitTime = 20
    }
    // snippet-end:[sqs.go.send_receive_long_polling.args]

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.send_receive_long_polling.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.send_receive_long_polling.sess]

    // Get URL of queue
    result, err := GetQueueURL(sess, queue)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    queueURL := result.QueueUrl

    err = SendMsg(sess, queueURL)
    if err != nil {
        fmt.Println("Got an error sending the message:")
        fmt.Println(err)
        return
    }

    fmt.Println("Sent message to queue with URL " + *queueURL)

    // Get messages and show them
    // snippet-start:[sqs.go.send_receive_long_polling.get_msgs]
    results, err := GetLPMessages(sess, queueURL, waitTime)
    if err != nil {
        fmt.Println("Got error retrieving LP messages:")
        fmt.Println(err)
        return
    }

    fmt.Println("Message IDs:")

    for _, msg := range results.Messages {
        fmt.Println("    " + *msg.MessageId)
    }
    // snippet-end:[sqs.go.send_receive_long_polling.get_msgs]
}
// snippet-end:[sqs.go.send_receive_long_polling]
