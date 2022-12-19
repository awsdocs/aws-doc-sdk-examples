// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the role actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubListRoles(maxRoles int32, roleNames []string, raiseErr *testtools.StubError) testtools.Stub {
	var roles []types.Role
	for _, name := range roleNames {
		roles = append(roles, types.Role{RoleName: aws.String(name)})
	}
	return testtools.Stub{
		OperationName: "ListRoles",
		Input:         &iam.ListRolesInput{MaxItems: aws.Int32(maxRoles)},
		Output:        &iam.ListRolesOutput{Roles: roles},
		Error:         raiseErr,
	}
}

func StubCreateRole(roleName string, roleArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateRole",
		Input:         &iam.CreateRoleInput{RoleName: aws.String(roleName)},
		Output:        &iam.CreateRoleOutput{Role: &types.Role{
			RoleName: aws.String(roleName),
			Arn: aws.String(roleArn),
		}},
		IgnoreFields:  []string{"AssumeRolePolicyDocument"},
		Error:         raiseErr,
	}
}

func StubGetRole(roleName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetRole",
		Input:         &iam.GetRoleInput{RoleName: aws.String(roleName)},
		Output:        &iam.GetRoleOutput{Role: &types.Role{RoleName: aws.String(roleName)}},
		Error:         raiseErr,
	}
}

func StubCreateServiceLinkedRole(serviceName string, description string, roleName string,
		raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateServiceLinkedRole",
		Input:         &iam.CreateServiceLinkedRoleInput{
			AWSServiceName: aws.String(serviceName),
			Description:    aws.String(description),
		},
		Output:        &iam.CreateServiceLinkedRoleOutput{Role: &types.Role{RoleName: aws.String(roleName)}},
		Error:         raiseErr,
	}
}

func StubDeleteServiceLinkedRole(roleName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteServiceLinkedRole",
		Input:         &iam.DeleteServiceLinkedRoleInput{RoleName: aws.String(roleName)},
		Output:        &iam.DeleteServiceLinkedRoleOutput{},
		Error:         raiseErr,
	}
}

func StubAttachRolePolicy(roleName string, policyArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "AttachRolePolicy",
		Input:         &iam.AttachRolePolicyInput{
			PolicyArn: aws.String(policyArn),
			RoleName:  aws.String(roleName),
		},
		Output:        &iam.AttachRolePolicyOutput{},
		Error:         raiseErr,
	}
}

func StubListAttachedRolePolicies(roleName string, policyNames map[string]string, raiseErr *testtools.StubError) testtools.Stub {
	var policies []types.AttachedPolicy
	for name, arn := range policyNames {
		policies = append(policies, types.AttachedPolicy{
			PolicyName: aws.String(name),
			PolicyArn: aws.String(arn),
		})
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
		Input:         &iam.DetachRolePolicyInput{
			PolicyArn: aws.String(policyArn),
			RoleName:  aws.String(roleName),
		},
		Output:        &iam.DetachRolePolicyOutput{},
		Error:         raiseErr,
	}
}

func StubListRolePolicies(roleName string, policyNames []string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ListRolePolicies",
		Input:         &iam.ListRolePoliciesInput{RoleName: aws.String(roleName)},
		Output:        &iam.ListRolePoliciesOutput{PolicyNames: policyNames},
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
