package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"strconv"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
)

type IAMListUsersImpl struct{}

func (dt IAMListUsersImpl) ListUsers(ctx context.Context,
	params *iam.ListUsersInput,
	optFns ...func(*iam.Options)) (*iam.ListUsersOutput, error) {

	// Create dummy list of two users
	users := make([]*types.User, 2)
	users[0] = &types.User{
		Arn:        aws.String("aws-docs-example-user1-arn"),
		CreateDate: aws.Time(time.Now()),
		Path:       aws.String("aws-docs-example-user1-path"),
		UserId:     aws.String("aws-docs-example-user1-path"),
		UserName:   aws.String("aws-docs-example-user1-name"),
	}
	users[1] = &types.User{
		Arn:        aws.String("aws-docs-example-user2-arn"),
		CreateDate: aws.Time(time.Now()),
		Path:       aws.String("aws-docs-example-user2-path"),
		UserId:     aws.String("aws-docs-example-user2-path"),
		UserName:   aws.String("aws-docs-example-user2-name"),
	}

	output := &iam.ListUsersOutput{
		Users: users,
	}

	return output, nil
}

type Config struct {
	MaxUsers string `json:"MaxUsers"`
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

	if globalConfig.MaxUsers == "" {
		msg := "You must specifiy a MaxUsers value in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestListUsers(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	api := &IAMListUsersImpl{}

	maxUsers, err := strconv.ParseInt(globalConfig.MaxUsers, 10, 32)
	if err != nil {
		t.Log(globalConfig.MaxUsers + " is not an integer")
		t.Log(err)
		return
	}

	input := &iam.ListUsersInput{
		MaxItems: aws.Int32(int32(maxUsers)),
	}

	result, err := GetUsers(context.Background(), api, input)
	if err != nil {
		t.Log("Got an error retrieving users:")
		t.Log(err)
		return
	}

	for _, user := range result.Users {
		t.Log("ARN:       ", *user.Arn)
		t.Log("CreateDate:", *user.CreateDate)
		t.Log("Path:      ", *user.Path)
		t.Log("UserID:    ", *user.UserId)
		t.Log("User name: ", *user.UserName)
		t.Log("")
	}
}
