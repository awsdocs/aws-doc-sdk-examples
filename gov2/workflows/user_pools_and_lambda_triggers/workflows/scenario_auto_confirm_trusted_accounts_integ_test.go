// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//go:build integration
// +build integration

// SPDX-License-Identifier: Apache-2.0

package workflows

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

func TestRunAutoConfirmScenario_Integration(t *testing.T) {
	t.Skip("Skip until we can run integration tests that require CDK setup.")

	password := os.Getenv("POOLS_AND_TRIGGERS_PASSWORD")
	mockQuestioner := &demotools.MockQuestioner{
		Answers: []string{
			"1", password, // SignUpUser
			"",  // SignInUser
			"y", // Cleanup
		},
	}

	ctx := context.Background()
	sdkConfig, err := config.LoadDefaultConfig(ctx)
	if err != nil {
		log.Fatalf("unable to load SDK config, %v", err)
	}

	log.SetFlags(0)
	var buf bytes.Buffer
	log.SetOutput(&buf)

	helper := IntegHelper{
		NewScenarioHelper(sdkConfig, mockQuestioner),
	}
	scenario := NewAutoConfirm(sdkConfig, mockQuestioner, &helper)
	scenario.Run(ctx, "PoolsAndTriggersStackForGo")

	log.SetOutput(os.Stderr)
	if !strings.Contains(buf.String(), "Thanks for watching") {
		t.Errorf("didn't run to successful completion. Here's the log:\n%v", buf.String())
	}
}
