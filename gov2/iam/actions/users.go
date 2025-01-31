// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.iam.UserWrapper.complete]
// snippet-start:[gov2.iam.UserWrapper.struct]

import (
	"context"
	"encoding/json"
	"errors"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/aws/smithy-go"
)

// UserWrapper encapsulates user actions used in the examples.
// It contains an IAM service client that is used to perform user actions.
type UserWrapper struct {
	IamClient *iam.Client
}

// snippet-end:[gov2.iam.UserWrapper.struct]

// snippet-start:[gov2.iam.ListUsers]

// ListUsers gets up to maxUsers number of users.
func (wrapper UserWrapper) ListUsers(ctx context.Context, maxUsers int32) ([]types.User, error) {
	var users []types.User
	result, err := wrapper.IamClient.ListUsers(ctx, &iam.ListUsersInput{
		MaxItems: aws.Int32(maxUsers),
	})
	if err != nil {
		log.Printf("Couldn't list users. Here's why: %v\n", err)
	} else {
		users = result.Users
	}
	return users, err
}

// snippet-end:[gov2.iam.ListUsers]

// snippet-start:[gov2.iam.GetUser]

// GetUser gets data about a user.
func (wrapper UserWrapper) GetUser(ctx context.Context, userName string) (*types.User, error) {
	var user *types.User
	result, err := wrapper.IamClient.GetUser(ctx, &iam.GetUserInput{
		UserName: aws.String(userName),
	})
	if err != nil {
		var apiError smithy.APIError
		if errors.As(err, &apiError) {
			switch apiError.(type) {
			case *types.NoSuchEntityException:
				log.Printf("User %v does not exist.\n", userName)
				err = nil
			default:
				log.Printf("Couldn't get user %v. Here's why: %v\n", userName, err)
			}
		}
	} else {
		user = result.User
	}
	return user, err
}

// snippet-end:[gov2.iam.GetUser]

// snippet-start:[gov2.iam.CreateUser]

// CreateUser creates a new user with the specified name.
func (wrapper UserWrapper) CreateUser(ctx context.Context, userName string) (*types.User, error) {
	var user *types.User
	result, err := wrapper.IamClient.CreateUser(ctx, &iam.CreateUserInput{
		UserName: aws.String(userName),
	})
	if err != nil {
		log.Printf("Couldn't create user %v. Here's why: %v\n", userName, err)
	} else {
		user = result.User
	}
	return user, err
}

// snippet-end:[gov2.iam.CreateUser]

// snippet-start:[gov2.iam.PutUserPolicy]

// CreateUserPolicy adds an inline policy to a user. This example creates a policy that
// grants a list of actions on a specified role.
// PolicyDocument shows how to work with a policy document as a data structure and
// serialize it to JSON by using Go's JSON marshaler.
func (wrapper UserWrapper) CreateUserPolicy(ctx context.Context, userName string, policyName string, actions []string,
	roleArn string) error {
	policyDoc := PolicyDocument{
		Version: "2012-10-17",
		Statement: []PolicyStatement{{
			Effect:   "Allow",
			Action:   actions,
			Resource: aws.String(roleArn),
		}},
	}
	policyBytes, err := json.Marshal(policyDoc)
	if err != nil {
		log.Printf("Couldn't create policy document for %v. Here's why: %v\n", roleArn, err)
		return err
	}
	_, err = wrapper.IamClient.PutUserPolicy(ctx, &iam.PutUserPolicyInput{
		PolicyDocument: aws.String(string(policyBytes)),
		PolicyName:     aws.String(policyName),
		UserName:       aws.String(userName),
	})
	if err != nil {
		log.Printf("Couldn't create policy for user %v. Here's why: %v\n", userName, err)
	}
	return err
}

// snippet-end:[gov2.iam.PutUserPolicy]

// snippet-start:[gov2.iam.ListUserPolicies]

// ListUserPolicies lists the inline policies for the specified user.
func (wrapper UserWrapper) ListUserPolicies(ctx context.Context, userName string) ([]string, error) {
	var policies []string
	result, err := wrapper.IamClient.ListUserPolicies(ctx, &iam.ListUserPoliciesInput{
		UserName: aws.String(userName),
	})
	if err != nil {
		log.Printf("Couldn't list policies for user %v. Here's why: %v\n", userName, err)
	} else {
		policies = result.PolicyNames
	}
	return policies, err
}

// snippet-end:[gov2.iam.ListUserPolicies]

// snippet-start:[gov2.iam.DeleteUserPolicy]

// DeleteUserPolicy deletes an inline policy from a user.
func (wrapper UserWrapper) DeleteUserPolicy(ctx context.Context, userName string, policyName string) error {
	_, err := wrapper.IamClient.DeleteUserPolicy(ctx, &iam.DeleteUserPolicyInput{
		PolicyName: aws.String(policyName),
		UserName:   aws.String(userName),
	})
	if err != nil {
		log.Printf("Couldn't delete policy from user %v. Here's why: %v\n", userName, err)
	}
	return err
}

// snippet-end:[gov2.iam.DeleteUserPolicy]

// snippet-start:[gov2.iam.DeleteUser]

// DeleteUser deletes a user.
func (wrapper UserWrapper) DeleteUser(ctx context.Context, userName string) error {
	_, err := wrapper.IamClient.DeleteUser(ctx, &iam.DeleteUserInput{
		UserName: aws.String(userName),
	})
	if err != nil {
		log.Printf("Couldn't delete user %v. Here's why: %v\n", userName, err)
	}
	return err
}

// snippet-end:[gov2.iam.DeleteUser]

// snippet-start:[gov2.iam.CreateAccessKey]

// CreateAccessKeyPair creates an access key for a user. The returned access key contains
// the ID and secret credentials needed to use the key.
func (wrapper UserWrapper) CreateAccessKeyPair(ctx context.Context, userName string) (*types.AccessKey, error) {
	var key *types.AccessKey
	result, err := wrapper.IamClient.CreateAccessKey(ctx, &iam.CreateAccessKeyInput{
		UserName: aws.String(userName)})
	if err != nil {
		log.Printf("Couldn't create access key pair for user %v. Here's why: %v\n", userName, err)
	} else {
		key = result.AccessKey
	}
	return key, err
}

// snippet-end:[gov2.iam.CreateAccessKey]

// snippet-start:[gov2.iam.DeleteAccessKey]

// DeleteAccessKey deletes an access key from a user.
func (wrapper UserWrapper) DeleteAccessKey(ctx context.Context, userName string, keyId string) error {
	_, err := wrapper.IamClient.DeleteAccessKey(ctx, &iam.DeleteAccessKeyInput{
		AccessKeyId: aws.String(keyId),
		UserName:    aws.String(userName),
	})
	if err != nil {
		log.Printf("Couldn't delete access key %v. Here's why: %v\n", keyId, err)
	}
	return err
}

// snippet-end:[gov2.iam.DeleteAccessKey]

// snippet-start:[gov2.iam.ListAccessKeys]

// ListAccessKeys lists the access keys for the specified user.
func (wrapper UserWrapper) ListAccessKeys(ctx context.Context, userName string) ([]types.AccessKeyMetadata, error) {
	var keys []types.AccessKeyMetadata
	result, err := wrapper.IamClient.ListAccessKeys(ctx, &iam.ListAccessKeysInput{
		UserName: aws.String(userName),
	})
	if err != nil {
		log.Printf("Couldn't list access keys for user %v. Here's why: %v\n", userName, err)
	} else {
		keys = result.AccessKeyMetadata
	}
	return keys, err
}

// snippet-end:[gov2.iam.ListAccessKeys]
// snippet-end:[gov2.iam.UserWrapper.complete]
