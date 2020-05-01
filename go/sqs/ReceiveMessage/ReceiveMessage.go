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
// snippet-start:[sqs.go.receive_messages]
package main

// snippet-start:[sqs.go.receive_messages.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.receive_messages.imports]

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

// GetMessages gets the messages from an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
//     timeout is how long, in seconds, the message is unavailable to other consumers
// Output:
//     If success, the latest message and nil
//     Otherwise, nil and an error from the call to ReceiveMessage
func GetMessages(sess *session.Session, queueURL *string, timeout *int64) (*sqs.ReceiveMessageOutput, error) {
    // Create an SQS service client
    // snippet-start:[sqs.go.receive_messages.call]
    svc := sqs.New(sess)

    result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
        AttributeNames: []*string{
            aws.String(sqs.MessageSystemAttributeNameSentTimestamp),
        },
        MessageAttributeNames: []*string{
            aws.String(sqs.QueueAttributeNameAll),
        },
        QueueUrl:            queueURL,
        MaxNumberOfMessages: aws.Int64(1),
        VisibilityTimeout:   timeout,
    })
    // snippet-end:[sqs.go.receive_messages.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[sqs.go.receive_messages.args]
    queue := flag.String("q", "", "The name of the queue")
    timeout := flag.Int64("t", 5, "How long, in seconds, that the message is hidden from others")
    flag.Parse()

    if *queue == "" {
        fmt.Println("You must supply the name of a queue (-q QUEUE)")
        return
    }

    if *timeout < 0 {
        *timeout = 0
    }

    if *timeout > 12*60*60 {
        *timeout = 12 * 60 * 60
    }
    // snippet-end:[sqs.go.receive_messages.args]

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.receive_messages.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.receive_messages.sess]

    // Get URL of queue
    urlResult, err := GetQueueURL(sess, queue)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    queueURL := urlResult.QueueUrl

    result, err := GetMessages(sess, queueURL, timeout)
    if err != nil {
        fmt.Println("Got an error receiving messages:")
        fmt.Println(err)
        return
    }

    fmt.Println("Message ID:     " + *result.Messages[0].MessageId)

    // snippet-start:[sqs.go.receive_messages.print_handle]
    fmt.Println("Message Handle: " + *result.Messages[0].ReceiptHandle)
    // snippet-end:[sqs.go.receive_messages.print_handle]
}

// snippet-end:[sqs.go.receive_messages]
