package main

import (
	"context"
	"fmt"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
)

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

	api := &EC2MockImpl{}

	if globalConfig.Monitor == "ON" {
		input := &ec2.MonitorInstancesInput{
			InstanceIds: []string{
				globalConfig.InstanceID,
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
			InstanceIds: []string{
				globalConfig.InstanceID,
			},
			DryRun: aws.Bool(true),
		}

		result, err := DisableMonitoring(context.Background(), api, input)
		if err != nil {
			fmt.Println("Got an error disabling monitoring for instance:")
			fmt.Println(err)
			return
		}

		fmt.Println("Success", *result.InstanceMonitorings[0].InstanceId)
	}
}
