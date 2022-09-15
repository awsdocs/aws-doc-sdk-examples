package main

import (
	"context"
	"encoding/json"
	"errors"
	"github.com/aws/aws-sdk-go-v2/aws"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/cloudwatchlogs"
)

type CWLGetLogEventsImpl struct{}

func (dt CWLGetLogEventsImpl) GetLogEvents(ctx context.Context,
	params *cloudwatchlogs.GetLogEventsInput,
	optFns ...func(*cloudwatchlogs.Options)) (*cloudwatchlogs.GetLogEventsOutput, error) {
	return &cloudwatchlogs.GetLogEventsOutput{}, nil
}

type Config struct {
	LogGroup  string `json:"LogGroup"`
	LogStream string `json:"LogStream"`
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

	if globalConfig.LogGroup == "" || globalConfig.LogStream == "" {
		msg := "You must supply a value for LogGroup and LogStream in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestGetLogEvents(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	input := &cloudwatchlogs.GetLogEventsInput{
		LogGroupName:  aws.String(globalConfig.LogGroup),
		LogStreamName: aws.String(globalConfig.LogStream),
	}

	api := &CWLGetLogEventsImpl{}

	_, err = GetEvents(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Fetched Log Events successfully")
}
