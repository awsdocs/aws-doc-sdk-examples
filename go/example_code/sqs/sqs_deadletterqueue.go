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
    "encoding/json"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// Usage:
// go run sqs_deadletterqueue.go
func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create a SQS service client.
    svc := sqs.New(sess)

    // Our redrive policy for our queue
    policy := map[string]string{
        "deadLetterTargetArn": "SQS_QUEUE_ARN",
        "maxReceiveCount":     "10",
    }

    // Marshal our policy to be used as input for our SetQueueAttributes
    // call.
    b, err := json.Marshal(policy)
    if err != nil {
        fmt.Println("Failed to marshal policy:", err)
        return
    }

    result, err := svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
        QueueUrl: aws.String("SQS_QUEUE_URL"),
        Attributes: map[string]*string{
            sqs.QueueAttributeNameRedrivePolicy: aws.String(string(b)),
        },
    })

    if err != nil {
        fmt.Println("Error", err)
        return
    }

    fmt.Println("Success", result)
}
