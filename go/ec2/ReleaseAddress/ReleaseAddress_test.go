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

func (m *mockEC2Client) ReleaseAddress(input *ec2.ReleaseAddressInput) (*ec2.ReleaseAddressOutput, error) {
    // Check that required inputs exist
    if input.AllocationId == nil || *input.AllocationId == "" {
        return nil, errors.New("ReleaseAddressInput.AllocationId is nil or an empty string")
    }

    resp := ec2.ReleaseAddressOutput{}
    return &resp, nil
}

func TestReleaseAddress(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    allocationID := "test-allocation-ID"

    mockSvc := &mockEC2Client{}

    err := ClearAddress(mockSvc, &allocationID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Released allocated address")
}
