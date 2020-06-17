// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "strconv"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
)

func TestListProjects(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    result, err := GetProjects(sess)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Got " + strconv.Itoa(len(result.Projects)) + " project(s)")
}
