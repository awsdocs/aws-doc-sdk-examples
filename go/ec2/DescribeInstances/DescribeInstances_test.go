// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
)

func TestDescribeInstances(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    result, err := GetInstances(sess)
    if err != nil {
        t.Fatal(err)
    }

    for _, r := range result.Reservations {
        t.Log("Reservation ID: " + *r.ReservationId)
        t.Log("Instance IDs:")
        for _, i := range r.Instances {
            t.Log("   " + *i.InstanceId)
        }

        t.Log("")
    }
}
