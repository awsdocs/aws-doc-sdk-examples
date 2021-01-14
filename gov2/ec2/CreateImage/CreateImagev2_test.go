package main

import (
    "context"
    "encoding/json"
    "errors"
    "fmt"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/ec2"
    "github.com/aws/aws-sdk-go-v2/service/ec2/types"
)

type EC2CreateImageImpl struct{}

func (dt EC2CreateImageImpl) CreateImage(ctx context.Context,
    params *ec2.CreateImageInput,
    optFns ...func(*ec2.Options)) (*ec2.CreateImageOutput, error) {

    output := &ec2.CreateImageOutput{
        ImageId: aws.String("aws-docs-example-imageID"),
    }

    return output, nil
}

type Config struct {
    Description string `json:"Description"`
    InstanceID  string `json:"InstanceID"`
    ImageName   string `json:"ImageName"`
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

    if globalConfig.Description == "" || globalConfig.InstanceID == "" || globalConfig.ImageName == "" {
        msg := "You must supply a value for Description, InstanceID, and ImageName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestCreateImage(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    input := &ec2.CreateImageInput{
        Description: &globalConfig.Description,
        InstanceId:  &globalConfig.InstanceID,
        Name:        &globalConfig.ImageName,
        BlockDeviceMappings: []*types.BlockDeviceMapping{
            {
                DeviceName: aws.String("/dev/sda1"),
                NoDevice:   aws.String(""),
            },
            {
                DeviceName: aws.String("/dev/sdb"),
                NoDevice:   aws.String(""),
            },
            {
                DeviceName: aws.String("/dev/sdc"),
                NoDevice:   aws.String(""),
            },
        },
    }

    api := &EC2CreateImageImpl{}

    resp, err := MakeImage(context.TODO(), api, input)
    if err != nil {
        fmt.Println("Got an error createing image:")
        fmt.Println(err)
        return
    }

    t.Log("ID: " + *resp.ImageId)
}
