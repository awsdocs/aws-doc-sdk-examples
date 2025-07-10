// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"context"
	"flag"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/ssm"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/ssm/actions"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/ssm/scenarios"
)

// main loads default AWS credentials and configuration from the ~/.aws folder and runs
// a scenario specified by the `-scenario` flag.
//
// `-scenario` can be one of the following:
//   - `basics` - Runs an interactive scenario that shows how to use SSM for common tasks.
func main() {
	scenarioMap := map[string]func(ctx context.Context, sdkConfig aws.Config){
		"basics": runBasicsScenario,
	}
	choices := make([]string, 0, len(scenarioMap))
	for choice := range scenarioMap {
		choices = append(choices, choice)
	}
	scenario := flag.String(
		"scenario", "",
		fmt.Sprintf("The scenario to run. Must be one of %v", choices))
	flag.Parse()

	if *scenario == "" {
		fmt.Printf("You must specify a scenario to run with the -scenario flag.\n")
		fmt.Printf("Valid scenarios are %v\n", choices)
		return
	}

	ctx := context.Background()
	sdkConfig, err := config.LoadDefaultConfig(ctx)
	if err != nil {
		log.Fatalf("unable to load SDK config, %v", err)
	}

	log.SetFlags(0)
	log.SetPrefix("")

	runScenario, exists := scenarioMap[*scenario]
	if !exists {
		fmt.Printf("Scenario %q not found. Valid scenarios are %v\n", *scenario, choices)
		return
	}

	runScenario(ctx, sdkConfig)
}

func runBasicsScenario(ctx context.Context, sdkConfig aws.Config) {
	log.Println("Running SSM basics scenario.")
	ssmClient := ssm.NewFromConfig(sdkConfig)
	questioner := demotools.NewQuestioner()

	parameterWrapper := actions.ParameterWrapper{SSMClient: ssmClient}
	documentWrapper := actions.DocumentWrapper{SSMClient: ssmClient}
	commandWrapper := actions.CommandWrapper{SSMClient: ssmClient}

	basics := scenarios.NewSSMBasics(parameterWrapper, documentWrapper, commandWrapper, questioner)
	basics.Run(ctx)
}
