// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Uses chainable setters on nested fields in an API operation request.]
// snippet-keyword:[Extending the SDK]
// snippet-keyword:[UpdateService function]
// snippet-keyword:[Go]
// snippet-service:[aws-go-sdk]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-03-14]
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
package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ecs"
)

func main() {
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := ecs.New(sess)

	// start snippet
	resp, err := svc.UpdateService((&ecs.UpdateServiceInput{}).
		SetService("myService").
		SetDeploymentConfiguration((&ecs.DeploymentConfiguration{}).
			SetMinimumHealthyPercent(80),
		),
	)
	// end snippet
	if err != nil {
		fmt.Println("Error calling UpdateService:")
		fmt.Println(err.Error())
	} else {
		fmt.Println(resp)
	}
}
