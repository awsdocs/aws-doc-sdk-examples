// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.list_server_certs]
package main

// snippet-start:[iam.go.list_server_certs.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.list_server_certs.imports]

// GetServerCerts retrieves the metadata for your server certificates.
// Inputs:
//     svc is an IAM service client
// Output:
//     If success, the list of the server certificate metadata and nil
//     Otherwise, nil and an error from the call to ListServerCertificates
func GetServerCerts(svc iamiface.IAMAPI) ([]*iam.ServerCertificateMetadata, error) {
    // snippet-start:[iam.go.list_server_certs.call]
    var metadata []*iam.ServerCertificateMetadata

    result, err := svc.ListServerCertificates(nil)
    // snippet-end:[iam.go.list_server_certs.call]
    if err != nil {
        return nil, err
    }

    // snippet-start:[iam.go.list_server_certs.metadata]
    for _, m := range result.ServerCertificateMetadataList {
        metadata = append(metadata, m)
    }
    // snippet-end:[iam.go.list_server_certs.metadata]

    return metadata, nil
}

func main() {
    // snippet-start:[iam.go.list_server_certs.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.list_server_certs.session]

    metadataList, err := GetServerCerts(svc)
    if err != nil {
        fmt.Println("Got an error retrieving the server certificates:")
        fmt.Println(err)
        return
    }

    if len(metadataList) < 1 {
        fmt.Println("Could not find any server certificates")
        return
    }

    // snippet-start:[iam.go.list_server_certs.display]
    for _, metadata := range metadataList {
        fmt.Println("ARN:                  " + *metadata.Arn)
        fmt.Println("Expiration:           " + (*metadata.Expiration).Format("2006-01-02 15:04:05 Monday"))
        fmt.Println("Path:                 " + *metadata.Path)
        fmt.Println("ServerCertificateId   " + *metadata.ServerCertificateId)
        fmt.Println("ServerCertificateName " + *metadata.ServerCertificateName)
        fmt.Println("UploadDate:           " + (*metadata.UploadDate).Format("2006-01-02 15:04:05 Monday"))
        fmt.Println("")
    }
    // snippet-end:[iam.go.list_server_certs.display]
}
// snippet-end:[iam.go.list_server_certs]
