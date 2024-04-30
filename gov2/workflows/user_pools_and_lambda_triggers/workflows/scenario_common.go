// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

import (
	"log"
	"strings"
	"time"
	"user_pools_and_lambda_triggers/actions"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cloudformation"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatchlogs"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// snippet-start:[gov2.workflows.PoolsAndTriggers.ScenarioHelper]

// IScenarioHelper defines common functions used by the workflows in this example.
type IScenarioHelper interface {
	Pause(secs int)
	GetStackOutputs(stackName string) (actions.StackOutputs, error)
	PopulateUserTable(tableName string)
	GetKnownUsers(tableName string) (actions.UserList, error)
	AddKnownUser(tableName string, user actions.User)
	ListRecentLogEvents(functionName string)
}

// ScenarioHelper contains AWS wrapper structs used by the workflows in this example.
type ScenarioHelper struct {
	questioner  demotools.IQuestioner
	dynamoActor *actions.DynamoActions
	cfnActor    *actions.CloudFormationActions
	cwlActor    *actions.CloudWatchLogsActions
	isTestRun   bool
}

// NewScenarioHelper constructs a new scenario helper.
func NewScenarioHelper(sdkConfig aws.Config, questioner demotools.IQuestioner) ScenarioHelper {
	scenario := ScenarioHelper{
		questioner:  questioner,
		dynamoActor: &actions.DynamoActions{DynamoClient: dynamodb.NewFromConfig(sdkConfig)},
		cfnActor:    &actions.CloudFormationActions{CfnClient: cloudformation.NewFromConfig(sdkConfig)},
		cwlActor:    &actions.CloudWatchLogsActions{CwlClient: cloudwatchlogs.NewFromConfig(sdkConfig)},
	}
	return scenario
}

// Pause waits for the specified number of seconds.
func (helper ScenarioHelper) Pause(secs int) {
	if !helper.isTestRun {
		time.Sleep(time.Duration(secs) * time.Second)
	}
}

// GetStackOutputs gets the outputs from the specified CloudFormation stack in a structured format.
func (helper ScenarioHelper) GetStackOutputs(stackName string) (actions.StackOutputs, error) {
	return helper.cfnActor.GetOutputs(stackName), nil
}

// PopulateUserTable fills the known user table with example data.
func (helper ScenarioHelper) PopulateUserTable(tableName string) {
	log.Printf("First, let's add some users to the DynamoDB %v table we'll use for this example.\n", tableName)
	err := helper.dynamoActor.PopulateTable(tableName)
	if err != nil {
		panic(err)
	}
}

// GetKnownUsers gets the users from the known users table in a structured format.
func (helper ScenarioHelper) GetKnownUsers(tableName string) (actions.UserList, error) {
	knownUsers, err := helper.dynamoActor.Scan(tableName)
	if err != nil {
		log.Printf("Couldn't get known users from table %v. Here's why: %v\n", tableName, err)
	}
	return knownUsers, err
}

// AddKnownUser adds a user to the known users table.
func (helper ScenarioHelper) AddKnownUser(tableName string, user actions.User) {
	log.Printf("Adding user '%v' with email '%v' to the DynamoDB known users table...\n",
		user.UserName, user.UserEmail)
	err := helper.dynamoActor.AddUser(tableName, user)
	if err != nil {
		panic(err)
	}
}

// ListRecentLogEvents gets the most recent log stream and events for the specified Lambda function and displays them.
func (helper ScenarioHelper) ListRecentLogEvents(functionName string) {
	log.Println("Waiting a few seconds to let Lambda write to CloudWatch Logs...")
	helper.Pause(10)
	log.Println("Okay, let's check the logs to find what's happened recently with your Lambda function.")
	logStream, err := helper.cwlActor.GetLatestLogStream(functionName)
	if err != nil {
		panic(err)
	}
	log.Printf("Getting some recent events from log stream %v\n", *logStream.LogStreamName)
	events, err := helper.cwlActor.GetLogEvents(functionName, *logStream.LogStreamName, 10)
	if err != nil {
		panic(err)
	}
	for _, event := range events {
		log.Printf("\t%v", *event.Message)
	}
	log.Println(strings.Repeat("-", 88))
}

// snippet-end:[gov2.workflows.PoolsAndTriggers.ScenarioHelper]
