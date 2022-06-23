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

    urlResult, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
        QueueName: queue,
    })
    if err != nil {
        return nil, err
    }

    return urlResult, nil
}

// SetMsgVisibility sets the visibility timeout for a message in an SQS queue
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
    // snippet-start:[sqs.go.change_message_visibility.args]
    queue := flag.String("q", "", "The name of the queue")
    handle := flag.String("h", "", "The receipt handle of the message")
    visibility := flag.Int64("v", 30, "The duration, in seconds, that the message is not visible to other consumers")
    flag.Parse()

    if *queue == "" || *handle == "" {
        fmt.Println("You must supply a queue name (-q QUEUE) and message receipt handle (-h HANDLE)")
        return
    }

    if *visibility < 0 {
        *visibility = 0
    }

    if *visibility > 12*60*60 {
        *visibility = 12 * 60 * 60
    }
    // snippet-end:[sqs.go.change_message_visibility.args]

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default Region from ~/.aws/config
    // snippet-start:[sqs.go.change_message_visibility.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.change_message_visibility.sess]

    // Get URL of queue
    urlResult, err := GetQueueURL(sess, queue)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    // snippet-start:[sqs.go.change_message_visibility.url]
    queueURL := urlResult.QueueUrl
    // snippet-end:[sqs.go.change_message_visibility.url]
    
    err = SetMsgVisibility(sess, handle, queueURL, visibility)
    if err != nil {
        fmt.Println("Got an error setting the visibility of the message:")
        fmt.Println(err)
        return
    }
}
// snippet-end:[sqs.go.change_message_visibility]
