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

type mockEC2Client struct {
    ec2iface.EC2API
}

func (m *mockEC2Client) AllocateAddress(input *ec2.AllocateAddressInput) (*ec2.AllocateAddressOutput, error) {
    // Check that required inputs exist
    if input.Domain == nil || *input.Domain == "" {
        return nil, errors.New("AllocateAddressInput.Domain is nil or an empty string")
    }

    resp := ec2.AllocateAddressOutput{
        PublicIp:     aws.String("test-public-IP"),
        AllocationId: aws.String("test-allocation-ID"),
    }
    return &resp, nil
}

func (m *mockEC2Client) AssociateAddress(input *ec2.AssociateAddressInput) (*ec2.AssociateAddressOutput, error) {
    // Check that required inputs exist
    if input.AllocationId == nil || *input.AllocationId == "" || input.InstanceId == nil || *input.InstanceId == "" {
        return nil, errors.New("AssociateAddressInput.AllocationId or AssociateAddressInput.InstanceId is nil or an empty string")
    }

    resp := ec2.AssociateAddressOutput{
        AssociationId: aws.String("test-association-ID"),
    }
    return &resp, nil
}

func TestAllocateAddress(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    instanceID := "test-instance-ID"

    mockSvc := &mockEC2Client{}

    allocRes, assocRes, err := AllocateAndAssociate(mockSvc, &instanceID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Allocated IP address", *allocRes.PublicIp, "with instance", instanceID)
    t.Log("Allocation id:", *allocRes.AllocationId, "association id: ", *assocRes.AssociationId)
}
