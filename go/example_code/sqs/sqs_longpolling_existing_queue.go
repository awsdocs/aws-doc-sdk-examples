//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Enables long polling on an SQS queue.]
//snippet-keyword:[Amazon Simple Queue Service]
//snippet-keyword:[Amazon SQS]
//snippet-keyword:[GetQueueUrl function]
//snippet-keyword:[SetQueueAttributes function]
//snippet-keyword:[Go]
//snippet-service:[sqs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "flag"
    "fmt"
    "os"
    "strconv"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/awserr"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// Updates the existing queue to use the long polling option.
//
// Usage:
//    go run sqs_longpolling_existing_queue.go -n queue_name -t timeout
func main() {
    var name string
    var timeout int
    flag.StringVar(&name, "n", "", "Queue name")
    flag.IntVar(&timeout, "t", 20, "(Optional) Timeout in seconds for long polling")
    flag.Parse()

    if len(name) == 0 {
        flag.PrintDefaults()
        exitErrorf("Queue name required")
    }

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create a SQS service client.
    svc := sqs.New(sess)

    // Need to convert the queue name into a URL. Make the GetQueueUrl
    // API call to retrieve the URL. This is needed for setting attributes
    // on the queue.
    resultURL, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
        QueueName: aws.String(name),
    })
    if err != nil {
        if aerr, ok := err.(awserr.Error); ok && aerr.Code() == sqs.ErrCodeQueueDoesNotExist {
            exitErrorf("Unable to find queue %q.", name)
        }
        exitErrorf("Unable to get queue %q, %v.", name, err)
    }

    // Update the queue enabling long polling.
    _, err = svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
        QueueUrl: resultURL.QueueUrl,
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": strconv.Itoa(timeout),
        }),
    })
    if err != nil {
        exitErrorf("Unable to update queue %q, %v.", name, err)
    }

    fmt.Printf("Successfully updated queue %q.\n", name)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
