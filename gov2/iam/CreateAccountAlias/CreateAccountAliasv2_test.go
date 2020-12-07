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

type IAMCreateAccountAliasImpl struct{}

func (dt IAMCreateAccountAliasImpl) CreateAccountAlias(ctx context.Context,
    params *iam.CreateAccountAliasInput,
    optFns ...func(*iam.Options)) (*iam.CreateAccountAliasOutput, error) {

    if nil == params.AccountAlias || *params.AccountAlias == "" {
        msg := "AccountAlias arg is nil or an empty string"
        return nil, errors.New(msg)
    }

    output := &iam.CreateAccountAliasOutput{}

    return output, nil
}

type Config struct {
    Alias string `json:"Alias"`
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

    if globalConfig.Alias == "" {
        msg := "You must supply a value for Alias in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestCreateAccountAlias(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMCreateAccountAliasImpl{}

    input := &iam.CreateAccountAliasInput{
        AccountAlias: &globalConfig.Alias,
    }

    _, err = MakeAccountAlias(context.Background(), api, input)
    if err != nil {
        t.Log("Got an error creating an account alias")
        t.Log(err)
        return
    }

    t.Log("Created account alias " + globalConfig.Alias)
}
