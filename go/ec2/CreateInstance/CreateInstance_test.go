// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)

// Define a mock struct to use in unit tests
type mockEC2Client struct {
    ec2iface.EC2API
}

func (m *mockEC2Client) RunInstances(input *ec2.RunInstancesInput) (*ec2.Reservation, error) {
    // Check that required inputs exist
    if input.MaxCount == nil || *input.MaxCount < int64(0) || input.MinCount == nil || *input.MinCount < int64(0) {
        return nil, errors.New("RunInstancesInput.MaxCount or RunInstancesInput.MinCount is nil or less than zero")
    }

    resp := ec2.Reservation{
        Instances: []*ec2.Instance{&ec2.Instance{
            InstanceId: aws.String("test-instance-id"),
        }},
    }
    return &resp, nil
}

func (m *mockEC2Client) CreateTags(input *ec2.CreateTagsInput) (*ec2.CreateTagsOutput, error) {
    if input.Tags == nil {
        return nil, errors.New("CreateTagsInput.Tags is nil")
    }

    if input.Tags[0].Key == nil || *input.Tags[0].Key == "" || input.Tags[0].Value == nil || *input.Tags[0].Value == "" {
        return nil, errors.New("CreateTagsInput.Tags[0].Tag or CreateTagsInput.Tags[0].Value is nil or an empty string")
    }

    resp := ec2.CreateTagsOutput{}
    return &resp, nil
}

func TestCreateInstance(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    InstanceName := "test-instance"
    InstanceValue := "text-value"

    mockSvc := &mockEC2Client{}

    result, err := MakeInstance(mockSvc, &InstanceName, &InstanceValue)
    if err != nil {
        t.Fatal("Got an error creating an instance with tag " + InstanceName)
    }

    t.Log("Created tagged instance with ID " + *result.Instances[0].InstanceId)
}
