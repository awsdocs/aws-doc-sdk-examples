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
// snippet-start:[cloudwatch.go.create_target]
package main

// snippet-start:[cloudwatch.go.create_target.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatchevents"
)
// snippet-end:[cloudwatch.go.create_target.imports]

// CreateTarget creates a target that is the resource that is invoked when the rule is triggered
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     rule is the name of the rule
//     lammbdaARN is the ARN of the Lambda function that is invoked
//     targetID is the identifier for the target
// Output:
//     If successful, a PutTargetsOutput and nil
//     Otherwise, nil and the error from a call to PutTargets
//
func CreateTarget(sess *session.Session, rule *string, lambdaARN *string, targetID *string) error {
    // Create the service client
    // snippet-start:[cloudwatch.go.create_target.call]
    svc := cloudwatchevents.New(sess)

    _, err := svc.PutTargets(&cloudwatchevents.PutTargetsInput{
        Rule: rule,
        Targets: []*cloudwatchevents.Target{
            &cloudwatchevents.Target{
                Arn: lambdaARN,
                Id:  targetID,
            },
        },
    })
    // snippet-end:[cloudwatch.go.create_target.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[cloudwatch.go.create_target.args]
    rule := flag.String("r", "", "The name of the rule")
    lambdaARN := flag.String("l", "", "The ARN of the Lambda function that is invoked")
    targetID := flag.String("t", "", "The identifier for the target")
    flag.Parse()

    if *rule == "" || *lambdaARN == "" || *targetID == "" {
        fmt.Println("You must supply a rule name (-r RULE), Lambda ARN (-l LAMBDA) and target ID (-t TARGET)")
        return
    }
    // snippet-end:[cloudwatch.go.create_target.args]

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file (~/.aws/credentials)
    // snippet-start:[cloudwatch.go.create_target.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[cloudwatch.go.create_target.session]

    err := CreateTarget(sess, rule, lambdaARN, targetID)
    if err != nil {
        fmt.Println("Got an error creating a target:")
        fmt.Println(err)
        return
    }

    // snippet-start:[cloudwatch.go.create_target.print]
    fmt.Println("Target " + *targetID + " created")
    // snippet-end:[cloudwatch.go.create_target.print]
}
// snippet-end:[cloudwatch.go.create_target]
