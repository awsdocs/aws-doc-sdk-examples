package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/iam"
)

// Usage:
// go run iam_createuser.go <username>
func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create a IAM service client.
	svc := iam.New(sess)

	_, err := svc.GetUser(&iam.GetUserInput{
		UserName: &os.Args[1],
	})

	if awserr, ok := err.(awserr.Error); ok && awserr.Code() == "NoSuchEntity" {
		result, err := svc.CreateUser(&iam.CreateUserInput{
			UserName: &os.Args[1],
		})

		if err != nil {
			fmt.Println("CreateUser Error", err)
			return
		}

		fmt.Println("Success", result)
	} else {
		fmt.Println("GetUser Error", err)
	}
}
