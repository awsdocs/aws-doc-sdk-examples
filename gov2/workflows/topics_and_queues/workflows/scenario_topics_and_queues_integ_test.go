// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//go:build integration
// +build integration

// SPDX-License-Identifier: Apache-2.0

// Integration test for the topics and queues scenario.

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

func TestRunTopicsAndQueuesScenario_Integration(t *testing.T) {
	topicName := "doc-example-topics-and-queues-workflow-test-topic"
	baseQueueName := "doc-example-topics-and-queues-workflow-test-queue-"
	message := "Test message."
	groupId := "test-group-id"
	dedupId := "test-dedup-id"
	mockQuestioner := &demotools.MockQuestioner{
		Answers: []string{
			"y", "n", topicName, // CreateTopic
			baseQueueName + "1", "y", "1", "n", baseQueueName + "2", "y", "1", "n", // CreateQueue and SubscribeQueueToTopic
			message, groupId, dedupId, "y", "1", "n", // PublishMessages
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

	RunTopicsAndQueuesScenario(ctx, sdkConfig, mockQuestioner)

	log.SetOutput(os.Stderr)
	if !strings.Contains(buf.String(), "Thanks for watching") {
		t.Errorf("didn't run to successful completion. Here's the log:\n%v", buf.String())
	}
}
