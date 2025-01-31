// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

import (
	"context"
	"testing"
	"user_pools_and_lambda_triggers/stubs"

	"github.com/aws/aws-sdk-go-v2/aws"
	cogidptypes "github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider/types"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// TestRunMigrateUserScenario runs the scenario multiple times. The first time,
// it runs with no errors. In subsequent runs, it specifies that each stub in the sequence
// should raise an error and verifies the results.
func TestRunMigrateUserScenario(t *testing.T) {
	scenTest := MigrateUserScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// MigrateUserScenarioTest encapsulates data for a scenario test.
type MigrateUserScenarioTest struct {
	Answers   []string
	stackName string
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *MigrateUserScenarioTest) SetupDataAndStubs() []testtools.Stub {
	scenTest.stackName = "test-stack"
	stackOutputs := map[string]string{
		"MigrateUserFunction":    "test-migrate-user",
		"MigrateUserFunctionArn": "test-migrate-user-arn",
		"UserPoolArn":            "test-user-pool-arn",
		"UserPoolClientId":       "test-client-id",
		"UserPoolId":             "test-user-pool-id",
		"TableName":              "test-table-name",
	}
	migrateUserConfig := cogidptypes.LambdaConfigType{UserMigration: aws.String(stackOutputs["MigrateUserFunctionArn"])}
	userName := "test_user_2"
	userEmail := "test_email_2@example.com"
	ddbUser := map[string]types.AttributeValue{
		"UserName":  &types.AttributeValueMemberS{Value: userName},
		"UserEmail": &types.AttributeValueMemberS{Value: userEmail},
	}
	logStreamName := "test-log-stream-name"
	logMsgs := []string{"test-message-1", "test-message-2", "test-message-3"}
	authToken := "test-auth-token"
	code := "123456"
	password := "test-password"

	scenTest.Answers = []string{
		userName, userEmail, // SignInUser
		"y", code, password, password, // ResetPassword
		"y", // Cleanup
	}

	var stubList []testtools.Stub

	// GetOutputs
	stubList = append(stubList, stubs.StubDescribeStacks(scenTest.stackName, stackOutputs, nil))

	// AddMigrateUserTrigger
	stubList = append(stubList, stubs.StubDescribeUserPool(stackOutputs["UserPoolId"], cogidptypes.LambdaConfigType{}, nil))
	stubList = append(stubList, stubs.StubUpdateUserPool(stackOutputs["UserPoolId"], migrateUserConfig, nil))

	// SignInUser
	stubList = append(stubList, stubs.StubPutItem(stackOutputs["TableName"], ddbUser, nil))
	stubList = append(stubList, stubs.StubInitiateAuth(stackOutputs["UserPoolClientId"], userName, "_", authToken,
		&testtools.StubError{Err: &cogidptypes.PasswordResetRequiredException{Message: aws.String("test-password-reset")}, ContinueAfter: true}))

	// ListRecentLogEvents
	stubList = append(stubList, stubs.StubDescribeLogStreams(stackOutputs["MigrateUserFunction"], logStreamName, nil))
	stubList = append(stubList, stubs.StubGetLogEvents(stackOutputs["MigrateUserFunction"], logStreamName, 10, logMsgs, nil))

	// ResetPassword
	stubList = append(stubList, stubs.StubForgotPassword(stackOutputs["UserPoolClientId"], userName, userEmail, nil))
	stubList = append(stubList, stubs.StubConfirmForgotPassword(stackOutputs["UserPoolClientId"], code, userName, password,
		&testtools.StubError{
			Err: &cogidptypes.InvalidPasswordException{
				Message: aws.String("test-invalid-password"),
			},
			ContinueAfter: true,
		}))
	stubList = append(stubList, stubs.StubConfirmForgotPassword(stackOutputs["UserPoolClientId"], code, userName, password, nil))
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
func (scenTest *MigrateUserScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	helper := NewScenarioHelper(*stubber.SdkConfig, &mockQuestioner)
	helper.isTestRun = true
	scenario := NewMigrateUser(*stubber.SdkConfig, &mockQuestioner, &helper)
	scenario.Run(context.Background(), scenTest.stackName)
}

func (scenTest *MigrateUserScenarioTest) Cleanup() {}
