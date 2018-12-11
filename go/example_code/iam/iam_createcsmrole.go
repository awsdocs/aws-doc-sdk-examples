//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Creates an IAM policy, IAM role, and attaches the policy to the role.]
//snippet-keyword:[AWS Identity and Access Management]
//snippet-keyword:[CreatePolicy function]
//snippet-keyword:[CreateRole function]
//snippet-keyword:[AttachRolePolicy function]
//snippet-keyword:[Go]
//snippet-service:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-04-16]
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
//snippet-start:[iam.go.create_csm_role]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"

    "fmt"
    "encoding/json"
    "os"
)

/**
 * Creates a new managed policy for your AWS account.
 *
 * This code assumes that you have already set up AWS credentials. See
 * https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html#specifying-credentials
 */

func main() {
    // Default name for policy, role policy.
    RoleName := "AmazonCSM"

    // Override name if provided
    if len(os.Args) == 2 {
        RoleName = os.Args[1]
    }

    Description := "An instance role that has permission for AWS Systems Manager and SDK Metric Monitoring."

    // Initialize a session that the SDK uses to
    // load credentials from ~/.aws/credentials
    // and region from ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create new IAM client
    svc := iam.New(sess)

    AmazonCSMPolicy := map[string]interface{}{
        "Version": "2012-10-17",
        "Statement": []map[string]interface{}{
            {
                "Effect": "Allow",
                "Action": "sdkmetrics:*",
                "Resource": "*",
            },
            {
                "Effect": "Allow",
                "Action": "ssm:GetParameter",
                "Resource": "arn:aws:ssm:*:*:parameter/AmazonCSM*",
            },
        },
    }

    policy, err := json.Marshal(AmazonCSMPolicy)
    if err != nil {
        fmt.Println("Got error marshalling policy")
        fmt.Println(err.Error())
        os.Exit(0)
    }

    // Create policy
    policyResponse, err := svc.CreatePolicy(&iam.CreatePolicyInput{
        PolicyDocument: aws.String(string(policy)),
        PolicyName: aws.String(RoleName + "policy"),
    })
    if err != nil {
        fmt.Println("Got error creating policy:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    // Create role policy
    RolePolicyJSON := map[string]interface{}{
        "Version": "2012-10-17",
        "Statement": []map[string]interface{}{
            {
                "Effect": "Allow",
                "Principal": map[string]interface{}{
                    "Service": "ec2.amazonaws.com",
                },
                "Action": "sts:AssumeRole",
            },
        },
    }

    RolePolicy, err := json.Marshal(RolePolicyJSON)
    if err != nil {
        fmt.Println("Got error marshalling role policy:")
        fmt.Println(err.Error())
        os.Exit(0)
    }

    // Create the inputs for the role
    input := &iam.CreateRoleInput{
        AssumeRolePolicyDocument: aws.String(string(RolePolicy)),
        Description: aws.String(Description),
        RoleName: aws.String(RoleName),
    }

    _, err = svc.CreateRole(input)
    if err != nil {
        fmt.Println("Got error creating role:")
        fmt.Println(err.Error())
        os.Exit(0)
    }

    // Attach policy to role
    _, err = svc.AttachRolePolicy(&iam.AttachRolePolicyInput{
        PolicyArn: aws.String(*policyResponse.Policy.Arn),
        RoleName: aws.String(RoleName),
    })
    if err != nil {
        fmt.Println("Got error attaching policy to role:")
        fmt.Println(err.Error())
        os.Exit(0)
    }

    fmt.Println("Successfully created role: " + RoleName)
}
//snippet-end:[iam.go.create_csm_role]