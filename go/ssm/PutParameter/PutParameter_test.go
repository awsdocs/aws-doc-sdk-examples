// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
	"errors"
	"strconv"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go/aws"

	"github.com/aws/aws-sdk-go/service/ssm"
	"github.com/aws/aws-sdk-go/service/ssm/ssmiface"
)

// Define a mock struct to use in unit tests
type mockSSMClient struct {
	ssmiface.SSMAPI
}

func (m *mockSSMClient) PutParameter(input *ssm.PutParameterInput) (*ssm.PutParameterOutput, error) {
	// Check that required inputs exist
	if input.Name == nil || *input.Name == "" || input.Value == nil || *input.Value == "" {
		return nil, errors.New("PutParameterInput.Name or .Value is nil of an empty string")
	}

	if input.Type == nil || (*input.Type != ssm.ParameterTypeString && *input.Type != ssm.ParameterTypeStringList && *input.Type != ssm.ParameterTypeSecureString) {
		return nil, errors.New("PutParameterInput.Type should be one among (String,StringList,SecureString)")
	}

	resp := ssm.PutParameterOutput{
		Version: aws.Int64(1),
	}
	return &resp, nil
}

func TestPutParameter(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	// mock resource
	name := "test-param"
	value := "test-value"
	paramType := "String"

	mockSvc := &mockSSMClient{}

	results, err := PutParameter(mockSvc, &name, &value, &paramType)
	if err != nil {
		t.Fatal(err)
	}
	version := strconv.FormatInt(*results.Version, 10)

	t.Log("Created parameter with version " + version)
}
