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

type IAMCreateUserImpl struct{}

func (dt IAMCreateUserImpl) CreateUser(ctx context.Context,
    params *iam.CreateUserInput,
    optFns ...func(*iam.Options)) (*iam.CreateUserOutput, error) {

    // Create an example user.
    user := &types.User{
        Arn:        aws.String("aws-docs-example-user1-arn"),
        CreateDate: aws.Time(time.Now()),
        Path:       aws.String("aws-docs-example-user1-path"),
        UserId:     aws.String("aws-docs-example-user1-path"),
        UserName:   aws.String("aws-docs-example-user1-name"),
    }

    output := &iam.CreateUserOutput{
        User: user,
    }

    return output, nil
}

type Config struct {
    UserName string `json:"UserName"`
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

    if globalConfig.UserName == "" {
        msg := "You must supply a value for UserName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestCreateUser(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    api := &IAMCreateUserImpl{}

    input := &iam.CreateUserInput{
        UserName: &globalConfig.UserName,
    }

    results, err := MakeUser(context.TODO(), api, input)
    if err != nil {
        t.Log("Got an error creating user " + globalConfig.UserName)
    }

    t.Log("Created user " + *results.User.UserName)
}
