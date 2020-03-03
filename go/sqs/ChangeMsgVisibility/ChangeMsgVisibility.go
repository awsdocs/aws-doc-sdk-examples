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
// snippet-start:[sqs.go.change_message_visibility]
package main

// snippet-start:[sqs.go.change_message_visibility.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sqs"
)

// snippet-end:[sqs.go.change_message_visibility.imports]

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

// SetMsgVisibility Sets the visibility timeout for a message in an SQS queue
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     handle is the receipt handle of the message
//     queueURL is the URL of the queue
//     visibility is the duration, in seconds, while messages are in the queue, but not available
// Output:
//     If success, nil
//     Otherwise, an error from the call to ReceiveQueue
func SetMsgVisibility(sess *session.Session, handle string, queueURL string, visibility int64) error {
	// Create an SQS service client
	svc := sqs.New(sess)

	// snippet-start:[sqs.go.change_message_visibility.op]
	_, err := svc.ChangeMessageVisibility(&sqs.ChangeMessageVisibilityInput{
		ReceiptHandle:     &handle,
		QueueUrl:          &queueURL,
		VisibilityTimeout: &visibility,
	})
	// snippet-end:[sqs.go.change_message_visibility.op]
	if err != nil {
		return err
	}

	return nil
}

func main() {
	queueNamePtr := flag.String("q", "", "The name of the queue")
	msgHandlePtr := flag.String("h", "", "The receipt handle of the message")
	visibilityPtr := flag.Int64("v", 30, "The duration, in seconds, that the message is visible")
	flag.Parse()

	if *queueNamePtr == "" || *msgHandlePtr == "" {
		fmt.Println("You must supply a queue name (-q QUEUE-NAME) and message receipt handle (-h HANDLE)")
		return
	}

	if *visibilityPtr < 0 {
		*visibilityPtr = 0
	}

	if *visibilityPtr > 12*60*60 { // 12 hours
		*visibilityPtr = 12 * 60 * 60
	}

	// Create a session that get credential values from ~/.aws/credentials
	// and the default region from ~/.aws/config
	// snippet-start:[sqs.go.change_message_visibility.sess]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[sqs.go.change_message_visibility.sess]

	queueURL, err := getQueueURL(sess, *queueNamePtr)
	if err != nil {
		fmt.Println("Got an error retrieving the URL for the queue:")
		fmt.Println(err)
		return
	}

	err = SetMsgVisibility(sess, *msgHandlePtr, queueURL, *visibilityPtr)
	if err != nil {
		fmt.Println("Got an error setting the visibility of the message:")
		fmt.Println(err)
		return
	}
}

// snippet-end:[sqs.go.change_message_visibility]
