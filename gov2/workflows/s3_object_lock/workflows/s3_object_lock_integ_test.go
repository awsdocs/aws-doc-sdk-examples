// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//go:build integration
// +build integration

// Integration test for the scenario.

package workflows

import (
	"bytes"
	"context"
	"fmt"
	"log"
	"os"
	"strings"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

func TestObjectLockScenario_Integration(t *testing.T) {
	bucketPrefix := fmt.Sprintf("test-bucket-%d", time.Now().Unix())

	mockQuestioner := demotools.MockQuestioner{
		Answers: []string{
			bucketPrefix,       // CreateBuckets
			"",                 // EnableLockOnBucket
			"30",               // SetDefaultRetentionPolicy
			"",                 // UploadTestObjects
			"y", "y", "y", "y", // SetObjectLockConfigurations
			"1", "2", "1", "3", "1", "4", "1", "5", "1", "6", "1", "7", // InteractWithObjects
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

	scenario := NewObjectLockScenario(sdkConfig, &mockQuestioner)
	scenario.Run(ctx)

	log.SetOutput(os.Stderr)
	if !strings.Contains(buf.String(), "Thanks for watching") {
		t.Errorf("didn't run to successful completion. Here's the log:\n%v", buf.String())
	}

}
