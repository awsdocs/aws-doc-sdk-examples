// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.delete_server_cert]
package main

// snippet-start:[iam.go.delete_server_cert.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.delete_server_cert.imports]

// DeleteServerCert deletes an IAM server certificate.
// Inputs:
//     svc is an IAM service client
//     certName is the name of the service certificate
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteServerCertificate
func DeleteServerCert(svc iamiface.IAMAPI, certName *string) error {
    // snippet-start:[iam.go.delete_server_cert.call]
    _, err := svc.DeleteServerCertificate(&iam.DeleteServerCertificateInput{
        ServerCertificateName: certName,
    })
    // snippet-end:[iam.go.delete_server_cert.call]

    return err
}

func main() {
    // snippet-start:[iam.go.delete_server_cert.args]
    certName := flag.String("c", "", "The name of the certificate")
    flag.Parse()

    if *certName == "" {
        fmt.Println("You must supply the name of a certificate (-c CERT-NAME)")
        return
    }
    // snippet-end:[iam.go.delete_server_cert.args]

    // snippet-start:[iam.go.delete_server_cert.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.delete_server_cert.session]

    err := DeleteServerCert(svc, certName)
    if err != nil {
        fmt.Println("Got an error deleting the server certificate:")
        fmt.Println(err)
        return
    }

    fmt.Println("Deleted the server certificate " + *certName)
}
// snippet-end:[iam.go.delete_server_cert]
