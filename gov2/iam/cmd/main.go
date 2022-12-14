// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"math/rand"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/iam/scenarios"
)

// main loads default AWS credentials and configuration from the ~/.aws folder and runs
// a scenario specified by the `-scenario` flag.
//
// `-scenario` can be one of the following:
//
// * `assumerole`    -  Runs an interactive scenario that shows you how to assume a role
//						with limited permissions and perform actions on AWS services.
func main() {
	scenarioMap := map[string]func(sdkConfig aws.Config){
		"assumerole":    runAssumeRoleScenario,
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
		sdkConfig, err := config.LoadDefaultConfig(context.TODO())
		if err != nil {
			log.Fatalf("unable to load SDK config, %v", err)
		}

		log.SetFlags(0)
		runScenario(sdkConfig)
	}
}

func runAssumeRoleScenario(sdkConfig aws.Config) {
	helper := scenarios.ScenarioHelper{
		Prefix: "doc-example-assumerole-",
		Random: rand.New(rand.NewSource(time.Now().Unix())),
	}
	scenario := scenarios.NewAssumeRoleScenario(sdkConfig, demotools.NewQuestioner(), &helper)
	scenario.Run()
}
