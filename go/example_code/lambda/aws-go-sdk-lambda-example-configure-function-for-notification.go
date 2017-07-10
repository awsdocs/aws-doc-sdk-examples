package main

import (
	"fmt"
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

	permArgs := &lambda.AddPermissionInput{
		Action:       aws.String("lambda:InvokeFunction"),
		FunctionName: aws.String("my-notification-function"),
		Principal:    aws.String("s3.amazonaws.com"),
		SourceArn:    aws.String("my-resource-arn"),
		StatementId:  aws.String("lambda_s3_notification"),
	}

	result, err := svc.AddPermission(permArgs)

	if err != nil {
		fmt.Println("Cannot configure function for notifications")
		os.Exit(0)
	} else {
		fmt.Println(result)
	}
}
