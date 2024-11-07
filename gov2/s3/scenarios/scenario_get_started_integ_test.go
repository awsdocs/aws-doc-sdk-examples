// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//go:build integration
// +build integration

// Integration test for the Amazon S3 get started scenario.

package scenarios

import (
	"bytes"
	"context"
	"fmt"
	"log"
	"os"
	"strings"
	"testing"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/google/uuid"
)

func TestGetStartedScenario_Integration(t *testing.T) {
	bucket := os.Getenv("S3_BUCKET_NAME_PREFIX")
	if bucket == "" {
		bucket = "amzn-s3-demo-bucket"
	} else {
		bucket = fmt.Sprintf("%s-%s", bucket, uuid.New())
	}
	outFile := "integ-test.out"
	mockQuestioner := &demotools.MockQuestioner{
		Answers: []string{
			bucket, "../README.md", outFile, "test-folder", "", "y",
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

	RunGetStartedScenario(ctx, sdkConfig, mockQuestioner)

	_ = os.Remove(outFile)

	log.SetOutput(os.Stderr)
	if !strings.Contains(buf.String(), "Thanks for watching") {
		t.Errorf("didn't run to successful completion. Here's the log:\n%v", buf.String())
	}
}
