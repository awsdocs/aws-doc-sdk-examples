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

func (m *mockEC2Client) MonitorInstances(input *ec2.MonitorInstancesInput) (*ec2.MonitorInstancesOutput, error) {
    // Check that required inputs exist
    if input.InstanceIds == nil || *input.InstanceIds[0] == "" {
        return nil, errors.New("MonitorInstancesInput.InstanceIds is nil or MonitorInstancesInput.InstanceIds[0] is an empty string")
    }

    resp := ec2.MonitorInstancesOutput{}
    return &resp, nil
}

func (m *mockEC2Client) UnmonitorInstances(input *ec2.UnmonitorInstancesInput) (*ec2.UnmonitorInstancesOutput, error) {
    // Check that required inputs exist
    if input.InstanceIds == nil || *input.InstanceIds[0] == "" {
        return nil, errors.New("UnmonitorInstancesInput.InstanceIds is nil or UnmonitorInstancesInput.InstanceIds[0] is an empty string")
    }

    resp := ec2.UnmonitorInstancesOutput{}
    return &resp, nil
}

func TestMonitorInstances(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    instanceID := "test-instance-id"

    mockSvc := &mockEC2Client{}

    _, err := EnableMonitoring(mockSvc, &instanceID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Enabled monitoring for instance with ID: " + instanceID)

    _, err = DisableMonitoring(mockSvc, &instanceID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Disabled monitoring for instance with ID: " + instanceID)
}
