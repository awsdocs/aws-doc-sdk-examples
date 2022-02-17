// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "strconv"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
)

func TestListStacks(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    resp, err := GetStackSummaries(sess, "all")
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Found " + strconv.Itoa(len(resp.StackSummaries)) + " stacks")
}
