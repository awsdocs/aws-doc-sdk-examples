package main

import (
    "context"
    "encoding/json"
    "errors"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/iam"
    "github.com/aws/aws-sdk-go-v2/service/iam/types"
)

type IAMGetServerCertificateImpl struct{}

func (dt IAMGetServerCertificateImpl) GetServerCertificate(ctx context.Context,
    params *iam.GetServerCertificateInput,
    optFns ...func(*iam.Options)) (*iam.GetServerCertificateOutput, error) {

    metadata := &types.ServerCertificateMetadata{
        Arn:                   aws.String("aws-docs-example-certificate-ARN"),
        Expiration:            aws.Time(time.Now()),
        Path:                  aws.String("aws-docs-example-certificate-path"),
        ServerCertificateId:   aws.String("aws-docs-example-certificate-ID"),
        ServerCertificateName: aws.String("aws-docs-example-certificate-name"),
        UploadDate:            aws.Time(time.Now()),
    }

    certificate := &types.ServerCertificate{
        CertificateBody:           aws.String("aws-docs-example-certificate-body"),
        ServerCertificateMetadata: metadata,
        CertificateChain:          aws.String("aws-docs-example-certificate-chain"),
    }

    output := &iam.GetServerCertificateOutput{
        ServerCertificate: certificate,
    }

    return output, nil
}

type Config struct {
    Certificate string `json:"Certificate"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration() error {
    content, err := ioutil.ReadFile(configFileName)
    if err != nil {
        return err
    }

    text := string(content)

    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    if globalConfig.Certificate == "" {
        msg := "You musts supply a value for Certificate in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestGetServerCertificate(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMGetServerCertificateImpl{}

    input := &iam.GetServerCertificateInput{
        ServerCertificateName: &globalConfig.Certificate,
    }

    result, err := FindServerCert(context.Background(), api, input)
    if err != nil {
        t.Log("Got an error retrieving the server certificate:")
        t.Log(err)
        return
    }

    metadata := result.ServerCertificate.ServerCertificateMetadata

    t.Log("ARN:                  " + *metadata.Arn)
    t.Log("Expiration:           " + (*metadata.Expiration).Format("2006-01-02 15:04:05 Monday"))
    t.Log("Path:                 " + *metadata.Path)
    t.Log("ServerCertificateId   " + *metadata.ServerCertificateId)
    t.Log("ServerCertificateName " + *metadata.ServerCertificateName)
    t.Log("UploadDate:           " + (*metadata.UploadDate).Format("2006-01-02 15:04:05 Monday"))
    t.Log("")
}
