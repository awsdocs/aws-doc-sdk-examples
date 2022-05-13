// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/cloudwatchevents"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatchevents/types"
)

type CWEPutEventsImpl struct{}

func (dt CWEPutEventsImpl) PutEvents(ctx context.Context,
	params *cloudwatchevents.PutEventsInput,
	optFns ...func(*cloudwatchevents.Options)) (*cloudwatchevents.PutEventsOutput, error) {
	return &cloudwatchevents.PutEventsOutput{}, nil
}

type Config struct {
	LambdaARN string `json:"LambdaARN"`
	EventFile string `json:"EventFile"`
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

	if globalConfig.LambdaARN == "" || globalConfig.EventFile == "" {
		msg := "You must supply a value for LambdaARN and EventFile in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestPutEvents(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	event, err := getEventInfo(globalConfig.EventFile)
	if err != nil {
		fmt.Println("Got an error calling getEventInfo:")
		fmt.Println(err)
		return
	}

	myDetails := "{ "
	for i, d := range event.Details {
		if i == (len(event.Details) - 1) {
			myDetails = myDetails + "\"" + d.Key + "\": \"" + d.Value + "\""	
		} else {
			myDetails = myDetails + "\"" + d.Key + "\": \"" + d.Value + "\","
		}
	}
	
	myDetails = myDetails + " }"

	input := &cloudwatchevents.PutEventsInput{
		Entries: []types.PutEventsRequestEntry{
			{
				Detail:     &myDetails,
				DetailType: &event.DetailType,
				Resources: []string{
					globalConfig.LambdaARN,
				},
				Source: &event.Source,
			},
		},
	}

	api := &CWEPutEventsImpl{}

	_, err = CreateEvent(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Created event")
}
