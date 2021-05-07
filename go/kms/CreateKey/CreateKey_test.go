// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/service/kms"
    "github.com/aws/aws-sdk-go/service/kms/kmsiface"
)

// Define a mock struct to use in unit tests
type mockKMSClient struct {
    kmsiface.KMSAPI
}

func (m *mockKMSClient) CreateKey(input *kms.CreateKeyInput) (*kms.CreateKeyOutput, error) {
    resp := kms.CreateKeyOutput{
        KeyMetadata: &kms.KeyMetadata{
            KeyId: aws.String("test-kms-key-ID"),
        },
    }

    return &resp, nil
}

func TestCreateCMK(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    key := "test-key"
    value := "test-value"

    mockSvc := &mockKMSClient{}

    result, err := MakeKey(mockSvc, &key, &value)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created CMK key with ID: " + *result.KeyMetadata.KeyId)
}
