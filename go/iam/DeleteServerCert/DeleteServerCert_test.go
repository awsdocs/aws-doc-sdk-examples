// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)

// Define a mock struct to use in unit tests
type mockIAMClient struct {
    iamiface.IAMAPI
}

func (m *mockIAMClient) DeleteServerCertificate(input *iam.DeleteServerCertificateInput) (*iam.DeleteServerCertificateOutput, error) {
    // Check that required inputs exist
    if input.ServerCertificateName == nil || *input.ServerCertificateName == "" {
        return nil, errors.New("UpdateServerCertificateInput.ServerCertificateName is nil or an empty string")
    }

    resp := iam.DeleteServerCertificateOutput{}

    return &resp, nil
}

func TestGetServerCert(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    certName := "test-cert"

    mockSvc := &mockIAMClient{}

    err := DeleteServerCert(mockSvc, &certName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted server certificate: " + certName)
}
