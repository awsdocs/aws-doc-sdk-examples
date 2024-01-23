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
	"github.com/aws/aws-sdk-go-v2/service/sqs"
	"github.com/aws/aws-sdk-go-v2/service/sqs/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubCreateQueue(queueName string, attributes map[string]string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateQueue",
		Input:         &sqs.CreateQueueInput{QueueName: aws.String(queueName), Attributes: attributes},
		Output: &sqs.CreateQueueOutput{
			QueueUrl: aws.String(fmt.Sprintf("https://%v", queueName))},
		Error: raiseErr,
	}
}

func StubDeleteQueue(queueUrl string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteQueue",
		Input:         &sqs.DeleteQueueInput{QueueUrl: aws.String(queueUrl)},
		Output:        &sqs.DeleteQueueOutput{},
		Error:         raiseErr,
	}
}

func StubGetQueueAttributes(queueUrl string, attributeNames []types.QueueAttributeName,
	attributesOutput map[string]string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetQueueAttributes",
		Input: &sqs.GetQueueAttributesInput{
			QueueUrl:       aws.String(queueUrl),
			AttributeNames: attributeNames,
		},
		Output: &sqs.GetQueueAttributesOutput{
			Attributes: attributesOutput,
		},
		Error: raiseErr,
	}
}

func StubSetQueueAttributes(queueUrl string, attributes map[string]string, raiseErr *testtools.StubError) testtools.Stub {
	var igFields []string
	if attributes == nil {
		igFields = []string{"Attributes"}
	}
	return testtools.Stub{
		OperationName: "SetQueueAttributes",
		Input: &sqs.SetQueueAttributesInput{
			Attributes: attributes,
			QueueUrl:   aws.String(queueUrl),
		},
		Output:       &sqs.SetQueueAttributesOutput{},
		IgnoreFields: igFields,
		Error:        raiseErr,
	}
}

func StubReceiveMessage(queueUrl string, maxMessages int32, waitTime int32, messageBodies []string, receiptHandles []string, raiseErr *testtools.StubError) testtools.Stub {
	messages := make([]types.Message, len(messageBodies))
	for msgIndex := range messageBodies {
		messages[msgIndex] = types.Message{
			Body:          aws.String(fmt.Sprintf("{\"Message\": \"%v\" }", messageBodies[msgIndex])),
			ReceiptHandle: aws.String(receiptHandles[msgIndex]),
		}
	}
	return testtools.Stub{
		OperationName: "ReceiveMessage",
		Input: &sqs.ReceiveMessageInput{
			QueueUrl:            aws.String(queueUrl),
			MaxNumberOfMessages: maxMessages,
			WaitTimeSeconds:     waitTime,
		},
		Output: &sqs.ReceiveMessageOutput{
			Messages: messages,
		},
		Error: raiseErr,
	}
}

func StubDeleteMessageBatch(queueUrl string, receiptHandles []string, raiseErr *testtools.StubError) testtools.Stub {
	entries := make([]types.DeleteMessageBatchRequestEntry, len(receiptHandles))
	for entryIndex := range receiptHandles {
		entries[entryIndex] = types.DeleteMessageBatchRequestEntry{
			Id:            aws.String(fmt.Sprintf("%v", entryIndex)),
			ReceiptHandle: aws.String(receiptHandles[entryIndex]),
		}
	}
	return testtools.Stub{
		OperationName: "DeleteMessageBatch",
		Input: &sqs.DeleteMessageBatchInput{
			Entries:  entries,
			QueueUrl: aws.String(queueUrl),
		},
		Output: &sqs.DeleteMessageBatchOutput{},
		Error:  raiseErr,
	}
}
