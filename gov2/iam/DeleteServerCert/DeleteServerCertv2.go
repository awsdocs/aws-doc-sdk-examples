// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.DeleteServerCert]
package main

import (
    "context"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/iam"
)

// IAMDeleteServerCertificateAPI defines the interface for the DeleteServerCertificate function.
// We use this interface to test the function using a mocked service.
type IAMDeleteServerCertificateAPI interface {
    DeleteServerCertificate(ctx context.Context,
        params *iam.DeleteServerCertificateInput,
        optFns ...func(*iam.Options)) (*iam.DeleteServerCertificateOutput, error)
}

// DeleteServerCert deletes an AWS Identity and Access Management (IAM) server certificate.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a DeleteServerCertificateOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to DeleteServerCertificate.
func DeleteServerCert(c context.Context, api IAMDeleteServerCertificateAPI, input *iam.DeleteServerCertificateInput) (*iam.DeleteServerCertificateOutput, error) {
    return api.DeleteServerCertificate(c, input)
}

func main() {
    certName := flag.String("c", "", "The name of the certificate")
    flag.Parse()

    if *certName == "" {
        fmt.Println("You must supply the name of a certificate (-c CERT-NAME)")
        return
    }

    cfg, err := config.LoadDefaultConfig(context.TODO())
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := iam.NewFromConfig(cfg)

    input := &iam.DeleteServerCertificateInput{
        ServerCertificateName: certName,
    }

    _, err = DeleteServerCert(context.Background(), client, input)
    if err != nil {
        fmt.Println("Got an error deleting the server certificate:")
        fmt.Println(err)
        return
    }

    fmt.Println("Deleted the server certificate " + *certName)
}

// snippet-end:[iam.go-v2.DeleteServerCert]
