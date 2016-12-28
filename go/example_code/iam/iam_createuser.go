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

	// If we received an error and it is an `awserr.Error`, we want to ensure
	// that it is something other than a `NoSuchEntity` error.
	if awserr, ok := err.(awserr.Error); ok && awserr.Code() != "NoSuchEntity" {
		fmt.Println("GetUser Error", err)
		return
	} else if err == nil { // If there is no error, that means the user exists.
		fmt.Println(fmt.Sprintf("User %s already exists", os.Args[1]))
		return
	}

	result, err := svc.CreateUser(&iam.CreateUserInput{
		UserName: &os.Args[1],
	})

	if err != nil {
		fmt.Println("CreateUser Error", err)
		return
	}

	fmt.Println("Success", result)
}
