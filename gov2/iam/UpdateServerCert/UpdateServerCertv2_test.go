package main

import (
    "context"
    "encoding/json"
    "errors"
    "fmt"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/service/iam"
)

type IAMUpdateServerCertificateImpl struct{}

func (dt IAMUpdateServerCertificateImpl) UpdateServerCertificate(ctx context.Context,
    params *iam.UpdateServerCertificateInput,
    optFns ...func(*iam.Options)) (*iam.UpdateServerCertificateOutput, error) {

    if nil == params.ServerCertificateName || *params.ServerCertificateName == "" || nil == params.NewServerCertificateName || *params.NewServerCertificateName == "" {
        msg := "Either ServerCertificateName or NewServerCertificateName is nil or an empty string"
        return nil, errors.New(msg)
    }

    output := &iam.UpdateServerCertificateOutput{}

    return output, nil
}

type Config struct {
    CertificateName string `json:"CertificateName"`
    NewName         string `json:"NewName"`
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

    if globalConfig.CertificateName == "" || globalConfig.NewName == "" {
        msg := "You must supply a value for CertificateName and NewName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestUpdateServerCertificate(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMUpdateServerCertificateImpl{}

    input := &iam.UpdateServerCertificateInput{
        ServerCertificateName:    &globalConfig.CertificateName,
        NewServerCertificateName: &globalConfig.NewName,
    }

    _, err = RenameServerCert(context.TODO(), api, input)
    if err != nil {
        fmt.Println("Got an error renaming the server certificate:")
        fmt.Println(err)
        return
    }

    t.Log("Renamed the server certificate from " + globalConfig.CertificateName + " to " + globalConfig.NewName)
}
