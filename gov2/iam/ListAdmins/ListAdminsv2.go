// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.ListAdmins]
package main

import (
	"context"
	"flag"
	"fmt"
	"strings"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
)

// Determines whether user
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a METHODOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to METHOD.

func userPolicyHasAdmin(user *types.UserDetail, admin string) bool {
	for _, policy := range user.UserPolicyList {
		if *policy.PolicyName == admin {
			return true
		}
	}

	return false
}

// Documentation for func FUNCTION(c context.Context, api SERVICEMETHODAPI, input *SERVICE.METHODInput) (*SERVICE.METHODOutput, error)
// FUNCTION VERBs an Amazon/AWS SERVICE RESOURCE.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a METHODOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to METHOD.

func attachedUserPolicyHasAdmin(user *types.UserDetail, admin string) bool {
	for _, policy := range user.AttachedManagedPolicies {
		if *policy.PolicyName == admin {
			return true
		}
	}

	return false
}

// Documentation for func FUNCTION(c context.Context, api SERVICEMETHODAPI, input *SERVICE.METHODInput) (*SERVICE.METHODOutput, error)
// FUNCTION VERBs an Amazon/AWS SERVICE RESOURCE.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a METHODOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to METHOD.

func groupPolicyHasAdmin(c context.Context, client *iam.Client, group *types.Group, admin string) (bool, error) {
	input := &iam.ListGroupPoliciesInput{
		GroupName: group.GroupName,
	}

	result, err := client.ListGroupPolicies(c, input)
	if err != nil {
		return false, err
	}

	// Wade through policies
	for _, policyName := range result.PolicyNames {
		if *policyName == admin {
			return true, nil
		}
	}

	return false, nil
}

// Documentation for func FUNCTION(c context.Context, api SERVICEMETHODAPI, input *SERVICE.METHODInput) (*SERVICE.METHODOutput, error)
// FUNCTION VERBs an Amazon/AWS SERVICE RESOURCE.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a METHODOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to METHOD.

func attachedGroupPolicyHasAdmin(c context.Context, client *iam.Client, group *types.Group, admin string) (bool, error) {
	input := &iam.ListAttachedGroupPoliciesInput{
		GroupName: group.GroupName,
	}

	result, err := client.ListAttachedGroupPolicies(c, input)
	if err != nil {
		return false, err
	}

	for _, policy := range result.AttachedPolicies {
		if *policy.PolicyName == admin {
			return true, nil
		}
	}

	return false, nil
}

// Documentation for func FUNCTION(c context.Context, api SERVICEMETHODAPI, input *SERVICE.METHODInput) (*SERVICE.METHODOutput, error)
// FUNCTION VERBs an Amazon/AWS SERVICE RESOURCE.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a METHODOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to METHOD.

func usersGroupsHaveAdmin(c context.Context, client *iam.Client, user *types.UserDetail, admin string) (bool, error) {
	input := &iam.ListGroupsForUserInput{
		UserName: user.UserName,
	}

	result, err := client.ListGroupsForUser(c, input)
	if err != nil {
		return false, err
	}

	for _, group := range result.Groups {
		groupPolicyHasAdmin, err := groupPolicyHasAdmin(c, client, group, admin)
		if err != nil {
			return false, err
		}

		if groupPolicyHasAdmin {
			return true, nil
		}

		attachedGroupPolicyHasAdmin, err := attachedGroupPolicyHasAdmin(c, client, group, admin)
		if err != nil {
			return false, err
		}

		if attachedGroupPolicyHasAdmin {
			return true, nil
		}
	}

	return false, nil
}

// GetNumUsersAndAdmins determines how many users have administrator privileges.
// Inputs:
//     client is the IAM service client.
//     c is the context of the method call, which includes the AWS Region.
// Output:
//     If success, the list of users and admins, and nil
//     Otherwise, "", "" and an error
func GetNumUsersAndAdmins(c context.Context, client *iam.Client) (string, string, error) {
	users := ""
	admins := ""

	filters := make([]types.EntityType, 1)
	filters[0] = types.EntityTypeUser

	input := &iam.GetAccountAuthorizationDetailsInput{
		Filter: filters,
	}

	resp, err := client.GetAccountAuthorizationDetails(c, input)
	if err != nil {
		return "", "", err
	}

	// The policy name that indicates administrator access
	adminName := "AdministratorAccess"

	// Wade through resulting users
	for _, user := range resp.UserDetailList {
		isAdmin, err := isUserAdmin(c, client, user, adminName)
		if err != nil {
			return "", "", err
		}

		users += " " + *user.UserName

		if isAdmin {
			admins += " " + *user.UserName
		}
	}

	for *resp.IsTruncated {
		input := &iam.GetAccountAuthorizationDetailsInput{
			Filter: filters,
			Marker: resp.Marker,
		}

		resp, err = client.GetAccountAuthorizationDetails(c, input)
		if err != nil {
			return "", "", err
		}

		// Wade through resulting users
		for _, user := range resp.UserDetailList {
			isAdmin, err := isUserAdmin(c, client, user, adminName)
			if err != nil {
				return "", "", err
			}

			users += " " + *user.UserName

			if isAdmin {
				admins += " " + *user.UserName
			}
		}
	}

	return users, admins, nil
}

func isUserAdmin(c context.Context, client *iam.Client, user *types.UserDetail, admin string) (bool, error) {
	// Check policy, attached policy, and groups (policy and attached policy)
	policyHasAdmin := userPolicyHasAdmin(user, admin)
	if policyHasAdmin {
		return true, nil
	}

	attachedPolicyHasAdmin := attachedUserPolicyHasAdmin(user, admin)
	if attachedPolicyHasAdmin {
		return true, nil
	}

	userGroupsHaveAdmin, err := usersGroupsHaveAdmin(c, client, user, admin)
	if err != nil {
		return false, err
	}
	if userGroupsHaveAdmin {
		return true, nil
	}

	return false, nil
}

func main() {
	showDetails := flag.Bool("d", false, "Whether to print out names of users and admins")
	flag.Parse()

	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := iam.NewFromConfig(cfg)

	users, admins, err := GetNumUsersAndAdmins(context.Background(), client)
	if err != nil {
		fmt.Println("Got an error finding users who are admins:")
		fmt.Println(err)
		return
	}

	userList := strings.Split(users, " ")
	adminList := strings.Split(admins, " ")

	fmt.Println("")
	fmt.Println("Found", len(adminList)-1, "admin(s) out of", len(userList)-1, "user(s)")

	if *showDetails {
		fmt.Println("")
		fmt.Println("Users")
		for _, u := range userList {
			fmt.Println("  " + u)
		}

		fmt.Println("")
		fmt.Println("Admins")
		for _, a := range adminList {
			fmt.Println("  " + a)
		}
	}
}

// snippet-end:[iam.go-v2.ListAdmins]
