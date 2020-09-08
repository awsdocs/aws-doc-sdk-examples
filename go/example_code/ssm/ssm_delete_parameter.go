// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[nprajilesh]
// snippet-sourcedescription:[ssm_delete_parameter.go demonstrates how to delete a SSM parameter]
// snippet-keyword:[AWS System Manager]
// snippet-keyword:[Amazon SSM]
// snippet-keyword:[AWS SSM DeleteParameter function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[ssm]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-08]
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
// snippet-start:[ssm.go.delete_parameter]
package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ssm"
)

// Usage:
// go run ssm_delete_parameter.go
func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2"),
	})

	if err != nil {
		fmt.Println("NewSession Error", err)
		return
	}

	// Create a SSM client
	svc := ssm.New(sess)

	parameterName := "test-param"
	_, err = svc.DeleteParameter(&ssm.DeleteParameterInput{
		Name:  &parameterName,
	})

	if err != nil {
		fmt.Println("DeleteParameter Error", err)
		return
	}

}
// snippet-end:[ssm.go.delete_parameter]
