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
// snippet-start:[sqs.go.send_message]
package main

// snippet-start:[sqs.go.send_message.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.send_message.imports]

func getQueueURL(sess *session.Session, queueName string) (string, error) {
    // Create a SQS service client
    svc := sqs.New(sess)

    result, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
        QueueName: aws.String(queueName),
    })
    if err != nil {
        return "", err
    }

    return *result.QueueUrl, nil
}

// SendMsg sends a message to an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
// Output:
//     If success, nil
//     Otherwise, an error from the call to SendMessage
func SendMsg(sess *session.Session, queueURL string) error {
    // Create a SQS service client
    // snippet-start:[sqs.go.send_message.call]
    svc := sqs.New(sess)

    _, err := svc.SendMessage(&sqs.SendMessageInput{
        DelaySeconds: aws.Int64(10),
        MessageAttributes: map[string]*sqs.MessageAttributeValue{
            "Title": &sqs.MessageAttributeValue{
                DataType:    aws.String("String"),
                StringValue: aws.String("The Whistler"),
            },
            "Author": &sqs.MessageAttributeValue{
                DataType:    aws.String("String"),
                StringValue: aws.String("John Grisham"),
            },
            "WeeksOn": &sqs.MessageAttributeValue{
                DataType:    aws.String("Number"),
                StringValue: aws.String("6"),
            },
        },
        MessageBody: aws.String("Information about current NY Times fiction bestseller for week of 12/11/2016."),
        QueueUrl:    &queueURL,
    })
    // snippet-end:[sqs.go.send_message.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[sqs.go.send_message.args]
    queueNamePtr := flag.String("q", "", "The name of the queue")
    flag.Parse()

    if *queueNamePtr == "" {
        fmt.Println("You must supply a queue name (-u QUEUE-NAME)")
        return
    }
    // snippet-end:[sqs.go.send_message.args]

    // Create a session that get credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.send_message.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.send_message.sess]

    queueURL, err := getQueueURL(sess, *queueNamePtr)
    if err != nil {
        fmt.Println("Got an error retrieving the URL for the queue")
        fmt.Println(err)
        return
    }

    err = SendMsg(sess, queueURL)
    if err != nil {
        fmt.Println("Got an error sending the message:")
        fmt.Println(err)
        return
    }

    fmt.Println("Sent message to queue " + *queueNamePtr)
}

// snippet-end:[sqs.go.send_message]
