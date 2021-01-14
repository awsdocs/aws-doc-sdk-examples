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

type KMSDecryptImpl struct{}

func (dt KMSDecryptImpl) Decrypt(ctx context.Context,
    params *kms.DecryptInput,
    optFns ...func(*kms.Options)) (*kms.DecryptOutput, error) {

    plainText := []byte{66, 108, 97, 104, 44, 32, 98, 108, 97, 104, 44, 32, 98, 108, 97, 104}

    output := &kms.DecryptOutput{
        Plaintext: plainText,
    }

    return output, nil
}

type Config struct {
    Data string `json:"Data"`
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

    if globalConfig.Data == "" {
        msg := "You must supply a value for Data in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestDecrypt(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    api := &KMSDecryptImpl{}

    blob := []byte(globalConfig.Data)

    input := &kms.DecryptInput{
        CiphertextBlob: blob,
    }

    resp, err := DecodeData(context.TODO(), api, input)
    if err != nil {
        t.Log("Got an error ...:")
        t.Log(err)
        return
    }

    t.Log(string(resp.Plaintext))
}
