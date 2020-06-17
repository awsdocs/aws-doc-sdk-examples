// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/polly"
    "github.com/aws/aws-sdk-go/service/polly/pollyiface"
)

// Define a mock struct to use in unit tests
type mockPollyClient struct {
    pollyiface.PollyAPI
}

func (m *mockPollyClient) DescribeVoices(input *polly.DescribeVoicesInput) (*polly.DescribeVoicesOutput, error) {
    // Check that required inputs exist

    resp := polly.DescribeVoicesOutput{}
    return &resp, nil
}

func TestDescribeVoices(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    mockSvc := &mockPollyClient{}

    _, err := GetVoices(mockSvc)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Retrieved the list of voices")
}
