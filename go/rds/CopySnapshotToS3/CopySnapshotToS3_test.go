// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/rds"
    "github.com/aws/aws-sdk-go/service/rds/rdsiface"
)

// Define a mock struct to use in unit tests
type mockRDSClient struct {
    rdsiface.RDSAPI
}

var rdsSnapshot = "test-rds-snapshot"
var dBSnapshotArn = rdsSnapshot + "-arn"

func (m *mockRDSClient) DescribeDBSnapshots(input *rds.DescribeDBSnapshotsInput) (*rds.DescribeDBSnapshotsOutput, error) {
    resp := rds.DescribeDBSnapshotsOutput{
        DBSnapshots: []*rds.DBSnapshot{
            &rds.DBSnapshot{
                DBSnapshotArn: &dBSnapshotArn,
            },
        },
    }

    return &resp, nil
}

func (m *mockRDSClient) StartExportTask(input *rds.StartExportTaskInput) (*rds.StartExportTaskOutput, error) {

    resp := rds.StartExportTaskOutput{}

    return &resp, nil
}

func TestCopySnapshot(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    roleArn := "test-role-arn"
    kmsKey := "test-kms-key"
    bucketName := "test-bucket"
    rdsSnapshotName := "test-rds-snapshot"
    exportRDSSnapshotName := "test-export-name"

    mockSvc := &mockRDSClient{}

    _, err := StoreInstance(mockSvc, &rdsSnapshotName, &exportRDSSnapshotName, &roleArn, &kmsKey, &bucketName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Stored instance snapshot " + rdsSnapshotName + " to bucket " + bucketName)
}
