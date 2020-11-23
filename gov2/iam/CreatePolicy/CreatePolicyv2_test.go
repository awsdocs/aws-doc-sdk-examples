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
)

type IAMCreatePolicyImpl struct{}

func (dt IAMCreatePolicyImpl) CreatePolicy(ctx context.Context,
    params *iam.CreatePolicyInput,
    optFns ...func(*iam.Options)) (*iam.CreatePolicyOutput, error) {

    output := &iam.CreatePolicyOutput{}

    return output, nil
}

type Config struct {
    PolicyName string `json:"PolicyName"`
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

    if globalConfig.PolicyName == "" {
        msg := "You musts supply a value for PolicyName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestCreatePolicy(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMCreatePolicyImpl{}

    b, err := CreatePolicyDoc()
    if err != nil {
        t.Log("Got an error creating the policy doc:")
        t.Log(err)
        return
    }

    input := &iam.CreatePolicyInput{
        PolicyDocument: aws.String(string(b)),
        PolicyName:     &globalConfig.PolicyName,
    }

    _, err = MakePolicy(context.Background(), api, input)
    if err != nil {
        t.Log("Got an error creating the policy:")
        t.Log(err)
        return
    }

    t.Log("Created policy " + globalConfig.PolicyName)
}
