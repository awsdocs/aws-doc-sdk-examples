// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)

// Define a mock struct to use in unit tests
type mockIAMClient struct {
    iamiface.IAMAPI
}

func (m *mockIAMClient) GetServerCertificate(input *iam.GetServerCertificateInput) (*iam.GetServerCertificateOutput, error) {
    // Check that required inputs exist
    if input.ServerCertificateName == nil || *input.ServerCertificateName == "" {
        return nil, errors.New("GetServerCertificateInput.ServerCertificateName is nil or an empty string")
    }

    resp := iam.GetServerCertificateOutput{
        ServerCertificate: &iam.ServerCertificate{
            ServerCertificateMetadata: &iam.ServerCertificateMetadata{
                Arn:                   aws.String("test-cert.ARN"),
                Expiration:            aws.Time(time.Now()),
                Path:                  aws.String("a/b/c"),
                ServerCertificateId:   aws.String("test-cert-ID"),
                ServerCertificateName: aws.String("my-server-cert"),
                UploadDate:            aws.Time(time.Now()),
            },
        },
    }

    return &resp, nil
}

func TestGetServerCert(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    certName := "test-cert"

    mockSvc := &mockIAMClient{}

    result, err := FindServerCert(mockSvc, &certName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Retrieved metadata for certificate: " + certName + ":")
    metadata := result.ServerCertificate.ServerCertificateMetadata

    t.Log("ARN:                  " + *metadata.Arn)
    t.Log("Expiration:           " + (*metadata.Expiration).Format("2006-01-02 15:04:05 Monday"))
    t.Log("Path:                 " + *metadata.Path)
    t.Log("ServerCertificateId   " + *metadata.ServerCertificateId)
    t.Log("ServerCertificateName " + *metadata.ServerCertificateName)
    t.Log("UploadDate:           " + (*metadata.UploadDate).Format("2006-01-02 15:04:05 Monday"))
}
