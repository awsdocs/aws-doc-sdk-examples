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

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

func TestRunActivityLogScenario_Integration(t *testing.T) {
	t.Skip("Skip until we can run integration tests that require CDK setup.")
	password := os.Getenv("POOLS_AND_TRIGGERS_PASSWORD")
	mockQuestioner := &demotools.MockQuestioner{
		Answers: []string{
			password, // AddUserToPool
			"",       // SignInUser
			"",       // GetKnownUserLastLogin
			"y",      // Cleanup
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
	scenario := NewActivityLog(sdkConfig, mockQuestioner, &helper)
	scenario.Run(ctx, "PoolsAndTriggersStackForGo")

	log.SetOutput(os.Stderr)
	if !strings.Contains(buf.String(), "Thanks for watching") {
		t.Errorf("didn't run to successful completion. Here's the log:\n%v", buf.String())
	}
}
