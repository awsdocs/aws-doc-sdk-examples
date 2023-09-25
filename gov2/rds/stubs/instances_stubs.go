// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package stubs defines service action stubs that are used by both the action and
// scenario unit tests.
//
// Each stub expects specific data as input and returns specific data as an output.
// If an error is specified, it is raised by the stubber.
package stubs

import (
	"fmt"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/rds"
	"github.com/aws/aws-sdk-go-v2/service/rds/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubGetParameterGroup(parameterGroupName string, parameterGroupFamily string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeDBParameterGroups",
		Input: &rds.DescribeDBParameterGroupsInput{
			DBParameterGroupName: aws.String(parameterGroupName),
		},
		Output: &rds.DescribeDBParameterGroupsOutput{
			DBParameterGroups: []types.DBParameterGroup{
				{
					DBParameterGroupName:   aws.String(parameterGroupName),
					DBParameterGroupFamily: aws.String(parameterGroupFamily),
					DBParameterGroupArn:    aws.String("0000000000000000000000000000000000:param-group:test"),
					Description:            aws.String("test"),
				},
			},
		},
		Error: raiseErr,
	}
}

func StubCreateParameterGroup(parameterGroupName string, familyName string, description string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateDBParameterGroup",
		Input: &rds.CreateDBParameterGroupInput{
			DBParameterGroupName:   aws.String(parameterGroupName),
			DBParameterGroupFamily: aws.String(familyName),
			Description:            aws.String(description),
		},
		Output: &rds.CreateDBParameterGroupOutput{
			DBParameterGroup: &types.DBParameterGroup{
				DBParameterGroupName: aws.String(parameterGroupName),
			},
		},
		Error: raiseErr,
	}
}

func StubDeleteParameterGroup(parameterGroupName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteDBParameterGroup",
		Input: &rds.DeleteDBParameterGroupInput{
			DBParameterGroupName: aws.String(parameterGroupName),
		},
		Output: &rds.DeleteDBParameterGroupOutput{},
		Error:  raiseErr,
	}
}

func StubGetParameters(parameterGroupName string, source string, outParams []types.Parameter,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeDBParameters",
		Input: &rds.DescribeDBParametersInput{
			DBParameterGroupName: aws.String(parameterGroupName),
			Source:               aws.String(source),
		},
		Output: &rds.DescribeDBParametersOutput{
			Parameters: outParams,
		},
		Error: raiseErr,
	}
}

func StubUpdateParameters(parameterGroupName string, updateParams []types.Parameter, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ModifyDBParameterGroup",
		Input: &rds.ModifyDBParameterGroupInput{
			DBParameterGroupName: aws.String(parameterGroupName),
			Parameters:           updateParams,
		},
		Output: &rds.ModifyDBParameterGroupOutput{},
		Error:  raiseErr,
	}
}

func StubCreateSnapshot(instanceIdentifier string, snapshotIdentifier string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateDBSnapshot",
		Input: &rds.CreateDBSnapshotInput{
			DBInstanceIdentifier: aws.String(instanceIdentifier),
			DBSnapshotIdentifier: aws.String(snapshotIdentifier),
		},
		Output: &rds.CreateDBSnapshotOutput{
			DBSnapshot: &types.DBSnapshot{
				DBSnapshotIdentifier: aws.String(snapshotIdentifier),
				Status:               aws.String("creating"),
			},
		},
		Error: raiseErr,
	}
}

func StubGetSnapshot(snapshotIdentifier string, status string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeDBSnapshots",
		Input: &rds.DescribeDBSnapshotsInput{
			DBSnapshotIdentifier: aws.String(snapshotIdentifier),
		},
		Output: &rds.DescribeDBSnapshotsOutput{
			DBSnapshots: []types.DBSnapshot{
				{
					DBSnapshotIdentifier: aws.String(snapshotIdentifier),
					Status:               aws.String(status),
					DBSnapshotArn:        aws.String("0000000000000000000000000000000000:snapshot:test"),
					Engine:               aws.String("test-engine"),
					EngineVersion:        aws.String("test-engine-version"),
					DBInstanceIdentifier: aws.String("test"),
					SnapshotCreateTime:   aws.Time(time.Now()),
				},
			},
		},
		Error: raiseErr,
	}
}

func StubCreateInstance(instanceName string, dbName string,
	dbEngine string, dbEngineVersion string, parameterGroupName string, dbInstanceClass string,
	storageType string, allocatedStorage int32, adminName string, adminPassword string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateDBInstance",
		Input: &rds.CreateDBInstanceInput{
			DBInstanceIdentifier: aws.String(instanceName),
			DBName:               aws.String(dbName),
			DBParameterGroupName: aws.String(parameterGroupName),
			Engine:               aws.String(dbEngine),
			EngineVersion:        aws.String(dbEngineVersion),
			DBInstanceClass:      aws.String(dbInstanceClass),
			StorageType:          aws.String(storageType),
			AllocatedStorage:     aws.Int32(allocatedStorage),
			MasterUsername:       aws.String(adminName),
			MasterUserPassword:   aws.String(adminPassword),
		},
		Output: &rds.CreateDBInstanceOutput{
			DBInstance: &types.DBInstance{
				DBInstanceIdentifier: aws.String(instanceName),
				Engine:               aws.String(dbEngine),
				DBInstanceClass:      aws.String(dbInstanceClass),
				DBInstanceStatus:     aws.String("starting"),
			},
		},
		Error: raiseErr,
	}
}

func StubGetInstance(instanceIdentifier string, status string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeDBInstances",
		Input: &rds.DescribeDBInstancesInput{
			DBInstanceIdentifier: aws.String(instanceIdentifier),
		},
		Output: &rds.DescribeDBInstancesOutput{
			DBInstances: []types.DBInstance{
				{
					DBInstanceIdentifier: aws.String(instanceIdentifier),
					DBInstanceStatus:     aws.String(status),
					DBInstanceArn:        aws.String("0000000000000000000000000000000000:instance:test"),
					Engine:               aws.String("test-engine"),
					EngineVersion:        aws.String("test-engine-version"),
					Endpoint: &types.Endpoint{
						Address: aws.String("test-address"),
						Port:    13,
					},
					MasterUsername: aws.String("test-user"),
				},
			},
		},
		Error: raiseErr,
	}
}

func StubDeleteInstance(instanceIdentifier string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteDBInstance",
		Input: &rds.DeleteDBInstanceInput{
			DBInstanceIdentifier:   aws.String(instanceIdentifier),
			DeleteAutomatedBackups: aws.Bool(true),
			SkipFinalSnapshot:      true,
		},
		Output: &rds.DeleteDBInstanceOutput{},
		Error:  raiseErr,
	}
}

func StubGetEngineVersions(engine string, family string, families []string, raiseErr *testtools.StubError) testtools.Stub {
	var outVersions []types.DBEngineVersion
	for index, family := range families {
		outVersions = append(outVersions, types.DBEngineVersion{
			Engine:                 aws.String(engine),
			EngineVersion:          aws.String(fmt.Sprintf("%v-%v", engine, index)),
			DBParameterGroupFamily: aws.String(family),
		})
	}
	return testtools.Stub{
		OperationName: "DescribeDBEngineVersions",
		Input: &rds.DescribeDBEngineVersionsInput{
			Engine:                 aws.String(engine),
			DBParameterGroupFamily: aws.String(family),
		},
		Output: &rds.DescribeDBEngineVersionsOutput{
			DBEngineVersions: outVersions,
		},
		Error: raiseErr,
	}
}

func StubGetOrderableInstances(engine string, engineVersion string, instanceClasses []string, raiseErr *testtools.StubError) testtools.Stub {
	var outInstances []types.OrderableDBInstanceOption
	for _, instanceClass := range instanceClasses {
		outInstances = append(outInstances, types.OrderableDBInstanceOption{
			Engine:          aws.String(engine),
			EngineVersion:   aws.String(engineVersion),
			DBInstanceClass: aws.String(instanceClass),
		})
	}
	return testtools.Stub{
		OperationName: "DescribeOrderableDBInstanceOptions",
		Input: &rds.DescribeOrderableDBInstanceOptionsInput{
			Engine:        aws.String(engine),
			EngineVersion: aws.String(engineVersion),
		},
		Output: &rds.DescribeOrderableDBInstanceOptionsOutput{
			OrderableDBInstanceOptions: outInstances,
		},
		Error: raiseErr,
	}
}
