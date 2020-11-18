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
    "github.com/aws/aws-sdk-go-v2/service/sqs"
    "github.com/aws/aws-sdk-go-v2/service/sqs/types"
)

type SQSSendMessageImpl struct{}

func (dt SQSSendMessageImpl) GetQueueUrl(ctx context.Context,
    params *sqs.GetQueueUrlInput,
    optFns ...func(*sqs.Options)) (*sqs.GetQueueUrlOutput, error) {

    // URLs look like:
    //    https://sqs.REGION.amazonaws.com/ACCOUNT#/QUEUE-NAME
    prefix := "https://sqs.REGION.amazonaws.com/ACCOUNT#/"

    output := &sqs.GetQueueUrlOutput{
        QueueUrl: aws.String(prefix + "aws-docs-example-queue-url1"),
    }

    return output, nil
}

func (dt SQSSendMessageImpl) SendMessage(ctx context.Context,
    params *sqs.SendMessageInput,
    optFns ...func(*sqs.Options)) (*sqs.SendMessageOutput, error) {

    output := &sqs.SendMessageOutput{
        MessageId: aws.String("aws-docs-example-messageID"),
    }

    return output, nil
}

type Config struct {
    QueueName string `json:"QueueName"`
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

    if globalConfig.QueueName == "" {
        msg := "You musts supply a value for QueueName in " + configFileName
        return errors.New(msg)
    }

    return nil
}

func TestSendMessage(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    api := &SQSSendMessageImpl{}

    // Get URL of queue
    gQInput := &sqs.GetQueueUrlInput{
        QueueName: &globalConfig.QueueName,
    }

    result, err := GetQueueURL(context.Background(), api, gQInput)
    if err != nil {
        fmt.Println("Got an error getting the queue URL:")
        fmt.Println(err)
        return
    }

    queueURL := result.QueueUrl

    sMInput := &sqs.SendMessageInput{
        DelaySeconds: aws.Int32(10),
        MessageAttributes: map[string]*types.MessageAttributeValue{
            "Title": &types.MessageAttributeValue{
                DataType:    aws.String("String"),
                StringValue: aws.String("The Whistler"),
            },
            "Author": &types.MessageAttributeValue{
                DataType:    aws.String("String"),
                StringValue: aws.String("John Grisham"),
            },
            "WeeksOn": &types.MessageAttributeValue{
                DataType:    aws.String("Number"),
                StringValue: aws.String("6"),
            },
        },
        MessageBody: aws.String("Information about current NY Times fiction bestseller for week of 12/11/2016."),
        QueueUrl:    queueURL,
    }

    resp, err := SendMsg(context.Background(), api, sMInput)
    if err != nil {
        fmt.Println("Got an error sending the message:")
        fmt.Println(err)
        return
    }

    t.Log("Sent message with ID: " + *resp.MessageId)
}
