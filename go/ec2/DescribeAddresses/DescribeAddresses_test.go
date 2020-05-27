// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
)

func TestDescribeAddresses(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    result, err := GetAddresses(sess)
    if err != nil {
        t.Fatal(err)
    }

    for _, addr := range result.Addresses {
        t.Log("IP address:   ", *addr.PublicIp)
        t.Log("Allocation ID:", *addr.AllocationId)
        if addr.InstanceId != nil {
            t.Log("Instance ID:  ", *addr.InstanceId)
        }
    }
}
