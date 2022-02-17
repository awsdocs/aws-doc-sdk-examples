// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
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

func (m *mockEC2Client) DescribeVpcs(input *ec2.DescribeVpcsInput) (*ec2.DescribeVpcsOutput, error) {
    resp := ec2.DescribeVpcsOutput{
        Vpcs: []*ec2.Vpc{
            {
                VpcId: aws.String("test-vpc-id"),
            },
        },
    }
    return &resp, nil
}

func (m *mockEC2Client) CreateSecurityGroup(input *ec2.CreateSecurityGroupInput) (*ec2.CreateSecurityGroupOutput, error) {
    // Check that required inputs exist

    resp := ec2.CreateSecurityGroupOutput{
        GroupId: aws.String("test-group-id"),
    }
    return &resp, nil
}

func (m *mockEC2Client) AuthorizeSecurityGroupIngress(input *ec2.AuthorizeSecurityGroupIngressInput) (*ec2.AuthorizeSecurityGroupIngressOutput, error) {
    // Check that required inputs exist

    resp := ec2.AuthorizeSecurityGroupIngressOutput{}
    return &resp, nil
}

func TestCreateSecurityGroup(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    name := "test-security-group"
    description := "A very important security group"
    vpcID := "my-test-vpc-id"

    mockSvc := &mockEC2Client{}

    vID, gID, err := MakeSecurityGroup(mockSvc, &name, &description, &vpcID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created security group with ID " + gID + " with VPC with ID " + vID)
}
