package main

import (
    "context"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/iam"
    "github.com/aws/aws-sdk-go-v2/service/iam/types"
)

type IAMListServerCertificatesImpl struct{}

func (dt IAMListServerCertificatesImpl) ListServerCertificates(ctx context.Context,
    params *iam.ListServerCertificatesInput,
    optFns ...func(*iam.Options)) (*iam.ListServerCertificatesOutput, error) {

    metadataList := make([]*types.ServerCertificateMetadata, 2)
    metadataList[0] = &types.ServerCertificateMetadata{
        Arn:                   aws.String("aws-docs-example-certificate1-ARN"),
        Expiration:            aws.Time(time.Now()),
        Path:                  aws.String("aws-docs-example-certificate1-path"),
        ServerCertificateId:   aws.String("aws-docs-example-certificate1-ID"),
        ServerCertificateName: aws.String("aws-docs-example-certificate1-name"),
        UploadDate:            aws.Time(time.Now()),
    }
    metadataList[1] = &types.ServerCertificateMetadata{
        Arn:                   aws.String("aws-docs-example-certificate2-ARN"),
        Expiration:            aws.Time(time.Now()),
        Path:                  aws.String("aws-docs-example-certificate2-path"),
        ServerCertificateId:   aws.String("aws-docs-example-certificate2-ID"),
        ServerCertificateName: aws.String("aws-docs-example-certificate2-name"),
        UploadDate:            aws.Time(time.Now()),
    }

    output := &iam.ListServerCertificatesOutput{
        ServerCertificateMetadataList: metadataList,
    }

    return output, nil
}

func TestListServerCertificates(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    api := &IAMListServerCertificatesImpl{}

    input := &iam.ListServerCertificatesInput{}

    result, err := GetServerCerts(context.TODO(), api, input)
    if err != nil {
        t.Log("Got an error retrieving the server certificates:")
        t.Log(err)
        return
    }

    var metadataList []*types.ServerCertificateMetadata

    metadataList = append(metadataList, result.ServerCertificateMetadataList...)

    if len(metadataList) < 1 {
        t.Log("Could not find any server certificates")
        return
    }

    for _, metadata := range metadataList {
        t.Log("ARN:                  " + *metadata.Arn)
        t.Log("Expiration:           " + (*metadata.Expiration).Format("2006-01-02 15:04:05 Monday"))
        t.Log("Path:                 " + *metadata.Path)
        t.Log("ServerCertificateId   " + *metadata.ServerCertificateId)
        t.Log("ServerCertificateName " + *metadata.ServerCertificateName)
        t.Log("UploadDate:           " + (*metadata.UploadDate).Format("2006-01-02 15:04:05 Monday"))
        t.Log("")
    }
}
