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

func (m *mockEC2Client) CreateImage(input *ec2.CreateImageInput) (*ec2.CreateImageOutput, error) {
    // Check that required inputs exist
    if input.Name == nil || *input.Name == "" {
        return nil, errors.New("CreateImageInput.name is nil or an empty string")
    }

    resp := ec2.CreateImageOutput{
        ImageId: aws.String("test-image-id"),
    }
    return &resp, nil
}

func TestCreateImage(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    imageDescription := "test-image-description"
    instanceID := "test-instance-ID"
    imageName := "test-image-name"

    mockSvc := &mockEC2Client{}

    resp, err := MakeImage(mockSvc, &imageDescription, &instanceID, &imageName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created image with ID: " + *resp.ImageId)
}
