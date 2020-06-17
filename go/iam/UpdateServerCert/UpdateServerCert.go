// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.rename_server_cert]
package main

// snippet-start:[iam.go.rename_server_cert.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.rename_server_cert.imports]

// RenameServerCert renames an IAM server certificate.
// Inputs:
//     svc is an IAM service client
//     certName is the name of the service certificate
//     newName is the new name of the service certificate
// Output:
//     If success, nil
//     Otherwise, an error from the call to UpdateServerCertificate
func RenameServerCert(svc iamiface.IAMAPI, certName, newName *string) error {
    // snippet-start:[iam.go.rename_server_cert.call]
    _, err := svc.UpdateServerCertificate(&iam.UpdateServerCertificateInput{
        ServerCertificateName:    certName,
        NewServerCertificateName: newName,
    })
    // snippet-end:[iam.go.rename_server_cert.call]

    return err
}

func main() {
    // snippet-start:[iam.go.rename_server_cert.args]
    certName := flag.String("c", "", "The name of the certificate")
    newName := flag.String("n", "", "The new name of the certificate")
    flag.Parse()

    if *certName == "" {
        fmt.Println("You must supply the original and new names of a certificate (-c CERT-NAME -n NEW-NAME)")
        return
    }
    // snippet-end:[iam.go.rename_server_cert.args]

    // snippet-start:[iam.go.rename_server_cert.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.rename_server_cert.session]

    err := RenameServerCert(svc, certName, newName)
    if err != nil {
        fmt.Println("Got an error renaming the server certificate:")
        fmt.Println(err)
        return
    }

    fmt.Println("Renamed the server certificate from " + *certName + " to " + *newName)
}
// snippet-end:[iam.go.rename_server_cert]
