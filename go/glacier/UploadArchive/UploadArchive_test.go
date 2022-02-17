// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/service/glacier"
    "github.com/aws/aws-sdk-go/service/glacier/glacieriface"
)

// Define a mock struct to use in unit tests
type mockGlacierClient struct {
    glacieriface.GlacierAPI
}

func (m *mockGlacierClient) UploadArchive(input *glacier.UploadArchiveInput) (*glacier.ArchiveCreationOutput, error) {
    // Check that required inputs exist
    if input.VaultName == nil || *input.VaultName == "" || input.AccountId == nil || *input.AccountId == "" || input.Body == nil {
        return nil, errors.New("UploadArchiveInput.AccountId or UploadArchiveInput.VaultName is nil or an empty string or UploadArchiveInput.Body is nil")
    }

    resp := glacier.ArchiveCreationOutput{
        ArchiveId: aws.String("test-archive-ID"),
    }
    return &resp, nil
}

func TestUploadArchive(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    vaultName := "test-vault"
    fileName := "this_is_a_test.txt"

    mockSvc := &mockGlacierClient{}

    result, err := ArchiveFile(mockSvc, &vaultName, &fileName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Archive ID for file " + fileName + " in vault " + vaultName + ": " + *result.ArchiveId)
}
