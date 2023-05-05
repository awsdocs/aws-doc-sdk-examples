//go:build integration
// +build integration

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"bytes"
	"context"
	"log"
	"os"
	"strings"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// IntegHelper adds functionality to a ScenarioHelper for integration tests.
type IntegHelper struct {
	ScenarioHelper
}

// Pause increases all pause durations by 5 seconds. This is needed during integration
// testing to make sure that AWS has enough time to create all resources.
func (helper IntegHelper) Pause(secs int) {
	time.Sleep(time.Duration(secs+5) * time.Second)
}

func TestRunGetStartedFunctionsScenario_Integration(t *testing.T) {
	helper := IntegHelper{
		ScenarioHelper{HandlerPath: "../handlers/"},
	}
	mockQuestioner := &demotools.MockQuestioner{
		Answers: []string{
			"doc-example-integ-test-getstartedfunctions-role",     // GetOrCreateRole
			"doc-example-integ-test-getstartedfunctions-function", // CreateFunction
			"12",                    // InvokeIncrement
			"",                      // UpdateFunction
			"1", "16", "3", "", "n", // InvokeCalculator
			"5", // ListFunctions
			"y", // Cleanup
		},
	}

	sdkConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		log.Fatalf("unable to load SDK config, %v", err)
	}

	log.SetFlags(0)
	var buf bytes.Buffer
	log.SetOutput(&buf)

	scenario := NewGetStartedFunctionsScenario(sdkConfig, mockQuestioner, &helper)
	scenario.Run()

	log.SetOutput(os.Stderr)
	if !strings.Contains(buf.String(), "Thanks for watching") {
		t.Errorf("didn't run to successful completion. Here's the log:\n%v", buf.String())
	}
}
