// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[lambda.go.configure_function.complete]
package main

// snippet-start:[lambda.go.configure_function.imports]
import (
	"flag"
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/lambda"
)

// snippet-end:[lambda.go.configure_function.imports]

func main() {
	// snippet-start:[lambda.go.configure_function.vars]
	functionPtr := flag.String("f", "", "The name of the Lambda function")
	sourcePtr := flag.String("a", "", "The ARN of the entity invoking the function")
	flag.Parse()

	if *functionPtr == "" || *sourcePtr == "" {
		fmt.Println("You must supply the name of the function and of the entity invoking the function")
		flag.PrintDefaults()
		os.Exit(1)
	}
	// snippet-end:[lambda.go.configure_function.vars]

	// Initialize a session
	// snippet-start:[lambda.go.configure_function.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := lambda.New(sess)
	// snippet-end:[lambda.go.configure_function.session]

	// snippet-start:[lambda.go.configure_function.struct]
	permArgs := &lambda.AddPermissionInput{
		Action:       aws.String("lambda:InvokeFunction"),
		FunctionName: functionPtr,
		Principal:    aws.String("s3.amazonaws.com"),
		SourceArn:    sourcePtr,
		StatementId:  aws.String("lambda_s3_notification"),
	}
	// snippet-end:[lambda.go.configure_function.struct]

	// snippet-start:[lambda.go.configure_function.add_permission]
	result, err := svc.AddPermission(permArgs)
	if err != nil {
		fmt.Println("Cannot configure function for notifications")
		os.Exit(0)
	}

	fmt.Println(result)
	// snippet-end:[lambda.go.configure_function.add_permission]
}

// snippet-end:[lambda.go.configure_function.complete]
