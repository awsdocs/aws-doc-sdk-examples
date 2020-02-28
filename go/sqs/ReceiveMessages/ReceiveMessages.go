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

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"
)

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

// ReceiveMessages gets the messages from an Amazon SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
// Output:
//     If success, a list of messages and nil
//     Otherwise, an empty list and an error from the call to ReceiveMessage
func ReceiveMessages(sess *session.Session, queueURL string) ([]*sqs.Message, error) {
	var msgs []*sqs.Message

	// Create a SQS service client
	svc := sqs.New(sess)

	result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
		AttributeNames: []*string{
			aws.String(sqs.MessageSystemAttributeNameSentTimestamp),
		},
		MessageAttributeNames: []*string{
			aws.String(sqs.QueueAttributeNameAll),
		},
		QueueUrl:            &queueURL,
		MaxNumberOfMessages: aws.Int64(1),
		VisibilityTimeout:   aws.Int64(20), // 20 seconds
		WaitTimeSeconds:     aws.Int64(0),
	})
	if err != nil {
		return msgs, err
	}

	return result.Messages, nil
}

func main() {
	queueNamePtr := flag.String("q", "", "The name of the queue")
	flag.Parse()

	if *queueNamePtr == "" {
		fmt.Println("You must supply a queue name (-q QUEUE-NAME)")
		return
	}

	// Create a session that get credential values from ~/.aws/credentials
	// and the default region from ~/.aws/config
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	queueURL, err := getQueueURL(sess, *queueNamePtr)
	if err != nil {
		fmt.Println("Got an error getting URL of queue:")
		fmt.Println(err)
		return
	}

	msgs, err := ReceiveMessages(sess, queueURL)
	if err != nil {
		fmt.Println("Got an error receiving messages:")
		fmt.Println(err)
		return
	}

	fmt.Println("Message ID")
	fmt.Println("Message Handle:")

	for _, msg := range msgs {
		fmt.Println("    " + *msg.MessageId)
		fmt.Println("    " + *msg.ReceiptHandle)
		fmt.Println()
	}
}
