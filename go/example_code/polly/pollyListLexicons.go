// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/polly"

	"fmt"
	"os"
)

func main() {
	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file. (~/.aws/credentials).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create Polly client
	svc := polly.New(sess)

	resp, err := svc.ListLexicons(nil)
	if err != nil {
		fmt.Println("Got error calling ListLexicons:")
		fmt.Print(err.Error())
		os.Exit(1)
	}

	for _, l := range resp.Lexicons {
		fmt.Println(*l.Name)
		fmt.Println("  Alphabet: " + *l.Attributes.Alphabet)
		fmt.Println("  Language: " + *l.Attributes.LanguageCode)
		fmt.Println("")
	}
}
