// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package stubs defines service action stubs that are used by the SSM unit tests.
//
// Each stub expects specific data as input and returns specific data as an output.
// If an error is specified, it is raised instead of returning data.
package stubs

import (
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ssm"
	"github.com/aws/aws-sdk-go-v2/service/ssm/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// StubPutParameter builds a stub for the PutParameter action.
func StubPutParameter(name, value string, paramType types.ParameterType, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "PutParameter",
		Input: &ssm.PutParameterInput{
			Name:      aws.String(name),
			Value:     aws.String(value),
			Type:      paramType,
			Overwrite: aws.Bool(true),
		},
		Output: &ssm.PutParameterOutput{
			Version: 1,
		},
		Error: raiseErr,
	}
}

// StubGetParameter builds a stub for the GetParameter action.
func StubGetParameter(name, value string, paramType types.ParameterType, withDecryption bool, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetParameter",
		Input: &ssm.GetParameterInput{
			Name:           aws.String(name),
			WithDecryption: aws.Bool(withDecryption),
		},
		Output: &ssm.GetParameterOutput{
			Parameter: &types.Parameter{
				Name:  aws.String(name),
				Value: aws.String(value),
				Type:  paramType,
			},
		},
		Error: raiseErr,
	}
}

// StubGetParameters builds a stub for the GetParameters action.
func StubGetParameters(names []string, parameters []types.Parameter, invalidParams []string, withDecryption bool, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetParameters",
		Input: &ssm.GetParametersInput{
			Names:          names,
			WithDecryption: aws.Bool(withDecryption),
		},
		Output: &ssm.GetParametersOutput{
			Parameters:        parameters,
			InvalidParameters: invalidParams,
		},
		Error: raiseErr,
	}
}

// StubGetParametersByPath builds a stub for the GetParametersByPath action.
func StubGetParametersByPath(path string, parameters []types.Parameter, recursive, withDecryption bool, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetParametersByPath",
		Input: &ssm.GetParametersByPathInput{
			Path:           aws.String(path),
			Recursive:      aws.Bool(recursive),
			WithDecryption: aws.Bool(withDecryption),
			MaxResults:     aws.Int32(10),
		},
		Output: &ssm.GetParametersByPathOutput{
			Parameters: parameters,
		},
		Error: raiseErr,
	}
}

// StubDeleteParameter builds a stub for the DeleteParameter action.
func StubDeleteParameter(name string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteParameter",
		Input: &ssm.DeleteParameterInput{
			Name: aws.String(name),
		},
		Output: &ssm.DeleteParameterOutput{},
		Error:  raiseErr,
	}
}

// StubListDocuments builds a stub for the ListDocuments action.
func StubListDocuments(documents []types.DocumentIdentifier, maxResults int32, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ListDocuments",
		Input: &ssm.ListDocumentsInput{
			MaxResults: aws.Int32(maxResults),
		},
		Output: &ssm.ListDocumentsOutput{
			DocumentIdentifiers: documents,
		},
		Error: raiseErr,
	}
}

// StubDescribeDocument builds a stub for the DescribeDocument action.
func StubDescribeDocument(name string, document *types.DocumentDescription, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeDocument",
		Input: &ssm.DescribeDocumentInput{
			Name: aws.String(name),
		},
		Output: &ssm.DescribeDocumentOutput{
			Document: document,
		},
		Error: raiseErr,
	}
}

// StubSendCommand builds a stub for the SendCommand action.
func StubSendCommand(documentName string, instanceIds []string, parameters map[string][]string, command *types.Command, raiseErr *testtools.StubError) testtools.Stub {
	input := &ssm.SendCommandInput{
		DocumentName: aws.String(documentName),
		InstanceIds:  instanceIds,
	}
	if parameters != nil {
		input.Parameters = parameters
	}

	return testtools.Stub{
		OperationName: "SendCommand",
		Input:         input,
		Output: &ssm.SendCommandOutput{
			Command: command,
		},
		Error: raiseErr,
	}
}

// StubGetCommandInvocation builds a stub for the GetCommandInvocation action.
func StubGetCommandInvocation(commandId, instanceId string, status types.CommandInvocationStatus, stdout, stderr string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetCommandInvocation",
		Input: &ssm.GetCommandInvocationInput{
			CommandId:  aws.String(commandId),
			InstanceId: aws.String(instanceId),
		},
		Output: &ssm.GetCommandInvocationOutput{
			CommandId:             aws.String(commandId),
			InstanceId:            aws.String(instanceId),
			Status:                status,
			StandardOutputContent: aws.String(stdout),
			StandardErrorContent:  aws.String(stderr),
		},
		Error: raiseErr,
	}
}

// StubListCommands builds a stub for the ListCommands action.
func StubListCommands(instanceId string, commands []types.Command, maxResults int32, raiseErr *testtools.StubError) testtools.Stub {
	input := &ssm.ListCommandsInput{
		MaxResults: aws.Int32(maxResults),
	}
	if instanceId != "" {
		input.InstanceId = aws.String(instanceId)
	}

	return testtools.Stub{
		OperationName: "ListCommands",
		Input:         input,
		Output: &ssm.ListCommandsOutput{
			Commands: commands,
		},
		Error: raiseErr,
	}
}

// StubListCommandInvocations builds a stub for the ListCommandInvocations action.
func StubListCommandInvocations(commandId string, invocations []types.CommandInvocation, maxResults int32, raiseErr *testtools.StubError) testtools.Stub {
	input := &ssm.ListCommandInvocationsInput{
		MaxResults: aws.Int32(maxResults),
	}
	if commandId != "" {
		input.CommandId = aws.String(commandId)
	}

	return testtools.Stub{
		OperationName: "ListCommandInvocations",
		Input:         input,
		Output: &ssm.ListCommandInvocationsOutput{
			CommandInvocations: invocations,
		},
		Error: raiseErr,
	}
}

// StubCancelCommand builds a stub for the CancelCommand action.
func StubCancelCommand(commandId string, instanceIds []string, raiseErr *testtools.StubError) testtools.Stub {
	input := &ssm.CancelCommandInput{
		CommandId: aws.String(commandId),
	}
	if len(instanceIds) > 0 {
		input.InstanceIds = instanceIds
	}

	return testtools.Stub{
		OperationName: "CancelCommand",
		Input:         input,
		Output:        &ssm.CancelCommandOutput{},
		Error:         raiseErr,
	}
}

// BuildDocumentIdentifier creates a DocumentIdentifier for testing.
func BuildDocumentIdentifier(name string, docFormat types.DocumentFormat) types.DocumentIdentifier {
	return types.DocumentIdentifier{
		Name:           aws.String(name),
		DocumentFormat: docFormat,
		CreatedDate:    aws.Time(time.Now()),
	}
}

// BuildParameter creates a Parameter for testing.
func BuildParameter(name, value string, paramType types.ParameterType) types.Parameter {
	return types.Parameter{
		Name:  aws.String(name),
		Value: aws.String(value),
		Type:  paramType,
	}
}

// BuildCommand creates a Command for testing.
func BuildCommand(commandId, documentName string, instanceIds []string, status types.CommandStatus) types.Command {
	return types.Command{
		CommandId:         aws.String(commandId),
		DocumentName:      aws.String(documentName),
		InstanceIds:       instanceIds,
		Status:            status,
		RequestedDateTime: aws.Time(time.Now()),
	}
}
