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
// snippet-start:[sqs.go.send_receive_long_polling]
package main

// snippet-start:[sqs.go.send_receive_long_polling.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)
// snippet-end:[sqs.go.send_receive_long_polling.imports]

// SendMsg sends a message to an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteQueue
func SendMsg(sess *session.Session, queueURL *string) error {
    // Create an SQS service client
    // snippet-start:[sqs.go.send_receive_long_polling.call]
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
        QueueUrl:    queueURL,
    })
    // snippet-end:[sqs.go.send_receive_long_polling.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[sqs.go.send_receive_long_polling.args]
    queueURL := flag.String("u", "", "The URL of the queue")
    flag.Parse()

    if *queueURL == "" {
        fmt.Println("You must supply a queue URL (-u QUEUE-URL")
        return
    }
    // snippet-end:[sqs.go.send_receive_long_polling.args]

    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.send_receive_long_polling.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.send_receive_long_polling.sess]

    err := SendMsg(sess, queueURL)
    if err != nil {
        fmt.Println("Got an error sending the message:")
        fmt.Println(err)
        return
    }

    fmt.Println("Sent message to queue with URL ")
}
// snippet-end:[sqs.go.send_receive_long_polling]
