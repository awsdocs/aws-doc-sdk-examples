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

func (m *mockEC2Client) RebootInstances(input *ec2.RebootInstancesInput) (*ec2.RebootInstancesOutput, error) {
    // Check that required inputs exist
    if input.InstanceIds[0] == nil || *input.InstanceIds[0] == "" {
        return nil, errors.New("RebootInstancesInput.InstanceIds[0] is nil or an empty string")
    }

    resp := ec2.RebootInstancesOutput{}
    return &resp, nil
}

func TestRebootInstances(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    instanceID := "test-instance-id"

    mockSvc := &mockEC2Client{}

    err := RestartInstance(mockSvc, &instanceID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Rebooted instance with ID " + instanceID)
}
