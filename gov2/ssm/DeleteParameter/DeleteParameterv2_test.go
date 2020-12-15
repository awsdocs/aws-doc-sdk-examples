package main

import (
    "context"
    "encoding/json"
    "errors"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/service/ssm"
)

type SSMDeleteParameterImpl struct{}

func (dt SSMDeleteParameterImpl) DeleteParameter(ctx context.Context,
    params *ssm.DeleteParameterInput,
    optFns ...func(*ssm.Options)) (*ssm.DeleteParameterOutput, error) {

    output := &ssm.DeleteParameterOutput{}

    return output, nil
}

type Config struct {
    ParameterName string `json:"ParameterName"`
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

    if globalConfig.ParameterName == "" {
        msg := "You must supply a ParameterName value in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestDeleteParameter(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    api := &SSMDeleteParameterImpl{}

    input := &ssm.DeleteParameterInput{
        Name: &globalConfig.ParameterName,
    }

    _, err = RemoveParameter(context.Background(), *api, input)
    if err != nil {
        t.Log("Got an error ...:")
        t.Log(err)
        return
    }

    t.Log("Deleted parameter " + globalConfig.ParameterName)
}
