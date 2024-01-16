// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[s3.go.update_deployment]
package main

import (
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ecs"

	"fmt"
)

func main() {
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := ecs.New(sess)

	// snippet-start:[s3.go.update_deployment.call]
	resp, err := svc.UpdateService((&ecs.UpdateServiceInput{}).
		SetService("myService").
		SetDeploymentConfiguration((&ecs.DeploymentConfiguration{}).
			SetMinimumHealthyPercent(80),
		),
	)
	// snippet-end:[s3.go.update_deployment.call]
	if err != nil {
		fmt.Println("Error calling UpdateService:")
		fmt.Println(err.Error())
	} else {
		fmt.Println(resp)
	}
}

// snippet-end:[s3.go.update_deployment]
