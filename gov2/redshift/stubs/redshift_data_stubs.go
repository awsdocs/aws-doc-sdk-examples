// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/redshiftdata"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubListDatabases(clusterId string, databaseName string, userName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ListDatabases",
		Input: &redshiftdata.ListDatabasesInput{
			ClusterIdentifier: aws.String(clusterId),
			Database:          aws.String(databaseName),
			DbUser:            aws.String(userName),
		},
		Output: &redshiftdata.ListDatabasesOutput{
			Databases: []string{databaseName},
		},
		Error: raiseErr,
	}
}

func StubExecuteStatement(clusterId string, databaseName string, userName, sql string, resultId string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ExecuteStatement",
		Input: &redshiftdata.ExecuteStatementInput{
			ClusterIdentifier: aws.String(clusterId),
			Database:          aws.String(databaseName),
			DbUser:            aws.String(userName),
			Sql:               aws.String(sql),
		},
		Output: &redshiftdata.ExecuteStatementOutput{
			Id: aws.String(resultId),
		},
		IgnoreFields: []string{"ClientToken", "Sql"},
		Error:        raiseErr,
	}
}

func StubBatchExecuteStatement(clusterId string, databaseName string, userName string, sqlStatements []string, resultId string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "BatchExecuteStatement",
		Input: &redshiftdata.BatchExecuteStatementInput{
			ClusterIdentifier: aws.String(clusterId),
			Database:          aws.String(databaseName),
			DbUser:            aws.String(userName),
			Sqls:              sqlStatements,
		},
		Output: &redshiftdata.BatchExecuteStatementOutput{
			Id: aws.String(resultId),
		},
		IgnoreFields: []string{"Sqls", "ClientToken"},
		Error:        raiseErr,
	}
}

func StubDescribeStatement(statementId string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeStatement",
		Input: &redshiftdata.DescribeStatementInput{
			Id: aws.String(statementId),
		},
		Output: &redshiftdata.DescribeStatementOutput{
			Status: "FINISHED",
		},
		IgnoreFields: []string{"Id"},
		Error:        raiseErr,
	}
}

func StubGetStatementResult(raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetStatementResult",
		Input: &redshiftdata.GetStatementResultInput{
			Id: aws.String("test-result-id"),
		},
		Output: &redshiftdata.GetStatementResultOutput{},
		Error:  raiseErr,
	}
}
