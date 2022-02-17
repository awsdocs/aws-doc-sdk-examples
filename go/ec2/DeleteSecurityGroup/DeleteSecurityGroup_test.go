// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
	"testing"
	"time"

	"github.com/aws/aws-sdk-go/service/ec2"
	"github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)

// Define a mock struct to use in unit tests
type mockEC2Client struct {
	ec2iface.EC2API
}

func (m *mockEC2Client) DeleteSecurityGroup(input *ec2.DeleteSecurityGroupInput) (*ec2.DeleteSecurityGroupOutput, error) {
	// Check that required inputs exist

	resp := ec2.DeleteSecurityGroupOutput{}
	return &resp, nil
}

func TestDeleteSecurityGroup(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	// mock resources
	name := "test-security-group"

	mockSvc := &mockEC2Client{}

	err := RemoveSecurityGroup(mockSvc, &name)
	if err != nil {
		t.Fatal(err)
	}

	t.Log("Deleted security group " + name)
}
