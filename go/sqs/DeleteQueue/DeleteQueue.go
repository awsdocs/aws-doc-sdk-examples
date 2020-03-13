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
// snippet-start:[sqs.go.delete_queue]
package main

// snippet-start:[sqs.go.delete_queue.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)
// snippet-end:[sqs.go.delete_queue.imports]

// DeleteQueue deletes an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteQueue
func DeleteQueue(sess *session.Session, queueURL *string) error {
    // Create an SQS service client
    // snippet-start:[sqs.go.delete_queue.call]
    svc := sqs.New(sess)

    _, err := svc.DeleteQueue(&sqs.DeleteQueueInput{
        QueueUrl: queueURL,
    })
    // snippet-end:[sqs.go.delete_queue.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[sqs.go.delete_queue.args]
    queueURL := flag.String("u", "", "The URL of the queue")
    flag.Parse()

    if *queueURL == "" {
        fmt.Println("You must supply a queue URL (-n QUEUE-URL")
        return
    }
    // snippet-end:[sqs.go.delete_queue.args]

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.delete_queue.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.delete_queue.sess]

    err := DeleteQueue(sess, queueURL)
    if err != nil {
        fmt.Println("Got an error deleting the queue:")
        fmt.Println(err)
        return
    }

    fmt.Println("Deleted queue with URL " + *queueURL + " deleted")
}
// snippet-end:[sqs.go.delete_queue]
