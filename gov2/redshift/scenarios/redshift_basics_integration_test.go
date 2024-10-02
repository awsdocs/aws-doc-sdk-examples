// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//go:build integration
// +build integration

// SPDX-License-Identifier: Apache-2.0

// Integration test for the Amazon Redshift Basics scenario.

package scenarios

import (
	"bytes"
	"context"
	"log"
	"math/rand"
	"os"
	"strings"
	"testing"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// MockPauser holds the pausable object.
type MockPauser struct{}

// Pause waits for the specified number of seconds.
func (pausable MockPauser) Pause(secs int) {}

func TestBasicsScenario_Integration(t *testing.T) {
	outFile := "integ-test.out"
	mockQuestioner := &demotools.MockQuestioner{
		Answers: []string{
			"enter", "enter", "10", "2013", "n", "y",
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

	pwd, _ := os.Getwd()
	file, err := os.Open(pwd + "/../../../resources/sample_files/Movies.json")
	helper := ScenarioHelper{
		Prefix: "redshift_basics_integration_tests",
		Random: rand.New(rand.NewSource(0)),
	}
	scenario := RedshiftBasics(sdkConfig, mockQuestioner, demotools.Pauser{}, demotools.NewMockFileSystem(file), helper)
	scenario.Run(ctx)

	_ = os.Remove(outFile)

	log.SetOutput(os.Stderr)
	output := strings.ToLower(buf.String())
	if strings.Contains(output, "error") || strings.Contains(output, "failed") {
		t.Errorf("didn't run to successful completion. Here's the log:\n%v", buf.String())
	}
}
