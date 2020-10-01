// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
	"errors"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go/service/ssm"
	"github.com/aws/aws-sdk-go/service/ssm/ssmiface"
)

// Define a mock struct to use in unit tests
type mockSSMClient struct {
	ssmiface.SSMAPI
}

func (m *mockSSMClient) DeleteParameter(input *ssm.DeleteParameterInput) (*ssm.DeleteParameterOutput, error) {
	// Check that required inputs exist
	if input.Name == nil || *input.Name == "" {
		return nil, errors.New("GetParameterInput.Name is nil or an empty string")
	}
	return nil, nil
}

func TestDeleteParameter(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	// mock resource
	name := "test-param"

	mockSvc := &mockSSMClient{}

	err := DeleteParameter(mockSvc, &name)
	if err != nil {
		t.Fatal(err)
	}
}
