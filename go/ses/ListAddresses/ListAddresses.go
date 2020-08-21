// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ses.go.list_addresses]
package main

// snippet-start:[ses.go.list_addresses.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ses"
    "github.com/aws/aws-sdk-go/service/ses/sesiface"
)
// snippet-end:[ses.go.list_addresses.imports]

// GetAddresses retrieves the valid email addresses
// Inputs:
//     svc is an SES service client
// Output:
//     If success, information about the valid addresses and nil
//     Otherwise, nil and an error from the call to ListIdentities
func GetAddresses(svc sesiface.SESAPI) ([]string, error) {
    // snippet-start:[ses.go.list_addresses.list_identities]
    var addresses []string

    result, err := svc.ListIdentities(&ses.ListIdentitiesInput{
        IdentityType: aws.String("EmailAddress"),
    })
    // snippet-end:[ses.go.list_addresses.list_identities]
    if err != nil {
        return addresses, err
    }

    // snippet-start:[ses.go.list_addresses.get_attributes]
    for _, email := range result.Identities {
        var e = []*string{email}

        verified, err := svc.GetIdentityVerificationAttributes(&ses.GetIdentityVerificationAttributesInput{
            Identities: e,
        })
        // snippet-end:[ses.go.list_addresses.get_attributes]
        if err != nil {
            fmt.Println("Got an error retrieving an identity attribute:")
            fmt.Println(err)
            continue
        }

        // snippet-start:[ses.go.list_addresses.add_attributes]
        for _, va := range verified.VerificationAttributes {
            if *va.VerificationStatus == "Success" {
                addresses = append(addresses, *email)
            }
        }
        // snippet-end:[ses.go.list_addresses.add_attributes]
    }

    return addresses, nil
}

func main() {

    // snippet-start:[ses.go.list_addresses.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ses.New(sess)
    // snippet-end:[ses.go.list_addresses.session]

    addresses, err := GetAddresses(svc)
    if err != nil {
        fmt.Println("Got an error retrieving addresses:")
        fmt.Println(err)
        return
    }

    // snippet-start:[ses.go.list_addresses.display]
    for _, address := range addresses {
        fmt.Println(address)
    }
    // snippet-end:[ses.go.list_addresses.display]
}
// snippet-end:[ses.go.list_addresses]
