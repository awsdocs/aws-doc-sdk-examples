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

// GetQueueURL gets the URL of an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueName is the name of the queue
// Output:
//     If success, the URL of the queue and nil
//     Otherwise, an empty string and an error from the call to
func GetQueueURL(sess *session.Session, queue *string) (*sqs.GetQueueUrlOutput, error) {
    // snippet-start:[sqs.go.configure_lp_queue.get_url]
    svc := sqs.New(sess)

    result, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
        QueueName: queue,
    })
    // snippet-end:[sqs.go.configure_lp_queue.get_url]    
    if err != nil {
        return nil, err
    }

    return result, nil
}

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
    // snippet-start:[sqs.go.configure_lp_queue.args]
    queue := flag.String("q", "", "The name of the queue")
    waitTime := flag.Int("w", 10, "The wait time, in seconds, for long polling")
    flag.Parse()

    if *queue == "" {
        fmt.Println("You must supply a queue name (-q QUEUE")
        return
    }

    if *waitTime < 1 {
        *waitTime = 1
    }

    if *waitTime > 20 {
        *waitTime = 20
    }
    // snippet-end:[sqs.go.configure_lp_queue.args]
    
    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.configure_lp_queue.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.configure_lp_queue.sess]

    result, err := GetQueueURL(sess, queue)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    // snippet-start:[sqs.go.configure_lp_queue.url]    
    queueURL := result.QueueUrl
    // snippet-end:[sqs.go.configure_lp_queue.url]
    
    err = ConfigureLPQueue(sess, queueURL, waitTime)
    if err != nil {
        fmt.Println("Got an error deleting the queue:")
        fmt.Println(err)
        return
    }

    fmt.Println("Queue with URL " + *queueURL + " deleted")
}
// snippet-end:[sqs.go.configure_lp_queue]
