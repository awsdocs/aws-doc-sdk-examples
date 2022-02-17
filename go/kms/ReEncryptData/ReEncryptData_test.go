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

func (m *mockKMSClient) ReEncrypt(input *kms.ReEncryptInput) (*kms.ReEncryptOutput, error) {
    if input.DestinationKeyId == nil || *input.DestinationKeyId == "" || input.CiphertextBlob == nil || len(input.CiphertextBlob) == 0 {
        return nil, errors.New("ReEncryptInput.DestinationKeyId is nil or an empty string or ReEncryptInput.CiphertextBlob is nil or empty")
    }

    resp := kms.ReEncryptOutput{
        CiphertextBlob: []byte("jibberish"),
    }
    return &resp, nil
}

func TestEncryptData(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    keyID := "test-kms-key-ID"
    data := "test-data"
    blob := []byte(data)

    mockSvc := &mockKMSClient{}

    result, err := ReEncryptText(mockSvc, &blob, &keyID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log(result.CiphertextBlob)
}
