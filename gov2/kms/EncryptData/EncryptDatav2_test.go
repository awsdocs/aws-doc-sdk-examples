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

type KMSEncryptImpl struct{}

func (dt KMSEncryptImpl) Encrypt(ctx context.Context,
    params *kms.EncryptInput,
    optFns ...func(*kms.Options)) (*kms.EncryptOutput, error) {

    blob := []byte(globalConfig.Text)

    output := &kms.EncryptOutput{
        CiphertextBlob: blob,
    }

    return output, nil
}

type Config struct {
    KeyID string `json:"KeyID"`
    Text  string `json:"Text"`
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

    if globalConfig.KeyID == "" || globalConfig.Text == "" {
        msg := "You must supply a value for KeyID and Text in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestEncrypt(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    api := &KMSEncryptImpl{}

    input := &kms.EncryptInput{
        KeyId:     &globalConfig.KeyID,
        Plaintext: []byte(globalConfig.Text),
    }

    resp, err := EncryptText(context.TODO(), api, input)
    if err != nil {
        t.Log("Got an error ...:")
        t.Log(err)
        return
    }

    t.Log("Blob (base-64 byte array):")
    t.Log(resp.CiphertextBlob)
}
