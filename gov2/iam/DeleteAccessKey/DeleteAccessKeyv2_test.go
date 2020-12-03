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

type IAMDeleteAccessKeyImpl struct{}

func (dt IAMDeleteAccessKeyImpl) DeleteAccessKey(ctx context.Context,
    params *iam.DeleteAccessKeyInput,
    optFns ...func(*iam.Options)) (*iam.DeleteAccessKeyOutput, error) {

    output := &iam.DeleteAccessKeyOutput{}

    return output, nil
}

type Config struct {
    KeyID    string `json:"KeyID"`
    UserName string `json:"UserName"`
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

    if globalConfig.KeyID == "" || globalConfig.UserName == "" {
        msg := "You must supply a value for KeyID and UserName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestDeleteAccessKey(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMDeleteAccessKeyImpl{}

    input := &iam.DeleteAccessKeyInput{
        AccessKeyId: &globalConfig.KeyID,
        UserName:    &globalConfig.UserName,
    }

    _, err = RemoveAccessKey(context.Background(), api, input)
    if err != nil {
        t.Log("Error", err)
        return
    }

    t.Log("Deleted key with ID " + globalConfig.KeyID + " from user " + globalConfig.UserName)
}
