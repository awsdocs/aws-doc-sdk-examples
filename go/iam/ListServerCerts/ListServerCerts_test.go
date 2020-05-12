// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)

// Define a mock struct to use in unit tests
type mockIAMClient struct {
    iamiface.IAMAPI
}

func (m *mockIAMClient) ListServerCertificates(input *iam.ListServerCertificatesInput) (*iam.ListServerCertificatesOutput, error) {
    // Check that required inputs exist

    resp := iam.ListServerCertificatesOutput{}
    return &resp, nil
}

func TestListServerCerts(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    mockSvc := &mockIAMClient{}

    _, err := GetServerCerts(mockSvc)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Retrieved the server certificates")
}
