//go:build integration
// +build integration

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Integration test for the Amazon S3 presigning scenario.

package scenarios

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

func TestRunPresigningScenario_Integration(t *testing.T) {
	mockQuestioner := &demotools.MockQuestioner{
		Answers: []string{
			"doc-example-go-test-bucket", "../README.md", "test-object", "", "",
		},
	}

	sdkConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		log.Fatalf("unable to load SDK config, %v", err)
	}

	log.SetFlags(0)
	var buf bytes.Buffer
	log.SetOutput(&buf)

	RunPresigningScenario(sdkConfig, mockQuestioner, HttpRequester{})

	log.SetOutput(os.Stderr)
	if !strings.Contains(buf.String(), "Thanks for watching") {
		t.Errorf("didn't run to successful completion. Here's the log:\n%v", buf.String())
	}
}
