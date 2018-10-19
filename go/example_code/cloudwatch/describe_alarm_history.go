//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Gets the history for a CloudWatch alarm.]
//snippet-keyword:[Amazon CloudWatch]
//snippet-keyword:[DescribeAlarmHistory function]
//snippet-keyword:[Go]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "github.com/aws/aws-sdk-go_/service/cloudwatch"

    "fmt"
    "os"
)

func main() {
    // Must have one arg, the name of the alarm
    if len(os.Args) != 2 {
        fmt.Println("You must supply an alarm name")
        os.Exit(1)
    }

    alarmName := os.Args[1]

    sess, err := session.NewSession()
    if err != nil {
        fmt.Println("failed to create session,", err)
        os.Exit(1)
    }

    svc := cloudwatch.New(sess, &aws.Config{Region: aws.String("us-west-2")})

    params := &cloudwatch.DescribeAlarmHistoryInput{
        AlarmName: aws.String(alarmName),

    }
    
    resp, err := svc.DescribeAlarmHistory(params)
    if err != nil {
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println(resp)
}
