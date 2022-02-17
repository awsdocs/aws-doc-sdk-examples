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

func (m *mockKMSClient) Encrypt(input *kms.EncryptInput) (*kms.EncryptOutput, error) {
    // Check that required inputs exist
    if input.KeyId == nil || *input.KeyId == "" || input.Plaintext == nil || len(input.Plaintext) == 0 {
        return nil, errors.New("EncryptInput.KeyId is nil or an empty string or EncryptInput.Plaintext is nil or empty")
    }

    resp := kms.EncryptOutput{
        CiphertextBlob: []byte("jibberish"),
    }
    return &resp, nil
}

func TestEncryptData(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    keyID := "test-kms-key-ID"
    text := "This is a bunch of text"

    mockSvc := &mockKMSClient{}

    result, err := EncryptText(mockSvc, &keyID, &text)
    if err != nil {
        t.Fatal(err)
    }

    t.Log(result.CiphertextBlob)
}
