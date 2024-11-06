// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.cognito-identity-provider.CognitoActions.complete]
// snippet-start:[gov2.cognito-identity-provider.CognitoActions.struct]

import (
	"context"
	"errors"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider"
	"github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider/types"
)

type CognitoActions struct {
	CognitoClient *cognitoidentityprovider.Client
}

// snippet-end:[gov2.cognito-identity-provider.CognitoActions.struct]

// snippet-start:[gov2.cognito-identity-provider.UpdateUserPool]

// Trigger and TriggerInfo define typed data for updating an Amazon Cognito trigger.
type Trigger int

const (
	PreSignUp Trigger = iota
	UserMigration
	PostAuthentication
)

type TriggerInfo struct {
	Trigger    Trigger
	HandlerArn *string
}

// UpdateTriggers adds or removes Lambda triggers for a user pool. When a trigger is specified with a `nil` value,
// it is removed from the user pool.
func (actor CognitoActions) UpdateTriggers(ctx context.Context, userPoolId string, triggers ...TriggerInfo) error {
	output, err := actor.CognitoClient.DescribeUserPool(ctx, &cognitoidentityprovider.DescribeUserPoolInput{
		UserPoolId: aws.String(userPoolId),
	})
	if err != nil {
		log.Printf("Couldn't get info about user pool %v. Here's why: %v\n", userPoolId, err)
		return err
	}
	lambdaConfig := output.UserPool.LambdaConfig
	for _, trigger := range triggers {
		switch trigger.Trigger {
		case PreSignUp:
			lambdaConfig.PreSignUp = trigger.HandlerArn
		case UserMigration:
			lambdaConfig.UserMigration = trigger.HandlerArn
		case PostAuthentication:
			lambdaConfig.PostAuthentication = trigger.HandlerArn
		}
	}
	_, err = actor.CognitoClient.UpdateUserPool(ctx, &cognitoidentityprovider.UpdateUserPoolInput{
		UserPoolId:   aws.String(userPoolId),
		LambdaConfig: lambdaConfig,
	})
	if err != nil {
		log.Printf("Couldn't update user pool %v. Here's why: %v\n", userPoolId, err)
	}
	return err
}

// snippet-end:[gov2.cognito-identity-provider.UpdateUserPool]

// snippet-start:[gov2.cognito-identity-provider.SignUp]

// SignUp signs up a user with Amazon Cognito.
func (actor CognitoActions) SignUp(ctx context.Context, clientId string, userName string, password string, userEmail string) (bool, error) {
	confirmed := false
	output, err := actor.CognitoClient.SignUp(ctx, &cognitoidentityprovider.SignUpInput{
		ClientId: aws.String(clientId),
		Password: aws.String(password),
		Username: aws.String(userName),
		UserAttributes: []types.AttributeType{
			{Name: aws.String("email"), Value: aws.String(userEmail)},
		},
	})
	if err != nil {
		var invalidPassword *types.InvalidPasswordException
		if errors.As(err, &invalidPassword) {
			log.Println(*invalidPassword.Message)
		} else {
			log.Printf("Couldn't sign up user %v. Here's why: %v\n", userName, err)
		}
	} else {
		confirmed = output.UserConfirmed
	}
	return confirmed, err
}

// snippet-end:[gov2.cognito-identity-provider.SignUp]

// snippet-start:[gov2.cognito-identity-provider.InitiateAuth]

// SignIn signs in a user to Amazon Cognito using a username and password authentication flow.
func (actor CognitoActions) SignIn(ctx context.Context, clientId string, userName string, password string) (*types.AuthenticationResultType, error) {
	var authResult *types.AuthenticationResultType
	output, err := actor.CognitoClient.InitiateAuth(ctx, &cognitoidentityprovider.InitiateAuthInput{
		AuthFlow:       "USER_PASSWORD_AUTH",
		ClientId:       aws.String(clientId),
		AuthParameters: map[string]string{"USERNAME": userName, "PASSWORD": password},
	})
	if err != nil {
		var resetRequired *types.PasswordResetRequiredException
		if errors.As(err, &resetRequired) {
			log.Println(*resetRequired.Message)
		} else {
			log.Printf("Couldn't sign in user %v. Here's why: %v\n", userName, err)
		}
	} else {
		authResult = output.AuthenticationResult
	}
	return authResult, err
}

// snippet-end:[gov2.cognito-identity-provider.InitiateAuth]

// snippet-start:[gov2.cognito-identity-provider.ForgotPassword]

// ForgotPassword starts a password recovery flow for a user. This flow typically sends a confirmation code
// to the user's configured notification destination, such as email.
func (actor CognitoActions) ForgotPassword(ctx context.Context, clientId string, userName string) (*types.CodeDeliveryDetailsType, error) {
	output, err := actor.CognitoClient.ForgotPassword(ctx, &cognitoidentityprovider.ForgotPasswordInput{
		ClientId: aws.String(clientId),
		Username: aws.String(userName),
	})
	if err != nil {
		log.Printf("Couldn't start password reset for user '%v'. Here;s why: %v\n", userName, err)
	}
	return output.CodeDeliveryDetails, err
}

// snippet-end:[gov2.cognito-identity-provider.ForgotPassword]

// snippet-start:[gov2.cognito-identity-provider.ConfirmForgotPassword]

// ConfirmForgotPassword confirms a user with a confirmation code and a new password.
func (actor CognitoActions) ConfirmForgotPassword(ctx context.Context, clientId string, code string, userName string, password string) error {
	_, err := actor.CognitoClient.ConfirmForgotPassword(ctx, &cognitoidentityprovider.ConfirmForgotPasswordInput{
		ClientId:         aws.String(clientId),
		ConfirmationCode: aws.String(code),
		Password:         aws.String(password),
		Username:         aws.String(userName),
	})
	if err != nil {
		var invalidPassword *types.InvalidPasswordException
		if errors.As(err, &invalidPassword) {
			log.Println(*invalidPassword.Message)
		} else {
			log.Printf("Couldn't confirm user %v. Here's why: %v", userName, err)
		}
	}
	return err
}

// snippet-end:[gov2.cognito-identity-provider.ConfirmForgotPassword]

// snippet-start:[gov2.cognito-identity-provider.DeleteUser]

// DeleteUser removes a user from the user pool.
func (actor CognitoActions) DeleteUser(ctx context.Context, userAccessToken string) error {
	_, err := actor.CognitoClient.DeleteUser(ctx, &cognitoidentityprovider.DeleteUserInput{
		AccessToken: aws.String(userAccessToken),
	})
	if err != nil {
		log.Printf("Couldn't delete user. Here's why: %v\n", err)
	}
	return err
}

// snippet-end:[gov2.cognito-identity-provider.DeleteUser]

// snippet-start:[gov2.cognito-identity-provider.AdminCreateUser]

// AdminCreateUser uses administrator credentials to add a user to a user pool. This method leaves the user
// in a state that requires they enter a new password next time they sign in.
func (actor CognitoActions) AdminCreateUser(ctx context.Context, userPoolId string, userName string, userEmail string) error {
	_, err := actor.CognitoClient.AdminCreateUser(ctx, &cognitoidentityprovider.AdminCreateUserInput{
		UserPoolId:     aws.String(userPoolId),
		Username:       aws.String(userName),
		MessageAction:  types.MessageActionTypeSuppress,
		UserAttributes: []types.AttributeType{{Name: aws.String("email"), Value: aws.String(userEmail)}},
	})
	if err != nil {
		var userExists *types.UsernameExistsException
		if errors.As(err, &userExists) {
			log.Printf("User %v already exists in the user pool.", userName)
			err = nil
		} else {
			log.Printf("Couldn't create user %v. Here's why: %v\n", userName, err)
		}
	}
	return err
}

// snippet-end:[gov2.cognito-identity-provider.AdminCreateUser]

// snippet-start:[gov2.cognito-identity-provider.AdminSetUserPassword]

// AdminSetUserPassword uses administrator credentials to set a password for a user without requiring a
// temporary password.
func (actor CognitoActions) AdminSetUserPassword(ctx context.Context, userPoolId string, userName string, password string) error {
	_, err := actor.CognitoClient.AdminSetUserPassword(ctx, &cognitoidentityprovider.AdminSetUserPasswordInput{
		Password:   aws.String(password),
		UserPoolId: aws.String(userPoolId),
		Username:   aws.String(userName),
		Permanent:  true,
	})
	if err != nil {
		var invalidPassword *types.InvalidPasswordException
		if errors.As(err, &invalidPassword) {
			log.Println(*invalidPassword.Message)
		} else {
			log.Printf("Couldn't set password for user %v. Here's why: %v\n", userName, err)
		}
	}
	return err
}

// snippet-end:[gov2.cognito-identity-provider.AdminSetUserPassword]
// snippet-end:[gov2.cognito-identity-provider.CognitoActions.complete]
