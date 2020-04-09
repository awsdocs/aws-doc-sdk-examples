/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
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
