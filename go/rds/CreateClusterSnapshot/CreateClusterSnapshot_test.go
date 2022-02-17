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

func (m *mockRDSClient) CreateDBClusterSnapshot(input *rds.CreateDBClusterSnapshotInput) (*rds.CreateDBClusterSnapshotOutput, error) {
    // Check that required inputs exist
    if input.DBClusterIdentifier == nil || *input.DBClusterIdentifier == "" {
        return nil, errors.New("The DBInstanceIdentifier argument is null or empty")
    }

    resp := rds.CreateDBClusterSnapshotOutput{}

    return &resp, nil
}

func TestCreateClusterSnapshot(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    clusterID := "test-cluster-1234"

    mockSvc := &mockRDSClient{}

    err := MakeClusterSnapshot(mockSvc, &clusterID)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created cluster snapshot")
}
