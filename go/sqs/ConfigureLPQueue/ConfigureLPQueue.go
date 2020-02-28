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

package main

import (
    "flag"
    "fmt"
    "strconv"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// ConfigureLPQueue configures an Amazon SQS queue to use long-polling
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteQueue
func ConfigureLPQueue(sess *session.Session, queueURL string, timeout int) error {
    // Create a SQS service client
    svc := sqs.New(sess)

    _, err := svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
        QueueUrl: &queueURL,
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": strconv.Itoa(timeout),
        }),
    })
    if err != nil {
        return err
    }

    return nil
}

func main() {
    queueURLPtr := flag.String("u", "", "The URL of the queue")
    timeoutPtr := flag.Int("t", 20, "The timeout, in seconds, for long polling")
    flag.Parse()

    if *queueURLPtr == "" {
        fmt.Println("You must supply a queue URL (-n QUEUE-URL")
        return
    }

    if *timeoutPtr < 0 {
        *timeoutPtr = 0
    }

    // Create a session that get credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    err := ConfigureLPQueue(sess, *queueURLPtr, *timeoutPtr)
    if err != nil {
        fmt.Println("Got an error deleting the queue:")
        fmt.Println(err)
        return
    }

    fmt.Println("Queue with URL " + *queueURLPtr + " deleted")
}
