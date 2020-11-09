package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
	"github.com/aws/aws-sdk-go-v2/service/ec2/types"
)

type EC2MonitorInstancesImpl struct{}

func (dt EC2MonitorInstancesImpl) MonitorInstances(ctx context.Context,
	params *ec2.MonitorInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.MonitorInstancesOutput, error) {

	// Create a dummy instance for output
	instances := make([]*types.InstanceMonitoring, 1)
	instances[0] = &types.InstanceMonitoring{InstanceId: aws.String("aws-docs-example-instanceID")}

	output := &ec2.MonitorInstancesOutput{
		InstanceMonitorings: instances,
	}

	if *params.DryRun {
		return output, errors.New("DryRunOperation")
	}

	return output, nil
}

func (dt EC2MonitorInstancesImpl) UnmonitorInstances(ctx context.Context,
	params *ec2.UnmonitorInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.UnmonitorInstancesOutput, error) {

	// Create a dummy instance for output
	instances := make([]*types.InstanceMonitoring, 1)
	instances[0] = &types.InstanceMonitoring{InstanceId: aws.String("aws-docs-example-instanceID")}

	output := &ec2.UnmonitorInstancesOutput{
		InstanceMonitorings: instances,
	}

	if *params.DryRun {
		return output, errors.New("DryRunOperation")
	}

	return output, nil
}

type Config struct {
	InstanceID string `json:"InstanceID"`
	Monitor    string `json:"Monitor"`
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

	if globalConfig.InstanceID == "" || globalConfig.Monitor == "" {
		msg := "You must specify a value for InstanceID and Monitor in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestMonitorInstances(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	if globalConfig.Monitor != "ON" && globalConfig.Monitor != "OFF" {
		t.Fatal("You must set Monitor to ON or OFF in " + configFileName)
	}

	api := &EC2MonitorInstancesImpl{}

	if globalConfig.Monitor == "ON" {
		input := &ec2.MonitorInstancesInput{
			InstanceIds: []*string{
				&globalConfig.InstanceID,
			},
			DryRun: aws.Bool(true),
		}

		result, err := EnableMonitoring(context.Background(), api, input)
		if err != nil {
			fmt.Println("Got an error enablying monitoring for instance:")
			fmt.Println(err)
			return
		}

		fmt.Println("Success", result.InstanceMonitorings)
	} else {
		input := &ec2.UnmonitorInstancesInput{
			InstanceIds: []*string{
				&globalConfig.InstanceID,
			},
			DryRun: aws.Bool(true),
		}

		result, err := DisableMonitoring(context.Background(), api, input)
		if err != nil {
			fmt.Println("Got an error disablying monitoring for instance:")
			fmt.Println(err)
			return
		}

		fmt.Println("Success", *result.InstanceMonitorings[0].InstanceId)
	}
}
