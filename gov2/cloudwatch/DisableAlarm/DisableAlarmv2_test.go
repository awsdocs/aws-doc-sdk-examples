package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
)

type CWDisableAlarmImpl struct{}

func (dt CWDisableAlarmImpl) DisableAlarmActions(ctx context.Context,
	params *cloudwatch.DisableAlarmActionsInput,
	optFns ...func(*cloudwatch.Options)) (*cloudwatch.DisableAlarmActionsOutput, error) {

	output := &cloudwatch.DisableAlarmActionsOutput{}

	return output, nil
}

type Config struct {
	AlarmName string `json:"AlarmName"`
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

	if globalConfig.AlarmName == "" {
		msg := "You must supply a value for AlarmName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestDisableAlarm(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	input := cloudwatch.DisableAlarmActionsInput{}

	api := &CWDisableAlarmImpl{}

	_, err = DisableAlarm(context.TODO(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Disabled alarm: " + globalConfig.AlarmName)
}
