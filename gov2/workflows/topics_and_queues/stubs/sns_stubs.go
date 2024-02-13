// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package stubs defines service action stubs that are used by the scenario unit tests.
//
// Each stub expects specific data as input and returns specific data as an output.
// If an error is specified, it is raised by the stubber.
package stubs

import (
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/sns"
	"github.com/aws/aws-sdk-go-v2/service/sns/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubCreateTopic(topicName string, attributes map[string]string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateTopic",
		Input:         &sns.CreateTopicInput{Name: aws.String(topicName), Attributes: attributes},
		Output: &sns.CreateTopicOutput{
			TopicArn: aws.String(fmt.Sprintf("arn:%v", topicName))},
		Error: raiseErr,
	}
}

func StubDeleteTopic(topicArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteTopic",
		Input:         &sns.DeleteTopicInput{TopicArn: aws.String(topicArn)},
		Output:        &sns.DeleteTopicOutput{},
		Error:         raiseErr,
	}
}

func StubSubscribe(protocol string, topicArn string, attributes map[string]string, endpoint string,
	subscriptionArn string, raiseErr *testtools.StubError) testtools.Stub {
	var igFields []string
	if attributes == nil {
		igFields = []string{"Attributes"}
	}
	return testtools.Stub{
		OperationName: "Subscribe",
		Input: &sns.SubscribeInput{
			Protocol:              aws.String(protocol),
			TopicArn:              aws.String(topicArn),
			Attributes:            attributes,
			Endpoint:              aws.String(endpoint),
			ReturnSubscriptionArn: true,
		},
		Output: &sns.SubscribeOutput{
			SubscriptionArn: aws.String(subscriptionArn),
		},
		IgnoreFields: igFields,
		Error:        raiseErr,
	}
}

func StubPublish(topicArn string, message string, groupId string, dedupId string, attributes map[string]types.MessageAttributeValue, raiseErr *testtools.StubError) testtools.Stub {
	input := sns.PublishInput{
		Message:           aws.String(message),
		TopicArn:          aws.String(topicArn),
		MessageAttributes: attributes,
	}
	if groupId != "" {
		input.MessageGroupId = aws.String(groupId)
	}
	if dedupId != "" {
		input.MessageDeduplicationId = aws.String(dedupId)
	}
	return testtools.Stub{
		OperationName: "Publish",
		Input:         &input,
		Output:        &sns.PublishOutput{},
		Error:         raiseErr,
	}
}
