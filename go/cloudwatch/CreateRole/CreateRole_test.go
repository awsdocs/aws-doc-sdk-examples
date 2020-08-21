/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package main

import (
    "encoding/json"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/google/uuid"
)

// Config represents the information for the test
type Config struct {
    PolicyName string `json:"PolicyName"`
    RoleName   string `json:"RoleName"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfig(t *testing.T) error {
    content, err := ioutil.ReadFile(configFileName)
    if err != nil {
        return err
    }

    text := string(content)

    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    id := uuid.New()

    if globalConfig.PolicyName == "" {
        globalConfig.PolicyName = "TestPolicy-" + id.String()
    }

    if globalConfig.RoleName == "" {
        globalConfig.RoleName = "TestRole-" + id.String()
    }

    t.Log("PolicyName:  " + globalConfig.PolicyName)
    t.Log("RoleName:    " + globalConfig.RoleName)

    return nil
}

func deleteRole(sess *session.Session, roleName *string) error {
    svc := iam.New(sess)

    _, err := svc.DeleteRole(&iam.DeleteRoleInput{
        RoleName: roleName,
    })
    if err != nil {
        return err
    }

    return nil
}

func TestCreateEvent(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfig(t)
    if err != nil {
        t.Fatal(err)
    }

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    _, err = CreateRole(sess, &globalConfig.RoleName, &globalConfig.PolicyName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created role " + globalConfig.RoleName)

    err = deleteRole(sess, &globalConfig.RoleName)
    if err != nil {
        t.Log("You must delete role " + globalConfig.RoleName + " yourself")
        t.Fatal(err)
    }

    t.Log("Deleted role " + globalConfig.RoleName)
}
