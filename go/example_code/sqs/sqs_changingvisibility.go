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
// go run sqs_changingvisibility.go
func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

   // Create a SQS service client.
   svc := sqs.New(sess)

   // URL to our queue
   qURL := "QueueURL"

   result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
      AttributeNames: []*string{
         aws.String(sqs.MessageSystemAttributeNameSentTimestamp),
      },
      MaxNumberOfMessages: aws.Int64(1),
      MessageAttributeNames: []*string{
         aws.String(sqs.QueueAttributeNameAll),
      },
      QueueUrl: &qURL,
   })

   if err != nil {
      fmt.Println("Error", err)
      return
   }

   // Check if we have any messages
   if len(result.Messages) == 0 {
      fmt.Println("Received no messages")
      return
   }

   // 10 hour timeout
   duration := int64(36000)
   resultVisibility, err := svc.ChangeMessageVisibility(&sqs.ChangeMessageVisibilityInput{
      ReceiptHandle:     result.Messages[0].ReceiptHandle,
      QueueUrl:          &qURL,
      VisibilityTimeout: &duration,
   })

   if err != nil {
      fmt.Println("Visibility Error", err)
      return
   }

   fmt.Println("Time Changed", resultVisibility)
}
