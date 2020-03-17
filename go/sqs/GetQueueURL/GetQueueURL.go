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
// snippet-start:[sqs.go.get_queue_url]
package main

// snippet-start:[sqs.go.get_queue_url.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.get_queue_url.imports]

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

func main() {
    // snippet-start:[sqs.go.get_queue_url.args]
    queue := flag.String("q", "", "The name of the queue")
    flag.Parse()

    if *queue == "" {
        fmt.Println("You must supply a queue name (-q QUEUE")
        return
    }
    // snippet-end:[sqs.go.get_queue_url.args]

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.get_queue_url.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.get_queue_url.sess]

    result, err := GetQueueURL(sess, queue)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    // snippet-start:[sqs.go.get_queue_url.print]
    fmt.Println("URL: " + *result.QueueUrl)
    // snippet-end:[sqs.go.get_queue_url.print]
}

// snippet-end:[sqs.go.get_queue_url]
