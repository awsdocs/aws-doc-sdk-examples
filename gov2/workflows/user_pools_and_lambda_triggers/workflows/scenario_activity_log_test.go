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

// TestRunActivityLogScenario runs the scenario multiple times. The first time,
// it runs with no errors. In subsequent runs, it specifies that each stub in the sequence
// should raise an error and verifies the results.
func TestRunActivityLogScenario(t *testing.T) {
	scenTest := ActivityLogScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// ActivityLogScenarioTest encapsulates data for a scenario test.
type ActivityLogScenarioTest struct {
	Answers   []string
	stackName string
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *ActivityLogScenarioTest) SetupDataAndStubs() []testtools.Stub {
	scenTest.stackName = "test-stack"
	stackOutputs := map[string]string{
		"ActivityLogFunction":    "test-activity-log",
		"ActivityLogFunctionArn": "test-activity-log-arn",
		"UserPoolArn":            "test-user-pool-arn",
		"UserPoolClientId":       "test-client-id",
		"UserPoolId":             "test-user-pool-id",
		"TableName":              "test-table-name",
	}
	activityLogConfig := cogidptypes.LambdaConfigType{PostAuthentication: aws.String(stackOutputs["ActivityLogFunctionArn"])}
	userCount := 3
	users := make([]map[string]dynamotypes.AttributeValue, userCount)
	for i := 0; i < userCount; i++ {
		users[i] = map[string]dynamotypes.AttributeValue{
			"UserName":  &dynamotypes.AttributeValueMemberS{Value: fmt.Sprintf("test_user_%v", i+1)},
			"UserEmail": &dynamotypes.AttributeValueMemberS{Value: fmt.Sprintf("test_email_%v@example.com", i+1)},
		}
	}
	usersWithLogin := make([]map[string]dynamotypes.AttributeValue, userCount)
	for i := 0; i < userCount; i++ {
		usersWithLogin[i] = map[string]dynamotypes.AttributeValue{
			"UserName":  &dynamotypes.AttributeValueMemberS{Value: fmt.Sprintf("test_user_%v", i+1)},
			"UserEmail": &dynamotypes.AttributeValueMemberS{Value: fmt.Sprintf("test_email_%v@example.com", i+1)},
			"LastLogin": &dynamotypes.AttributeValueMemberM{Value: map[string]dynamotypes.AttributeValue{
				"UserPoolId": &dynamotypes.AttributeValueMemberS{Value: stackOutputs["UserPoolId"]},
				"ClientId":   &dynamotypes.AttributeValueMemberS{Value: stackOutputs["UserPoolClientId"]},
				"Time":       &dynamotypes.AttributeValueMemberS{Value: "test-time"}}},
		}
	}
	userName := "test_user_1"
	userEmail := "test_email_1@example.com"
	popTableWriteReqs := make([]dynamotypes.WriteRequest, userCount)
	for i, user := range users {
		popTableWriteReqs[i] = dynamotypes.WriteRequest{PutRequest: &dynamotypes.PutRequest{Item: user}}
	}
	logStreamName := "test-log-stream-name"
	logMsgs := []string{"test-message-1", "test-message-2", "test-message-3"}
	authToken := "test-auth-token"
	password := "test-password"

	scenTest.Answers = []string{
		password, password, // AddUserToPool
		"",  // SignInUser
		"",  //GetKnownUserLastLogin
		"y", // Cleanup
	}

	var stubList []testtools.Stub

	// GetOutputs
	stubList = append(stubList, stubs.StubDescribeStacks(scenTest.stackName, stackOutputs, nil))

	// PopulateUserTable
	stubList = append(stubList, stubs.StubBatchWriteItem(stackOutputs["TableName"], popTableWriteReqs, nil))

	// AddUserToPool
	stubList = append(stubList, stubs.StubScan(stackOutputs["TableName"], users, nil))
	stubList = append(stubList, stubs.StubAdminCreateUser(stackOutputs["UserPoolId"], userName, userEmail, nil))
	stubList = append(stubList, stubs.StubAdminSetUserPassword(stackOutputs["UserPoolId"], userName, password,
		&testtools.StubError{
			Err: &cogidptypes.InvalidPasswordException{
				Message: aws.String("test-invalid-password"),
			},
			ContinueAfter: true,
		}))
	stubList = append(stubList, stubs.StubAdminSetUserPassword(stackOutputs["UserPoolId"], userName, password, nil))

	// AddActivityLogTrigger
	stubList = append(stubList, stubs.StubDescribeUserPool(stackOutputs["UserPoolId"], cogidptypes.LambdaConfigType{}, nil))
	stubList = append(stubList, stubs.StubUpdateUserPool(stackOutputs["UserPoolId"], activityLogConfig, nil))

	// SignInUser
	stubList = append(stubList, stubs.StubInitiateAuth(stackOutputs["UserPoolClientId"], userName, password, authToken, nil))

	// ListRecentLogEvents
	stubList = append(stubList, stubs.StubDescribeLogStreams(stackOutputs["ActivityLogFunction"], logStreamName, nil))
	stubList = append(stubList, stubs.StubGetLogEvents(stackOutputs["ActivityLogFunction"], logStreamName, 10, logMsgs, nil))

	// GetKnownUserLastLogin
	stubList = append(stubList, stubs.StubScan(stackOutputs["TableName"], usersWithLogin, nil))

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
func (scenTest *ActivityLogScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	helper := NewScenarioHelper(*stubber.SdkConfig, &mockQuestioner)
	helper.isTestRun = true
	scenario := NewActivityLog(*stubber.SdkConfig, &mockQuestioner, &helper)
	scenario.Run(context.Background(), scenTest.stackName)
}

func (scenTest *ActivityLogScenarioTest) Cleanup() {}
