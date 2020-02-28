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

// ReceiveLPMessages gets a message from an Amazon SQS long-polling queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     queueURL is the URL of the queue
// Output:
//     If success, nil
//     Otherwise, an error from the call to ???
func ReceiveLPMessages(sess *session.Session, queueURL string, timeout int64) ([]*sqs.Message, error) {
	var msgs []*sqs.Message

	// Create a SQS service client
	svc := sqs.New(sess)

	result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
		QueueUrl: &queueURL,
		AttributeNames: aws.StringSlice([]string{
			"SentTimestamp",
		}),
		MaxNumberOfMessages: aws.Int64(1),
		MessageAttributeNames: aws.StringSlice([]string{
			"All",
		}),
		WaitTimeSeconds: &timeout,
	})
	if err != nil {
		return msgs, err
	}

	return result.Messages, nil
}

func main() {
	queueURLPtr := flag.String("u", "", "The URL of the queue")
	timeoutPtr := flag.Int64("t", 20, "The duration, in seconds, for long-polling")
	flag.Parse()

	if *queueURLPtr == "" {
		fmt.Println("You must supply a queue URL (-u QUEUE-URL")
		return
	}

	if *timeoutPtr < 0 {
		*timeoutPtr = 0
	}

	if *timeoutPtr > 20 {
		*timeoutPtr = 20
	}

	// Create a session that get credential values from ~/.aws/credentials
	// and the default region from ~/.aws/config
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	msgs, err := ReceiveLPMessages(sess, *queueURLPtr, *timeoutPtr)
	if err != nil {
		fmt.Println("Got an error receiving messages:")
		fmt.Println(err)
		return
	}

	fmt.Println("Message IDs:")

	for _, msg := range msgs {
		fmt.Println("    " + *msg.MessageId)
	}
}
