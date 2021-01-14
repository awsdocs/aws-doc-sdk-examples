package main

import (
	"context"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/sns"
	"github.com/aws/aws-sdk-go-v2/service/sns/types"
)

type SNSListSubscriptionsImpl struct{}

func (dt SNSListSubscriptionsImpl) ListSubscriptions(ctx context.Context,
	params *sns.ListSubscriptionsInput,
	optFns ...func(*sns.Options)) (*sns.ListSubscriptionsOutput, error) {
	// Create a dummy list of two subscriptions.
	subscriptions := make([]*types.Subscription, 2)
	subscriptions[0] = &types.Subscription{
		SubscriptionArn: aws.String("dummysubscriptionarn1"),
		TopicArn:        aws.String("dummytopicarn1"),
	}
	subscriptions[1] = &types.Subscription{
		SubscriptionArn: aws.String("dummysubscriptionarn1"),
		TopicArn:        aws.String("dummytopicarn2"),
	}

	output := &sns.ListSubscriptionsOutput{
		Subscriptions: subscriptions,
	}

	return output, nil
}

func TestListSubscriptions(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	input := sns.ListSubscriptionsInput{}

	api := &SNSListSubscriptionsImpl{}

	resp, err := GetSubscriptions(context.TODO(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Topic ARN")
	t.Log("Subscription ARN")
	t.Log("-------------------------")
	for _, s := range resp.Subscriptions {
		t.Log(*s.TopicArn)
		t.Log(*s.SubscriptionArn)
		t.Log("")
	}
}
