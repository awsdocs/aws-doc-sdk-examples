// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"encoding/json"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
)

// snippet-start:[gov2.iam.RoleWrapper.complete]
// snippet-start:[gov2.iam.RoleWrapper.struct]

// RoleWrapper encapsulates AWS Identity and Access Management (IAM) role actions
// used in the examples.
// It contains an IAM service client that is used to perform role actions.
type RoleWrapper struct {
	IamClient *iam.Client
}

// snippet-end:[gov2.iam.RoleWrapper.struct]

// snippet-start:[gov2.iam.ListRoles]

// ListRoles gets up to maxRoles roles.
func (wrapper RoleWrapper) ListRoles(maxRoles int32) ([]types.Role, error) {
	var roles []types.Role
	result, err := wrapper.IamClient.ListRoles(context.TODO(),
		&iam.ListRolesInput{MaxItems: aws.Int32(maxRoles)},
	)
	if err != nil {
		log.Printf("Couldn't list roles. Here's why: %v\n", err)
	} else {
		roles = result.Roles
	}
	return roles, err
}

// snippet-end:[gov2.iam.ListRoles]

// snippet-start:[gov2.iam.CreateRole]

// CreateRole creates a role that trusts a specified user. The trusted user can assume
// the role to acquire its permissions.
// PolicyDocument shows how to work with a policy document as a data structure and
// serialize it to JSON by using Go's JSON marshaler.
func (wrapper RoleWrapper) CreateRole(roleName string, trustedUserArn string) (*types.Role, error) {
	var role *types.Role
	trustPolicy := PolicyDocument{
		Version:   "2012-10-17",
		Statement: []PolicyStatement{{
			Effect: "Allow",
			Principal: map[string]string{"AWS": trustedUserArn},
			Action: []string{"sts:AssumeRole"},
		}},
	}
	policyBytes, err := json.Marshal(trustPolicy)
	if err != nil {
		log.Printf("Couldn't create trust policy for %v. Here's why: %v\n", trustedUserArn, err)
		return nil, err
	}
	result, err := wrapper.IamClient.CreateRole(context.TODO(), &iam.CreateRoleInput{
		AssumeRolePolicyDocument: aws.String(string(policyBytes)),
		RoleName:                 aws.String(roleName),
	})
	if err != nil {
		log.Printf("Couldn't create role %v. Here's why: %v\n", roleName, err)
	} else {
		role = result.Role
	}
	return role, err
}

// snippet-end:[gov2.iam.CreateRole]

// snippet-start:[gov2.iam.GetRole]

// GetRole gets data about a role.
func (wrapper RoleWrapper) GetRole(roleName string) (*types.Role, error) {
	var role *types.Role
	result, err := wrapper.IamClient.GetRole(context.TODO(),
		&iam.GetRoleInput{RoleName: aws.String(roleName)})
	if err != nil {
		log.Printf("Couldn't get role %v. Here's why: %v\n", roleName, err)
	} else {
		role = result.Role
	}
	return role, err
}

// snippet-end:[gov2.iam.GetRole]

// snippet-start:[gov2.iam.CreateServiceLinkedRole]

// CreateServiceLinkedRole creates a service-linked role that is owned by the specified service.
func (wrapper RoleWrapper) CreateServiceLinkedRole(serviceName string, description string) (*types.Role, error) {
	var role *types.Role
	result, err := wrapper.IamClient.CreateServiceLinkedRole(context.TODO(), &iam.CreateServiceLinkedRoleInput{
		AWSServiceName: aws.String(serviceName),
		Description:    aws.String(description),
	})
	if err != nil {
		log.Printf("Couldn't create service-linked role %v. Here's why: %v\n", serviceName, err)
	} else {
		role = result.Role
	}
	return role, err
}

// snippet-end:[gov2.iam.CreateServiceLinkedRole]

// snippet-start:[gov2.iam.DeleteServiceLinkedRole]

// DeleteServiceLinkedRole deletes a service-linked role.
func (wrapper RoleWrapper) DeleteServiceLinkedRole(roleName string) error {
	_, err := wrapper.IamClient.DeleteServiceLinkedRole(context.TODO(), &iam.DeleteServiceLinkedRoleInput{
		RoleName: aws.String(roleName)},
	)
	if err != nil {
		log.Printf("Couldn't delete service-linked role %v. Here's why: %v\n", roleName, err)
	}
	return err
}

// snippet-end:[gov2.iam.DeleteServiceLinkedRole]

// snippet-start:[gov2.iam.AttachRolePolicy]

// AttachRolePolicy attaches a policy to a role.
func (wrapper RoleWrapper) AttachRolePolicy(policyArn string, roleName string) error {
	_, err := wrapper.IamClient.AttachRolePolicy(context.TODO(), &iam.AttachRolePolicyInput{
		PolicyArn: aws.String(policyArn),
		RoleName:  aws.String(roleName),
	})
	if err != nil {
		log.Printf("Couldn't attach policy %v to role %v. Here's why: %v\n", policyArn, roleName, err)
	}
	return err
}

// snippet-end:[gov2.iam.AttachRolePolicy]

// snippet-start:[gov2.iam.ListAttachedRolePolicies]

// ListAttachedRolePolicies lists the policies that are attached to the specified role.
func (wrapper RoleWrapper) ListAttachedRolePolicies(roleName string) ([]types.AttachedPolicy, error) {
	var policies []types.AttachedPolicy
	result, err := wrapper.IamClient.ListAttachedRolePolicies(context.TODO(), &iam.ListAttachedRolePoliciesInput{
		RoleName: aws.String(roleName),
	})
	if err != nil {
		log.Printf("Couldn't list attached policies for role %v. Here's why: %v\n", roleName, err)
	} else {
		policies = result.AttachedPolicies
	}
	return policies, err
}

// snippet-end:[gov2.iam.ListAttachedRolePolicies]

// snippet-start:[gov2.iam.DetachRolePolicy]

// DetachRolePolicy detaches a policy from a role.
func (wrapper RoleWrapper) DetachRolePolicy(roleName string, policyArn string) error {
	_, err := wrapper.IamClient.DetachRolePolicy(context.TODO(), &iam.DetachRolePolicyInput{
		PolicyArn: aws.String(policyArn),
		RoleName:  aws.String(roleName),
	})
	if err != nil {
		log.Printf("Couldn't detach policy from role %v. Here's why: %v\n", roleName, err)
	}
	return err
}

// snippet-end:[gov2.iam.DetachRolePolicy]

// snippet-start:[gov2.iam.ListRolePolicies]

// ListRolePolicies lists the inline policies for a role.
func (wrapper RoleWrapper) ListRolePolicies(roleName string) ([]string, error) {
	var policies []string
	result, err := wrapper.IamClient.ListRolePolicies(context.TODO(), &iam.ListRolePoliciesInput{
		RoleName: aws.String(roleName),
	})
	if err != nil {
		log.Printf("Couldn't list policies for role %v. Here's why: %v\n", roleName, err)
	} else {
		policies = result.PolicyNames
	}
	return policies, err
}

// snippet-end:[gov2.iam.ListRolePolicies]

// snippet-start:[gov2.iam.DeleteRole]

// DeleteRole deletes a role. All attached policies must be detached before a
// role can be deleted.
func (wrapper RoleWrapper) DeleteRole(roleName string) error {
	_, err := wrapper.IamClient.DeleteRole(context.TODO(), &iam.DeleteRoleInput{
		RoleName: aws.String(roleName),
	})
	if err != nil {
		log.Printf("Couldn't delete role %v. Here's why: %v\n", roleName, err)
	}
	return err
}

// snippet-end:[gov2.iam.DeleteRole]

// snippet-end:[gov2.iam.RoleWrapper.complete]
