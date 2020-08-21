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

func (m *mockSESClient) VerifyEmailAddress(input *ses.VerifyEmailAddressInput) (*ses.VerifyEmailAddressOutput, error) {
    // Check that required inputs exist
    if input.EmailAddress == nil || *input.EmailAddress == "" {
        return nil, errors.New("VerifyEmailAddressInput.EmailAddress is nil or an empty string")
    }

    resp := ses.VerifyEmailAddressOutput{}
    return &resp, nil
}

func TestVerifyAddress(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    recipient := "test-recipient@example.com"

    mockSvc := &mockSESClient{}

    err := SendVerification(mockSvc, &recipient)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Verified email address " + recipient)
}
