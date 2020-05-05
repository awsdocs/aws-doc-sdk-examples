// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)

// Define a mock struct to use in unit tests
type mockIAMClient struct {
    iamiface.IAMAPI
}

var expectedDescription = "Provides Put, Get access to S3 and full access to CloudWatch Logs."

// Then for every *real* call you make in the example:
func (m *mockIAMClient) GetPolicy(input *iam.GetPolicyInput) (*iam.GetPolicyOutput, error) {
    // Check that required inputs exist
    if input.PolicyArn == nil || *input.PolicyArn == "" {
        return nil, errors.New("GetPolicyInput.PolicyArn is nil or an empty string")
    }

    resp := iam.GetPolicyOutput{
        Policy: &iam.Policy{
            Description: &expectedDescription,
        },
    }
    return &resp, nil
}

func TestGetPolicy(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    policyArn := "arn:aws:iam::aws:policy/AWSLambdaExecute"

    t.Log("Getting policy with ARN: " + policyArn)

    mockSvc := &mockIAMClient{}

    description, err := GetPolicyDescription(mockSvc, &policyArn)
    if err != nil {
        t.Fatal(err)
    }

    if description != expectedDescription {
        t.Log("Description:")
        t.Log(description)
        t.Log("Does not match expected description:")
        t.Fatal(expectedDescription)
    }

    t.Log("Description")
    t.Log(description)
}
