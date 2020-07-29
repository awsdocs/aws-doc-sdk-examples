// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/ses"
    "github.com/aws/aws-sdk-go/service/ses/sesiface"
)

// Define a mock struct to use in unit tests
type mockSESClient struct {
    sesiface.SESAPI
}

func (m *mockSESClient) GetSendStatistics(input *ses.GetSendStatisticsInput) (*ses.GetSendStatisticsOutput, error) {
    // Check that required inputs exist

    resp := ses.GetSendStatisticsOutput{}
    return &resp, nil
}

func TestGetStatistics(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    mockSvc := &mockSESClient{}

    _, err := GetStatistics(mockSvc)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Retrieved SES send statistics")
}
