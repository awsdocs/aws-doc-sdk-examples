// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

// snippet-start:[gov2.workflows.PoolsAndTriggers.ActivityLog]

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

// ActivityLog separates the steps of this scenario into individual functions so that
// they are simpler to read and understand.
type ActivityLog struct {
	helper       IScenarioHelper
	questioner   demotools.IQuestioner
	resources    Resources
	cognitoActor *actions.CognitoActions
}

// NewActivityLog constructs a new activity log runner.
func NewActivityLog(sdkConfig aws.Config, questioner demotools.IQuestioner, helper IScenarioHelper) ActivityLog {
	scenario := ActivityLog{
		helper:       helper,
		questioner:   questioner,
		resources:    Resources{},
		cognitoActor: &actions.CognitoActions{CognitoClient: cognitoidentityprovider.NewFromConfig(sdkConfig)},
	}
	scenario.resources.init(scenario.cognitoActor, questioner)
	return scenario
}

// AddUserToPool selects a user from the known users table and uses administrator credentials to add the user to the user pool.
func (runner *ActivityLog) AddUserToPool(ctx context.Context, userPoolId string, tableName string) (string, string) {
	log.Println("To facilitate this example, let's add a user to the user pool using administrator privileges.")
	users, err := runner.helper.GetKnownUsers(ctx, tableName)
	if err != nil {
		panic(err)
	}
	user := users.Users[0]
	log.Printf("Adding known user %v to the user pool.\n", user.UserName)
	err = runner.cognitoActor.AdminCreateUser(ctx, userPoolId, user.UserName, user.UserEmail)
	if err != nil {
		panic(err)
	}
	pwSet := false
	password := runner.questioner.AskPassword("\nEnter a password that has at least eight characters, uppercase, lowercase, numbers and symbols.\n"+
		"(the password will not display as you type):", 8)
	for !pwSet {
		log.Printf("\nSetting password for user '%v'.\n", user.UserName)
		err = runner.cognitoActor.AdminSetUserPassword(ctx, userPoolId, user.UserName, password)
		if err != nil {
			var invalidPassword *types.InvalidPasswordException
			if errors.As(err, &invalidPassword) {
				password = runner.questioner.AskPassword("\nEnter another password:", 8)
			} else {
				panic(err)
			}
		} else {
			pwSet = true
		}
	}

	log.Println(strings.Repeat("-", 88))

	return user.UserName, password
}

// AddActivityLogTrigger adds a Lambda handler as an invocation target for the PostAuthentication trigger.
func (runner *ActivityLog) AddActivityLogTrigger(ctx context.Context, userPoolId string, activityLogArn string) {
	log.Println("Let's add a Lambda function to handle the PostAuthentication trigger from Cognito.\n" +
		"This trigger happens after a user is authenticated, and lets your function take action, such as logging\n" +
		"the outcome.")
	err := runner.cognitoActor.UpdateTriggers(
		ctx, userPoolId,
		actions.TriggerInfo{Trigger: actions.PostAuthentication, HandlerArn: aws.String(activityLogArn)})
	if err != nil {
		panic(err)
	}
	runner.resources.triggers = append(runner.resources.triggers, actions.PostAuthentication)
	log.Printf("Lambda function %v added to user pool %v to handle PostAuthentication Cognito trigger.\n",
		activityLogArn, userPoolId)

	log.Println(strings.Repeat("-", 88))
}

// SignInUser signs in as the specified user.
func (runner *ActivityLog) SignInUser(ctx context.Context, clientId string, userName string, password string) {
	log.Printf("Now we'll sign in user %v and check the results in the logs and the DynamoDB table.", userName)
	runner.questioner.Ask("Press Enter when you're ready.")
	authResult, err := runner.cognitoActor.SignIn(ctx, clientId, userName, password)
	if err != nil {
		panic(err)
	}
	log.Println("Sign in successful.",
		"The PostAuthentication Lambda handler writes custom information to CloudWatch Logs.")

	runner.resources.userAccessTokens = append(runner.resources.userAccessTokens, *authResult.AccessToken)
}

// GetKnownUserLastLogin gets the login info for a user from the Amazon DynamoDB table and displays it.
func (runner *ActivityLog) GetKnownUserLastLogin(ctx context.Context, tableName string, userName string) {
	log.Println("The PostAuthentication handler also writes login data to the DynamoDB table.")
	runner.questioner.Ask("Press Enter when you're ready to continue.")
	users, err := runner.helper.GetKnownUsers(ctx, tableName)
	if err != nil {
		panic(err)
	}
	for _, user := range users.Users {
		if user.UserName == userName {
			log.Println("The last login info for the user in the known users table is:")
			log.Printf("\t%+v", *user.LastLogin)
		}
	}
	log.Println(strings.Repeat("-", 88))
}

// Run runs the scenario.
func (runner *ActivityLog) Run(ctx context.Context, stackName string) {
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
	userName, password := runner.AddUserToPool(ctx, stackOutputs["UserPoolId"], stackOutputs["TableName"])

	runner.AddActivityLogTrigger(ctx, stackOutputs["UserPoolId"], stackOutputs["ActivityLogFunctionArn"])
	runner.SignInUser(ctx, stackOutputs["UserPoolClientId"], userName, password)
	runner.helper.ListRecentLogEvents(ctx, stackOutputs["ActivityLogFunction"])
	runner.GetKnownUserLastLogin(ctx, stackOutputs["TableName"], userName)

	runner.resources.Cleanup(ctx)

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// snippet-end:[gov2.workflows.PoolsAndTriggers.ActivityLog]
