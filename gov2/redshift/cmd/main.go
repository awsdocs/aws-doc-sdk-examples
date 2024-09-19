// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"context"
	"flag"
	"fmt"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/redshift/scenarios"
	"log"
	"math/rand"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
)

// main loads default AWS credentials and configuration from the ~/.aws folder and runs
// a scenario specified by the `-scenario` flag.
//
// `-scenario` can be one of the following:
//
//   - `basics`    -  Runs the interactive Basics scenario to show core Redshift actions.
func main() {
	scenarioMap := map[string]func(sdkConfig aws.Config, helper scenarios.IScenarioHelper){
		"basics": runRedshiftBasicsScenario,
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
		helper := scenarios.ScenarioHelper{
			Prefix: "redshift_basics",
			Random: rand.New(rand.NewSource(time.Now().UnixNano())),
		}
		runScenario(sdkConfig, helper)
	}
}

func runRedshiftBasicsScenario(sdkConfig aws.Config, helper scenarios.IScenarioHelper) {
	pauser := demotools.Pauser{}
	scenario := scenarios.RedshiftBasics(sdkConfig, demotools.NewQuestioner(), pauser, demotools.NewStandardFileSystem(), helper)
	scenario.Run()
}
