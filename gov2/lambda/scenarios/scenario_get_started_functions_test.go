// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"bytes"
	"encoding/json"
	"fmt"
	"testing"

	iamtypes "github.com/aws/aws-sdk-go-v2/service/iam/types"
	lambdatypes "github.com/aws/aws-sdk-go-v2/service/lambda/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/lambda/actions"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/lambda/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// TestRunGetStartedFunctionsScenario runs the scenario multiple times. The first time,
// it runs with no errors. In subsequent runs, it specifies that each stub in the sequence
// should raise an error and verifies the results.
func TestRunGetStartedFunctionsScenario(t *testing.T) {
	scenTest := GetStartedFunctionsScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// GetStartedFunctionsScenarioTest encapsulates data for a scenario test.
type GetStartedFunctionsScenarioTest struct {
	Answers []string
	helper  testHelper
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *GetStartedFunctionsScenarioTest) SetupDataAndStubs() []testtools.Stub {
	roleName := "test-role"
	roleArn := "test-role-arn"
	trustPolicy := PolicyDocument{
		Version: "2012-10-17",
		Statement: []PolicyStatement{{
			Effect:    "Allow",
			Principal: map[string]string{"Service": "lambda.amazonaws.com"},
			Action:    []string{"sts:AssumeRole"},
		}},
	}
	policyArn := "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
	funcName := "test-function"
	codeBytes := []byte("test code")
	incremIn := "5"
	incremPayload, _ := json.Marshal(actions.IncrementParameters{
		Action: "increment",
		Number: 5,
	})
	incremResult, _ := json.Marshal(actions.LambdaResultInt{Result: 6})
	opChoice := "0"
	x := "6"
	y := "7"
	calcPayload, _ := json.Marshal(actions.CalculatorParameters{
		Action: "plus",
		X:      6,
		Y:      7,
	})
	calcResult, _ := json.Marshal(actions.LambdaResultInt{Result: 13})
	maxFuncs := 5

	scenTest.helper = testHelper{packageBytes: codeBytes}
	scenTest.Answers = []string{
		roleName,                // GetOrCreateRole
		funcName,                // CreateFunction
		incremIn,                // InvokeIncrement
		"",                      // UpdateFunction
		opChoice, x, y, "", "n", // InvokeCalculator
		"5", // ListFunctions
		"y", // Cleanup
	}

	var stubList []testtools.Stub

	// GetOrCreateRole
	stubList = append(stubList, stubs.StubGetRole(roleName, roleArn,
		&testtools.StubError{Err: &iamtypes.NoSuchEntityException{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubCreateRole(roleName, trustPolicy.String(), roleArn, nil))
	stubList = append(stubList, stubs.StubAttachRolePolicy(roleName, policyArn, nil))

	// CreateFunction
	stubList = append(stubList, stubs.StubCreateFunction(funcName, codeBytes, roleArn,
		fmt.Sprintf("%v.lambda_handler", funcName), true, lambdatypes.RuntimePython38,
		lambdatypes.StateActive, nil))
	stubList = append(stubList, stubs.StubGetFunction(funcName, lambdatypes.StateActive,
		lambdatypes.LastUpdateStatusSuccessful, nil))

	// InvokeIncrement
	stubList = append(stubList, stubs.StubInvoke(funcName, lambdatypes.LogTypeNone, incremPayload,
		incremResult, "", nil))

	// UpdateFunction
	stubList = append(stubList, stubs.StubUpdateFunctionCode(funcName, codeBytes, lambdatypes.StatePending, nil))
	stubList = append(stubList, stubs.StubGetFunction(funcName, lambdatypes.StateActive,
		lambdatypes.LastUpdateStatusSuccessful, nil))
	stubList = append(stubList, stubs.StubUpdateFunctionConfiguration(funcName, map[string]string{"LOG_LEVEL": "DEBUG"}, nil))

	// InvokeCalculator
	stubList = append(stubList, stubs.StubInvoke(funcName, lambdatypes.LogTypeTail, calcPayload, calcResult,
		"test log result", nil))

	// ListFunctions
	stubList = append(stubList, stubs.StubListFunctions(maxFuncs, []string{"function-1", "function-2"}, nil))

	// Cleanup
	stubList = append(stubList, stubs.StubListAttachedRolePolicies(roleName, []string{"test-policy"}, nil))
	stubList = append(stubList, stubs.StubDetachRolePolicy(roleName, "test-policy", nil))
	stubList = append(stubList, stubs.StubDeleteRole(roleName, nil))
	stubList = append(stubList, stubs.StubDeleteFunction(funcName, nil))

	return stubList
}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenTest *GetStartedFunctionsScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	scenario := NewGetStartedFunctionsScenario(*stubber.SdkConfig, &mockQuestioner, &scenTest.helper)
	scenario.isTestRun = true
	scenario.Run()
}

func (scenTest *GetStartedFunctionsScenarioTest) Cleanup() {}

// testHelper implements IScenarioHelper for unit testing.
type testHelper struct {
	packageBytes []byte
}

// Pause does nothing during unit testing.
func (helper *testHelper) Pause(secs int) {}

func (helper *testHelper) CreateDeploymentPackage(sourceFile string, destinationFile string) *bytes.Buffer {
	buffer := bytes.Buffer{}
	buffer.Write(helper.packageBytes)
	return &buffer
}
