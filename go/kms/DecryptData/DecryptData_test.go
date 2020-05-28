// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/kms"
    "github.com/aws/aws-sdk-go/service/kms/kmsiface"
)

// Define a mock struct to use in unit tests
type mockKMSClient struct {
    kmsiface.KMSAPI
}

func (m *mockKMSClient) Decrypt(input *kms.DecryptInput) (*kms.DecryptOutput, error) {
    // Check that required inputs exist
    if input.CiphertextBlob == nil || len(input.CiphertextBlob) == 0 {
        return nil, errors.New("DecryptInput.CiphertextBlob is nil or empty")
    }

    resp := kms.DecryptOutput{
        Plaintext: []byte("Some output"),
    }
    return &resp, nil
}

func TestDecryptData(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    data := "test-data"
    blob := []byte(data)

    mockSvc := &mockKMSClient{}

    result, err := DecodeData(mockSvc, &blob)
    if err != nil {
        t.Fatal(err)
    }

    t.Log(string(result.Plaintext))
}
