package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/iam"
)

// Usage:
// go run iam_updateuser.go <current username> <new username>
func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create a IAM service client.
	svc := iam.New(sess)

	result, err := svc.UpdateUser(&iam.UpdateUserInput{
		UserName:    &os.Args[1],
		NewUserName: &os.Args[2],
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Success", result)
}
