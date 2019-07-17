// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Disables the actions on an alarm.]
// snippet-keyword:[AWS CloudWatch]
// snippet-keyword:[DisableAlarmActions function]
// snippet-keyword:[Go]
// snippet-service:[cloudwatch]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"

    "fmt"
    "os"
)

func main() {
    if len(os.Args) != 2 {
        fmt.Println("You must supply an alarm name")
        os.Exit(1)
    }

    name := os.Args[1]

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create new CloudWatch client.
    svc := cloudwatch.New(sess)

    // Disable the alarm.
    _, err := svc.DisableAlarmActions(&cloudwatch.DisableAlarmActionsInput{
        AlarmNames: []*string{
            aws.String(name),
        },
    })
    if err != nil {
        fmt.Println("Error", err)
        return
    }

    fmt.Println("Success")
}
