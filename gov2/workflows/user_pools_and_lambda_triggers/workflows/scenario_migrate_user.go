// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

// snippet-start:[gov2.workflows.PoolsAndTriggers.MigrateUser]

import (
	"context"
	"errors"
	"fmt"
	"log"
	"strings"
	"user_pools_and_lambda_triggers/actions"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider"
	"github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// MigrateUser separates the steps of this scenario into individual functions so that
// they are simpler to read and understand.
type MigrateUser struct {
	helper       IScenarioHelper
	questioner   demotools.IQuestioner
	resources    Resources
	cognitoActor *actions.CognitoActions
}

// NewMigrateUser constructs a new migrate user runner.
func NewMigrateUser(sdkConfig aws.Config, questioner demotools.IQuestioner, helper IScenarioHelper) MigrateUser {
	scenario := MigrateUser{
		helper:       helper,
		questioner:   questioner,
		resources:    Resources{},
		cognitoActor: &actions.CognitoActions{CognitoClient: cognitoidentityprovider.NewFromConfig(sdkConfig)},
	}
	scenario.resources.init(scenario.cognitoActor, questioner)
	return scenario
}

// AddMigrateUserTrigger adds a Lambda handler as an invocation target for the MigrateUser trigger.
func (runner *MigrateUser) AddMigrateUserTrigger(ctx context.Context, userPoolId string, functionArn string) {
	log.Printf("Let's add a Lambda function to handle the MigrateUser trigger from Cognito.\n" +
		"This trigger happens when an unknown user signs in, and lets your function take action before Cognito\n" +
		"rejects the user.\n\n")
	err := runner.cognitoActor.UpdateTriggers(
		ctx, userPoolId,
		actions.TriggerInfo{Trigger: actions.UserMigration, HandlerArn: aws.String(functionArn)})
	if err != nil {
		panic(err)
	}
	log.Printf("Lambda function %v added to user pool %v to handle the MigrateUser trigger.\n",
		functionArn, userPoolId)

	log.Println(strings.Repeat("-", 88))
}

// SignInUser adds a new user to the known users table and signs that user in to Amazon Cognito.
func (runner *MigrateUser) SignInUser(ctx context.Context, usersTable string, clientId string) (bool, actions.User) {
	log.Println("Let's sign in a user to your Cognito user pool. When the username and email matches an entry in the\n" +
		"DynamoDB known users table, the email is automatically verified and the user is migrated to the Cognito user pool.")

	user := actions.User{}
	user.UserName = runner.questioner.Ask("\nEnter a username:")
	user.UserEmail = runner.questioner.Ask("\nEnter an email that you own. This email will be used to confirm user migration\n" +
		"during this example:")

	runner.helper.AddKnownUser(ctx, usersTable, user)

	var err error
	var resetRequired *types.PasswordResetRequiredException
	var authResult *types.AuthenticationResultType
	signedIn := false
	for !signedIn && resetRequired == nil {
		log.Printf("Signing in to Cognito as user '%v'. The expected result is a PasswordResetRequiredException.\n\n", user.UserName)
		authResult, err = runner.cognitoActor.SignIn(ctx, clientId, user.UserName, "_")
		if err != nil {
			if errors.As(err, &resetRequired) {
				log.Printf("\nUser '%v' is not in the Cognito user pool but was found in the DynamoDB known users table.\n"+
					"User migration is started and a password reset is required.", user.UserName)
			} else {
				panic(err)
			}
		} else {
			log.Printf("User '%v' successfully signed in. This is unexpected and probably means you have not\n"+
				"cleaned up a previous run of this scenario, so the user exist in the Cognito user pool.\n"+
				"You can continue this example and select to clean up resources, or manually remove\n"+
				"the user from your user pool and try again.", user.UserName)
			runner.resources.userAccessTokens = append(runner.resources.userAccessTokens, *authResult.AccessToken)
			signedIn = true
		}
	}

	log.Println(strings.Repeat("-", 88))
	return resetRequired != nil, user
}

// ResetPassword starts a password recovery flow.
func (runner *MigrateUser) ResetPassword(ctx context.Context, clientId string, user actions.User) {
	wantCode := runner.questioner.AskBool(fmt.Sprintf("In order to migrate the user to Cognito, you must be able to receive a confirmation\n"+
		"code by email at %v. Do you want to send a code (y/n)?", user.UserEmail), "y")
	if !wantCode {
		log.Println("To complete this example and successfully migrate a user to Cognito, you must enter an email\n" +
			"you own that can receive a confirmation code.")
		return
	}
	codeDelivery, err := runner.cognitoActor.ForgotPassword(ctx, clientId, user.UserName)
	if err != nil {
		panic(err)
	}
	log.Printf("\nA confirmation code has been sent to %v.", *codeDelivery.Destination)
	code := runner.questioner.Ask("Check your email and enter it here:")

	confirmed := false
	password := runner.questioner.AskPassword("\nEnter a password that has at least eight characters, uppercase, lowercase, numbers and symbols.\n"+
		"(the password will not display as you type):", 8)
	for !confirmed {
		log.Printf("\nConfirming password reset for user '%v'.\n", user.UserName)
		err = runner.cognitoActor.ConfirmForgotPassword(ctx, clientId, code, user.UserName, password)
		if err != nil {
			var invalidPassword *types.InvalidPasswordException
			if errors.As(err, &invalidPassword) {
				password = runner.questioner.AskPassword("\nEnter another password:", 8)
			} else {
				panic(err)
			}
		} else {
			confirmed = true
		}
	}
	log.Printf("User '%v' successfully confirmed and migrated.\n", user.UserName)
	log.Println("Signing in with your username and password...")
	authResult, err := runner.cognitoActor.SignIn(ctx, clientId, user.UserName, password)
	if err != nil {
		panic(err)
	}
	log.Printf("Successfully signed in. Your access token starts with: %v...\n", (*authResult.AccessToken)[:10])
	runner.resources.userAccessTokens = append(runner.resources.userAccessTokens, *authResult.AccessToken)

	log.Println(strings.Repeat("-", 88))
}

// Run runs the scenario.
func (runner *MigrateUser) Run(ctx context.Context, stackName string) {
	defer func() {
		if r := recover(); r != nil {
			log.Println("Something went wrong with the demo.")
			runner.resources.Cleanup(ctx)
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Printf("Welcome\n")

	log.Println(strings.Repeat("-", 88))

	stackOutputs, err := runner.helper.GetStackOutputs(ctx, stackName)
	if err != nil {
		panic(err)
	}
	runner.resources.userPoolId = stackOutputs["UserPoolId"]

	runner.AddMigrateUserTrigger(ctx, stackOutputs["UserPoolId"], stackOutputs["MigrateUserFunctionArn"])
	runner.resources.triggers = append(runner.resources.triggers, actions.UserMigration)
	resetNeeded, user := runner.SignInUser(ctx, stackOutputs["TableName"], stackOutputs["UserPoolClientId"])
	if resetNeeded {
		runner.helper.ListRecentLogEvents(ctx, stackOutputs["MigrateUserFunction"])
		runner.ResetPassword(ctx, stackOutputs["UserPoolClientId"], user)
	}

	runner.resources.Cleanup(ctx)

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// snippet-end:[gov2.workflows.PoolsAndTriggers.MigrateUser]
