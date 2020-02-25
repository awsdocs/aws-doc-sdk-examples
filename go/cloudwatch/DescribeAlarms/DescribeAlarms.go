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
//snippet-start:[cloudwatch.go.describe_alarms]
package main

//snippet-start:[cloudwatch.go.describe_alarms.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"
)
//snippet-end:[cloudwatch.go.describe_alarms.imports]

// ListAlarms lists your CloudWatch alarms
func ListAlarms() error {
    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file (~/.aws/credentials)
    //snippet-start:[cloudwatch.go.describe_alarms.session_client]    
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := cloudwatch.New(sess)
    //snippet-end:[cloudwatch.go.describe_alarms.session_client]

    //snippet-start:[cloudwatch.go.describe_alarms.session_call]
    resp, err := svc.DescribeAlarms(nil)
    if err != nil {
        return err
    }

    fmt.Println("Alarms:")
    for _, alarm := range resp.MetricAlarms {
        fmt.Println("    " + *alarm.AlarmName)
    }
    //snippet-end:[cloudwatch.go.describe_alarms.session_call]
    return nil
}

func main() {
    err := ListAlarms()
    if err != nil {
        return
    }
}
//snippet-end:[cloudwatch.go.describe_alarms]