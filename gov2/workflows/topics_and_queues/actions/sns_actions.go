// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.sns.SnsActions.complete]
// snippet-start:[gov2.sns.SnsActions.struct]

import (
	"context"
	"encoding/json"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/sns"
	"github.com/aws/aws-sdk-go-v2/service/sns/types"
)

// SnsActions encapsulates the Amazon Simple Notification Service (Amazon SNS) actions
// used in the examples.
type SnsActions struct {
	SnsClient *sns.Client
}

// snippet-end:[gov2.sns.SnsActions.struct]

// snippet-start:[gov2.sns.CreateTopic]

// CreateTopic creates an Amazon SNS topic with the specified name. You can optionally
// specify that the topic is created as a FIFO topic and whether it uses content-based
// deduplication instead of ID-based deduplication.
func (actor SnsActions) CreateTopic(ctx context.Context, topicName string, isFifoTopic bool, contentBasedDeduplication bool) (string, error) {
	var topicArn string
	topicAttributes := map[string]string{}
	if isFifoTopic {
		topicAttributes["FifoTopic"] = "true"
	}
	if contentBasedDeduplication {
		topicAttributes["ContentBasedDeduplication"] = "true"
	}
	topic, err := actor.SnsClient.CreateTopic(ctx, &sns.CreateTopicInput{
		Name:       aws.String(topicName),
		Attributes: topicAttributes,
	})
	if err != nil {
		log.Printf("Couldn't create topic %v. Here's why: %v\n", topicName, err)
	} else {
		topicArn = *topic.TopicArn
	}

	return topicArn, err
}

// snippet-end:[gov2.sns.CreateTopic]

// snippet-start:[gov2.sns.DeleteTopic]

// DeleteTopic delete an Amazon SNS topic.
func (actor SnsActions) DeleteTopic(ctx context.Context, topicArn string) error {
	_, err := actor.SnsClient.DeleteTopic(ctx, &sns.DeleteTopicInput{
		TopicArn: aws.String(topicArn)})
	if err != nil {
		log.Printf("Couldn't delete topic %v. Here's why: %v\n", topicArn, err)
	}
	return err
}

// snippet-end:[gov2.sns.DeleteTopic]

// snippet-start:[gov2.sns.Subscribe]

// SubscribeQueue subscribes an Amazon Simple Queue Service (Amazon SQS) queue to an
// Amazon SNS topic. When filterMap is not nil, it is used to specify a filter policy
// so that messages are only sent to the queue when the message has the specified attributes.
func (actor SnsActions) SubscribeQueue(ctx context.Context, topicArn string, queueArn string, filterMap map[string][]string) (string, error) {
	var subscriptionArn string
	var attributes map[string]string
	if filterMap != nil {
		filterBytes, err := json.Marshal(filterMap)
		if err != nil {
			log.Printf("Couldn't create filter policy, here's why: %v\n", err)
			return "", err
		}
		attributes = map[string]string{"FilterPolicy": string(filterBytes)}
	}
	output, err := actor.SnsClient.Subscribe(ctx, &sns.SubscribeInput{
		Protocol:              aws.String("sqs"),
		TopicArn:              aws.String(topicArn),
		Attributes:            attributes,
		Endpoint:              aws.String(queueArn),
		ReturnSubscriptionArn: true,
	})
	if err != nil {
		log.Printf("Couldn't susbscribe queue %v to topic %v. Here's why: %v\n",
			queueArn, topicArn, err)
	} else {
		subscriptionArn = *output.SubscriptionArn
	}

	return subscriptionArn, err
}

// snippet-end:[gov2.sns.Subscribe]

// snippet-start:[gov2.sns.Publish]

// Publish publishes a message to an Amazon SNS topic. The message is then sent to all
// subscribers. When the topic is a FIFO topic, the message must also contain a group ID
// and, when ID-based deduplication is used, a deduplication ID. An optional key-value
// filter attribute can be specified so that the message can be filtered according to
// a filter policy.
func (actor SnsActions) Publish(ctx context.Context, topicArn string, message string, groupId string, dedupId string, filterKey string, filterValue string) error {
	publishInput := sns.PublishInput{TopicArn: aws.String(topicArn), Message: aws.String(message)}
	if groupId != "" {
		publishInput.MessageGroupId = aws.String(groupId)
	}
	if dedupId != "" {
		publishInput.MessageDeduplicationId = aws.String(dedupId)
	}
	if filterKey != "" && filterValue != "" {
		publishInput.MessageAttributes = map[string]types.MessageAttributeValue{
			filterKey: {DataType: aws.String("String"), StringValue: aws.String(filterValue)},
		}
	}
	_, err := actor.SnsClient.Publish(ctx, &publishInput)
	if err != nil {
		log.Printf("Couldn't publish message to topic %v. Here's why: %v", topicArn, err)
	}
	return err
}

// snippet-end:[gov2.sns.Publish]
// snippet-end:[gov2.sns.SnsActions.complete]
