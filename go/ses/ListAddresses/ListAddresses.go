// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ses"
	"github.com/aws/aws-sdk-go/service/ses/sesiface"
)

// GetAddresses retrieves the valid email addresses
// Inputs:
//     svc is an SES service client
// Output:
//     If success, information about the valid addresses and nil
//     Otherwise, nil and an error from the call to ListIdentities
func GetAddresses(svc sesiface.SESAPI) (*ses.ListIdentitiesOutput, error) {
	result, err := svc.ListIdentities(&ses.ListIdentitiesInput{
		IdentityType: aws.String("EmailAddress"),
	})

	return result, err
}

func main() {

	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := ses.New(sess)

	result, err := GetAddresses(svc)
	if err != nil {
		fmt.Println("Got an error retrieving addresses:")
		fmt.Println(err)
		return
	}

	for _, email := range result.Identities {
		var e = []*string{email}

		verified, err := svc.GetIdentityVerificationAttributes(&ses.GetIdentityVerificationAttributesInput{
			Identities: e,
		})

		if err != nil {
			fmt.Println("Got an error retrieving the identity attributes:")
			fmt.Println(err)
			continue
		}

		for _, va := range verified.VerificationAttributes {
			if *va.VerificationStatus == "Success" {
				fmt.Println(*email)
			}
		}
	}
}
