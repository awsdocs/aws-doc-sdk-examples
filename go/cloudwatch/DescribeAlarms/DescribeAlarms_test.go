// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"fmt"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go/aws/session"
)

func TestCloudWatchOps(t *testing.T) {
	// When the test started
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Started unit test at " + nowString)

	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Show list of alarms
	resp, err := ListAlarms(sess)
	if err != nil {
		t.Fatal(err)
	}

	fmt.Println("Alarms:")
	for _, alarm := range resp.MetricAlarms {
		fmt.Println("    " + *alarm.AlarmName)
	}
}
