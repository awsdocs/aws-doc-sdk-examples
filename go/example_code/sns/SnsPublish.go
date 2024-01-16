// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[sns.go.publish]
package main

import (
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sns"

	"flag"
	"fmt"
	"os"
)

func main() {
	msgPtr := flag.String("m", "", "The message to send to the subscribed users of the topic")
	topicPtr := flag.String("t", "", "The ARN of the topic to which the user subscribes")

	flag.Parse()

	if *msgPtr == "" || *topicPtr == "" {
		fmt.Println("You must supply a message and topic ARN")
		fmt.Println("Usage: go run SnsPublish.go -m MESSAGE -t TOPIC-ARN")
		os.Exit(1)
	}

	// Initialize a session that the SDK will use to load
	// credentials from the shared credentials file. (~/.aws/credentials).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := sns.New(sess)

	result, err := svc.Publish(&sns.PublishInput{
		Message:  msgPtr,
		TopicArn: topicPtr,
	})
	if err != nil {
		fmt.Println(err.Error())
		os.Exit(1)
	}

	fmt.Println(*result.MessageId)
}

// snippet-end:[sns.go.publish]
