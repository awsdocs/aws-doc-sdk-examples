// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the assume role scenario.

package scenarios

import (
	"testing"

	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/aws/smithy-go"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/iam/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// TestRunAssumeRoleScenario runs the scenario multiple times. The first time, it runs with no
// errors. In subsequent runs, it specifies that each stub in the sequence should
// raise an error and verifies the results.
func TestRunAssumeRoleScenario(t *testing.T) {
	scenTest := AssumeRoleScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// AssumeRoleScenarioTest encapsulates data for a scenario test.
type AssumeRoleScenarioTest struct {
	Answers   []string
	helper testHelper
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *AssumeRoleScenarioTest) SetupDataAndStubs() []testtools.Stub {
	userName := "test-user"
	userArn := "test-user-arn"
	keyId := "test-key-id"
	keySecret := "test-key-secret"
	token := "test-token"
	roleName := "test-role"
	roleArn := roleName + "-arn"
	listBucketsPolicy := "test-list-buckets-policy"
	listBucketsPolicyArn := listBucketsPolicy + "-arn"
	userPolicy := "test-user-policy"


	scenTest.helper = testHelper{names: []string{roleName, listBucketsPolicy, userPolicy}}
	scenTest.Answers = []string{userName, "", "", "", "y"}

	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubGetUser(userName,
		&testtools.StubError{Err: &types.NoSuchEntityException{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubCreateUser(userName, userArn, nil))
	stubList = append(stubList, stubs.StubCreateAccessKeyPair(userName, keyId, keySecret, nil))
	stubList = append(stubList, stubs.StubCreateRole(roleName, roleArn, nil))
	stubList = append(stubList, stubs.StubCreatePolicy(listBucketsPolicy, listBucketsPolicyArn, nil))
	stubList = append(stubList, stubs.StubAttachRolePolicy(roleName, listBucketsPolicyArn, nil))
	stubList = append(stubList, stubs.StubCreateUserPolicy(userName, userPolicy, nil))
	stubList = append(stubList, stubs.StubListBuckets([]string{},
		&testtools.StubError{Err: &smithy.GenericAPIError{Code: "AccessDenied"}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubAssumeRole(roleArn, "AssumeRoleExampleSession",
		900, keyId, keySecret, token, nil))
	stubList = append(stubList, stubs.StubListBuckets([]string{"test-bucket-1", "test-bucket-2"}, nil))
	stubList = append(stubList, stubs.StubListAttachedRolePolicies(roleName,
		map[string]string{listBucketsPolicy: listBucketsPolicyArn}, nil))
	stubList = append(stubList, stubs.StubDetachRolePolicy(roleName, listBucketsPolicyArn, nil))
	stubList = append(stubList, stubs.StubDeletePolicy(listBucketsPolicyArn, nil))
	stubList = append(stubList, stubs.StubDeleteRole(roleName, nil))
	stubList = append(stubList, stubs.StubListUserPolicies(userName, []string{userPolicy}, nil))
	stubList = append(stubList, stubs.StubDeleteUserPolicy(userName, userPolicy, nil))
	stubList = append(stubList, stubs.StubListAccessKeys(userName, []string{keyId}, nil))
	stubList = append(stubList, stubs.StubDeleteAccessKey(userName, keyId, nil))
	stubList = append(stubList, stubs.StubDeleteUser(userName, nil))

	return stubList
}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenTest *AssumeRoleScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	scenario := NewAssumeRoleScenario(*stubber.SdkConfig, &mockQuestioner, &scenTest.helper)
	scenario.isTestRun = true
	scenario.Run()
}

func (scenTest *AssumeRoleScenarioTest) Cleanup() {}

// testHelper implements IScenarioHelper for unit testing.
type testHelper struct {
	names []string
}

// GetName returns the next name from a predefined stack of names.
func (helper *testHelper) GetName() string {
	name := helper.names[0]
	helper.names = helper.names[1:]
	return name
}

// Pause does nothing during unit testing.
func (helper testHelper) Pause(secs int) {}
