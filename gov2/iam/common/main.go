package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
)

func main() {
	// main

	// Get the general configuration

	cfg, err := config.LoadDefaultConfig(context.Background())

	if err != nil {
		panic("Couldn't load a configuration")
	}

	service := iam.NewFromConfig(cfg)

	// ListRoles

	roles, err := service.ListRoles(context.Background(), &iam.ListRolesInput{})

	if err != nil {
		panic("Could not list roles: " + err.Error())
	}

	for _, idxRole := range roles.Roles {

		fmt.Printf("%v\t%v\t%v\t%v\n",
			idxRole.RoleName,
			idxRole.Arn,
			idxRole.RoleId,
			idxRole.Description)
	}

	// CreateRole

	myRole, err := service.CreateRole(context.Background(), &iam.CreateRoleInput{
		RoleName:    aws.String("MySuperAwesomeRole"),
		Description: aws.String("My super awesome example role"),
	})

	if err != nil {
		panic("Couldn't create role: " + err.Error())
	}

	fmt.Printf("The new role's ARN is %v \n", myRole.Role.Arn)

	roleName := myRole.Role.RoleName

	// GetRole

	getRoleResult, err := service.GetRole(context.Background(), &iam.GetRoleInput{
		RoleName: roleName,
	})

	if err != nil {
		panic("Couldn't get role! " + err.Error())
	}

	fmt.Println("ARN: ", getRoleResult.Role.Arn)
	fmt.Println("Name: ", getRoleResult.Role.RoleName)
	fmt.Println("Created On: ", getRoleResult.Role.CreateDate)

	// AttachRolePolicy

	_, err = service.AttachRolePolicy(context.Background(), &iam.AttachRolePolicyInput{
		PolicyArn: aws.String("arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"),
		RoleName:  roleName,
	})

	if err != nil {
		panic("Couldn't apply a policy to the role!")
	}

	// ListRolePolicies

	rolePoliciesList, err := service.ListRolePolicies(context.Background(), &iam.ListRolePoliciesInput{
		RoleName: roleName,
	})

	for _, rolePolicy := range rolePoliciesList.PolicyNames {
		fmt.Printf("Policy ARN: %v", rolePolicy)
	}

	// ListAttachedRolePolicies

	attachedPoliciesList, err := service.ListAttachedRolePolicies(context.Background(), &iam.ListAttachedRolePoliciesInput{
		RoleName: roleName,
	})

	if err != nil {
		panic("Couldn't call ListAttachedRolePolicies: " + err.Error())
	}

	for _, attachedPolicy := range attachedPoliciesList.AttachedPolicies {
		fmt.Printf("attached policy: %v\n (%v) \n", attachedPolicy.PolicyArn, attachedPolicy.PolicyName)
	}

	// CreateServiceLinkedRole

	createSlrResult, err := service.CreateServiceLinkedRole(context.Background(), &iam.CreateServiceLinkedRoleInput{
		AWSServiceName: aws.String("lex.amazonaws.com"),
		Description:    aws.String("SLR for Amazon Lex"),
	})

	if err != nil {
		panic("Couldn't create Service Linked Role")
	}

	fmt.Printf("Created Service Linked Role with ARN %v\n", createSlrResult.Role.Arn)

	// CreateUser

	createUserResult, err := service.CreateUser(context.Background(), &iam.CreateUserInput{
		UserName: aws.String("dademurphy"),
	})

	if err != nil {
		panic("Couldn't create user: " + err.Error())
	}

	fmt.Printf("Created user %v\n", createUserResult.User.Arn)

	// ListUsers

	userListResult, err := service.ListUsers(context.Background(), &iam.ListUsersInput{})
	if err != nil {
		panic("Couldn't list users: " + err.Error())
	}
	for _, userResult := range userListResult.Users {
		fmt.Printf("%v\t%v\n", userResult.UserName, userResult.UserName)
	}
	// GetAccountPasswordPolicy

	accountPasswordPolicy, err := service.GetAccountPasswordPolicy(context.Background(), &iam.GetAccountPasswordPolicyInput{})

	if err != nil {
		panic("Couldn't get account password policy! " + err.Error())
	}

	fmt.Println("Users can change password: ", accountPasswordPolicy.PasswordPolicy.AllowUsersToChangePassword)
	fmt.Println("Passwords expire: ", accountPasswordPolicy.PasswordPolicy.ExpirePasswords)
	fmt.Println("Minimum password length: ", accountPasswordPolicy.PasswordPolicy.MinimumPasswordLength)

	// CreatePolicy

	policyDocument := `{
		"Version": "2012-10-17",
		"Statement": [
			{
				"Effect": "Allow",
				"Action": [
					"dynamodb:DeleteItem",
					"dynamodb:GetItem",
					"dynamodb:PutItem",
					"dynamodb:Query",
					"dynamodb:Scan",
					"dynamodb:UpdateItem"
				],
				"Resource": [
					"arn:aws:dynamodb:us-west-2:111222333444:table/mytable",
					"arn:aws:dynamodb:us-west-2:111222333444:table/mytable/*"
				]
			}
		]
	}`

	createPolicyResult, err := service.CreatePolicy(context.Background(), &iam.CreatePolicyInput{
		PolicyDocument: &policyDocument,
		PolicyName:     aws.String("mytable-policy"),
	})

	if err != nil {
		panic("Could't create policy!" + err.Error())
	}

	fmt.Print("Created a new policy: " + *createPolicyResult.Policy.Arn)

	policyArn := createPolicyResult.Policy.Arn

	// ListPolicies

	policyListResponse, err := service.ListPolicies(context.Background(), &iam.ListPoliciesInput{})

	if err != nil {
		panic("Couldn't get list of policies! " + err.Error())
	}

	for _, policy := range policyListResponse.Policies {
		fmt.Printf("%v\t%v\n%v\n", policy.PolicyName, policy.Arn, policy.Description)
	}

	// GetPolicy

	getPolicyResponse, err := service.GetPolicy(context.Background(), &iam.GetPolicyInput{
		PolicyArn: policyArn,
	})

	if err != nil {
		panic("Couldn't get policy from ARN: " + err.Error())
	}

	fmt.Printf("policy: %v, name %v\n---\n%v\n---\n",
		getPolicyResponse.Policy.Arn,
		getPolicyResponse.Policy.PolicyName,
		getPolicyResponse.Policy.Description)

	// ListGroups

	listGroupsResult, err := service.ListGroups(context.Background(), &iam.ListGroupsInput{})

	if err != nil {
		panic("Couldn't list groups! " + err.Error())
	}

	for _, group := range listGroupsResult.Groups {
		fmt.Printf("group %v - %v\n", group.GroupName, group.Arn)
	}

	// ListSAMLProviders

	samlProviderList, err := service.ListSAMLProviders(context.Background(), &iam.ListSAMLProvidersInput{})

	if err != nil {
		panic("Couldn't list saml providers: " + err.Error())
	}

	for _, provider := range samlProviderList.SAMLProviderList {
		fmt.Printf("%v %v -> %v", provider.Arn, provider.CreateDate, provider.ValidUntil)
	}

	// DeleteUser

	_, err = service.DeleteUser(context.Background(), &iam.DeleteUserInput{
		UserName: aws.String("dademurphy"),
	})

	if err != nil {
		panic("Couldn't delete user")
	}

}
