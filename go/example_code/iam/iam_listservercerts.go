package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/iam"
)

// Usage:
// go run iam_listservercerts.go
func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create a IAM service client.
	svc := iam.New(sess)

	result, err := svc.ListServerCertificates(nil)
	if err != nil {
		fmt.Println("Error", err)
		return
	}

	for i, metadata := range result.ServerCertificateMetadataList {
		if metadata == nil {
			continue
		}

		fmt.Printf("Metadata %d: %v\n", i, metadata)
	}
}
