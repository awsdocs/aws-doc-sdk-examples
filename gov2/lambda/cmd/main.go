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
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/lambda/scenarios"
)

// main loads default AWS credentials and configuration from the ~/.aws folder and runs
// a scenario specified by the `-scenario` flag.
//
// `-scenario` can be one of the following:
//
//   - `functions` - Runs an interactive scenario that shows you how to create and
//     invoke an AWS Lambda function, then update the code and invoke it again.
func main() {
	scenarioMap := map[string]func(ctx context.Context, sdkConfig aws.Config){
		"functions": runGetStartedFunctionsScenario,
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
		runScenario(ctx, sdkConfig)
	}
}

func runGetStartedFunctionsScenario(ctx context.Context, sdkConfig aws.Config) {
	helper := scenarios.ScenarioHelper{HandlerPath: "handlers/"}
	scenario := scenarios.NewGetStartedFunctionsScenario(sdkConfig, demotools.NewQuestioner(), &helper)
	scenario.Run(ctx)
}
