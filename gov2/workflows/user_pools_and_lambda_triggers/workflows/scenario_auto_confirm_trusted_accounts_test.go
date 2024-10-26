// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

import (
	"context"
	"fmt"
	"testing"
	"user_pools_and_lambda_triggers/stubs"

	"github.com/aws/aws-sdk-go-v2/aws"
	cogidptypes "github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider/types"
	dynamotypes "github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// TestRunAutoConfirmScenario runs the scenario multiple times. The first time,
// it runs with no errors. In subsequent runs, it specifies that each stub in the sequence
// should raise an error and verifies the results.
func TestRunAutoConfirmScenario(t *testing.T) {
	scenTest := AutoConfirmScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// AutoConfirmScenarioTest encapsulates data for a scenario test.
type AutoConfirmScenarioTest struct {
	Answers   []string
	stackName string
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *AutoConfirmScenarioTest) SetupDataAndStubs() []testtools.Stub {
	scenTest.stackName = "test-stack"
	stackOutputs := map[string]string{
		"AutoConfirmFunction":    "test-auto-confirm",
		"AutoConfirmFunctionArn": "test-auto-confirm-arn",
		"UserPoolArn":            "test-user-pool-arn",
		"UserPoolClientId":       "test-client-id",
		"UserPoolId":             "test-user-pool-id",
		"TableName":              "test-table-name",
	}
	userCount := 3
	users := make([]map[string]dynamotypes.AttributeValue, userCount)
	for i := 0; i < userCount; i++ {
		users[i] = map[string]dynamotypes.AttributeValue{
			"UserName":  &dynamotypes.AttributeValueMemberS{Value: fmt.Sprintf("test_user_%v", i+1)},
			"UserEmail": &dynamotypes.AttributeValueMemberS{Value: fmt.Sprintf("test_email_%v@example.com", i+1)},
		}
	}
	popTableWriteReqs := make([]dynamotypes.WriteRequest, userCount)
	for i, user := range users {
		popTableWriteReqs[i] = dynamotypes.WriteRequest{PutRequest: &dynamotypes.PutRequest{Item: user}}
	}
	preSignUpConfig := cogidptypes.LambdaConfigType{PreSignUp: aws.String(stackOutputs["AutoConfirmFunctionArn"])}
	userName := "test_user_1"
	userEmail := "test_email_1@example.com"
	password := "test-password"
	logStreamName := "test-log-stream-name"
	logMsgs := []string{"test-message-1", "test-message-2", "test-message-3"}
	authToken := "test-auth-token"

	scenTest.Answers = []string{
		"1", password, password, // SignUpUser
		"",  // SignInUser
		"y", // Cleanup
	}

	var stubList []testtools.Stub

	// GetOutputs
	stubList = append(stubList, stubs.StubDescribeStacks(scenTest.stackName, stackOutputs, nil))

	// PopulateUserTable
	stubList = append(stubList, stubs.StubBatchWriteItem(stackOutputs["TableName"], popTableWriteReqs, nil))

	// AddPreSignUpTrigger
	stubList = append(stubList, stubs.StubDescribeUserPool(stackOutputs["UserPoolId"], cogidptypes.LambdaConfigType{}, nil))
	stubList = append(stubList, stubs.StubUpdateUserPool(stackOutputs["UserPoolId"], preSignUpConfig, nil))

	// SignUpUser
	stubList = append(stubList, stubs.StubScan(stackOutputs["TableName"], users, nil))
	stubList = append(stubList, stubs.StubSignUp(stackOutputs["UserPoolClientId"], userName, password, userEmail, true,
		&testtools.StubError{
			Err: &cogidptypes.InvalidPasswordException{
				Message: aws.String("test-invalid-password"),
			},
			ContinueAfter: true,
		}))
	stubList = append(stubList, stubs.StubSignUp(stackOutputs["UserPoolClientId"], userName, password, userEmail, true, nil))

	// ListRecentLogEvents
	stubList = append(stubList, stubs.StubDescribeLogStreams(stackOutputs["AutoConfirmFunction"], logStreamName, nil))
	stubList = append(stubList, stubs.StubGetLogEvents(stackOutputs["AutoConfirmFunction"], logStreamName, 10, logMsgs, nil))

	// SignInUser
	stubList = append(stubList, stubs.StubInitiateAuth(stackOutputs["UserPoolClientId"], userName, password, authToken, nil))

	// Cleanup
	stubList = append(stubList, stubs.StubDeleteUser(authToken, &testtools.StubError{Err: nil, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubDescribeUserPool(stackOutputs["UserPoolId"], cogidptypes.LambdaConfigType{},
		&testtools.StubError{Err: nil, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubUpdateUserPool(stackOutputs["UserPoolId"], cogidptypes.LambdaConfigType{},
		&testtools.StubError{Err: nil, ContinueAfter: true}))

	return stubList
}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenTest *AutoConfirmScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	helper := NewScenarioHelper(*stubber.SdkConfig, &mockQuestioner)
	helper.isTestRun = true
	scenario := NewAutoConfirm(*stubber.SdkConfig, &mockQuestioner, &helper)
	scenario.Run(context.Background(), scenTest.stackName)
}

func (scenTest *AutoConfirmScenarioTest) Cleanup() {}
