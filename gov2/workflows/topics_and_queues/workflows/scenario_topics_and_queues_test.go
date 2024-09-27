// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the movie table scenario.

package workflows

import (
	"context"
	"fmt"
	"testing"
	"topics_and_queues/stubs"

	"github.com/aws/aws-sdk-go-v2/aws"
	snstypes "github.com/aws/aws-sdk-go-v2/service/sns/types"
	sqstypes "github.com/aws/aws-sdk-go-v2/service/sqs/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// TestRunTopicsAndQueuesScenario runs the scenario multiple times. The first time, it runs with no
// errors. In subsequent runs, it specifies that each stub in the sequence should
// raise an error and verifies the results.
func TestRunTopicsAndQueuesScenario(t *testing.T) {
	scenTest := TopicsAndQueuesScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// TopicsAndQueuesScenarioTest encapsulates data for a scenario test.
type TopicsAndQueuesScenarioTest struct {
	Answers []string
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *TopicsAndQueuesScenarioTest) SetupDataAndStubs() []testtools.Stub {
	baseTopicName := "test-topic-name"
	fifoTopicName := baseTopicName + ".fifo"
	topicArn := fmt.Sprintf("arn:%v", fifoTopicName)
	topicAttribs := map[string]string{"FifoTopic": "true"}
	queueCount := 2
	baseQueueName := "test-queue-"
	baseQueueNames := make([]string, queueCount)
	fifoQueueNames := make([]string, queueCount)
	queueUrls := make([]string, queueCount)
	queueArns := make([]string, queueCount)
	subscriptionArns := make([]string, queueCount)
	for q_idx := 0; q_idx < queueCount; q_idx++ {
		baseQueueNames[q_idx] = fmt.Sprintf("%v%v", baseQueueName, q_idx)
		fifoQueueNames[q_idx] = baseQueueNames[q_idx] + ".fifo"
		queueUrls[q_idx] = fmt.Sprintf("https://%v", fifoQueueNames[q_idx])
		queueArns[q_idx] = fmt.Sprintf("arn:sqs:test:%v", fifoQueueNames[q_idx])
		subscriptionArns[q_idx] = fmt.Sprintf("arn:sns:test:subscription/%v", fifoQueueNames[q_idx])
	}
	createQueueAttribs := map[string]string{"FifoQueue": "true"}
	getQueueArnAttribNames := []sqstypes.QueueAttributeName{sqstypes.QueueAttributeNameQueueArn}
	messages := []string{"Test message 1.", "Test message 2.", "Test message 3."}
	groupId := "test-group-id"
	dedupId := "test-dedup-id"
	toneChoice := 1
	filterAttributes := map[string]snstypes.MessageAttributeValue{TONE_KEY: {
		DataType:    aws.String("String"),
		StringValue: aws.String(ToneChoices[toneChoice-1]),
	}}
	receiptHandles := []string{"test-handle-1", "test-handle-2", "test-handle-3"}

	scenTest.Answers = []string{
		"y", "n", baseTopicName, // CreateTopic
		baseQueueNames[0], "y", "1", "n", baseQueueNames[1], "y", "1", "n", // CreateQueue and SubscribeQueueToTopic
		messages[0], groupId, dedupId, "y", "1", "y", messages[1], groupId, dedupId, "y", "1", "y", messages[2], groupId, dedupId, "y", "1", "n", // PublishMessages
		"y", // Cleanup
	}

	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubCreateTopic(fifoTopicName, topicAttribs, nil))
	for queueIndex := range fifoQueueNames {
		stubList = append(stubList, stubs.StubCreateQueue(fifoQueueNames[queueIndex], createQueueAttribs, nil))
		stubList = append(stubList, stubs.StubGetQueueAttributes(
			queueUrls[queueIndex], getQueueArnAttribNames,
			map[string]string{string(sqstypes.QueueAttributeNameQueueArn): queueArns[queueIndex]}, nil))
		stubList = append(stubList, stubs.StubSetQueueAttributes(queueUrls[queueIndex], nil, nil))
		stubList = append(stubList, stubs.StubSubscribe("sqs", topicArn, nil, queueArns[queueIndex],
			subscriptionArns[queueIndex], nil))
	}
	for _, message := range messages {
		stubList = append(stubList, stubs.StubPublish(topicArn, message, groupId, dedupId, filterAttributes, nil))
	}
	for _, queueUrl := range queueUrls {
		stubList = append(stubList, stubs.StubReceiveMessage(queueUrl, 10, 1, messages, receiptHandles, nil))
		stubList = append(stubList, stubs.StubReceiveMessage(queueUrl, 10, 1, nil, nil, nil))
		stubList = append(stubList, stubs.StubDeleteMessageBatch(queueUrl, receiptHandles, nil))
	}

	//cleanup
	stubList = append(stubList, stubs.StubDeleteTopic(topicArn, nil))
	for _, queueUrl := range queueUrls {
		stubList = append(stubList, stubs.StubDeleteQueue(queueUrl, nil))
	}

	return stubList
}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenTest *TopicsAndQueuesScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	RunTopicsAndQueuesScenario(context.Background(), *stubber.SdkConfig, &mockQuestioner)
}

func (scenTest *TopicsAndQueuesScenarioTest) Cleanup() {}
