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

// GetMessage gets the latest message from an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
//     timeout is how long, in seconds, the message is unavailable to other consumers
// Output:
//     If success, the latest message and nil
//     Otherwise, nil and an error from the call to ReceiveMessage
func GetMessage(sess *session.Session, queueURL *string, timeout *int64) (*sqs.Message, error) {
    var msg *sqs.Message

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
        return msg, err
    }

    return result.Messages[0], nil
}

func main() {
    // snippet-start:[sqs.go.receive_messages.args]
    queueURL := flag.String("u", "", "The URL of the queue")
    timeout := flag.Int64("t", 5, "How long, in seconds, that the message is hidden from others")
    flag.Parse()

    if *queueURL == "" {
        fmt.Println("You must supply the URL of a queue (-u QUEUE-URL)")
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

    msg, err := GetMessage(sess, queueURL, timeout)
    if err != nil {
        fmt.Println("Got an error receiving messages:")
        fmt.Println(err)
        return
    }

    fmt.Println("Message ID:     " + *msg.MessageId)
    fmt.Println("Message Handle: " + *msg.ReceiptHandle)
}
// snippet-end:[sqs.go.receive_messages]
