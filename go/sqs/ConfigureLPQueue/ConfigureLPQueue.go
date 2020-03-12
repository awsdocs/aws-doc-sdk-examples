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
// snippet-start:[sqs.go.configure_lp_queue]
package main

// snippet-start:[sqs.go.configure_lp_queue.imports]
import (
    "flag"
    "fmt"
    "strconv"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)
// snippet-end:[sqs.go.configure_lp_queue.imports]

// ConfigureLPQueue configures an Amazon SQS queue to use long polling
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteQueue
func ConfigureLPQueue(sess *session.Session, queueURL *string, waitTime *int) error {
    // Create an SQS service client
    svc := sqs.New(sess)

    // snippet-start:[sqs.go.configure_lp_queue.set_attributes]
    _, err := svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
        QueueUrl: queueURL,
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": strconv.Itoa(aws.IntValue(waitTime)),
        }),
    })
    // snippet-end:[sqs.go.configure_lp_queue.set_attributes]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    queueURL := flag.String("u", "", "The URL of the queue")
    waitTime := flag.Int("w", 10, "The wait time, in seconds, for long polling")
    flag.Parse()

    if *queueURL == "" {
        fmt.Println("You must supply a queue URL (-n QUEUE-URL")
        return
    }

    if *waitTime < 1 {
        *waitTime = 1
    }

    if *waitTime > 20 {
        *waitTime = 20
    }

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.configure_lp_queue.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.configure_lp_queue.sess]

    err := ConfigureLPQueue(sess, queueURL, waitTime)
    if err != nil {
        fmt.Println("Got an error deleting the queue:")
        fmt.Println(err)
        return
    }

    fmt.Println("Queue with URL " + *queueURL + " deleted")
}
// snippet-end:[sqs.go.configure_lp_queue]
