package main

import (
	"fmt"
	"io/ioutil"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/lambda"
)

func main() {
	// Initialize a session
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create Lambda service client
	svc := lambda.New(sess, &aws.Config{Region: aws.String("us-west-2")})

	contents, err := ioutil.ReadFile("my-zip-file.zip")

	if err != nil {
		fmt.Println("Could not read my-zip-file.zip")
		os.Exit(0)
	}

	createCode := &lambda.FunctionCode{
		S3Bucket:        aws.String("my-notification-bucket"),
		S3Key:           aws.String("my-zip-file"),
		S3ObjectVersion: aws.String(""),
		ZipFile:         contents,
	}

	createArgs := &lambda.CreateFunctionInput{
		Code:         createCode,
		FunctionName: aws.String("my-notification-function"),
		Handler:      aws.String("my-package.my-class"),
		Role:         aws.String("my-resource-arn"),
		Runtime:      aws.String("java8"),
	}

	result, err := svc.CreateFunction(createArgs)

	if err != nil {
		fmt.Println("Cannot create function")
		os.Exit(0)
	} else {
		fmt.Println(result)
	}
}
