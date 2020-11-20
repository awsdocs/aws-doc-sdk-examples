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

type IAMCreateAccessKeyImpl struct{}

func (dt IAMCreateAccessKeyImpl) CreateAccessKey(ctx context.Context,
    params *iam.CreateAccessKeyInput,
    optFns ...func(*iam.Options)) (*iam.CreateAccessKeyOutput, error) {

    output := &iam.CreateAccessKeyOutput{
        AccessKey: &types.AccessKey{
            AccessKeyId:     aws.String("aws-docs-example-accesskey-ID"),
            SecretAccessKey: aws.String("aws-docs-example-accesskey-secretkey"),
        },
    }

    return output, nil
}

type Config struct {
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

    if globalConfig.UserName == "" {
        msg := "You musts supply a value for UserName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestCreateAccessKey(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMCreateAccessKeyImpl{}

    input := &iam.CreateAccessKeyInput{
        UserName: &globalConfig.UserName,
    }

    result, err := MakeAccessKey(context.Background(), api, input)
    if err != nil {
        t.Log("Got an error creating a new access key")
        t.Log(err)
        return
    }

    t.Log("Created new access key with ID: " + *result.AccessKey.AccessKeyId + " and secret key: " + *result.AccessKey.SecretAccessKey)
}
