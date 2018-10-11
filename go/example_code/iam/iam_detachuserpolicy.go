 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Go]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
)

// Usage:
// go run iam_detachuserpolicy.go <role name>
func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create a IAM service client.
    svc := iam.New(sess)

    foundPolicy := false
    policyName := "AmazonDynamoDBFullAccess"
    policyArn := "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"

    // Paginate through all role policies. If our role exists on any role
    // policy we will stop iterating to detach the role.
    err = svc.ListAttachedRolePoliciesPages(
        &iam.ListAttachedRolePoliciesInput{
            RoleName: &os.Args[1],
        },
        func(page *iam.ListAttachedRolePoliciesOutput, lastPage bool) bool {
            if page != nil && len(page.AttachedPolicies) > 0 {
                for _, policy := range page.AttachedPolicies {
                    if *policy.PolicyName == policyName {
                        foundPolicy = true
                        return false
                    }
                }
                return true
            }
            return false
        },
    )

    if err != nil {
        fmt.Println("Error", err)
        return
    }

    if !foundPolicy {
        fmt.Println("Policy was not attached to role")
        return
    }

    _, err = svc.DetachRolePolicy(&iam.DetachRolePolicyInput{
        PolicyArn: &policyArn,
        RoleName:  &os.Args[1],
    })

    if err != nil {
        fmt.Println("Unable to detach role policy to role")
        return
    }
    fmt.Println("Role detached successfully")
}
