//go:build integration
// +build integration

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Integration test for the assume role scenario.

package scenarios

import (
	"bytes"
	"context"
	"log"
	"math/rand"
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
// testing to ensure AWS has enough time to create all resources.
func (helper IntegHelper) Pause(secs int) {
	time.Sleep(time.Duration(secs + 5) * time.Second)
}

func TestRunAssumeRoleScenario_Integration(t *testing.T) {
	helper := IntegHelper{
		ScenarioHelper{
			Prefix: "doc-example-test-assumerole-",
			Random: rand.New(rand.NewSource(time.Now().Unix())),
		},
	}
	mockQuestioner := &demotools.MockQuestioner{
		Answers: []string{helper.GetName(), "", "", "", "y"},
	}

	sdkConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		log.Fatalf("unable to load SDK config, %v", err)
	}

	log.SetFlags(0)
	var buf bytes.Buffer
	log.SetOutput(&buf)

	scenario := NewAssumeRoleScenario(sdkConfig, mockQuestioner, &helper)
	scenario.Run()

	log.SetOutput(os.Stderr)
	if !strings.Contains(buf.String(), "Thanks for watching") {
		t.Errorf("didn't run to successful completion. Here's the log:\n%v", buf.String())
	}
}
