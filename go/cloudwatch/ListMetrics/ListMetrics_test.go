// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"strconv"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go/aws/session"
)

func TestCloudWatchOps(t *testing.T) {
	// When the test started
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Started unit test at " + nowString)

	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Show list of metrics
	result, err := GetMetrics(sess)
	if err != nil {
		t.Fatal(err)
	}

	numMetrics := len(result.Metrics)
	t.Log("Found " + strconv.Itoa(numMetrics) + " metrics")
}
