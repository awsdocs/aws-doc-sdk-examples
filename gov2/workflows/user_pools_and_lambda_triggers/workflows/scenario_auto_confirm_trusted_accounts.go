// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

// snippet-start:[gov2.workflows.PoolsAndTriggers.AutoConfirm]

import (
	"context"
	"errors"
	"log"
	"strings"
	"user_pools_and_lambda_triggers/actions"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider"
	"github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// AutoConfirm separates the steps of this scenario into individual functions so that
// they are simpler to read and understand.
type AutoConfirm struct {
	helper       IScenarioHelper
	questioner   demotools.IQuestioner
	resources    Resources
	cognitoActor *actions.CognitoActions
}

// NewAutoConfirm constructs a new auto confirm runner.
func NewAutoConfirm(sdkConfig aws.Config, questioner demotools.IQuestioner, helper IScenarioHelper) AutoConfirm {
	scenario := AutoConfirm{
		helper:       helper,
		questioner:   questioner,
		resources:    Resources{},
		cognitoActor: &actions.CognitoActions{CognitoClient: cognitoidentityprovider.NewFromConfig(sdkConfig)},
	}
	scenario.resources.init(scenario.cognitoActor, questioner)
	return scenario
}

// AddPreSignUpTrigger adds a Lambda handler as an invocation target for the PreSignUp trigger.
func (runner *AutoConfirm) AddPreSignUpTrigger(ctx context.Context, userPoolId string, functionArn string) {
	log.Printf("Let's add a Lambda function to handle the PreSignUp trigger from Cognito.\n" +
		"This trigger happens when a user signs up, and lets your function take action before the main Cognito\n" +
		"sign up processing occurs.\n")
	err := runner.cognitoActor.UpdateTriggers(
		ctx, userPoolId,
		actions.TriggerInfo{Trigger: actions.PreSignUp, HandlerArn: aws.String(functionArn)})
	if err != nil {
		panic(err)
	}
	log.Printf("Lambda function %v added to user pool %v to handle the PreSignUp trigger.\n",
		functionArn, userPoolId)
}

// SignUpUser signs up a user from the known user table with a password you specify.
func (runner *AutoConfirm) SignUpUser(ctx context.Context, clientId string, usersTable string) (string, string) {
	log.Println("Let's sign up a user to your Cognito user pool. When the user's email matches an email in the\n" +
		"DynamoDB known users table, it is automatically verified and the user is confirmed.")

	knownUsers, err := runner.helper.GetKnownUsers(ctx, usersTable)
	if err != nil {
		panic(err)
	}
	userChoice := runner.questioner.AskChoice("Which user do you want to use?\n", knownUsers.UserNameList())
	user := knownUsers.Users[userChoice]

	var signedUp bool
	var userConfirmed bool
	password := runner.questioner.AskPassword("Enter a password that has at least eight characters, uppercase, lowercase, numbers and symbols.\n"+
		"(the password will not display as you type):", 8)
	for !signedUp {
		log.Printf("Signing up user '%v' with email '%v' to Cognito.\n", user.UserName, user.UserEmail)
		userConfirmed, err = runner.cognitoActor.SignUp(ctx, clientId, user.UserName, password, user.UserEmail)
		if err != nil {
			var invalidPassword *types.InvalidPasswordException
			if errors.As(err, &invalidPassword) {
				password = runner.questioner.AskPassword("Enter another password:", 8)
			} else {
				panic(err)
			}
		} else {
			signedUp = true
		}
	}
	log.Printf("User %v signed up, confirmed = %v.\n", user.UserName, userConfirmed)

	log.Println(strings.Repeat("-", 88))

	return user.UserName, password
}

// SignInUser signs in a user.
func (runner *AutoConfirm) SignInUser(ctx context.Context, clientId string, userName string, password string) string {
	runner.questioner.Ask("Press Enter when you're ready to continue.")
	log.Printf("Let's sign in as %v...\n", userName)
	authResult, err := runner.cognitoActor.SignIn(ctx, clientId, userName, password)
	if err != nil {
		panic(err)
	}
	log.Printf("Successfully signed in. Your access token starts with: %v...\n", (*authResult.AccessToken)[:10])
	log.Println(strings.Repeat("-", 88))
	return *authResult.AccessToken
}

// Run runs the scenario.
func (runner *AutoConfirm) Run(ctx context.Context, stackName string) {
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
	runner.helper.PopulateUserTable(ctx, stackOutputs["TableName"])

	runner.AddPreSignUpTrigger(ctx, stackOutputs["UserPoolId"], stackOutputs["AutoConfirmFunctionArn"])
	runner.resources.triggers = append(runner.resources.triggers, actions.PreSignUp)
	userName, password := runner.SignUpUser(ctx, stackOutputs["UserPoolClientId"], stackOutputs["TableName"])
	runner.helper.ListRecentLogEvents(ctx, stackOutputs["AutoConfirmFunction"])
	runner.resources.userAccessTokens = append(runner.resources.userAccessTokens,
		runner.SignInUser(ctx, stackOutputs["UserPoolClientId"], userName, password))

	runner.resources.Cleanup(ctx)

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// snippet-end:[gov2.workflows.PoolsAndTriggers.AutoConfirm]
