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
// snippet-start:[cloudwatch.go.create_role]
package main

// snippet-start:[cloudwatch.go.create_role.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
)

// snippet-end:[cloudwatch.go.create_role.imports]

// CreateRole creates a role that grants permission to CloudWatch Events
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     role is the name of the role
// Output:
//     If successful, a CreateRoleOutput and nil
//     Otherwise, nil and the error from a call to CreateRole
func CreateRole(sess *session.Session, roleName *string, policyName *string) (*iam.CreateRoleOutput, error) {
    // Create the service client
    // snippet-start:[cloudwatch.go.create_role.call]
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
        return nil, err
    }

    policyARN := createPolicyResult.Policy.Arn

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
    // snippet-end:[cloudwatch.go.create_role.call]
    if err != nil {
        return nil, err
    }

    getUserResult, err := svc.GetUser(nil)
    if err != nil {
        return nil, err
    }

    userName := getUserResult.User.UserName

    _, err = svc.AttachUserPolicy(&iam.AttachUserPolicyInput{
        PolicyArn: policyARN,
        UserName:  userName,
    })
    if err != nil {
        return nil, err
    }

    return createRoleResult, nil
}

func main() {
    // snippet-start:[cloudwatch.go.create_role.args]
    policyName := flag.String("p", "", "The name of the policy")
    roleName := flag.String("r", "", "The name of the role")
    flag.Parse()

    if *policyName == "" || *roleName == "" {
        fmt.Println("You must supply a policy name (-p POLICY) and role name (-r ROLE)")
        return
    }
    // snippet-end:[cloudwatch.go.create_role.args]

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file (~/.aws/credentials)
    // snippet-start:[cloudwatch.go.create_role.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[cloudwatch.go.create_role.session]

    result, err := CreateRole(sess, roleName, policyName)
    if err != nil {
        fmt.Println("Got an error creating the role:")
        fmt.Println(err)
        return
    }

    // snippet-start:[cloudwatch.go.create_role.print]
    fmt.Println("Role ARN: " + *result.Role.Arn)
    // snippet-end:[cloudwatch.go.create_role.print]
}

// snippet-end:[cloudwatch.go.create_role]
