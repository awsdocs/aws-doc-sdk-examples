// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider"
	"github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubDescribeUserPool(userPoolId string, lambdaConfig types.LambdaConfigType, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeUserPool",
		Input:         &cognitoidentityprovider.DescribeUserPoolInput{UserPoolId: aws.String(userPoolId)},
		Output: &cognitoidentityprovider.DescribeUserPoolOutput{
			UserPool: &types.UserPoolType{LambdaConfig: &lambdaConfig}},
		Error: raiseErr,
	}
}

func StubUpdateUserPool(userPoolId string, lambdaConfig types.LambdaConfigType, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "UpdateUserPool",
		Input: &cognitoidentityprovider.UpdateUserPoolInput{
			UserPoolId:   aws.String(userPoolId),
			LambdaConfig: &lambdaConfig,
		},
		Output: &cognitoidentityprovider.UpdateUserPoolOutput{},
		Error:  raiseErr,
	}
}

func StubSignUp(clientId string, userName string, password string, email string, confirmed bool, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "SignUp",
		Input: &cognitoidentityprovider.SignUpInput{
			ClientId:       aws.String(clientId),
			Username:       aws.String(userName),
			Password:       aws.String(password),
			UserAttributes: []types.AttributeType{{Name: aws.String("email"), Value: aws.String(email)}},
		},
		Output: &cognitoidentityprovider.SignUpOutput{UserConfirmed: confirmed},
		Error:  raiseErr,
	}
}

func StubInitiateAuth(clientId string, userName string, password string, authToken string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "InitiateAuth",
		Input: &cognitoidentityprovider.InitiateAuthInput{
			ClientId:       aws.String(clientId),
			AuthFlow:       "USER_PASSWORD_AUTH",
			AuthParameters: map[string]string{"USERNAME": userName, "PASSWORD": password},
		},
		Output: &cognitoidentityprovider.InitiateAuthOutput{AuthenticationResult: &types.AuthenticationResultType{
			AccessToken: aws.String(authToken),
		}},
		Error: raiseErr,
	}
}

func StubForgotPassword(clientId string, userName string, destination string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ForgotPassword",
		Input: &cognitoidentityprovider.ForgotPasswordInput{
			ClientId: aws.String(clientId), Username: aws.String(userName)},
		Output: &cognitoidentityprovider.ForgotPasswordOutput{
			CodeDeliveryDetails: &types.CodeDeliveryDetailsType{Destination: aws.String(destination)}},
		Error: raiseErr,
	}
}

func StubConfirmForgotPassword(clientId string, code string, userName string, password string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ConfirmForgotPassword",
		Input: &cognitoidentityprovider.ConfirmForgotPasswordInput{
			ClientId:         aws.String(clientId),
			ConfirmationCode: aws.String(code),
			Username:         aws.String(userName),
			Password:         aws.String(password),
		},
		Output: &cognitoidentityprovider.ConfirmForgotPasswordOutput{},
		Error:  raiseErr,
	}
}

func StubDeleteUser(authToken string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteUser",
		Input:         &cognitoidentityprovider.DeleteUserInput{AccessToken: aws.String(authToken)},
		Output:        &cognitoidentityprovider.DeleteUserOutput{},
		Error:         raiseErr,
	}
}

func StubAdminCreateUser(userPoolId string, userName string, userEmail string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "AdminCreateUser",
		Input: &cognitoidentityprovider.AdminCreateUserInput{
			UserPoolId:     aws.String(userPoolId),
			Username:       aws.String(userName),
			MessageAction:  types.MessageActionTypeSuppress,
			UserAttributes: []types.AttributeType{{Name: aws.String("email"), Value: aws.String(userEmail)}}},
		Output: &cognitoidentityprovider.AdminCreateUserOutput{},
		Error:  raiseErr,
	}
}

func StubAdminSetUserPassword(userPoolId string, userName string, password string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "AdminSetUserPassword",
		Input: &cognitoidentityprovider.AdminSetUserPasswordInput{
			Password:   aws.String(password),
			UserPoolId: aws.String(userPoolId),
			Username:   aws.String(userName),
			Permanent:  true,
		},
		Output: &cognitoidentityprovider.AdminSetUserPasswordOutput{},
		Error:  raiseErr,
	}
}
