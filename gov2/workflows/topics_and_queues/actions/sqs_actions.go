// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.sqs.SqsActions.complete]
// snippet-start:[gov2.sqs.SqsActions.struct]

import (
	"context"
	"encoding/json"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
	"github.com/aws/aws-sdk-go-v2/service/sqs/types"
)

// SqsActions encapsulates the Amazon Simple Queue Service (Amazon SQS) actions
// used in the examples.
type SqsActions struct {
	SqsClient *sqs.Client
}

// snippet-end:[gov2.sqs.SqsActions.struct]

// snippet-start:[gov2.sqs.CreateQueue]

// CreateQueue creates an Amazon SQS queue with the specified name. You can specify
// whether the queue is created as a FIFO queue.
func (actor SqsActions) CreateQueue(ctx context.Context, queueName string, isFifoQueue bool) (string, error) {
	var queueUrl string
	queueAttributes := map[string]string{}
	if isFifoQueue {
		queueAttributes["FifoQueue"] = "true"
	}
	queue, err := actor.SqsClient.CreateQueue(ctx, &sqs.CreateQueueInput{
		QueueName:  aws.String(queueName),
		Attributes: queueAttributes,
	})
	if err != nil {
		log.Printf("Couldn't create queue %v. Here's why: %v\n", queueName, err)
	} else {
		queueUrl = *queue.QueueUrl
	}

	return queueUrl, err
}

// snippet-end:[gov2.sqs.CreateQueue]

// snippet-start:[gov2.sqs.GetQueueAttributes]

// GetQueueArn uses the GetQueueAttributes action to get the Amazon Resource Name (ARN)
// of an Amazon SQS queue.
func (actor SqsActions) GetQueueArn(ctx context.Context, queueUrl string) (string, error) {
	var queueArn string
	arnAttributeName := types.QueueAttributeNameQueueArn
	attribute, err := actor.SqsClient.GetQueueAttributes(ctx, &sqs.GetQueueAttributesInput{
		QueueUrl:       aws.String(queueUrl),
		AttributeNames: []types.QueueAttributeName{arnAttributeName},
	})
	if err != nil {
		log.Printf("Couldn't get ARN for queue %v. Here's why: %v\n", queueUrl, err)
	} else {
		queueArn = attribute.Attributes[string(arnAttributeName)]
	}
	return queueArn, err
}

// snippet-end:[gov2.sqs.GetQueueAttributes]

// snippet-start:[gov2.sqs.SetQueueAttributes]

// AttachSendMessagePolicy uses the SetQueueAttributes action to attach a policy to an
// Amazon SQS queue that allows the specified Amazon SNS topic to send messages to the
// queue.
func (actor SqsActions) AttachSendMessagePolicy(ctx context.Context, queueUrl string, queueArn string, topicArn string) error {
	policyDoc := PolicyDocument{
		Version: "2012-10-17",
		Statement: []PolicyStatement{{
			Effect:    "Allow",
			Action:    "sqs:SendMessage",
			Principal: map[string]string{"Service": "sns.amazonaws.com"},
			Resource:  aws.String(queueArn),
			Condition: PolicyCondition{"ArnEquals": map[string]string{"aws:SourceArn": topicArn}},
		}},
	}
	policyBytes, err := json.Marshal(policyDoc)
	if err != nil {
		log.Printf("Couldn't create policy document. Here's why: %v\n", err)
		return err
	}
	_, err = actor.SqsClient.SetQueueAttributes(ctx, &sqs.SetQueueAttributesInput{
		Attributes: map[string]string{
			string(types.QueueAttributeNamePolicy): string(policyBytes),
		},
		QueueUrl: aws.String(queueUrl),
	})
	if err != nil {
		log.Printf("Couldn't set send message policy on queue %v. Here's why: %v\n", queueUrl, err)
	}
	return err
}

// PolicyDocument defines a policy document as a Go struct that can be serialized
// to JSON.
type PolicyDocument struct {
	Version   string
	Statement []PolicyStatement
}

// PolicyStatement defines a statement in a policy document.
type PolicyStatement struct {
	Effect    string
	Action    string
	Principal map[string]string `json:",omitempty"`
	Resource  *string           `json:",omitempty"`
	Condition PolicyCondition   `json:",omitempty"`
}

// PolicyCondition defines a condition in a policy.
type PolicyCondition map[string]map[string]string

// snippet-end:[gov2.sqs.SetQueueAttributes]

// snippet-start:[gov2.sqs.ReceiveMessage]

// GetMessages uses the ReceiveMessage action to get messages from an Amazon SQS queue.
func (actor SqsActions) GetMessages(ctx context.Context, queueUrl string, maxMessages int32, waitTime int32) ([]types.Message, error) {
	var messages []types.Message
	result, err := actor.SqsClient.ReceiveMessage(ctx, &sqs.ReceiveMessageInput{
		QueueUrl:            aws.String(queueUrl),
		MaxNumberOfMessages: maxMessages,
		WaitTimeSeconds:     waitTime,
	})
	if err != nil {
		log.Printf("Couldn't get messages from queue %v. Here's why: %v\n", queueUrl, err)
	} else {
		messages = result.Messages
	}
	return messages, err
}

// snippet-end:[gov2.sqs.ReceiveMessage]

// snippet-start:[gov2.sqs.DeleteMessageBatch]

// DeleteMessages uses the DeleteMessageBatch action to delete a batch of messages from
// an Amazon SQS queue.
func (actor SqsActions) DeleteMessages(ctx context.Context, queueUrl string, messages []types.Message) error {
	entries := make([]types.DeleteMessageBatchRequestEntry, len(messages))
	for msgIndex := range messages {
		entries[msgIndex].Id = aws.String(fmt.Sprintf("%v", msgIndex))
		entries[msgIndex].ReceiptHandle = messages[msgIndex].ReceiptHandle
	}
	_, err := actor.SqsClient.DeleteMessageBatch(ctx, &sqs.DeleteMessageBatchInput{
		Entries:  entries,
		QueueUrl: aws.String(queueUrl),
	})
	if err != nil {
		log.Printf("Couldn't delete messages from queue %v. Here's why: %v\n", queueUrl, err)
	}
	return err
}

// snippet-end:[gov2.sqs.DeleteMessageBatch]

// snippet-start:[gov2.sqs.DeleteQueue]

// DeleteQueue deletes an Amazon SQS queue.
func (actor SqsActions) DeleteQueue(ctx context.Context, queueUrl string) error {
	_, err := actor.SqsClient.DeleteQueue(ctx, &sqs.DeleteQueueInput{
		QueueUrl: aws.String(queueUrl)})
	if err != nil {
		log.Printf("Couldn't delete queue %v. Here's why: %v\n", queueUrl, err)
	}
	return err
}

// snippet-end:[gov2.sqs.DeleteQueue]
// snippet-end:[gov2.sqs.SqsActions.complete]
