// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "log"
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

func (m *mockEC2Client) CreateKeyPair(input *ec2.CreateKeyPairInput) (*ec2.CreateKeyPairOutput, error) {
    // Check that required inputs exist
    if input.KeyName == nil || *input.KeyName == "" {
        return nil, errors.New("CreateKeyPairInput.KeyName is nil or an empty string")
    }

    resp := ec2.CreateKeyPairOutput{
        KeyName:        input.KeyName,
        KeyFingerprint: aws.String("test-key-fingerprint"),
        KeyMaterial:    aws.String("test-key-material"),
    }
    return &resp, nil
}

func TestCreateKeyPair(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    keyName := "test-key-name"

    mockSvc := &mockEC2Client{}

    result, err := MakeKeyPair(mockSvc, &keyName)
    if err != nil {
        log.Fatal(err)
    }

    t.Log("Created key pair:")
    t.Log("  Name:        " + *result.KeyName)
    t.Log("  Fingerprint: " + *result.KeyFingerprint)
    t.Log("  Material:    " + *result.KeyMaterial)
}
