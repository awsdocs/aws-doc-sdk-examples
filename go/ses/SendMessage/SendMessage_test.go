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

func (m *mockSESClient) SendEmail(input *ses.SendEmailInput) (*ses.SendEmailOutput, error) {
    // Check that required inputs exist
    if input.Source == nil || *input.Source == "" ||
        input.Destination.ToAddresses == nil {
        return nil, errors.New("SendEmailInput.Source is nil or an empty string or SendMailInput.Destination.ToAddresses is nil")
    }

    resp := ses.SendEmailOutput{}
    return &resp, nil
}

func TestSendMessage(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    sender := "test-sender@example.com"
    recipient := "test-recipient@example.com"

    mockSvc := &mockSESClient{}

    err := SendMsg(mockSvc, &sender, &recipient)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Sent email message to address " + recipient)
}
