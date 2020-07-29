// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/glacier"
    "github.com/aws/aws-sdk-go/service/glacier/glacieriface"
)

// Define a mock struct to use in unit tests
type mockGlacierClient struct {
    glacieriface.GlacierAPI
}

func (m *mockGlacierClient) CreateVault(input *glacier.CreateVaultInput) (*glacier.CreateVaultOutput, error) {
    // Check that required inputs exist
    if input.VaultName == nil || *input.VaultName == "" {
        return nil, errors.New("CreateVaultInput.VaultName is nil or an empty string")
    }

    resp := glacier.CreateVaultOutput{}
    return &resp, nil
}

func TestCreateVault(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    name := "test-vault"

    mockSvc := &mockGlacierClient{}

    err := MakeVault(mockSvc, &name)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created vault " + name)
}
