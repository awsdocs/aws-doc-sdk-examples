// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[nprajilesh]
// snippet-sourcedescription:[ssm_put_parameter.go demonstrates how to create a new AWS SSM parameter]
// snippet-keyword:[AWS System Manager]
// snippet-keyword:[Amazon SSM]
// snippet-keyword:[AWS SSM PutParameter function]
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
// snippet-start:[ssm.go.put_parameter]
package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ssm"
)

// Usage:
// go run ssm_put_parameter.go
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
	parameterValue := "test-value"
	parameterType := "String"
	result, err := svc.PutParameter(&ssm.PutParameterInput{
		Name:  &parameterName,
		Value: &parameterValue,
		Type:  &parameterType,
	})

	if err != nil {
		fmt.Println("PutParameter Error", err)
		return
	}

	fmt.Println(*result.Version)
}
// snippet-end:[ssm.go.put_parameter]
