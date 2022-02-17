// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/rds"
    "github.com/aws/aws-sdk-go/service/rds/rdsiface"
)

// Define a mock struct to use in unit tests
type mockRDSClient struct {
    rdsiface.RDSAPI
}

func (m *mockRDSClient) CreateDBSnapshot(input *rds.CreateDBSnapshotInput) (*rds.CreateDBSnapshotOutput, error) {
    // Check that required inputs exist
    if input.DBInstanceIdentifier == nil || *input.DBInstanceIdentifier == "" {
        return nil, errors.New("The DBInstanceIdentifier argument is null or empty")
    }

    if input.DBSnapshotIdentifier == nil || *input.DBSnapshotIdentifier == "" {
        return nil, errors.New("The DBSnapshotIdentifier argument is null or empty")
    }

    resp := rds.CreateDBSnapshotOutput{}

    return &resp, nil
}

func TestCreateInstanceSnapshot(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    instance := "test-instance"

    mockSvc := &mockRDSClient{}

    err := MakeInstanceSnapshot(mockSvc, &instance)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created instance snapshot")
}
