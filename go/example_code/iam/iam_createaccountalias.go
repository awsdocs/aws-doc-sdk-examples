package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/iam"
)

// Usage:
// go run iam_createaccountalias.go <alias>
func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create a IAM service client.
	svc := iam.New(sess)

	_, err := svc.CreateAccountAlias(&iam.CreateAccountAliasInput{
		AccountAlias: &os.Args[1],
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println(fmt.Sprintf("Account alias %s has been created", os.Args[1]))
}
