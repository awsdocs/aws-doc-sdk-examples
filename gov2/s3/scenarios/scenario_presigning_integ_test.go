// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//go:build integration
// +build integration

// SPDX-License-Identifier: Apache-2.0

// Integration test for the Amazon S3 presigning scenario.

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

func TestRunPresigningScenario_Integration(t *testing.T) {
	bucket := os.Getenv("S3_BUCKET_NAME_PREFIX")
	if bucket == "" {
		bucket = "amzn-s3-demo-bucket"
	} else {
		bucket = fmt.Sprintf("%s-%s", bucket, uuid.New())
	}
	mockQuestioner := &demotools.MockQuestioner{
		Answers: []string{
			bucket, "../README.md", "test-object", "", "", "",
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

	RunPresigningScenario(ctx, sdkConfig, mockQuestioner, HttpRequester{})

	log.SetOutput(os.Stderr)
	if !strings.Contains(buf.String(), "Thanks for watching") {
		t.Errorf("didn't run to successful completion. Here's the log:\n%v", buf.String())
	}
}
