// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing function actions.

package stubs

import (
	"encoding/base64"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/lambda"
	"github.com/aws/aws-sdk-go-v2/service/lambda/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubGetFunction(funcName string, state types.State, updateStatus types.LastUpdateStatus,
	raiseErr *testtools.StubError) testtools.Stub {

	return testtools.Stub{
		OperationName: "GetFunction",
		Input:         &lambda.GetFunctionInput{FunctionName: aws.String(funcName)},
		Output: &lambda.GetFunctionOutput{Configuration: &types.FunctionConfiguration{
			FunctionName:     aws.String(funcName),
			State:            state,
			LastUpdateStatus: updateStatus,
		}},
		SkipErrorTest: true, // Because this action is used by a waiter, skip it for error testing.
		Error:         raiseErr,
	}
}

func StubCreateFunction(funcName string, zipBytes []byte, roleArn string, handler string,
	publish bool, runtime types.Runtime, state types.State, raiseErr *testtools.StubError) testtools.Stub {

	return testtools.Stub{
		OperationName: "CreateFunction",
		Input: &lambda.CreateFunctionInput{
			FunctionName: aws.String(funcName),
			Code:         &types.FunctionCode{ZipFile: zipBytes},
			Role:         aws.String(roleArn),
			Handler:      aws.String(handler),
			Publish:      publish,
			Runtime:      runtime,
		},
		Output: &lambda.CreateFunctionOutput{State: state},
		Error:  raiseErr,
	}
}

func StubUpdateFunctionCode(funcName string, zipBytes []byte, state types.State, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "UpdateFunctionCode",
		Input: &lambda.UpdateFunctionCodeInput{
			FunctionName: aws.String(funcName),
			ZipFile:      zipBytes,
		},
		Output: &lambda.UpdateFunctionCodeOutput{State: state},
		Error:  raiseErr,
	}
}

func StubUpdateFunctionConfiguration(funcName string, envVars map[string]string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "UpdateFunctionConfiguration",
		Input: &lambda.UpdateFunctionConfigurationInput{
			FunctionName: aws.String(funcName),
			Environment:  &types.Environment{Variables: envVars},
		},
		Output: &lambda.UpdateFunctionConfigurationOutput{},
		Error:  raiseErr,
	}
}

func StubListFunctions(maxItems int, funcNames []string, raiseErr *testtools.StubError) testtools.Stub {
	var functions []types.FunctionConfiguration
	for _, funcName := range funcNames {
		functions = append(functions, types.FunctionConfiguration{FunctionName: aws.String(funcName)})
	}
	return testtools.Stub{
		OperationName: "ListFunctions",
		Input:         &lambda.ListFunctionsInput{MaxItems: aws.Int32(int32(maxItems))},
		Output:        &lambda.ListFunctionsOutput{Functions: functions},
		Error:         raiseErr,
	}
}

func StubDeleteFunction(funcName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteFunction",
		Input:         &lambda.DeleteFunctionInput{FunctionName: aws.String(funcName)},
		Output:        &lambda.DeleteFunctionOutput{},
		Error:         raiseErr,
	}
}

func StubInvoke(funcName string, logType types.LogType, inputPayload []byte, resultPayload []byte, logResult string,
	raiseErr *testtools.StubError) testtools.Stub {

	if logResult != "" {
		logResult = base64.StdEncoding.EncodeToString([]byte(logResult))
	}
	return testtools.Stub{
		OperationName: "Invoke",
		Input: &lambda.InvokeInput{
			FunctionName: aws.String(funcName),
			LogType:      logType,
			Payload:      inputPayload,
		},
		Output: &lambda.InvokeOutput{Payload: resultPayload, LogResult: aws.String(logResult)},
		Error:  raiseErr,
	}
}
