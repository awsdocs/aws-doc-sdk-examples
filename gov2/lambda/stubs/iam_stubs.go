// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing IAM actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubGetRole(roleName string, roleArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetRole",
		Input:         &iam.GetRoleInput{RoleName: aws.String(roleName)},
		Output: &iam.GetRoleOutput{Role: &types.Role{
			Arn:      aws.String(roleArn),
			RoleName: aws.String(roleName),
		}},
		Error: raiseErr,
	}
}

func StubCreateRole(roleName string, trustPol string, roleArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateRole",
		Input: &iam.CreateRoleInput{
			RoleName: aws.String(roleName), AssumeRolePolicyDocument: aws.String(trustPol)},
		Output: &iam.CreateRoleOutput{Role: &types.Role{
			Arn:      aws.String(roleArn),
			RoleName: aws.String(roleName),
		}},
		Error: raiseErr,
	}
}

func StubAttachRolePolicy(roleName string, policyArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "AttachRolePolicy",
		Input:         &iam.AttachRolePolicyInput{RoleName: aws.String(roleName), PolicyArn: aws.String(policyArn)},
		Output:        &iam.AttachRolePolicyOutput{},
		Error:         raiseErr,
	}
}

func StubListAttachedRolePolicies(roleName string, policyArns []string, raiseErr *testtools.StubError) testtools.Stub {
	var policies []types.AttachedPolicy
	for _, polArn := range policyArns {
		policies = append(policies, types.AttachedPolicy{PolicyArn: aws.String(polArn)})
	}
	return testtools.Stub{
		OperationName: "ListAttachedRolePolicies",
		Input:         &iam.ListAttachedRolePoliciesInput{RoleName: aws.String(roleName)},
		Output:        &iam.ListAttachedRolePoliciesOutput{AttachedPolicies: policies},
		Error:         raiseErr,
	}
}

func StubDetachRolePolicy(roleName string, policyArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DetachRolePolicy",
		Input:         &iam.DetachRolePolicyInput{RoleName: aws.String(roleName), PolicyArn: aws.String(policyArn)},
		Output:        &iam.DetachRolePolicyOutput{},
		Error:         raiseErr,
	}
}

func StubDeleteRole(roleName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteRole",
		Input:         &iam.DeleteRoleInput{RoleName: aws.String(roleName)},
		Output:        &iam.DeleteRoleOutput{},
		Error:         raiseErr,
	}
}
