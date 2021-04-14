package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/ssm"
	"github.com/aws/aws-sdk-go-v2/service/ssm/types"
)

type SSMPutParameterImpl struct{}

func (dt SSMPutParameterImpl) PutParameter(ctx context.Context,
	params *ssm.PutParameterInput,
	optFns ...func(*ssm.Options)) (*ssm.PutParameterOutput, error) {

	output := &ssm.PutParameterOutput{
		Version: 1,
	}

	return output, nil
}

type Config struct {
	ParameterName  string `json:"ParameterName"`
	ParameterValue string `json:"ParameterValue"`
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

	if globalConfig.ParameterName == "" || globalConfig.ParameterValue == "" {
		msg := "You must supply a value for ParameterName and ParameterValue in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestPutParameter(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	api := &SSMPutParameterImpl{}

	input := &ssm.PutParameterInput{
		Name:  &globalConfig.ParameterName,
		Value: &globalConfig.ParameterValue,
		Type:  types.ParameterTypeString,
	}

	resp, err := AddStringParameter(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Parameter version:", resp.Version)
}
