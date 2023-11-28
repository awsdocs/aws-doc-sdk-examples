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
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/scenarios"
)

// main loads default AWS credentials and configuration from the ~/.aws folder and runs
// a scenario specified by the `-scenario` flag.
//
// `-scenario` can be one of the following:
//
// * `invokemodels`  -	Runs a scenario that shows how to invoke various image and text
//						generation models on Amazon Bedrock.
func main() {

	scenarioMap := map[string]func(sdkConfig aws.Config){
		"invokemodels":    runInvokeModelsScenario,
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

    var region string
    flag.StringVar(&region, "region", "us-east-1", "The AWS region")

    flag.Parse()

    fmt.Println("The selected region is: ", region)

	if runScenario, ok := scenarioMap[*scenario]; !ok {
		fmt.Printf("'%v' is not a valid scenario.\n", *scenario)
		flag.Usage()
	} else {
		sdkConfig, err := config.LoadDefaultConfig(context.Background(), config.WithRegion(region))
		if err != nil {
			log.Fatalf("unable to load SDK config, %v", err)
		}

		log.SetFlags(0)
		runScenario(sdkConfig)
	}
}

func runInvokeModelsScenario(sdkConfig aws.Config) {
	scenario := scenarios.NewInvokeModelsScenario(sdkConfig, demotools.NewQuestioner())
	scenario.Run()
}
