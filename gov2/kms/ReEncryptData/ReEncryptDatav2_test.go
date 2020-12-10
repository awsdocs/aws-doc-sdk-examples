package main

import (
    "context"
    "encoding/json"
    "errors"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/service/kms"
)

type KMSReEncryptImpl struct{}

func (dt KMSReEncryptImpl) ReEncrypt(ctx context.Context,
    params *kms.ReEncryptInput,
    optFns ...func(*kms.Options)) (*kms.ReEncryptOutput, error) {

    blob := []byte(globalConfig.Data)

    output := &kms.ReEncryptOutput{
        CiphertextBlob: blob,
    }

    return output, nil
}

type Config struct {
    KeyID string `json:"KeyID"`
    Data  string `json:"Data"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration(t *testing.T) error {
    content, err := ioutil.ReadFile(configFileName)
    if err != nil {
        return err
    }

    text := string(content)

    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    if globalConfig.KeyID == "" || globalConfig.Data == "" {
        msg := "You must supply a value for KeyID and Data in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestReEncrypt(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    api := &KMSReEncryptImpl{}

    blob := []byte(globalConfig.Data)

    input := &kms.ReEncryptInput{
        CiphertextBlob:   blob,
        DestinationKeyId: &globalConfig.KeyID,
    }

    resp, err := ReEncryptText(context.Background(), api, input)
    if err != nil {
        t.Log("Got an error ...:")
        t.Log(err)
        return
    }

    t.Log("Blob (base-64 byte array):")
    t.Log(resp.CiphertextBlob)
}
