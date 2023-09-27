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
		OperationName: "DescribeDBClusterParameterGroups",
		Input: &rds.DescribeDBClusterParameterGroupsInput{
			DBClusterParameterGroupName: aws.String(parameterGroupName),
		},
		Output: &rds.DescribeDBClusterParameterGroupsOutput{
			DBClusterParameterGroups: []types.DBClusterParameterGroup{
				{
					DBClusterParameterGroupName: aws.String(parameterGroupName),
					DBParameterGroupFamily:      aws.String(parameterGroupFamily),
					DBClusterParameterGroupArn:  aws.String("0000000000000000000000000000000000:cluster-param-group:test"),
					Description:                 aws.String("test"),
				},
			},
		},
		Error: raiseErr,
	}
}

func StubCreateParameterGroup(parameterGroupName string, familyName string, description string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateDBClusterParameterGroup",
		Input: &rds.CreateDBClusterParameterGroupInput{
			DBClusterParameterGroupName: aws.String(parameterGroupName),
			DBParameterGroupFamily:      aws.String(familyName),
			Description:                 aws.String(description),
		},
		Output: &rds.CreateDBClusterParameterGroupOutput{
			DBClusterParameterGroup: &types.DBClusterParameterGroup{
				DBClusterParameterGroupName: aws.String(parameterGroupName),
			},
		},
		Error: raiseErr,
	}
}

func StubDeleteParameterGroup(parameterGroupName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteDBClusterParameterGroup",
		Input: &rds.DeleteDBClusterParameterGroupInput{
			DBClusterParameterGroupName: aws.String(parameterGroupName),
		},
		Output: &rds.DeleteDBClusterParameterGroupOutput{},
		Error:  raiseErr,
	}
}

func StubGetParameters(parameterGroupName string, source string, outParams []types.Parameter,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeDBClusterParameters",
		Input: &rds.DescribeDBClusterParametersInput{
			DBClusterParameterGroupName: aws.String(parameterGroupName),
			Source:                      aws.String(source),
		},
		Output: &rds.DescribeDBClusterParametersOutput{
			Parameters: outParams,
		},
		Error: raiseErr,
	}
}

func StubUpdateParameters(parameterGroupName string, updateParams []types.Parameter, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ModifyDBClusterParameterGroup",
		Input: &rds.ModifyDBClusterParameterGroupInput{
			DBClusterParameterGroupName: aws.String(parameterGroupName),
			Parameters:                  updateParams,
		},
		Output: &rds.ModifyDBClusterParameterGroupOutput{},
		Error:  raiseErr,
	}
}

func StubGetDbCluster(clusterIdentifier string, status string, dbEngine string, dbEngineVersion string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeDBClusters",
		Input: &rds.DescribeDBClustersInput{
			DBClusterIdentifier: aws.String(clusterIdentifier),
		},
		Output: &rds.DescribeDBClustersOutput{
			DBClusters: []types.DBCluster{
				{
					DBClusterIdentifier:     aws.String(clusterIdentifier),
					Engine:                  aws.String(dbEngine),
					EngineVersion:           aws.String(dbEngineVersion),
					Status:                  aws.String(status),
					DBClusterArn:            aws.String("0000000000000000000000000000000000:cluster:test"),
					DBClusterParameterGroup: aws.String("test"),
					EngineMode:              aws.String("serverless"),
					Endpoint:                aws.String("test.com"),
					MasterUsername:          aws.String("test"),
					Port:                    aws.Int32(123),
				},
			},
		},
		Error: raiseErr,
	}
}

func StubCreateDbCluster(clusterIdentifier string, dbEngine string, parameterGroupName string,
	dbName string, dbEngineVersion string, adminName string, adminPassword string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateDBCluster",
		Input: &rds.CreateDBClusterInput{
			DBClusterIdentifier:         aws.String(clusterIdentifier),
			Engine:                      aws.String(dbEngine),
			DBClusterParameterGroupName: aws.String(parameterGroupName),
			DatabaseName:                aws.String(dbName),
			EngineVersion:               aws.String(dbEngineVersion),
			MasterUserPassword:          aws.String(adminPassword),
			MasterUsername:              aws.String(adminName),
		},
		Output: &rds.CreateDBClusterOutput{
			DBCluster: &types.DBCluster{
				DBClusterIdentifier: aws.String(clusterIdentifier),
				Engine:              aws.String(dbEngine),
				DatabaseName:        aws.String(dbName),
				EngineVersion:       aws.String(dbEngineVersion),
				MasterUsername:      aws.String(adminName),
				Status:              aws.String("starting"),
			},
		},
		Error: raiseErr,
	}
}

func StubDeleteDbCluster(clusterIdentifier string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteDBCluster",
		Input: &rds.DeleteDBClusterInput{
			DBClusterIdentifier: aws.String(clusterIdentifier),
			SkipFinalSnapshot:   true,
		},
		Output: &rds.DeleteDBClusterOutput{},
		Error:  raiseErr,
	}
}

func StubCreateClusterSnapshot(clusterIdentifier string, snapshotIdentifier string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateDBClusterSnapshot",
		Input: &rds.CreateDBClusterSnapshotInput{
			DBClusterIdentifier:         aws.String(clusterIdentifier),
			DBClusterSnapshotIdentifier: aws.String(snapshotIdentifier),
		},
		Output: &rds.CreateDBClusterSnapshotOutput{
			DBClusterSnapshot: &types.DBClusterSnapshot{
				DBClusterSnapshotIdentifier: aws.String(snapshotIdentifier),
				Status:                      aws.String("creating"),
			},
		},
		Error: raiseErr,
	}
}

func StubGetClusterSnapshot(snapshotIdentifier string, status string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeDBClusterSnapshots",
		Input: &rds.DescribeDBClusterSnapshotsInput{
			DBClusterSnapshotIdentifier: aws.String(snapshotIdentifier),
		},
		Output: &rds.DescribeDBClusterSnapshotsOutput{
			DBClusterSnapshots: []types.DBClusterSnapshot{
				{
					DBClusterSnapshotIdentifier: aws.String(snapshotIdentifier),
					Status:                      aws.String(status),
					DBClusterSnapshotArn:        aws.String("0000000000000000000000000000000000:cluster:test"),
					Engine:                      aws.String("test-engine"),
					EngineVersion:               aws.String("test-engine-version"),
					DBClusterIdentifier:         aws.String("test"),
					SnapshotCreateTime:          aws.Time(time.Now()),
				},
			},
		},
		Error: raiseErr,
	}
}

func StubCreateInstanceInCluster(instanceIdentifier string, clusterIdentifier string, engine string, instanceClass string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateDBInstance",
		Input: &rds.CreateDBInstanceInput{
			DBInstanceIdentifier: aws.String(instanceIdentifier),
			DBClusterIdentifier:  aws.String(clusterIdentifier),
			Engine:               aws.String(engine),
			DBInstanceClass:      aws.String(instanceClass),
		},
		Output: &rds.CreateDBInstanceOutput{
			DBInstance: &types.DBInstance{
				DBInstanceIdentifier: aws.String(instanceIdentifier),
				DBClusterIdentifier:  aws.String(clusterIdentifier),
				Engine:               aws.String(engine),
				DBInstanceClass:      aws.String(instanceClass),
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
					DBInstanceArn:        aws.String("0000000000000000000000000000000000:cluster:test"),
					Engine:               aws.String("test-engine"),
					EngineVersion:        aws.String("test-engine-version"),
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
