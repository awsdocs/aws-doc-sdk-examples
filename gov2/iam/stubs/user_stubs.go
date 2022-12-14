// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the user actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubListUsers(maxUsers int32, userNames []string, raiseErr *testtools.StubError) testtools.Stub {
	var users []types.User
	for _, name := range userNames {
		users = append(users, types.User{UserName: aws.String(name)})
	}
	return testtools.Stub{
		OperationName: "ListUsers",
		Input:         &iam.ListUsersInput{MaxItems: aws.Int32(maxUsers)},
		Output:        &iam.ListUsersOutput{Users: users},
		Error:         raiseErr,
	}
}

func StubGetUser(userName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetUser",
		Input:         &iam.GetUserInput{UserName: aws.String(userName)},
		Output:        &iam.GetUserOutput{User: &types.User{UserName: aws.String(userName)}},
		Error:         raiseErr,
	}
}

func StubCreateUser(userName string, userArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateUser",
		Input:         &iam.CreateUserInput{UserName: aws.String(userName)},
		Output:        &iam.CreateUserOutput{User: &types.User{
			UserName: aws.String(userName),
			Arn: aws.String(userArn),
		}},
		Error:         raiseErr,
	}
}

func StubCreateUserPolicy(userName string, policyName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "PutUserPolicy",
		Input:         &iam.PutUserPolicyInput{
			PolicyName: aws.String(policyName),
			UserName: aws.String(userName),
		},
		Output:        &iam.PutUserPolicyOutput{},
		IgnoreFields:  []string{"PolicyDocument"},
		Error:         raiseErr,
	}
}

func StubListUserPolicies(userName string, policyNames []string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ListUserPolicies",
		Input:         &iam.ListUserPoliciesInput{UserName: aws.String(userName)},
		Output:        &iam.ListUserPoliciesOutput{PolicyNames: policyNames},
		Error:         raiseErr,
	}
}

func StubDeleteUserPolicy(userName string, policyName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteUserPolicy",
		Input:         &iam.DeleteUserPolicyInput{
			PolicyName: aws.String(policyName),
			UserName:   aws.String(userName),
		},
		Output:        &iam.DeleteUserPolicyOutput{},
		Error:         raiseErr,
	}
}

func StubDeleteUser(userName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteUser",
		Input:         &iam.DeleteUserInput{UserName: aws.String(userName)},
		Output:        &iam.DeleteUserOutput{},
		Error:         raiseErr,
	}
}

func StubCreateAccessKeyPair(userName string, keyId string, keySecret string,
		raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateAccessKey",
		Input:         &iam.CreateAccessKeyInput{UserName: aws.String(userName)},
		Output:        &iam.CreateAccessKeyOutput{AccessKey: &types.AccessKey{
			AccessKeyId:     aws.String(keyId),
			SecretAccessKey: aws.String(keySecret),
			UserName:        aws.String(userName),
		}},
		Error:         raiseErr,
	}
}

func StubDeleteAccessKey(userName, keyId string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteAccessKey",
		Input:         &iam.DeleteAccessKeyInput{
			AccessKeyId: aws.String(keyId),
			UserName:    aws.String(userName),
		},
		Output:        &iam.DeleteAccessKeyOutput{},
		Error:         raiseErr,
	}
}

func StubListAccessKeys(userName string, keyIds []string, raiseErr *testtools.StubError) testtools.Stub {
	var keys []types.AccessKeyMetadata
	for _, id := range keyIds {
		keys = append(keys, types.AccessKeyMetadata{
			AccessKeyId: aws.String(id),
			UserName:    aws.String(userName),
		})
	}
	return testtools.Stub{
		OperationName: "ListAccessKeys",
		Input:         &iam.ListAccessKeysInput{UserName: aws.String(userName)},
		Output:        &iam.ListAccessKeysOutput{AccessKeyMetadata: keys},
		Error:         raiseErr,
	}
}
