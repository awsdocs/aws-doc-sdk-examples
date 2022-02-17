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

func (m *mockPollyClient) SynthesizeSpeech(input *polly.SynthesizeSpeechInput) (*polly.SynthesizeSpeechOutput, error) {
    // Check that required inputs exist

    resp := polly.SynthesizeSpeechOutput{}
    return &resp, nil
}

func TestSynthesizeSpeech(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    fileName := "test.txt"

    mockSvc := &mockPollyClient{}

    _, err := MakeSpeech(mockSvc, &fileName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Synthesized speech from " + fileName)
}
