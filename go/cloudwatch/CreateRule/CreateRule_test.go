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
    "encoding/json"
    "io/ioutil"
    "strconv"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatchevents"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/google/uuid"
)

// Config represents the information for the test
type Config struct {
    PolicyName string `json:"PolicyName"`
    PolicyWait int    `json:"PolicyWait"`
    RoleARN    string `json:"RoleARN"`
    RoleName   string `json:"RoleName"`
    RoleWait   int    `json:"RoleWait"`
    RuleName   string `json:"RuleName"`
    Schedule   string `json:"Schedule"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfig(t *testing.T) error {
    content, err := ioutil.ReadFile(configFileName)
    if err != nil {
        return err
    }

    text := string(content)

    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    id := uuid.New()

    if globalConfig.PolicyName == "" {
        globalConfig.PolicyName = "TestPolicy-" + id.String()
    }

    if globalConfig.RoleName == "" {
        globalConfig.RoleName = "TestRole-" + id.String()
    }

    if globalConfig.RuleName == "" {
        globalConfig.RuleName = "TestRule-" + id.String()
    }

    if globalConfig.Schedule == "" {
        globalConfig.Schedule = "rate(5 minutes)"
    }

    t.Log("PolicyName: " + globalConfig.PolicyName)
    t.Log("PolicyWait  " + strconv.Itoa(globalConfig.PolicyWait))
    t.Log("RoleARN:    " + globalConfig.RoleARN)
    t.Log("RoleName:   " + globalConfig.RoleName)
    t.Log("RoleWait:   " + strconv.Itoa(globalConfig.RoleWait))
    t.Log("RuleName:   " + globalConfig.RuleName)
    t.Log("Schedule:   " + globalConfig.Schedule)

    return nil
}

func multiplyDuration(factor int64, d time.Duration) time.Duration {
    return time.Duration(factor) * d
}

func createRole(t *testing.T, sess *session.Session, roleName *string, policyName *string) (*iam.CreateRoleOutput, string, error) {
    // Create the service client
    svc := iam.New(sess)

    // From: https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/cw-example-sending-events.html
    rolePolicy := []byte(`{
        "Version": "2012-10-17",
        "Statement": [
           {
              "Sid": "CloudWatchEventsFullAccess",
              "Effect": "Allow",
              "Action": "events:*",
              "Resource": "*"
           },
           {
              "Sid": "IAMPassRoleForCloudWatchEvents",
              "Effect": "Allow",
              "Action": "iam:PassRole",
              "Resource": "arn:aws:iam::*:role/AWS_Events_Invoke_Targets"
           }
        ]
     }`)

    policy := string(rolePolicy[:])

    createPolicyResult, err := svc.CreatePolicy(&iam.CreatePolicyInput{
        PolicyDocument: &policy,
        PolicyName:     policyName,
    })
    if err != nil {
        return nil, "", err
    }

    policyARN := createPolicyResult.Policy.Arn

    err = svc.WaitUntilPolicyExists(&iam.GetPolicyInput{
        PolicyArn: policyARN,
    })
    if err != nil {
        return nil, "", err
    }

    t.Log("Waiting " + strconv.Itoa(globalConfig.PolicyWait) + " seconds for policy to be created")
    ts := multiplyDuration(int64(globalConfig.PolicyWait), time.Second)
    time.Sleep(ts)

    trustRelationship := []byte(`{
                "Version": "2012-10-17",
                "Statement": [
                  {
                    "Effect": "Allow",
                    "Principal": {
                      "Service": "events.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                  }
                ]
              }`)

    trustPolicy := string(trustRelationship[:])

    createRoleResult, err := svc.CreateRole(&iam.CreateRoleInput{
        AssumeRolePolicyDocument: aws.String(trustPolicy),
        RoleName:                 roleName,
    })
    if err != nil {
        return nil, "", err
    }

    err = svc.WaitUntilRoleExists(&iam.GetRoleInput{
        RoleName: roleName,
    })
    if err != nil {
        return nil, "", err
    }

    t.Log("Waiting " + strconv.Itoa(globalConfig.RoleWait) + " seconds for role to be created")
    ts = multiplyDuration(int64(globalConfig.RoleWait), time.Second)
    time.Sleep(ts)

    _, err = svc.AttachRolePolicy(&iam.AttachRolePolicyInput{
        PolicyArn: policyARN,
        RoleName:  roleName,
    })
    if err != nil {
        return nil, "", err
    }

    return createRoleResult, *policyARN, nil
}

func deleteRule(sess *session.Session, ruleName *string) error {
    // Create the service client
    svc := cloudwatchevents.New(sess)

    _, err := svc.DeleteRule(&cloudwatchevents.DeleteRuleInput{
        Name: ruleName,
    })
    if err != nil {
        return err
    }

    return nil
}

func detachPolicy(sess *session.Session, policyARN *string, roleName *string) error {
    svc := iam.New(sess)

    _, err := svc.DetachRolePolicy(&iam.DetachRolePolicyInput{
        PolicyArn: policyARN,
        RoleName:  roleName,
    })
    if err != nil {
        return err
    }

    return nil
}
func deleteRole(sess *session.Session, roleName *string) error {
    svc := iam.New(sess)

    _, err := svc.DeleteRole(&iam.DeleteRoleInput{
        RoleName: roleName,
    })
    if err != nil {
        return err
    }

    return nil
}

func TestCreateEvent(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfig(t)
    if err != nil {
        t.Fatal(err)
    }

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    roleCreated := false
    var roleResult *iam.CreateRoleOutput
    policyARN := ""

    if globalConfig.RoleARN == "" {
        roleResult, policyARN, err = createRole(t, sess, &globalConfig.RoleName, &globalConfig.PolicyName)
        if err != nil {
            t.Fatal(err)
        }

        roleCreated = true

        globalConfig.RoleARN = *roleResult.Role.Arn

        t.Log("Created role with ARN: " + globalConfig.RoleARN)
    } else {
        t.Log("Using role with ARN " + globalConfig.RoleARN)
    }

    ruleResult, err := CreateRule(sess, &globalConfig.RuleName, &globalConfig.RoleARN, &globalConfig.Schedule)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created rule with ARN: " + *ruleResult.RuleArn)

    err = deleteRule(sess, &globalConfig.RuleName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted rule " + globalConfig.RuleName)

    if roleCreated {
        // Have to detach role policy first
        err := detachPolicy(sess, &policyARN, &globalConfig.RoleName)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Detached policy " + globalConfig.PolicyName)

        err = deleteRole(sess, &globalConfig.RoleName)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Deleted role " + globalConfig.RoleName)
    }
}
