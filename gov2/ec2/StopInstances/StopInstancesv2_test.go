package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/ec2"
	"github.com/aws/aws-sdk-go-v2/service/ec2/types"
	"github.com/aws/smithy-go"
)

type mockDryRunError struct {
	smithy.APIError
}

func (mockDryRunError) ErrorCode() string {
	return "DryRunOperation"
}

type EC2StopInstancesImpl struct{}

func (dt EC2StopInstancesImpl) StopInstances(ctx context.Context,
	params *ec2.StopInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.StopInstancesOutput, error) {

	var state types.InstanceState
	state.Name = types.InstanceStateNameStopped

	instances := []types.InstanceStateChange{
		{
			CurrentState: &state,
			InstanceId:   &params.InstanceIds[0],
		},
	}

	output := &ec2.StopInstancesOutput{
		StoppingInstances: instances,
	}

	if params.DryRun {
		return output, mockDryRunError{}
	}

	return output, nil
}

type Config struct {
	InstanceID string `json:"InstanceID"`
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

	if globalConfig.InstanceID == "" {
		msg := "You must specify a value for InstanceID in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestStopInstances(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	input := &ec2.StopInstancesInput{
		InstanceIds: []string{
			globalConfig.InstanceID,
		},
		DryRun: true,
	}

	api := &EC2StopInstancesImpl{}

	resp, err := StopInstance(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Instance with ID " + *resp.StoppingInstances[0].InstanceId + " is: " + string(resp.StoppingInstances[0].CurrentState.Name))
}
