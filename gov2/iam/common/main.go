//snippet-sourcedescription:[Common IAM actions with the AWS SDK for Go v2]
//snippet-keyword:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[02/22/2022]
//snippet-sourceauthor:[gangwere]
//snippet-start:[iam.go-v2.iam_basics]
package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
)

const (
	UserName = "bob"
	RoleName = "SandwichRole"
)

func main() {
	// main

	// Get the general configuration

	cfg, err := config.LoadDefaultConfig(context.Background())

	if err != nil {
		panic("Couldn't load a configuration")
	}

	service := iam.NewFromConfig(cfg)

	//snippet-start:[iam.go-v2.ListRoles]
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

	//snippet-end:[iam.go-v2.ListRoles]

	//snippet-start:[iam.go-v2.CreateRole]
	// CreateRole
	myRole, err := service.CreateRole(context.Background(), &iam.CreateRoleInput{
		RoleName:    aws.String(RoleName),
		Description: aws.String("My super awesome example role"),
	})

	if err != nil {
		panic("Couldn't create role: " + err.Error())
	}

	fmt.Printf("The new role's ARN is %v \n", myRole.Role.Arn)

	//snippet-end:[iam.go-v2.CreateRole]
	roleName := myRole.Role.RoleName

	//snippet-start:[iam.go-v2.GetRole]
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

	//snippet-end:[iam.go-v2.GetRole]

	//snippet-start:[iam.go-v2.AttachRolePolicy]
	// AttachRolePolicy

	_, err = service.AttachRolePolicy(context.Background(), &iam.AttachRolePolicyInput{
		PolicyArn: aws.String("arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"),
		RoleName:  roleName,
	})

	if err != nil {
		panic("Couldn't apply a policy to the role!")
	}
	//snippet-end:[iam.go-v2.AttachRolePolicy]
	//snippet-start:[iam.go-v2.ListRolePolicies]

	// ListRolePolicies

	rolePoliciesList, err := service.ListRolePolicies(context.Background(), &iam.ListRolePoliciesInput{
		RoleName: roleName,
	})

	if err != nil {
		panic("Couldn't list policies for role: " + err.Error())
	}

	for _, rolePolicy := range rolePoliciesList.PolicyNames {
		fmt.Printf("Policy ARN: %v", rolePolicy)
	}
	//snippet-end:[iam.go-v2.ListRolePolicies]
	//snippet-start:[iam.go-v2.ListAttachedRolePolicies]

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
	//snippet-end:[iam.go-v2.ListAttachedRolePolicies]

	//snippet-start:[iam.go-v2.CreateServiceLinkedRole]
	// CreateServiceLinkedRole

	createSlrResult, err := service.CreateServiceLinkedRole(context.Background(), &iam.CreateServiceLinkedRoleInput{
		AWSServiceName: aws.String("lex.amazonaws.com"),
		Description:    aws.String("SLR for Amazon Lex"),
	})

	if err != nil {
		panic("Couldn't create Service Linked Role")
	}

	fmt.Printf("Created Service Linked Role with ARN %v\n", createSlrResult.Role.Arn)

	//snippet-end:[iam.go-v2.CreateServiceLinkedRole]
	//snippet-start:[iam.go-v2.CreateUser]
	// CreateUser

	createUserResult, err := service.CreateUser(context.Background(), &iam.CreateUserInput{
		UserName: aws.String(UserName),
	})

	if err != nil {
		panic("Couldn't create user: " + err.Error())
	}

	fmt.Printf("Created user %v\n", createUserResult.User.Arn)
	//snippet-end:[iam.go-v2.CreateUser]

	//snippet-start:[iam.go-v2.ListUsers]
	// ListUsers

	userListResult, err := service.ListUsers(context.Background(), &iam.ListUsersInput{})
	if err != nil {
		panic("Couldn't list users: " + err.Error())
	}
	for _, userResult := range userListResult.Users {
		fmt.Printf("%v\t%v\n", userResult.UserName, userResult.UserName)
	}
	//snippet-end:[iam.go-v2.ListUsers]
	//snippet-start:[iam.go-v2.GetAccountPasswordPolicy]
	// GetAccountPasswordPolicy

	accountPasswordPolicy, err := service.GetAccountPasswordPolicy(context.Background(), &iam.GetAccountPasswordPolicyInput{})

	if err != nil {
		panic("Couldn't get account password policy! " + err.Error())
	}

	fmt.Println("Users can change password: ", accountPasswordPolicy.PasswordPolicy.AllowUsersToChangePassword)
	fmt.Println("Passwords expire: ", accountPasswordPolicy.PasswordPolicy.ExpirePasswords)
	fmt.Println("Minimum password length: ", accountPasswordPolicy.PasswordPolicy.MinimumPasswordLength)

	//snippet-end:[iam.go-v2.GetAccountPasswordPolicy]
	//snippet-start:[iam.go-v2.CreatePolicy]
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
	//snippet-end:[iam.go-v2.CreatePolicy]

	policyArn := createPolicyResult.Policy.Arn

	//snippet-start:[iam.go-v2.ListPolicies]
	// ListPolicies

	policyListResponse, err := service.ListPolicies(context.Background(), &iam.ListPoliciesInput{})

	if err != nil {
		panic("Couldn't get list of policies! " + err.Error())
	}

	for _, policy := range policyListResponse.Policies {
		fmt.Printf("%v\t%v\n%v\n", policy.PolicyName, policy.Arn, policy.Description)
	}

	//snippet-end:[iam.go-v2.ListPolicies]
	//snippet-start:[iam.go-v2.Getpolicy]
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

	//snippet-end:[iam.go-v2.Getpolicy]
	//snippet-start:[iam.go-v2.ListGroups]
	// ListGroups

	listGroupsResult, err := service.ListGroups(context.Background(), &iam.ListGroupsInput{})

	if err != nil {
		panic("Couldn't list groups! " + err.Error())
	}

	for _, group := range listGroupsResult.Groups {
		fmt.Printf("group %v - %v\n", group.GroupName, group.Arn)
	}
	//snippet-end:[iam.go-v2.ListGroups]

	//snippet-start:[iam.go-v2.ListSAMLProviders]
	// ListSAMLProviders

	samlProviderList, err := service.ListSAMLProviders(context.Background(), &iam.ListSAMLProvidersInput{})

	if err != nil {
		panic("Couldn't list saml providers: " + err.Error())
	}

	for _, provider := range samlProviderList.SAMLProviderList {
		fmt.Printf("%v %v -> %v", provider.Arn, provider.CreateDate, provider.ValidUntil)
	}

	//snippet-end:[iam.go-v2.ListSAMLProviders]
	//snippet-start:[iam.go-v2.DeleteUser]
	// DeleteUser

	_, err = service.DeleteUser(context.Background(), &iam.DeleteUserInput{
		UserName: aws.String(UserName),
	})

	if err != nil {
		panic("Couldn't delete user")
	}
	//snippet-end:[iam.go-v2.DeleteUser]

}

//snippet-end:[iam.go-v2.iam_basics]
