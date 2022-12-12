// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the actions not used in scenarios.

package actions

import (
	"errors"
	"log"
	"testing"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/aws/smithy-go"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/iam/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

const maxThings = 10
const policyArn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
const roleName = "Admin"
const linkService = "batch.amazonaws.com"

// CallNonScenarioActions calls the actions not used in scenarios to verify that they
// run as expected. This script can be run as a unit test using stubs so that AWS
// is not called, or as an integration test to verify it works when calling live AWS services.
func CallNonScenarioActions(sdkConfig aws.Config, ) {
	defer func() {
		if r := recover(); r != nil {
			log.Println(r)
		}
	}()

	iamClient := iam.NewFromConfig(sdkConfig)
	accountWrapper := AccountWrapper{IamClient: iamClient}
	groupWrapper := GroupWrapper{IamClient: iamClient}
	policyWrapper := PolicyWrapper{IamClient: iamClient}
	roleWrapper:= RoleWrapper{IamClient: iamClient}
	userWrapper:= UserWrapper{IamClient: iamClient}

	pwPolicy, err := accountWrapper.GetAccountPasswordPolicy()
	if err != nil {
		var apiError smithy.APIError
		if errors.As(err, &apiError) {
			switch apiError.(type) {
			case *types.NoSuchEntityException:
				log.Printf("No password policy is expected if none has been set.")
			default:
				panic(err)
			}
		}
	} else {
		log.Printf("Policy min length: %v\n", pwPolicy.MinimumPasswordLength)
	}

	providers, err := accountWrapper.ListSAMLProviders()
	if err != nil {panic(err)}
	for _, prov := range providers {
		log.Println(*prov.Arn)
	}

	groups, err := groupWrapper.ListGroups(maxThings)
	if err != nil {panic(err)}
	for _, group := range groups {
		log.Println(*group.GroupName)
	}

	policies, err := policyWrapper.ListPolicies(maxThings)
	if err != nil {panic(err)}
	for _, pol := range policies {
		log.Println(*pol.PolicyName)
	}

	policy, err := policyWrapper.GetPolicy(policyArn)
	if err != nil {panic(err)}
	log.Println(*policy.Arn)

	roles, err := roleWrapper.ListRoles(maxThings)
	if err != nil {panic(err)}
	for _, r := range roles {
		log.Println(*r.RoleName)
	}

	role, err := roleWrapper.GetRole(roleName)
	if err != nil {panic(err)}
	log.Println(*role.RoleName)

	svcRole, err := roleWrapper.CreateServiceLinkedRole("batch.amazonaws.com", "test")
	if err != nil {panic(err)}
	log.Println(*svcRole.RoleName)

	err = roleWrapper.DeleteServiceLinkedRole(*svcRole.RoleName)
	if err != nil {panic(err)}

	rPols, err := roleWrapper.ListRolePolicies(roleName)
	if err != nil {panic(err)}
	for _, rPol := range rPols {
		log.Println(rPol)
	}

	users, err := userWrapper.ListUsers(maxThings)
	if err != nil {panic(err)}
	for _, user := range users {
		log.Println(user.UserName)
	}

	log.Printf("Thanks for watching!")
}

// TestCallNonScenarioActions runs the scenario multiple times. The first time, it runs with no
// errors. In subsequent runs, it specifies that each stub in the sequence should
// raise an error and verifies the results.
func TestCallNonScenarioActions(t *testing.T) {
	scenTest := NonScenarioActionsTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// NonScenarioActionsTest encapsulates data for a scenario test.
type NonScenarioActionsTest struct {}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *NonScenarioActionsTest) SetupDataAndStubs() []testtools.Stub {
	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubGetAccountPasswordPolicy(nil))
	stubList = append(stubList, stubs.StubListSAMLProviders([]string{"test-provider-arn"}, nil))
	stubList = append(stubList, stubs.StubListGroups(maxThings, []string{"test-group"}, nil))
	stubList = append(stubList, stubs.StubListPolicies(maxThings, []string{"test-policy"}, nil))
	stubList = append(stubList, stubs.StubGetPolicy(policyArn, nil))
	stubList = append(stubList, stubs.StubListRoles(maxThings, []string{"test-role"}, nil))
	stubList = append(stubList, stubs.StubGetRole(roleName, nil))
	stubList = append(stubList, stubs.StubCreateServiceLinkedRole(linkService, "test", roleName, nil))
	stubList = append(stubList, stubs.StubDeleteServiceLinkedRole(roleName, nil))
	stubList = append(stubList, stubs.StubListRolePolicies(roleName, []string{"test-policy"}, nil))
	stubList = append(stubList, stubs.StubListUsers(maxThings, []string{"test-user"}, nil))

	return stubList
}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenTest *NonScenarioActionsTest) RunSubTest(stubber *testtools.AwsmStubber) {
	CallNonScenarioActions(*stubber.SdkConfig)
}

func (scenTest *NonScenarioActionsTest) Cleanup() {}
