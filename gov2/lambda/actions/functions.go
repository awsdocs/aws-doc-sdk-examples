// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"log"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/lambda"
	"github.com/aws/aws-sdk-go-v2/service/lambda/types"
)

// snippet-start:[gov2.lambda.FunctionWrapper.complete]
// snippet-start:[gov2.lambda.FunctionWrapper.struct]

// FunctionWrapper encapsulates function actions used in the examples.
// It contains an AWS Lambda service client that is used to perform user actions.
type FunctionWrapper struct {
	LambdaClient *lambda.Client
}

// snippet-end:[gov2.lambda.FunctionWrapper.struct]
// snippet-start:[gov2.lambda.GetFunction]

// GetFunction gets data about the Lambda function specified by functionName.
func (wrapper FunctionWrapper) GetFunction(functionName string) types.State {
	var state types.State
	funcOutput, err := wrapper.LambdaClient.GetFunction(context.TODO(), &lambda.GetFunctionInput{
		FunctionName: aws.String(functionName),
	})
	if err != nil {
		log.Panicf("Couldn't get function %v. Here's why: %v\n", functionName, err)
	} else {
		state = funcOutput.Configuration.State
	}
	return state
}

// snippet-end:[gov2.lambda.GetFunction]

// snippet-start:[gov2.lambda.CreateFunction]

// CreateFunction creates a new Lambda function from code contained in the zipPackage
// buffer. The specified handlerName must match the name of the file and function
// contained in the uploaded code. The role specified by iamRoleArn is assumed by
// Lambda and grants specific permissions.
// When the function already exists, types.StateActive is returned.
// When the function is created, a lambda.FunctionActiveV2Waiter is used to wait until the
// function is active.
func (wrapper FunctionWrapper) CreateFunction(functionName string, handlerName string,
	iamRoleArn *string, zipPackage *bytes.Buffer) types.State {
	var state types.State
	_, err := wrapper.LambdaClient.CreateFunction(context.TODO(), &lambda.CreateFunctionInput{
		Code:         &types.FunctionCode{ZipFile: zipPackage.Bytes()},
		FunctionName: aws.String(functionName),
		Role:         iamRoleArn,
		Handler:      aws.String(handlerName),
		Publish:      true,
		Runtime:      types.RuntimePython38,
	})
	if err != nil {
		var resConflict *types.ResourceConflictException
		if errors.As(err, &resConflict) {
			log.Printf("Function %v already exists.\n", functionName)
			state = types.StateActive
		} else {
			log.Panicf("Couldn't create function %v. Here's why: %v\n", functionName, err)
		}
	} else {
		waiter := lambda.NewFunctionActiveV2Waiter(wrapper.LambdaClient)
		funcOutput, err := waiter.WaitForOutput(context.TODO(), &lambda.GetFunctionInput{
			FunctionName: aws.String(functionName)}, 1*time.Minute)
		if err != nil {
			log.Panicf("Couldn't wait for function %v to be active. Here's why: %v\n", functionName, err)
		} else {
			state = funcOutput.Configuration.State
		}
	}
	return state
}

// snippet-end:[gov2.lambda.CreateFunction]

// snippet-start:[gov2.lambda.UpdateFunctionCode]

// UpdateFunctionCode updates the code for the Lambda function specified by functionName.
// The existing code for the Lambda function is entirely replaced by the code in the
// zipPackage buffer. After the update action is called, a lambda.FunctionUpdatedV2Waiter
// is used to wait until the update is successful.
func (wrapper FunctionWrapper) UpdateFunctionCode(functionName string, zipPackage *bytes.Buffer) types.State {
	var state types.State
	_, err := wrapper.LambdaClient.UpdateFunctionCode(context.TODO(), &lambda.UpdateFunctionCodeInput{
		FunctionName: aws.String(functionName), ZipFile: zipPackage.Bytes(),
	})
	if err != nil {
		log.Panicf("Couldn't update code for function %v. Here's why: %v\n", functionName, err)
	} else {
		waiter := lambda.NewFunctionUpdatedV2Waiter(wrapper.LambdaClient)
		funcOutput, err := waiter.WaitForOutput(context.TODO(), &lambda.GetFunctionInput{
			FunctionName: aws.String(functionName)}, 1*time.Minute)
		if err != nil {
			log.Panicf("Couldn't wait for function %v to be active. Here's why: %v\n", functionName, err)
		} else {
			state = funcOutput.Configuration.State
		}
	}
	return state
}

// snippet-end:[gov2.lambda.UpdateFunctionCode]

// snippet-start:[gov2.lambda.UpdateFunctionConfiguration]

// UpdateFunctionConfiguration updates a map of environment variables configured for
// the Lambda function specified by functionName.
func (wrapper FunctionWrapper) UpdateFunctionConfiguration(functionName string, envVars map[string]string) {
	_, err := wrapper.LambdaClient.UpdateFunctionConfiguration(context.TODO(), &lambda.UpdateFunctionConfigurationInput{
		FunctionName: aws.String(functionName),
		Environment:  &types.Environment{Variables: envVars},
	})
	if err != nil {
		log.Panicf("Couldn't update configuration for %v. Here's why: %v", functionName, err)
	}
}

// snippet-end:[gov2.lambda.UpdateFunctionConfiguration]

// snippet-start:[gov2.lambda.ListFunctions]

// ListFunctions lists up to maxItems functions for the account. This function uses a
// lambda.ListFunctionsPaginator to paginate the results.
func (wrapper FunctionWrapper) ListFunctions(maxItems int) []types.FunctionConfiguration {
	var functions []types.FunctionConfiguration
	paginator := lambda.NewListFunctionsPaginator(wrapper.LambdaClient, &lambda.ListFunctionsInput{
		MaxItems: aws.Int32(int32(maxItems)),
	})
	for paginator.HasMorePages() && len(functions) < maxItems {
		pageOutput, err := paginator.NextPage(context.TODO())
		if err != nil {
			log.Panicf("Couldn't list functions for your account. Here's why: %v\n", err)
		}
		functions = append(functions, pageOutput.Functions...)
	}
	return functions
}

// snippet-end:[gov2.lambda.ListFunctions]

// snippet-start:[gov2.lambda.DeleteFunction]

// DeleteFunction deletes the Lambda function specified by functionName.
func (wrapper FunctionWrapper) DeleteFunction(functionName string) {
	_, err := wrapper.LambdaClient.DeleteFunction(context.TODO(), &lambda.DeleteFunctionInput{
		FunctionName: aws.String(functionName),
	})
	if err != nil {
		log.Panicf("Couldn't delete function %v. Here's why: %v\n", functionName, err)
	}
}

// snippet-end:[gov2.lambda.DeleteFunction]

// snippet-start:[gov2.lambda.Invoke]

// Invoke invokes the Lambda function specified by functionName, passing the parameters
// as a JSON payload. When getLog is true, types.LogTypeTail is specified, which tells
// Lambda to include the last few log lines in the returned result.
func (wrapper FunctionWrapper) Invoke(functionName string, parameters any, getLog bool) *lambda.InvokeOutput {
	logType := types.LogTypeNone
	if getLog {
		logType = types.LogTypeTail
	}
	payload, err := json.Marshal(parameters)
	if err != nil {
		log.Panicf("Couldn't marshal parameters to JSON. Here's why %v\n", err)
	}
	invokeOutput, err := wrapper.LambdaClient.Invoke(context.TODO(), &lambda.InvokeInput{
		FunctionName: aws.String(functionName),
		LogType:      logType,
		Payload:      payload,
	})
	if err != nil {
		log.Panicf("Couldn't invoke function %v. Here's why: %v\n", functionName, err)
	}
	return invokeOutput
}

// snippet-end:[gov2.lambda.Invoke]

// snippet-start:[gov2.lambda.Handler_SerDe]

// IncrementParameters is used to serialize parameters to the increment Lambda handler.
type IncrementParameters struct {
	Action string `json:"action"`
	Number int    `json:"number"`
}

// CalculatorParameters is used to serialize parameters to the calculator Lambda handler.
type CalculatorParameters struct {
	Action string `json:"action"`
	X      int    `json:"x"`
	Y      int    `json:"y"`
}

// LambdaResultInt is used to deserialize an int result from a Lambda handler.
type LambdaResultInt struct {
	Result int `json:"result"`
}

// LambdaResultFloat is used to deserialize a float32 result from a Lambda handler.
type LambdaResultFloat struct {
	Result float32 `json:"result"`
}

// snippet-end:[gov2.lambda.Handler_SerDe]
// snippet-end:[gov2.lambda.FunctionWrapper.complete]
