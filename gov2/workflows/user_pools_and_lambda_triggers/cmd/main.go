// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"user_pools_and_lambda_triggers/workflows"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// main loads default AWS credentials and configuration from the ~/.aws folder and runs
// a scenario specified by the `-scenario` flag.
//
// `-scenario` can be one of the following:
//
//   - `auto_confirm` -  Runs an interactive scenario that shows you how to use an Amazon Cognito
//     Lambda trigger to automatically confirm a known user.
//   - `migrate_user` - Runs an interactive scenario that shows you how to use an Amazon Cognito
//     Lambda trigger to automatically migrate a known user.
//   - `activity_log` - Runs an interactive scenario that shows you how to use an Amazon Cognito
//     Lambda trigger to log custom activity data.
func main() {
	scenarioMap := map[string]func(ctx context.Context, sdkConfig aws.Config, questioner demotools.IQuestioner, helper workflows.IScenarioHelper, stack string){
		"auto_confirm": runAutoConfirmScenario,
		"migrate_user": runMigrateUserScenario,
		"activity_log": runActivityLogScenario,
	}
	choices := make([]string, len(scenarioMap))
	choiceIndex := 0
	for choice := range scenarioMap {
		choices[choiceIndex] = choice
		choiceIndex++
	}
	scenario := flag.String(
		"scenario", "",
		fmt.Sprintf("The scenario to run. Must be one of %v.", choices))
	stack := flag.String(
		"stack", "PoolsAndTriggersStack",
		"The name of the CloudFormation stack used to deploy resources for this scenario.")
	flag.Parse()

	if runScenario, ok := scenarioMap[*scenario]; !ok {
		fmt.Printf("'%v' is not a valid scenario.\n", *scenario)
		flag.Usage()
	} else {
		ctx := context.Background()
		sdkConfig, err := config.LoadDefaultConfig(ctx)
		if err != nil {
			log.Fatalf("unable to load SDK config, %v", err)
		}

		log.SetFlags(0)
		questioner := demotools.NewQuestioner()
		helper := workflows.NewScenarioHelper(sdkConfig, questioner)
		runScenario(ctx, sdkConfig, questioner, helper, *stack)
	}
}

func runAutoConfirmScenario(ctx context.Context, sdkConfig aws.Config, questioner demotools.IQuestioner, helper workflows.IScenarioHelper,
	stack string) {
	workflow := workflows.NewAutoConfirm(sdkConfig, questioner, helper)
	workflow.Run(ctx, stack)
}

func runMigrateUserScenario(ctx context.Context, sdkConfig aws.Config, questioner demotools.IQuestioner, helper workflows.IScenarioHelper,
	stack string) {
	workflow := workflows.NewMigrateUser(sdkConfig, questioner, helper)
	workflow.Run(ctx, stack)
}

func runActivityLogScenario(ctx context.Context, sdkConfig aws.Config, questioner demotools.IQuestioner, helper workflows.IScenarioHelper,
	stack string) {
	workflow := workflows.NewActivityLog(sdkConfig, questioner, helper)
	workflow.Run(ctx, stack)
}
