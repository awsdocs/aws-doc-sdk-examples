// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[sns.go-v2.Subscribe]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/sns"
)

// SNSSubscribeAPI defines the interface for the Subscribe function.
// We use this interface to test the function using a mocked service.
type SNSSubscribeAPI interface {
	Subscribe(ctx context.Context,
		params *sns.SubscribeInput,
		optFns ...func(*sns.Options)) (*sns.SubscribeOutput, error)
}

// SubscribeTopic subscribes a user to an Amazon Simple Notification Service (Amazon SNS) topic by their email address
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a SubscribeOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to Subscribe
func SubscribeTopic(c context.Context, api SNSSubscribeAPI, input *sns.SubscribeInput) (*sns.SubscribeOutput, error) {
	result, err := api.Subscribe(c, input)

	return result, err
}

func main() {
	email := flag.String("e", "", "The email address of the user subscribing to the topic")
	topicARN := flag.String("t", "", "The ARN of the topic to which the user subscribes")

	flag.Parse()

	if *email == "" || *topicARN == "" {
		fmt.Println("You must supply an email address and topic ARN")
		fmt.Println("-e EMAIL -t TOPIC-ARN")
		return
	}

	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := sns.NewFromConfig(cfg)

	input := &sns.SubscribeInput{
		Endpoint:              email,
		Protocol:              aws.String("email"),
		ReturnSubscriptionArn: aws.Bool(true), // Return the ARN, even if user has yet to confirm
		TopicArn:              topicARN,
	}

	result, err := SubscribeTopic(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error subscribing to the topic:")
		fmt.Println(err)
		return
	}

	fmt.Println(*result.SubscriptionArn)
}

// snippet-end:[sns.go-v2.Subscribe]
