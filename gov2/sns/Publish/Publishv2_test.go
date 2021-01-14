package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/sns"
	"github.com/aws/aws-sdk-go/aws"
)

type SNSPublishImpl struct{}

func (dt SNSPublishImpl) Publish(ctx context.Context,
	params *sns.PublishInput,
	optFns ...func(*sns.Options)) (*sns.PublishOutput, error) {

	output := &sns.PublishOutput{
		MessageId: aws.String("123"),
	}

	return output, nil
}

type Config struct {
	Message  string `json:"Message"`
	TopicArn string `json:"TopicArn"`
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

	if globalConfig.Message == "" || globalConfig.TopicArn == "" {
		msg := "You must specify a value for Message and TopicArn in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestPublish(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	// Build the request with its input parameters
	input := sns.PublishInput{
		Message:  &globalConfig.Message,
		TopicArn: &globalConfig.TopicArn,
	}

	api := &SNSPublishImpl{}

	resp, err := PublishMessage(context.TODO(), api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Message ID: " + *resp.MessageId)
}
