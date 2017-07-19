/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package main

import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// Usage:
// go run sqs_deletemessage.go
func main() {
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := sqs.New(sess)

    // URL to our queue
    qURL := "QueueURL"

    result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
        AttributeNames: []*string{
            aws.String(sqs.MessageSystemAttributeNameSentTimestamp),
        },
        MessageAttributeNames: []*string{
            aws.String(sqs.QueueAttributeNameAll),
        },
        QueueUrl:            &qURL,
        MaxNumberOfMessages: aws.Int64(1),
        VisibilityTimeout:   aws.Int64(0),
        WaitTimeSeconds:     aws.Int64(0),
    })

    if err != nil {
        fmt.Println("Error", err)
        return
    }

    if len(result.Messages) == 0 {
        fmt.Println("Received no messages")
        return
    }

    resultDelete, err := svc.DeleteMessage(&sqs.DeleteMessageInput{
        QueueUrl:      &qURL,
        ReceiptHandle: result.Messages[0].ReceiptHandle,
    })

    if err != nil {
        fmt.Println("Delete Error", err)
        return
    }

    fmt.Println("Message Deleted", resultDelete)
}
