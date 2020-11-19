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

type IAMDeleteUserImpl struct{}

func (dt IAMDeleteUserImpl) DeleteUser(ctx context.Context,
    params *iam.DeleteUserInput,
    optFns ...func(*iam.Options)) (*iam.DeleteUserOutput, error) {

    output := &iam.DeleteUserOutput{}

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

func TestDeleteUser(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMDeleteUserImpl{}

    input := &iam.DeleteUserInput{
        UserName: &globalConfig.UserName,
    }

    _, err = RemoveUser(context.Background(), api, input)
    if err != nil {
        fmt.Println("Got an error deleting user " + globalConfig.UserName)
    }

    t.Log("Deleted user " + globalConfig.UserName)
}
