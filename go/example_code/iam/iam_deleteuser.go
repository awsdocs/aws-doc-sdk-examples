package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/iam"
)

// Usage:
// go run iam_deleteuser.go <username>
func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create a IAM service client.
	svc := iam.New(sess)

	_, err := svc.DeleteUser(&iam.DeleteUserInput{
		UserName: &os.Args[1],
	})

	// If the user does not exist than we will log an error.
	if awserr, ok := err.(awserr.Error); ok && awserr.Code() == "NoSuchEntity" {
		fmt.Println(fmt.Sprintf("User %s does not exist", os.Args[1]))
		return
	} else if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println(fmt.Sprintf("User %s has been deleted", os.Args[1]))
}
