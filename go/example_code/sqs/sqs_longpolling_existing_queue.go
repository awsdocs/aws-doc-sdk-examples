// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Enables long polling on an SQS queue.]
// snippet-keyword:[Amazon Simple Queue Service]
// snippet-keyword:[Amazon SQS]
// snippet-keyword:[GetQueueUrl function]
// snippet-keyword:[SetQueueAttributes function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[sqs]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-1-6]
/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
// snippet-start:[sqs.go.longpolling_existing_queue.complete]
package main

// snippet-start:[sqs.go.longpolling_existing_queue.imports]
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
// snippet-end:[sqs.go.longpolling_existing_queue.imports]

// Updates the existing queue to use the long polling option.
//
// Usage:
//    go run sqs_longpolling_existing_queue.go -n queue_name -t timeout
func main() {
    // snippet-start:[sqs.go.longpolling_existing_queue.vars]
    namePtr := flag.String("n", "", "Queue name")
    timeoutPtr := flag.Int("t", 20, "(Optional) Timeout in seconds for long polling")

    flag.Parse()

    if *namePtr == "" {
        flag.PrintDefaults()
        exitErrorf("Queue name required")
    }
    // snippet-end:[sqs.go.longpolling_existing_queue.vars]

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    // snippet-start:[sqs.go.longpolling_existing_queue.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := sqs.New(sess)
    // snippet-end:[sqs.go.longpolling_existing_queue.session]

    // Need to convert the queue name into a URL. Make the GetQueueUrl
    // API call to retrieve the URL. This is needed for setting attributes
    // on the queue.
    // snippet-start:[sqs.go.longpolling_existing_queue.url]
    resultURL, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
        QueueName: namePtr,
    })
    if err != nil {
        if aerr, ok := err.(awserr.Error); ok && aerr.Code() == sqs.ErrCodeQueueDoesNotExist {
            exitErrorf("Unable to find queue %q.", *namePtr)
        }
        exitErrorf("Unable to get queue %q, %v.", *namePtr, err)
    }
    // snippet-end:[sqs.go.longpolling_existing_queue.url]

    // Update the queue enabling long polling.
    // snippet-start:[sqs.go.longpolling_existing_queue.enable]
    _, err = svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
        QueueUrl: resultURL.QueueUrl,
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": strconv.Itoa(*timeoutPtr),
        }),
    })
    if err != nil {
        exitErrorf("Unable to update queue %q, %v.", *namePtr, err)
    }

    fmt.Printf("Successfully updated queue %q.\n", *namePtr)
    // snippet-end:[sqs.go.longpolling_existing_queue.enable]
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
// snippet-end:[sqs.go.longpolling_existing_queue.complete]
