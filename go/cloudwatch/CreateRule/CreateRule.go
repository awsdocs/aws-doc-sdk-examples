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
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatchevents"
)

// CreateRule creates a rule that watches for events on a schedule.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     ruleName is the name of the rule
//     roleARN is the ARN of the role
//     schedule is the schedule expression
// Output:
//     If successful, a PutRuleOutput and nil
//     Otherwise, nil and the error from a call to PutRule
func CreateRule(sess *session.Session, ruleName *string, roleARN *string, schedule *string) (*cloudwatchevents.PutRuleOutput, error) {
    // Create the service client
    svc := cloudwatchevents.New(sess)

    result, err := svc.PutRule(&cloudwatchevents.PutRuleInput{
        Name:               ruleName,
        RoleArn:            roleARN,
        ScheduleExpression: schedule,
    })
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    ruleName := flag.String("r", "", "The name of the rule")
    roleARN := flag.String("a", "", "The ARN of the role")
    schedule := flag.String("s", "", "The schedule expression")
    flag.Parse()

    if *ruleName == "" || *roleARN == "" || *schedule == "" {
        fmt.Println("You must supply a rule name (-r RULE), role ARN (-a ROLE-ARN), and schedule expression (-s EXPRESSION)")
        return
    }

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file (~/.aws/credentials)
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    result, err := CreateRule(sess, ruleName, roleARN, schedule)
    if err != nil {
        fmt.Println("Got an error creating the rule:")
        fmt.Println(err)
        return
    }

    fmt.Println("Rule ARN:" + *result.RuleArn)
}
