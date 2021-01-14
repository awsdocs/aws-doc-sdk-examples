package main

import (
    "context"
    "encoding/json"
    "errors"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/service/iam"
)

type IAMDeleteServerCertificateImpl struct{}

func (dt IAMDeleteServerCertificateImpl) DeleteServerCertificate(ctx context.Context,
    params *iam.DeleteServerCertificateInput,
    optFns ...func(*iam.Options)) (*iam.DeleteServerCertificateOutput, error) {

    if nil == params.ServerCertificateName || *params.ServerCertificateName == "" {
        msg := "ServerCertificateName is either nil or an empty string"
        return nil, errors.New(msg)
    }

    output := &iam.DeleteServerCertificateOutput{}

    return output, nil
}

type Config struct {
    CertificateName string `json:"CertificateName"`
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

    if globalConfig.CertificateName == "" {
        msg := "You must supply a value for CertificateName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestDeleteServerCertificate(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMDeleteServerCertificateImpl{}

    input := &iam.DeleteServerCertificateInput{
        ServerCertificateName: &globalConfig.CertificateName,
    }

    _, err = DeleteServerCert(context.TODO(), api, input)
    if err != nil {
        t.Log("Got an error deleting the server certificate:")
        t.Log(err)
        return
    }

    t.Log("Deleted the server certificate " + globalConfig.CertificateName)
}
