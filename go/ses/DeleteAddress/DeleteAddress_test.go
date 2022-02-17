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

func (m *mockSESClient) DeleteVerifiedEmailAddress(input *ses.DeleteVerifiedEmailAddressInput) (*ses.DeleteVerifiedEmailAddressOutput, error) {
    // Check that required inputs exist
    if input.EmailAddress == nil || *input.EmailAddress == "" {
        return nil, errors.New("DeleteVerifiedEmailAddressInput.EmailAddress is nil or an empty string")
    }

    resp := ses.DeleteVerifiedEmailAddressOutput{}
    return &resp, nil
}

func TestDeleteAddress(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    address := "test-address@example.com"

    mockSvc := &mockSESClient{}

    err := RemoveAddress(mockSvc, &address)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Removed email address " + address)
}
