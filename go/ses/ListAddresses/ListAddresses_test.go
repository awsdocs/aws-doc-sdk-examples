// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/ses"
    "github.com/aws/aws-sdk-go/service/ses/sesiface"
)

// Define a mock struct to use in unit tests
type mockSESClient struct {
    sesiface.SESAPI
}

func (m *mockSESClient) ListIdentities(input *ses.ListIdentitiesInput) (*ses.ListIdentitiesOutput, error) {
    // Check that required inputs exist
    if input.IdentityType == nil || *input.IdentityType == "" {
        return nil, errors.New("ListIdentitiesInput.IdentityType is nil or an empty string")
    }

    resp := ses.ListIdentitiesOutput{}
    return &resp, nil
}

func (m *mockSESClient) GetIdentityVerificationAttributes(input *ses.GetIdentityVerificationAttributesInput) (*ses.GetIdentityVerificationAttributesOutput, error) {
    // Check that required inputs exist
    if input.Identities == nil {
        return nil, errors.New("GetIdentityVerificationAttributesInput.Identities is nil")
    }

    resp := ses.GetIdentityVerificationAttributesOutput{}
    return &resp, nil
}

func TestListAddresses(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    mockSvc := &mockSESClient{}

    _, err := GetAddresses(mockSvc)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Retrieved email addresses")
}
