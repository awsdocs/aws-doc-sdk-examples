package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/sns"
)

type SNSSubscribeImpl struct{}

func (dt SNSSubscribeImpl) Subscribe(ctx context.Context,
	params *sns.SubscribeInput,
	optFns ...func(*sns.Options)) (*sns.SubscribeOutput, error) {

	output := &sns.SubscribeOutput{
		SubscriptionArn: aws.String("dummysubscriptionarn"),
	}

	return output, nil
}

type Config struct {
	EmailAddress string `json:"EmailAddress"`
	TopicArn     string `json:"TopicArn"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration(t *testing.T) error {
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		return err
	}

	text := string(content)

	err = json.Unmarshal([]byte(text), &globalConfig)
	if err != nil {
		return err
	}

	if globalConfig.EmailAddress == "" || globalConfig.TopicArn == "" {
		msg := "You must specify a value for EmailAddress and TopicArn in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestSubscribe(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	api := &SNSSubscribeImpl{}

	input := &sns.SubscribeInput{
		Endpoint:              &globalConfig.EmailAddress,
		Protocol:              aws.String("email"),
		ReturnSubscriptionArn: true, // Return the ARN, even if user has yet to confirm
		TopicArn:              &globalConfig.TopicArn,
	}

	_, err = SubscribeTopic(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Subscribed user with email address " + globalConfig.EmailAddress)
	t.Log("to topic with ARN: " + globalConfig.TopicArn)
}
