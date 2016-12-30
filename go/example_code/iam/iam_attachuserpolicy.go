/*
   Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/iam"
)

// Usage:
// go run iam_attachuserpolicy.go <role name>
func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create a IAM service client.
	svc := iam.New(sess)

	var pageErr error
	policyName := "AmazonDynamoDBFullAccess"
	policyArn := "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"

	// Paginate through all role policies. If our role exists on any role
	// policy we will set the pageErr and return false. Stopping the
	// pagination.
	err := svc.ListAttachedRolePoliciesPages(
		&iam.ListAttachedRolePoliciesInput{
			RoleName: &os.Args[1],
		},
		func(page *iam.ListAttachedRolePoliciesOutput, lastPage bool) bool {
			if page != nil && len(page.AttachedPolicies) > 0 {
				for _, policy := range page.AttachedPolicies {
					if *policy.PolicyName == policyName {
						pageErr = fmt.Errorf("%s is already attached to this role", policyName)
						return false
					}
				}
				// We should keep paginating because we did not find our role
				return true
			}
			return false
		},
	)

	if pageErr != nil {
		fmt.Println("Error", pageErr)
		return
	}

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	_, err = svc.AttachRolePolicy(&iam.AttachRolePolicyInput{
		PolicyArn: &policyArn,
		RoleName:  &os.Args[1],
	})

	if err != nil {
		fmt.Println("Unable to attach role policy to role")
		return
	}
	fmt.Println("Role attached successfully")
}
