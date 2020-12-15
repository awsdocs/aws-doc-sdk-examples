package main

import (
	"context"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/sns"
	"github.com/aws/aws-sdk-go-v2/service/sns/types"
)

type SNSListTopicsImpl struct{}

func (dt SNSListTopicsImpl) ListTopics(ctx context.Context,
	params *sns.ListTopicsInput,
	optFns ...func(*sns.Options)) (*sns.ListTopicsOutput, error) {

	// Create dummy list of two topics
	topics := make([]*types.Topic, 2)
	topics[0] = &types.Topic{TopicArn: aws.String("dummytopicarn1")}
	topics[1] = &types.Topic{TopicArn: aws.String("dummytopicarn2")}

	output := &sns.ListTopicsOutput{
		Topics: topics,
	}

	return output, nil
}

func TestListTopics(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	// Build the request with its input parameters
	input := sns.ListTopicsInput{}

	api := &SNSListTopicsImpl{}

	resp, err := GetTopics(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	for _, topic := range resp.Topics {
		t.Log(*topic.TopicArn)
	}
}
