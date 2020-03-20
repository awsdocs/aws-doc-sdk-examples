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
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatchevents"
)

// CreateTarget creates a target that is the resource that is invoked when the rule is triggered
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     rule is the name of the rule
//     lammbdaARN is the ARN of the Lambda fuction that is invoked
//     targetID is the identifier for the target
// Output:
//     If successful, a PutTargetsOutput and nil
//     Otherwise, nil and the error from a call to PutTargets
//
func CreateTarget(sess *session.Session, rule *string, lambdaARN *string, targetID *string) (*cloudwatchevents.PutTargetsOutput, error) {
    // Create the service client
    svc := cloudwatchevents.New(sess)

    result, err := svc.PutTargets(&cloudwatchevents.PutTargetsInput{
        Rule: rule,
        Targets: []*cloudwatchevents.Target{
            &cloudwatchevents.Target{
                Arn: lambdaARN,
                Id:  targetID,
            },
        },
    })
    if err != nil {
        return nil, err
    }

    return result, nil
}
