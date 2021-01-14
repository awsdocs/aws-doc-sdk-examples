// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.ListServerCerts]
package main

import (
    "context"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/iam"
    "github.com/aws/aws-sdk-go-v2/service/iam/types"
)

// IAMListServerCertificatesAPI defines the interface for the ListServerCertificates function.
// We use this interface to test the function using a mocked service.
type IAMListServerCertificatesAPI interface {
    ListServerCertificates(ctx context.Context,
        params *iam.ListServerCertificatesInput,
        optFns ...func(*iam.Options)) (*iam.ListServerCertificatesOutput, error)
}

// GetServerCerts retrieves the server certificates.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a ListServerCertificatesOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to ListServerCertificates.
func GetServerCerts(c context.Context, api IAMListServerCertificatesAPI, input *iam.ListServerCertificatesInput) (*iam.ListServerCertificatesOutput, error) {
    return api.ListServerCertificates(c, input)
}

func main() {
    cfg, err := config.LoadDefaultConfig(context.TODO())
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := iam.NewFromConfig(cfg)

    input := &iam.ListServerCertificatesInput{}

    result, err := GetServerCerts(context.Background(), client, input)
    if err != nil {
        fmt.Println("Got an error retrieving the server certificates:")
        fmt.Println(err)
        return
    }

    var metadataList []*types.ServerCertificateMetadata

    metadataList = append(metadataList, result.ServerCertificateMetadataList...)

    if len(metadataList) < 1 {
        fmt.Println("Could not find any server certificates")
        return
    }

    for _, metadata := range metadataList {
        fmt.Println("ARN:                  " + *metadata.Arn)
        fmt.Println("Expiration:           " + (*metadata.Expiration).Format("2006-01-02 15:04:05 Monday"))
        fmt.Println("Path:                 " + *metadata.Path)
        fmt.Println("ServerCertificateId   " + *metadata.ServerCertificateId)
        fmt.Println("ServerCertificateName " + *metadata.ServerCertificateName)
        fmt.Println("UploadDate:           " + (*metadata.UploadDate).Format("2006-01-02 15:04:05 Monday"))
        fmt.Println("")
    }
}

// snippet-end:[iam.go-v2.ListServerCerts]
