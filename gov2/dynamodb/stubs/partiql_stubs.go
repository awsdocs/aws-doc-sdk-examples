// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package stubs defines service action stubs that are used by both the action and
// scenario unit tests.
//
// Each stub expects specific data as input and returns specific data as an output.
// If an error is specified, it is raised by the stubber.
package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/picante-io/aws-doc-sdk-examples/gov2/testtools"
)

func StubExecuteStatement(
	statement string, params []interface{}, output interface{},
	raiseErr *testtools.StubError) testtools.Stub {
	var paramAttribs []types.AttributeValue
	var err error
	if params != nil {
		paramAttribs, err = attributevalue.MarshalList(params)
	}
	if err != nil {
		panic(err)
	}
	statementOutput := dynamodb.ExecuteStatementOutput{}
	if output != nil {
		outputAttribs, err := attributevalue.MarshalMap(output)
		if err != nil {
			panic(err)
		}
		statementOutput.Items = append(statementOutput.Items, outputAttribs)
	}
	return testtools.Stub{
		OperationName: "ExecuteStatement",
		Input:         &dynamodb.ExecuteStatementInput{Statement: aws.String(statement), Parameters: paramAttribs},
		Output:        &statementOutput,
		Error:         raiseErr,
	}
}

func StubBatchExecuteStatement(
	statements []string, paramList [][]interface{}, outputs []interface{},
	raiseErr *testtools.StubError) testtools.Stub {
	statementRequests := make([]types.BatchStatementRequest, len(statements))
	for index := range statements {
		paramAttribs, err := attributevalue.MarshalList(paramList[index])
		if err != nil {
			panic(err)
		}
		statementRequests[index] = types.BatchStatementRequest{
			Statement:  aws.String(statements[index]),
			Parameters: paramAttribs,
		}
	}
	statementOutput := dynamodb.BatchExecuteStatementOutput{}
	for _, output := range outputs {
		outputAttribs, err := attributevalue.MarshalMap(output)
		if err != nil {
			panic(err)
		}
		statementOutput.Responses = append(statementOutput.Responses, types.BatchStatementResponse{
			Item: outputAttribs,
		})
	}
	return testtools.Stub{
		OperationName: "BatchExecuteStatement",
		Input:         &dynamodb.BatchExecuteStatementInput{Statements: statementRequests},
		Output:        &statementOutput,
		Error:         raiseErr,
	}
}
