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

type IAMGetAccessKeyLastUsedImpl struct{}

func (dt IAMGetAccessKeyLastUsedImpl) GetAccessKeyLastUsed(ctx context.Context,
    params *iam.GetAccessKeyLastUsedInput,
    optFns ...func(*iam.Options)) (*iam.GetAccessKeyLastUsedOutput, error) {

    if nil == params.AccessKeyId || *params.AccessKeyId == "" {
        return nil, errors.New("The AccessKeyId parameter is nil or an empty string")
    }

    used := types.AccessKeyLastUsed{
        LastUsedDate: aws.Time(time.Now()),
        Region:       aws.String("us-west-2"),
        ServiceName:  aws.String("iam"),
    }

    output := &iam.GetAccessKeyLastUsedOutput{
        AccessKeyLastUsed: &used,
        UserName:          aws.String("aws-docs-example-username"),
    }

    return output, nil
}

type Config struct {
    KeyID string `json:"KeyID"`
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

    if globalConfig.KeyID == "" {
        msg := "You must supply a value for KeyID in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestGetAccessKeyLastUsed(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMGetAccessKeyLastUsedImpl{}

    input := &iam.GetAccessKeyLastUsedInput{
        AccessKeyId: &globalConfig.KeyID,
    }

    result, err := WhenWasKeyUsed(context.TODO(), api, input)
    if err != nil {
        t.Log("Got an error retrieving when access key was last used:")
        t.Log(err)
        return
    }

    t.Log("The key was last used:", *result.AccessKeyLastUsed)
}
