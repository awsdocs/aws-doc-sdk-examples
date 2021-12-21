package main

import (
	"context"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
)

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
		DryRun: aws.Bool(true),
	}

	api := &EC2MockImpl{}

	resp, err := StopInstance(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Instance with ID " + *resp.StoppingInstances[0].InstanceId + " is: " + string(resp.StoppingInstances[0].CurrentState.Name))
}
