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
// snippet-start:[sqs.go.delete_message]
package main

// snippet-start:[sqs.go.delete_message.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.delete_message.imports]

// DeleteMessage deletes a message from an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
//     messageID is the ID of the message
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteMessage
func DeleteMessage(sess *session.Session, queueURL string, messageID string) error {
    // Create a SQS service client
    svc := sqs.New(sess)

    // snippet-start:[sqs.go.delete_message.call]
    _, err := svc.DeleteMessage(&sqs.DeleteMessageInput{
        QueueUrl:      &queueURL,
        ReceiptHandle: &messageID,
    })
    // snippet-end:[sqs.go.delete_message.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    queueURLPtr := flag.String("u", "", "The URL of the queue")
    messageIDPtr := flag.String("i", "", "The ID of the message")
    flag.Parse()

    if *queueURLPtr == "" || *messageIDPtr == "" {
        fmt.Println("You must supply a queue URL (-u QUEUE-URL) and message ID (-i MESSAGE-ID)")
        return
    }

    // Create a session that get credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.delete_message.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.delete_message.sess]

    err := DeleteMessage(sess, *queueURLPtr, *messageIDPtr)
    if err != nil {
        fmt.Println("Got an error deleting the message:")
        fmt.Println(err)
        return
    }

    fmt.Println("Deleted message from queue with URL " + *queueURLPtr)
}

// snippet-end:[sqs.go.delete_message]
