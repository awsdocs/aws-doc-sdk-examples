// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"aurora/scenarios"
	"context"
	"flag"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// main loads default AWS credentials and configuration from the ~/.aws folder and runs
// a scenario specified by the `-scenario` flag.
//
// `-scenario` can be one of the following:
//
//   - `clusters`    -  Runs the interactive DB clusters scenario that shows you how to use
//     Amazon Aurora commands to work with DB clusters and databases.
func main() {
	scenarioMap := map[string]func(sdkConfig aws.Config){
		"clusters": runClusterScenario,
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

func runClusterScenario(sdkConfig aws.Config) {
	scenario := scenarios.NewGetStartedClusters(sdkConfig, demotools.NewQuestioner(), scenarios.ScenarioHelper{})
	scenario.Run("aurora-mysql", "doc-example-cluster-parameter-group", "doc-example-aurora",
		"docexampledb")
}
