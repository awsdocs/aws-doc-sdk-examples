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
// snippet-start:[sqs.go.change_message_visibility]
package main

// snippet-start:[sqs.go.change_message_visibility.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)
// snippet-end:[sqs.go.change_message_visibility.imports]

// SetMsgVisibility Sets the visibility timeout for a message in an SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     handle is the receipt handle of the message
//     queueURL is the URL of the queue
//     visibility is the duration, in seconds, while messages are in the queue, but not available
// Output:
//     If success, nil
//     Otherwise, an error from the call to ReceiveQueue
func SetMsgVisibility(sess *session.Session, handle *string, queueURL *string, visibility *int64) error {
    // Create an SQS service client
    svc := sqs.New(sess)

    // snippet-start:[sqs.go.change_message_visibility.op]
    _, err := svc.ChangeMessageVisibility(&sqs.ChangeMessageVisibilityInput{
        ReceiptHandle:     handle,
        QueueUrl:          queueURL,
        VisibilityTimeout: visibility,
    })
    // snippet-end:[sqs.go.change_message_visibility.op]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    queueURL := flag.String("u", "", "The URL of the queue")
    handle := flag.String("h", "", "The receipt handle of the message")
    visibility := flag.Int64("v", 30, "The duration, in seconds, that the message is not visible to other consumers")
    flag.Parse()

    if *queueURL == "" || *handle == "" {
        fmt.Println("You must supply a queue URL (-u QUEUE-URL) and message receipt handle (-h HANDLE)")
        return
    }

    if *visibility < 0 {
        *visibility = 0
    }

    if *visibility > 12*60*60 {
        *visibility = 12 * 60 * 60
    }

    // Create a session that get credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.change_message_visibility.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.change_message_visibility.sess]

    err := SetMsgVisibility(sess, handle, queueURL, visibility)
    if err != nil {
        fmt.Println("Got an error setting the visibility of the message:")
        fmt.Println(err)
        return
    }
}
// snippet-end:[sqs.go.change_message_visibility]
