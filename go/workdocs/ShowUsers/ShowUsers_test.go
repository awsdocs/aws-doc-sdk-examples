// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/service/workdocs"
    "github.com/aws/aws-sdk-go/service/workdocs/workdocsiface"
)

// Define a mock struct to use in unit tests
type mockWorkDocsClient struct {
    workdocsiface.WorkDocsAPI
}

func (m *mockWorkDocsClient) DescribeUsers(input *workdocs.DescribeUsersInput) (*workdocs.DescribeUsersOutput, error) {
    // Check that required inputs exist
    if input.OrganizationId == nil || *input.OrganizationId == "" {
        return nil, errors.New("DescribeUsersInput.OrganizationId is nil or an empty string")
    }

    /*
       Username
       GivenName
       Surname
       EmailAddress
       RootFolderId
    */

    resp := workdocs.DescribeUsersOutput{
        Users: []*workdocs.User{
            {
                Username:     aws.String("test-user@example.com"),
                GivenName:    aws.String("test-user-first-name"),
                Surname:      aws.String("test-user-last-name"),
                EmailAddress: aws.String("test-user@example.com"),
                RootFolderId: aws.String("test-root-folder-ID"),
            },
        },
    }
    return &resp, nil
}

func TestShowUsers(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    orgID := "test-organization-ID"

    mockSvc := &mockWorkDocsClient{}

    result, err := ShowUsers(mockSvc, &orgID)
    if err != nil {
        t.Fatal(err)
    }

    for _, user := range result.Users {
        t.Log("Username:   " + *user.Username)
        t.Log("Firstname:  " + *user.GivenName)
        t.Log("Lastname:   " + *user.Surname)
        t.Log("Email:      " + *user.EmailAddress)
        t.Log("Root folder " + *user.RootFolderId)
    }
}
