// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubBatchWriteItem(tableName string, writeReqs []types.WriteRequest, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "BatchWriteItem",
		Input:         &dynamodb.BatchWriteItemInput{RequestItems: map[string][]types.WriteRequest{tableName: writeReqs}},
		Output:        &dynamodb.BatchWriteItemOutput{},
		Error:         raiseErr,
	}
}

func StubPutItem(tableName string, item map[string]types.AttributeValue, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "PutItem",
		Input:         &dynamodb.PutItemInput{TableName: aws.String(tableName), Item: item},
		Output:        &dynamodb.PutItemOutput{},
		Error:         raiseErr,
	}
}

func StubScan(tableName string, users []map[string]types.AttributeValue, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "Scan",
		Input:         &dynamodb.ScanInput{TableName: aws.String(tableName)},
		Output:        &dynamodb.ScanOutput{Items: users},
		Error:         raiseErr,
	}
}
