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

func (m *mockIAMClient) UpdateServerCertificate(input *iam.UpdateServerCertificateInput) (*iam.UpdateServerCertificateOutput, error) {
    // Check that required inputs exist
    if input.ServerCertificateName == nil || *input.ServerCertificateName == "" || input.NewServerCertificateName == nil || *input.NewServerCertificateName == "" {
        return nil, errors.New("UpdateServerCertificateInput.ServerCertificateName or UpdateServerCertificateInput.NewServerCertificateName is nil or an empty string")
    }

    resp := iam.UpdateServerCertificateOutput{}

    return &resp, nil
}

func TestGetServerCert(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    certName := "test-cert"
    newName := "new-test-cert"

    mockSvc := &mockIAMClient{}

    err := RenameServerCert(mockSvc, &certName, &newName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Renamed server certificate from: " + certName + " to: " + newName)
}
