// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.get_server_cert]
package main

// snippet-start:[iam.go.get_server_cert.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.get_server_cert.imports]

// FindServerCert retrieves the metadata for an IAM server certificate.
// Inputs:
//     svc is an IAM service client
//     certName is the name of the service certificate
// Output:
//     If success, the metadata of the server certificate and nil
//     Otherwise, nil and an error from the call to GetServerCertificate
func FindServerCert(svc iamiface.IAMAPI, certName *string) (*iam.GetServerCertificateOutput, error) {
    // snippet-start:[iam.go.get_server_cert.call]
    result, err := svc.GetServerCertificate(&iam.GetServerCertificateInput{
        ServerCertificateName: certName,
    })
    // snippet-end:[iam.go.get_server_cert.call]

    return result, err
}

func main() {
    // snippet-start:[iam.go.get_server_cert.args]
    certName := flag.String("c", "", "The name of the certificate")
    flag.Parse()

    if *certName == "" {
        fmt.Println("You must supply the name of a certificate (-c CERT-NAME)")
        return
    }
    // snippet-end:[iam.go.get_server_cert.args]

    // snippet-start:[iam.go.get_server_cert.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.get_server_cert.session]

    result, err := FindServerCert(svc, certName)
    if err != nil {
        fmt.Println("Got an error retrieving the server certificate:")
        fmt.Println(err)
        return
    }

    // snippet-start:[iam.go.get_server_cert.display]
    metadata := result.ServerCertificate.ServerCertificateMetadata

    fmt.Println("ARN:                  " + *metadata.Arn)
    fmt.Println("Expiration:           " + (*metadata.Expiration).Format("2006-01-02 15:04:05 Monday"))
    fmt.Println("Path:                 " + *metadata.Path)
    fmt.Println("ServerCertificateId   " + *metadata.ServerCertificateId)
    fmt.Println("ServerCertificateName " + *metadata.ServerCertificateName)
    fmt.Println("UploadDate:           " + (*metadata.UploadDate).Format("2006-01-02 15:04:05 Monday"))
    fmt.Println("")
    // snippet-end:[iam.go.get_server_cert.display]
}
// snippet-end:[iam.go.get_server_cert]
