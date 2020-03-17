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
// snippet-start:[sqs.go.dead_letter_queue]
package main

// snippet-start:[sqs.go.dead_letter_queue.imports]
import (
    "encoding/json"
    "flag"
    "fmt"
    "strings"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.dead_letter_queue.imports]

// GetQueueURL gets the URL of an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueName is the name of the queue
// Output:
//     If success, the URL of the queue and nil
//     Otherwise, an empty string and an error from the call to
func GetQueueURL(sess *session.Session, queue *string) (*sqs.GetQueueUrlOutput, error) {
    // Create an SQS service client
    // snippet-start:[sqs.go.get_queue_url.call]
    svc := sqs.New(sess)

    result, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
        QueueName: queue,
    })
    // snippet-end:[sqs.go.get_queue_url.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

// GetQueueArn gets the ARN of a queue based on its URL
func GetQueueArn(queueURL *string) string {
    parts := strings.Split(*queueURL, "/")
    subParts := strings.Split(parts[2], ".")

    return "arn:aws:" + subParts[0] + ":" + subParts[1] + ":" + parts[3] + ":" + parts[4]
}

// ConfigureDeadLetterQueue configures an Amazon SQS queue for messages that could not be delivered to another queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     deadLetterQueueARN is the ARN of the dead-letter queue
//     queueURL is the URL of the queue that did not get messages
// Output:
//     If success, the URL of the queue and nil
//     Otherwise, an empty string and an error from the call to json.Marshal or SetQueueAttributes
func ConfigureDeadLetterQueue(sess *session.Session, dlQueueARN *string, queueURL *string) error {
    // Create an SQS service client
    svc := sqs.New(sess)

    // Our redrive policy for our queue
    // snippet-start:[sqs.go.dead_letter_queue.policy]
    policy := map[string]string{
        "deadLetterTargetArn": *dlQueueARN,
        "maxReceiveCount":     "10",
    }
    // snippet-end:[sqs.go.dead_letter_queue.policy]

    // Marshal policy for SetQueueAttributes
    // snippet-start:[sqs.go.dead_letter_queue.marshall]
    b, err := json.Marshal(policy)
    // snippet-end:[sqs.go.dead_letter_queue.marshall]
    if err != nil {
        return err
    }

    // snippet-start:[sqs.go.dead_letter_queue.set_attributes]
    _, err = svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
        QueueUrl: queueURL,
        Attributes: map[string]*string{
            sqs.QueueAttributeNameRedrivePolicy: aws.String(string(b)),
        },
    })
    // snippet-end:[sqs.go.dead_letter_queue.set_attributes]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[sqs.go.dead_letter_queue.args]
    queue := flag.String("q", "", "The name of the queue")
    dlQueue := flag.String("d", "", "The name of the dead-letter queue")
    flag.Parse()

    if *queue == "" || *dlQueue == "" {
        fmt.Println("You must supply the names of the queue (-q QUEUE) and the dead-letter queue (-d DLQUEUE)")
        return
    }
    // snippet-end:[sqs.go.dead_letter_queue.args]

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.dead_letter_queue.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.dead_letter_queue.sess]

    result, err := GetQueueURL(sess, queue)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    queueURL := result.QueueUrl

    result, err = GetQueueURL(sess, dlQueue)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    dlQueueURL := result.QueueUrl

    // Get the ARN for the dead-letter queue
    dlQueueARN := GetQueueArn(dlQueueURL)

    err = ConfigureDeadLetterQueue(sess, &dlQueueARN, queueURL)
    if err != nil {
        fmt.Println("Got an error configuring the dead-letter queue:")
        fmt.Println(err)
        return
    }

    fmt.Println("Created dead-letter queue")
}

// snippet-end:[sqs.go.dead_letter_queue]
