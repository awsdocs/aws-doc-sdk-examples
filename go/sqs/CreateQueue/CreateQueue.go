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
// snippet-start:[sqs.go.create_queue]
package main

// snippet-start:[sqs.go.create_queue.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)
// snippet-end:[sqs.go.create_queue.imports]

// CreateQueue creates an Amazon SQS queue
// Inputs:
//     sess is the
//     queueName is the name of the queue
// Output:
//     If success, the URL of the queue and nil
//     Otherwise, an empty string and an error from the call to CreateQueue
func CreateQueue(sess *session.Session, queueName *string) (*string, error) {
    // Create an SQS service client
    // snippet-start:[sqs.go.create_queue.call]
    svc := sqs.New(sess)

    result, err := svc.CreateQueue(&sqs.CreateQueueInput{
        QueueName: queueName,
        Attributes: map[string]*string{
            "DelaySeconds":           aws.String("60"),
            "MessageRetentionPeriod": aws.String("86400"),
        },
    })
    // snippet-end:[sqs.go.create_queue.call]
    if err != nil {
        return nil, err
    }

    return result.QueueUrl, nil
}

func main() {
    // snippet-start:[sqs.go.create_queue.args]
    queueName := flag.String("n", "", "The name of the queue")
    flag.Parse()

    if *queueName == "" {
        fmt.Println("You must supply a queue name (-n QUEUE-NAME")
        return
    }
    // snippet-end:[sqs.go.create_queue.args]

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.create_queue.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.create_queue.sess]

    url, err := CreateQueue(sess, queueName)
    if err != nil {
        fmt.Println("Got an error creating the queue:")
        fmt.Println(err)
        return
    }

    fmt.Println("URL for queue " + *queueName + ": " + *url)
}
// snippet-end:[sqs.go.create_queue]
