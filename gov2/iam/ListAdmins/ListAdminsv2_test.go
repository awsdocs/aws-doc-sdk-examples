package main

import (
	"context"
	"encoding/json"
	"io/ioutil"
	"strings"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
)

type Config struct {
	EnableDetails string `json:"ShowDetails"`
	ShowDetails   bool
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

	if globalConfig.EnableDetails == "" {
		globalConfig.ShowDetails = false
	} else {
		globalConfig.ShowDetails = true
	}

	return nil
}

func TestListAdmins(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := iam.NewFromConfig(cfg)

	users, admins, err := GetNumUsersAndAdmins(context.Background(), client)
	if err != nil {
		t.Log("Got an error finding users who are admins:")
		t.Log(err)
		return
	}

	userList := strings.Split(users, " ")
	adminList := strings.Split(admins, " ")

	t.Log("")
	t.Log("Found", len(adminList)-1, "admin(s) out of", len(userList)-1, "user(s)")

	if globalConfig.ShowDetails {
		t.Log("")
		t.Log("Users")
		for _, u := range userList {
			t.Log("  " + u)
		}

		t.Log("")
		t.Log("Admins")
		for _, a := range adminList {
			t.Log("  " + a)
		}
	}
}
