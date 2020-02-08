// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Describes the Amazon Cloudwatch alarms.]
// snippet-keyword:[Amazon CloudWatch]
// snippet-keyword:[DescribeAlarms function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
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
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"

    "fmt"
    "os"
)

func main() {
    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := cloudwatch.New(sess)

    resp, err := svc.DescribeAlarms(nil)
    if err != nil {
        fmt.Println("Got error getting alarm descriptions:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    for _, alarm := range resp.MetricAlarms {
        fmt.Println(*alarm.AlarmName)
    }
}
