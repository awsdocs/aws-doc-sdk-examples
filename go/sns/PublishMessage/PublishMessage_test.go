// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/service/sns"
    "github.com/aws/aws-sdk-go/service/sns/snsiface"
)

// Define a mock struct to use in unit tests
type mockSNSClient struct {
    snsiface.SNSAPI
}

func (m *mockSNSClient) Publish(input *sns.PublishInput) (*sns.PublishOutput, error) {
    // Check that required inputs exist
    if input.Message == nil || *input.Message == "" || input.TopicArn == nil || *input.TopicArn == "" {
        return nil, errors.New("PublishInput.Message or PublishInput.TopicArn is nil or an empty string")
    }

    resp := sns.PublishOutput{
        MessageId: aws.String("test-message-ID"),
    }
    return &resp, nil
}

func TestPublishMessage(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    msg := "test-message"
    topicARN := "test-topic-ARN"

    mockSvc := &mockSNSClient{}

    result, err := PublishMessage(mockSvc, &msg, &topicARN)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Message ID: " + *result.MessageId)
}
