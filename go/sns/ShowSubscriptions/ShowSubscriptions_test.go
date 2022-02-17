// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/sns"
    "github.com/aws/aws-sdk-go/service/sns/snsiface"
)

// Define a mock struct to use in unit tests
type mockSNSClient struct {
    snsiface.SNSAPI
}

func (m *mockSNSClient) ListSubscriptions(input *sns.ListSubscriptionsInput) (*sns.ListSubscriptionsOutput, error) {
    // Check that required inputs exist

    resp := sns.ListSubscriptionsOutput{}
    return &resp, nil
}

func TestShowSubscriptions(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    mockSvc := &mockSNSClient{}

    _, err := GetSubscriptions(mockSvc)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Retrieved subscriptions")
}
