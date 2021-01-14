package main

import (
    "context"
    "encoding/json"
    "errors"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/ec2"
)

type EC2RebootInstancesImpl struct{}

func (dt EC2RebootInstancesImpl) RebootInstances(ctx context.Context,
    params *ec2.RebootInstancesInput,
    optFns ...func(*ec2.Options)) (*ec2.RebootInstancesOutput, error) {

    output := &ec2.RebootInstancesOutput{}

    if *params.DryRun {
        return output, errors.New("api error DryRunOperation")
    }

    return output, nil
}

type Config struct {
    InstanceID string `json:"InstanceID"`
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

    if globalConfig.InstanceID == "" {
        msg := "You must specify a value for InstanceID in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestStopInstances(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    input := &ec2.RebootInstancesInput{
        InstanceIds: []*string{
            &globalConfig.InstanceID,
        },
        DryRun: aws.Bool(true),
    }

    api := &EC2RebootInstancesImpl{}

    _, err = RebootInstance(context.TODO(), *api, input)
    if err != nil {
        t.Log("Got an error ...:")
        t.Log(err)
        return
    }

    t.Log("Rebooted instance with ID " + globalConfig.InstanceID)
}
