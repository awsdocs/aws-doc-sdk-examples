package main

import (
    "context"
    "encoding/json"
    "io/ioutil"
    "strconv"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/iam"
)

type IAMListAccountAliasesImpl struct{}

func (dt IAMListAccountAliasesImpl) ListAccountAliases(ctx context.Context,
    params *iam.ListAccountAliasesInput,
    optFns ...func(*iam.Options)) (*iam.ListAccountAliasesOutput, error) {

    aliases := make([]*string, 2)
    aliases[0] = aws.String("aws-docs-account-alias1")
    aliases[1] = aws.String("aws-docs-account-alias2")

    output := &iam.ListAccountAliasesOutput{
        AccountAliases: aliases,
    }

    return output, nil
}

type Config struct {
    MaxItemsString string `json:"MaxItems"`
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

    if globalConfig.MaxItemsString == "" {
        globalConfig.MaxItemsString = "10"
    }

    return nil
}

func TestListAccountAliases(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    maxItems, err := strconv.Atoi(globalConfig.MaxItemsString)
    if err != nil {
        msg := "The value of MaxItems (" + globalConfig.MaxItemsString + ") is not an integer"
        t.Log(msg)
        return
    }

    api := &IAMListAccountAliasesImpl{}

    input := &iam.ListAccountAliasesInput{
        MaxItems: aws.Int32(int32(maxItems)),
    }

    result, err := GetAccountAliases(context.TODO(), api, input)
    if err != nil {
        t.Log("Got an error retrieving account aliases")
        t.Log(err)
        return
    }

    for _, alias := range result.AccountAliases {
        t.Log("Alias: " + *alias)
    }
}
