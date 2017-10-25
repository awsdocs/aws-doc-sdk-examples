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

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// Usage:
// go run sqs_listqueues.go
func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create a SQS service client.
    svc := sqs.New(sess)

    // List the queues available in a given region.
    result, err := svc.ListQueues(nil)
    if err != nil {
        fmt.Println("Error", err)
        return
    }

    fmt.Println("Success")
    // As these are pointers, printing them out directly would not be useful.
    for i, urls := range result.QueueUrls {
        // Avoid dereferencing a nil pointer.
        if urls == nil {
            continue
        }
        fmt.Printf("%d: %s\n", i, *urls)
    }
}
