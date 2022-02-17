// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/sns"
    "github.com/aws/aws-sdk-go/service/sns/snsiface"
)

// Define a mock struct to use in unit tests
type mockSNSClient struct {
    snsiface.SNSAPI
}

func (m *mockSNSClient) Subscribe(input *sns.SubscribeInput) (*sns.SubscribeOutput, error) {
    // Check that required inputs exist
    if input.Endpoint == nil || *input.Endpoint == "" || input.TopicArn == nil || *input.TopicArn == "" {
        return nil, errors.New("SubscribeInput.Endpoint or SubscribeInput.TopicArn is nil or an empty string")
    }

    resp := sns.SubscribeOutput{}
    return &resp, nil
}

func TestSubscribeTopic(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    email := "test-email@example.com"
    topicARN := "test-topic-ARN"

    mockSvc := &mockSNSClient{}

    _, err := SubscribeTopic(mockSvc, &email, &topicARN)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Subscribed user with email " + email + " to topic with ARN " + topicARN)
}
