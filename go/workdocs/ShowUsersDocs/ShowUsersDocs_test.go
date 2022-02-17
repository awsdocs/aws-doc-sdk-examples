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

    resp := workdocs.DescribeUsersOutput{
        Users: []*workdocs.User{
            {
                RootFolderId: aws.String("test-root-folder-ID"),
            },
        },
    }
    return &resp, nil
}

func (m *mockWorkDocsClient) DescribeFolderContents(input *workdocs.DescribeFolderContentsInput) (*workdocs.DescribeFolderContentsOutput, error) {
    // Check that required inputs exist
    if input.FolderId == nil || *input.FolderId == "" {
        return nil, errors.New("DescribeFolderContentsInput.FolderId is nil or an empty string")
    }

    resp := workdocs.DescribeFolderContentsOutput{
        Documents: []*workdocs.DocumentMetadata{
            {
                LatestVersionMetadata: &workdocs.DocumentVersionMetadata{
                    Name: aws.String("test-document.txt"),
                },
            },
        },
    }
    return &resp, nil
}

func TestShowUsersDocs(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    orgID := "test-organization-ID"
    userName := "test-user-name"

    mockSvc := &mockWorkDocsClient{}

    result, err := ShowUserDocs(mockSvc, &orgID, &userName)
    if err != nil {
        t.Fatal(err)
    }

    for _, doc := range result.Documents {
        t.Log(*doc.LatestVersionMetadata.Name)
    }
}
