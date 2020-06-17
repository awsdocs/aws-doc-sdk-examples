// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"

    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)

// Define a mock struct to use in unit tests
type mockIAMClient struct {
    iamiface.IAMAPI
}

func (m *mockIAMClient) GetAccessKeyLastUsed(input *iam.GetAccessKeyLastUsedInput) (*iam.GetAccessKeyLastUsedOutput, error) {
    // Check that required inputs exist
    if input.AccessKeyId == nil || *input.AccessKeyId == "" {
        return nil, errors.New("GetAccessKeyLastUsedInput.AccessKeyId is nil or an empty string")
    }

    resp := iam.GetAccessKeyLastUsedOutput{
        AccessKeyLastUsed: &iam.AccessKeyLastUsed{
            LastUsedDate: aws.Time(time.Now()),
            Region:       aws.String("REGION"),
            ServiceName:  aws.String("SERVICE-NAME"),
        },
        UserName: aws.String("MrMagoo"),
    }
    return &resp, nil
}

func TestAccessKeyLastUsed(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    keyID := "test-access-key"

    mockSvc := &mockIAMClient{}

    result, err := WhenWasKeyUsed(mockSvc, &keyID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Access key was last used " + (*result.AccessKeyLastUsed.LastUsedDate).Format("2006-01-02 15:04:05 Monday"))
    t.Log("For service " + *result.AccessKeyLastUsed.ServiceName + " in region " + *result.AccessKeyLastUsed.Region)
}
