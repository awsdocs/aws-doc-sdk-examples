// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.GetServerCert]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
)

// IAMGetServerCertificateAPI defines the interface for the GetServerCertificate function.
// We use this interface to test the function using a mocked service.
type IAMGetServerCertificateAPI interface {
	GetServerCertificate(ctx context.Context,
		params *iam.GetServerCertificateInput,
		optFns ...func(*iam.Options)) (*iam.GetServerCertificateOutput, error)
}

// FindServerCert retrieves an AWS Identity and Access Management (IAM) server certificate.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a GetServerCertificateOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to GetServerCertificate.
func FindServerCert(c context.Context, api IAMGetServerCertificateAPI, input *iam.GetServerCertificateInput) (*iam.GetServerCertificateOutput, error) {
	result, err := api.GetServerCertificate(c, input)

	return result, err
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

	input := &iam.GetServerCertificateInput{
		ServerCertificateName: certName,
	}

	result, err := FindServerCert(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got an error retrieving the server certificate:")
		fmt.Println(err)
		return
	}

	metadata := result.ServerCertificate.ServerCertificateMetadata

	fmt.Println("ARN:                  " + *metadata.Arn)
	fmt.Println("Expiration:           " + (*metadata.Expiration).Format("2006-01-02 15:04:05 Monday"))
	fmt.Println("Path:                 " + *metadata.Path)
	fmt.Println("ServerCertificateId   " + *metadata.ServerCertificateId)
	fmt.Println("ServerCertificateName " + *metadata.ServerCertificateName)
	fmt.Println("UploadDate:           " + (*metadata.UploadDate).Format("2006-01-02 15:04:05 Monday"))
	fmt.Println("")
}

// snippet-end:[iam.go-v2.GetServerCert]
