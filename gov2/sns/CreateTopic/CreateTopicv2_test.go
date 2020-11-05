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

type SNSCreateTopicImpl struct{}

func (dt SNSCreateTopicImpl) CreateTopic(ctx context.Context,
	params *sns.CreateTopicInput,
	optFns ...func(*sns.Options)) (*sns.CreateTopicOutput, error) {

	output := &sns.CreateTopicOutput{
		TopicArn: aws.String("dummytopicarn"),
	}

	return output, nil
}

type Config struct {
	TopicName string `json:"TopicName"`
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

	if globalConfig.TopicName == "" {
		msg := "You must specify a value for TopicName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestCreateTopic(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	// Build the request with its input parameters
	input := sns.CreateTopicInput{
		Name: &globalConfig.TopicName,
	}

	api := &SNSCreateTopicImpl{}

	resp, err := MakeTopic(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Created topic with ARN: " + *resp.TopicArn + " for name: " + globalConfig.TopicName)
}
