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
    "testing"
    "time"
)

func TestCloudWatchOps(t *testing.T) {
    // When the test started
    thisTime := time.Now()
    nowString := thisTime.Format("20060102150405")
    t.Log("Started unit test at " + nowString)

    // Show list of metrics
    _, err := GetMetrics()
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Got the metrics")
}
