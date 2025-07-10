// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.go.hello]
package main

import (
	"context"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/ssm"
)

// main uses the AWS SDK for Go V2 to create an AWS Systems Manager (SSM) client
// and list the first 5 documents in your account.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {
	ctx := context.TODO()
	sdkConfig, err := config.LoadDefaultConfig(ctx)
	if err != nil {
		log.Fatalf("unable to load SDK config, %v", err)
	}

	ssmClient := ssm.NewFromConfig(sdkConfig)

	fmt.Println("Hello, AWS Systems Manager! Let's list some of your documents:")
	fmt.Println()

	err = listDocuments(ctx, ssmClient)
	if err != nil {
		log.Fatalf("failed to list documents, %v", err)
	}
}

// listDocuments lists the first 5 SSM documents in your account.
func listDocuments(ctx context.Context, ssmClient *ssm.Client) error {
	input := &ssm.ListDocumentsInput{
		MaxResults: aws.Int32(5),
	}

	result, err := ssmClient.ListDocuments(ctx, input)
	if err != nil {
		return fmt.Errorf("couldn't list documents: %w", err)
	}

	if len(result.DocumentIdentifiers) == 0 {
		fmt.Println("No documents found.")
		return nil
	}

	for _, doc := range result.DocumentIdentifiers {
		fmt.Printf("  %s - %s - %s\n",
			getStringValue(doc.Name),
			doc.DocumentFormat,
			doc.CreatedDate.Format("2006-01-02 15:04:05"))
	}

	return nil
}

// getStringValue safely gets a string value from a pointer, returning empty string if nil.
func getStringValue(s *string) string {
	if s == nil {
		return ""
	}
	return *s
}

// snippet-end:[ssm.go.hello]
