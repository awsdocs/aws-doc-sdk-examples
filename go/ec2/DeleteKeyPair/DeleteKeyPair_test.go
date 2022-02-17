// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "log"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)

// Define a mock struct to use in unit tests
type mockEC2Client struct {
    ec2iface.EC2API
}

func (m *mockEC2Client) DeleteKeyPair(input *ec2.DeleteKeyPairInput) (*ec2.DeleteKeyPairOutput, error) {
    // Check that required inputs exist
    if input.KeyName == nil || *input.KeyName == "" {
        return nil, errors.New("DeleteKeyPairInput.KeyName is nil or an empty string")
    }

    resp := ec2.DeleteKeyPairOutput{}
    return &resp, nil
}

func TestDeleteKeyPair(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    keyName := "test-key-name"

    mockSvc := &mockEC2Client{}

    err := RemoveKeyPair(mockSvc, &keyName)
    if err != nil {
        log.Fatal(err)
    }

    t.Log("Deleted key pair:" + keyName)
}
