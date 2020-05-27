// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)

// Define a mock struct to use in unit tests
type mockEC2Client struct {
    ec2iface.EC2API
}

func (m *mockEC2Client) StartInstances(input *ec2.StartInstancesInput) (*ec2.StartInstancesOutput, error) {
    // Check that required inputs exist
    if input.InstanceIds == nil || *input.InstanceIds[0] == "" {
        return nil, errors.New("StartInstances.Ids is nil or StartInstances.Ids[0] is an empty string")
    }

    resp := ec2.StartInstancesOutput{}
    return &resp, nil
}

func (m *mockEC2Client) StopInstances(input *ec2.StopInstancesInput) (*ec2.StopInstancesOutput, error) {
    // Check that required inputs exist
    if input.InstanceIds == nil || *input.InstanceIds[0] == "" {
        return nil, errors.New("StopInstances.Ids is nil or StopInstances.Ids[0] is an empty string")
    }

    resp := ec2.StopInstancesOutput{}
    return &resp, nil
}

func TestStartStopInstance(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    instanceID := "test-RESOURCE"

    mockSvc := &mockEC2Client{}

    err := StartInstance(mockSvc, &instanceID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Started instance with ID " + instanceID)

    err = StopInstance(mockSvc, &instanceID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Stopped instance with ID " + instanceID)
}
