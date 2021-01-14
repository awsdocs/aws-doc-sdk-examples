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

type IAMDetachRolePolicyImpl struct{}

func (dt IAMDetachRolePolicyImpl) DetachRolePolicy(ctx context.Context,
    params *iam.DetachRolePolicyInput,
    optFns ...func(*iam.Options)) (*iam.DetachRolePolicyOutput, error) {

    output := &iam.DetachRolePolicyOutput{}

    return output, nil
}

type Config struct {
    RoleName string `json:"RoleName"`
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

    if globalConfig.RoleName == "" {
        msg := "You must supply a value for RoleName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestDetachRolePolicy(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMDetachRolePolicyImpl{}

    policyArn := "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"
    input := &iam.DetachRolePolicyInput{
        PolicyArn: &policyArn,
        RoleName:  &globalConfig.RoleName,
    }

    _, err = DetachDynamoFullPolicy(context.TODO(), api, input)
    if err != nil {
        fmt.Println("Unable to detach DynamoDB full-access role policy from role")
        return
    }

    fmt.Println("DynamoDB full-access role policy successfully detached from role " + globalConfig.RoleName)
}
