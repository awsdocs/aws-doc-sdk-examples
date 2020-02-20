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
    "errors"
    "fmt"
    "io/ioutil"
    "os"
    "strconv"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)

/* ChangeVisibility changes the timeout visibility of a message
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     queueURL is the URL of the queue containing the message
 *     receiptHandle is the receipt handle of the message to modify
 * Output:
 *     If success, nil
 *     Otherwise, an error from the call to ChangeMessageVisibility
 */
// snippet-start:[sqs.go.change_visibility]
func ChangeVisibility(sess *session.Session, queueURL string, receiptHandle string, duration int64) error {
    // Create a SQS service client
    svc := sqs.New(sess)

    return svc.ChangeMessageVisibility(&sqs.ChangeMessageVisibilityInput{
        ReceiptHandle:     aws.String(receiptHandle),
        QueueUrl:          aws.String(queueURL),
        VisibilityTimeout: &duration,
    })
}
// snippet-end:[sqs.go.change_visibility]

/* ChangeQueue updates a queue to use long polling
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     queueURL is the URL of the queue
 *     timeout is the length of time, in seconds from 0 to 20, that ReceiveMessage
 *     waits for a message to arrive
 * Output:
 *     If success, nil
 *     Otherwise, an error from the call to SetQueueAttributes
 */
// snippet-start:[sqs.go.change_queue]
func ChangeQueue(sess *session.Session, queueURL string, timeout int) error {
    if timeout < 0 {
        timeout = 0
    }

    if timeout > 20 {
        timeout = 20
    }
    
    svc := sqs.New(sess)

    return svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
        QueueUrl: aws.String(queueURL),
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": strconv.Itoa(timeout),
        }),
    })
}
// snippet-end:[sqs.go.change_queue]

/* CreateQueue creates an Amazon SQS queue
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     queueName is the name of the queue to create
 * Output:
 *     If success, the queue's URL and nil
 *     Otherwise, an empty string and an error from the call to CreateQueue
 */
// snippet-start:[sqs.go.create_queue]
func CreateQueue(sess *session.Session, queueName string) (string, error) {
    // Create an Amazon SQS service client
    svc := sqs.New(sess)

    result, err := svc.CreateQueue(&sqs.CreateQueueInput{
        QueueName: aws.String(queueName),
        Attributes: map[string]*string{
            "DelaySeconds":           aws.String("60"),
            "MessageRetentionPeriod": aws.String("86400"),
        },
    })
    if err != nil {
        return "", err
    }

    return *result.QueueUrl, nil
}
// snippet-end:[sqs.go.create_queue]

/* DeleteMessage deletes a message
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     queueURL is the URL of the queue
 *     receiptHandle is the receipt handle of the message
 * Output:
 *     If success, nil
 *     Otherwise, an error from the call to DeleteMessage
 */
// snippet-start:[sqs.go.delete_message]
func DeleteMessage(sess *session.Session, queueURL string, receiptHandle string) error {
    svc := sqs.New(sess)

    return svc.DeleteMessage(&sqs.DeleteMessageInput{
        QueueUrl:      aws.String(queueURL),
        ReceiptHandle: aws.String(receiptHandle),
    })
}
// snippet-end:[sqs.go.delete_message]

/* listQueues displays a list of your queues
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 * Output:
 *     If success, nil
 *     Otherwise, an error from the call to ListQueues
 */
// snippet-start:[sqs.go.list_queues]
func listQueues(sess *session.Session) error {
    // Create an SQS service client
    svc := sqs.New(sess)

    result, err := svc.ListQueues(nil)
    if err != nil {
        return err
    }

    length := len(result.QueueUrls)

    fmt.Println("Queue URLs (" + strconv.Itoa(length) + "):")

    for _, url := range result.QueueUrls {
        fmt.Println(*url)
    }

    return nil
}
// snippet-end:[sqs.go.list_queues]

/* ConfigureDeadLetterQueue configures a queue as a dead letter queue
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     deadLetterQueueURL is the queue that gets dead letters from the queue with URL queueURL
 *     queueURL is the URL of the queue that sends dead letters to the queue with the URL deadLetterQueueURL
 *     maxReceive is the number of times SQS tries to deliver the message to the source queue before it moves the message to the dead-letter queue
 * Output:
 *     If success, nil
 *     Otherwise, an error from the call to GetQueueAttributes, json.Marshal, or SetQueueAttributes
 */
// snippet-start:[sqs.go.configure_deadletter_queue]
func ConfigureDeadLetterQueue(sess *session.Session, deadLetterQueueURL string, queueURL string, maxReceive int) error {
    // Create a SQS service client
    svc := sqs.New(sess)

    // Get ARN for dead-letter queue
    atts, err := svc.GetQueueAttributes(&sqs.GetQueueAttributesInput{
        AttributeNames: []*string{
            aws.String("QueueArn"),
        },
        QueueUrl: aws.String(deadLetterQueueURL),
    })
    if err != nil {
        return err
    }

    queueARN := *atts.Attributes["QueueArn"]

    // Our retry policy for our queue
    policy := map[string]string{
        "deadLetterTargetArn": queueARN,
        "maxReceiveCount":     strconv.Itoa(maxReceive),
    }

    // Marshal our policy to be used as input for our SetQueueAttributes
    // call.
    b, err := json.Marshal(policy)
    if err != nil {
        return err
    }

    _, err = svc.SetQueueAttributes(&sqs.SetQueueAttributesInput{
        QueueUrl: aws.String(queueURL),
        Attributes: map[string]*string{
            sqs.QueueAttributeNameRedrivePolicy: aws.String(string(b)),
        },
    })
    if err != nil {
        return err
    }

    return nil
}
// snippet-end:[sqs.go.configure_deadletter_queue]

/* CreateLongPollingQueue creates a queue with long polling enabled
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     queueName is the name of the queue
 *     timeout is the length of time, in seconds from 0 to 20, that ReceiveMessage
 *     waits for a message to arrive
 * Output:
 *     If success, the URL of the queue and nil
 *     Otherwise, an empty string and an error from the call to CreateQueue
 */
// snippet-start:[sqs.go.create_longpolling_queue]
func CreateLongPollingQueue(sess *session.Session, queueName string, timeout int) (string, error) {
    if timeout < 0 {
        timeout = 0
    }

    if timeout > 20 {
        timeout = 20
    }
    
    svc := sqs.New(sess)

    // Create the Queue with long polling enabled
    result, err := svc.CreateQueue(&sqs.CreateQueueInput{
        QueueName: aws.String(queueName),
        Attributes: aws.StringMap(map[string]string{
            "ReceiveMessageWaitTimeSeconds": strconv.Itoa(timeout),
        }),
    })
    if err != nil {
        return "", err
    }

    return *result.QueueUrl, nil
}
// snippet-end:[sqs.go.create_longpolling_queue]

/* DeleteQueue deletes a queue
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     queueURL is the URL of the queue
 * Output:
 *     If success, nil
 *     Otherwise, an error from the call to DeleteQueue
 */
// snippet-start:[sqs.go.delete_queue]
func DeleteQueue(sess *session.Session, queueURL string) error {
    // Create a SQS service client.
    svc := sqs.New(sess)

    _, err := svc.DeleteQueue(&sqs.DeleteQueueInput{
        QueueUrl: aws.String(queueURL),
    })
    if err != nil {
        return err
    }

    return nil
}
// snippet-end:[sqs.go.delete_queue]

/* GetQueueURL returns the URL of the queue with the name queueName.
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     queueName is the name of the queue
 * Output:
 *     If success, the URL of the queue and nil
 *     Otherwise, an empty string and an error from the call to GetQueueUrl
 */
// snippet-start:[sqs.go.get_queue_url]
func GetQueueURL(sess *session.Session, queueName string) (string, error) {
    // Create a SQS service client
    svc := sqs.New(sess)

    result, err := svc.GetQueueUrl(&sqs.GetQueueUrlInput{
        QueueName: aws.String(queueName),
    })
    if err != nil {
        return "", err
    }

    return *result.QueueUrl, nil
}
// snippet-end:[sqs.go.get_queue_url]

/* ReceiveMessages gets up to 10 messages from a queue
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     queueURL is the URL of the queue
 * Output:
 *     If success, the messages from the queue and nil
 *     Otherwise, an empty array and an error from the call to ReceiveMessage
 */
// snippet-start:[sqs.go.receive_messages]
func ReceiveMessages(sess *session.Session, queueURL string) ([]*sqs.Message, error) {
    var msgs []*sqs.Message

    svc := sqs.New(sess)

    result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
        AttributeNames: []*string{
            aws.String(sqs.MessageSystemAttributeNameSentTimestamp),
        },
        MessageAttributeNames: []*string{
            aws.String(sqs.QueueAttributeNameAll),
        },
        QueueUrl:            aws.String(queueURL),
        MaxNumberOfMessages: aws.Int64(10),
        VisibilityTimeout:   aws.Int64(60), // 60 seconds
        WaitTimeSeconds:     aws.Int64(0),
    })
    if err != nil {
        return msgs, err
    }
    if len(result.Messages) == 0 {
        msg := "Received no messages"
        return msgs, errors.New(msg)
    }

    return result.Messages, nil
}

// snippet-end:[sqs.go.receive_messages]

/* ReceiveLongPollingMessages receives messages a long-polling queue
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     queueURL is the URL of the queue
 *     timeout is the length of time, in seconds from 0 to 20, that ReceiveMessage
 *     waits for a message to arrive
 * Output:
 *     If success, the messages from the queue and nil
 *     Otherwise, an empty array and an error from the call to ReceiveMessage
 */
// snippet-start:[sqs.go.receive_longpolling_messages]
func ReceiveLongPollingMessages(sess *session.Session, queueURL string, timeout int64) ([]*sqs.Message, error) {
    var msgs []*sqs.Message
    svc := sqs.New(sess)

    // Receive a message from the SQS queue with long polling enabled.
    result, err := svc.ReceiveMessage(&sqs.ReceiveMessageInput{
        QueueUrl: aws.String(queueURL),
        AttributeNames: aws.StringSlice([]string{
            "SentTimestamp",
        }),
        MaxNumberOfMessages: aws.Int64(1),
        MessageAttributeNames: aws.StringSlice([]string{
            "All",
        }),
        WaitTimeSeconds: &timeout,
    })
    if err != nil {
        return msgs, err
    }

    return result.Messages, nil
}
// snippet-end:[sqs.go.receive_longpolling_messages]

/* SendMessage sends a message to a queue
 * Inputs:
 *     sess is the current session, which provides configuration for the SDK's service clients
 *     queueURL is the URL of the queue
 * Output:
 *     If success, the ID of the messages and nil
 *     Otherwise, an empty string and an error from the call to SendMessage
 */
// snippet-start:[sqs.go.send_message]
func SendMessage(sess *session.Session, queueURL string) (string, error) {
    svc := sqs.New(sess)

    result, err := svc.SendMessage(&sqs.SendMessageInput{
        DelaySeconds: aws.Int64(10),
        MessageAttributes: map[string]*sqs.MessageAttributeValue{
            "Title": &sqs.MessageAttributeValue{
                DataType:    aws.String("String"),
                StringValue: aws.String("The Whistler"),
            },
            "Author": &sqs.MessageAttributeValue{
                DataType:    aws.String("String"),
                StringValue: aws.String("John Grisham"),
            },
            "WeeksOn": &sqs.MessageAttributeValue{
                DataType:    aws.String("Number"),
                StringValue: aws.String("6"),
            },
        },
        MessageBody: aws.String("Information about current NY Times fiction bestseller for week of 12/11/2016."),
        QueueUrl:    aws.String(queueURL),
    })
    if err != nil {
        return "", err
    }

    return *result.MessageId, nil
}
// snippet-end:[sqs.go.send_message]

// Configuration defines a set of configuration values
type Config struct {
    Timeout      int `json:"Timeout"`
    RetrySeconds int `json:"RetrySeconds"`
}

// ConfigFile defines the name of the file containing configuration values
var ConfigFile = "config.json"

// GlobalConfig contains the configuration values
var GlobalConfig Config

/* PopulateConfiguration gets values from config.json and populates the configuration struct GlobalConfig
 * Inputs:
 *     none
 * Output:
 *     If success, nil
 *     Otherwise, an error from reading or parsing the configuration file
 */
func PopulateConfiguration() error {
    // Get configuration from config.json

    // Get entire file as a JSON string
    content, err := ioutil.ReadFile(ConfigFile)
    if err != nil {
        return err
    }

    // Convert []byte to string
    text := string(content)

    // Marshall JSON string in text into global struct
    err = json.Unmarshal([]byte(text), &GlobalConfig)
    if err != nil {
        return err
    }

    // Set minimum wait, in seconds, before reading/retrying
    if GlobalConfig.RetrySeconds < 10 {
        GlobalConfig.RetrySeconds = 10
    }

    // Set minimum duration for timeout
    if GlobalConfig.Timeout < 20 {
        GlobalConfig.Timeout = 20
    }

    return nil
}

/* usage displays information on using this app
 * Inputs:
 *     none
 * Output:
 *     none
 */
func usage() {
    fmt.Println("Usage:")
    fmt.Println("")
    fmt.Println("    go run sqsQueueOps.go OPERATION")
    fmt.Println("")
    fmt.Println("where OPERATION is one of:")
    fmt.Println("    -l")
    fmt.Println("        list your queues")
    fmt.Println("    -c  QUEUE-NAME")
    fmt.Println("        create queue QUEUE-NAME")
    fmt.Println("    -lp QUEUE-NAME")
    fmt.Println("        create long-polling queue QUEUE-NAME")
    fmt.Println("    -dq QUEUE-NAME")
    fmt.Println("        delete queue QUEUE-NAME")
    fmt.Println("    -x  QUEUE-NAME")
    fmt.Println("        convert queue QUEUE-NAME to long-polling")
    fmt.Println("    -dl DL-QUEUE-NAME QUEUE-NAME")
    fmt.Println("        create dead-letter queue DL-QUEUE-NAME for queue QUEUE-NAME")
    fmt.Println("    -cv MESSAGE-ID QUEUE-NAME")
    fmt.Println("        change the timeout visibilty for message with ID MESSAGE-ID in queue QUEUE-NAME")
    fmt.Println("    -r  QUEUE-NAME")
    fmt.Println("        receive messages in queue QUEUE-NAME")
    fmt.Println("    -dm RECEIPT-HANDLE QUEUE-NAME")
    fmt.Println("        deletes the message with RECEIPT-HANDLE from queue QUEUE-NAME")
    fmt.Println("    -rl QUEUE-NAME")
    fmt.Println("        receive messages from long-polling queue QUEUE-NAME")
    fmt.Println("    -s  QUEUE-NAME")
    fmt.Println("        sends a message to queue QUEUE-NAME")
    fmt.Println("    -h")
    fmt.Println("        prints this message and quits")
    fmt.Println("")
}

func main() {
    err := PopulateConfiguration()
    if err != nil {
        fmt.Println("Got an error populating configuration:")
        fmt.Println(err)
        return
    }

    // Parse args ourselves so user can only request one operation
    op := ""
    numOps := 0 // So we know when user has requested more than one
    length := len(os.Args)
    i := 1
    queue := ""
    dlQueue := ""
    receiptHandle := ""

    for i < length {
        switch os.Args[i] {
        case "-h":
            usage()
            return
        case "-c":
            // -c QUEUE-NAME
            op = "create"
            i++
            queue = os.Args[i]
            numOps++

        case "-cv":
            // -cv MESSAGE-ID QUEUE-NAME
            op = "changeVisibility"
            i++
            receiptHandle = os.Args[i]
            i++
            queue = os.Args[i]
            numOps++

        case "-l":
            // -l
            op = "list"
            numOps++

        case "-lp":
            // -lp QUEUE-NAME
            op = "createLp"
            i++
            queue = os.Args[i]
            numOps++

        case "-dl":
            // -dl DL-QUEUE-NAME QUEUE-NAME
            op = "createDl"
            i++
            dlQueue = os.Args[i]
            i++
            queue = os.Args[i]
            numOps++

        case "-dm":
            // -dm RECEIPT-HANDLE QUEUE-NAME
            op = "deleteMessage"
            i++
            receiptHandle = os.Args[i]
            i++
            queue = os.Args[i]
            numOps++

        case "-dq":
            op = "deleteQueue"
            i++
            queue = os.Args[i]
            numOps++

        case "-r":
            op = "receive"
            i++
            queue = os.Args[i]
            numOps++

        case "-rl":
            op = "receiveLp"
            i++
            queue = os.Args[i]
            numOps++

        case "-s":
            // -s  QUEUE-NAME
            op = "sendMsg"
            i++
            queue = os.Args[i]
            numOps++

        case "-x":
            op = "convert"
            i++
            queue = os.Args[i]
            numOps++

        default:
            fmt.Println("Unrecognized option: " + os.Args[i])
            usage()
            return
        }

        i++
    }

    if numOps > 1 {
        fmt.Println("You cannot request more than one operation")
        usage()
        return
    }

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    switch op {
    case "changeVisibility":
        if queue == "" || receiptHandle == "" {
            fmt.Println("You must supply a queue name and receipt handle")
            usage()
            return
        }

        queueURL, err := GetQueueURL(sess, queue)
        if err != nil {
            fmt.Println("Got an error retrieving URL for queue:")
            fmt.Println(err)
            return
        }

        err = ChangeVisibility(sess, queueURL, receiptHandle, int64(GlobalConfig.Timeout))
        if err != nil {
            fmt.Println("Got an error changing visibility:")
            fmt.Println(err)
            return
        }

        fmt.Println("Changed the visibility of message with receipt handle " + receiptHandle + "in queue " + queue)

    case "convert":
        if queue == "" {
            fmt.Println("You must supply the name of the queue to convert to long polling")
            usage()
            return
        }

        queueURL, err := GetQueueURL(sess, queue)
        if err != nil {
            fmt.Println("Got an error retrieving URL for queue:")
            fmt.Println(err)
            return
        }

        err = ChangeQueue(sess, queueURL, GlobalConfig.Timeout)
        if err != nil {
            fmt.Println("Got an error converting queue to long polling:")
            fmt.Println(err)
            return
        }

        fmt.Println("Converted queue " + queue + " to long-polling")

    case "createDl":
        if dlQueue == "" || queue == "" {
            fmt.Println("You must supply the name of the dead letter queue to create and the queue that might have dead letters")
            usage()
            return
        }

        // First create the dead-letter queue
        dlQueueURL, err := CreateQueue(sess, dlQueue)
        if err != nil {
            fmt.Println("Got an error creating dead-letter queue:")
            fmt.Println(err)
            return
        }

        queueURL, err := GetQueueURL(sess, queue)
        if err != nil {
            fmt.Println("Got an error retrieving URL for queue:")
            fmt.Println(err)
            return
        }

        err = ConfigureDeadLetterQueue(sess, dlQueueURL, queueURL, 10)
        if err != nil {
            fmt.Println("Got an error configuring dead-letter queue:")
            fmt.Println(err)
            return
        }

        fmt.Println("Successfully configured " + dlQueue + " as a dead-letter queue")

    case "createLp":
        if queue == "" {
            fmt.Println("You must supply the name of the long-polling queue to create")
            usage()
            return
        }

        fmt.Println("Creating long-polling queue " + queue)
        _, err := CreateLongPollingQueue(sess, queue, GlobalConfig.Timeout)
        if err != nil {
            fmt.Println("Got an error creating long-polling queue:")
            fmt.Println(err)
            return
        }

        fmt.Println("Successfully created long-polling queue: " + queue)

    case "create":
        if queue == "" {
            fmt.Println("You must supply the name of the queue to create")
            usage()
            return
        }

        fmt.Println("Creating queue " + queue)
        _, err := CreateQueue(sess, queue)
        if err != nil {
            fmt.Println("Got error creating queue:")
            fmt.Println(err)
            return
        }

        fmt.Println("Successfully created queue " + queue)

    case "deleteMessage":
        if queue == "" || receiptHandle == "" {
            fmt.Println("You must supply the name of the queue containing the message and the receipt handle of the message")
            usage()
            return
        }

        queueURL, err := GetQueueURL(sess, queue)
        if err != nil {
            fmt.Println("Got an error retrieving the URL for queue " + queue)
            fmt.Println(err)
            return
        }

        // DeleteMessage(sess *session.Session, queueURL string, receiptHandle string) error
        err = DeleteMessage(sess, queueURL, receiptHandle)
        if err != nil {
            fmt.Println("Got an error deleting message:")
            fmt.Println(err)
            return
        }

        fmt.Println("Deleted message")

    case "deleteQueue":
        if queue == "" {
            fmt.Println("You must supply the name of the queue to delete")
            usage()
            return
        }

        queueURL, err := GetQueueURL(sess, queue)
        if err != nil {
            fmt.Println("Got an error retrieving URL for queue:")
            fmt.Println(err)
            return
        }

        err = DeleteQueue(sess, queueURL)
        if err != nil {
            fmt.Println("Got an error trying to delete queue " + queue)
            fmt.Println(err)
            return
        }

        fmt.Println("Deleted queue " + queue)

    case "list":
        fmt.Println("Listing queues")
        err := listQueues(sess)
        if err != nil {
            fmt.Println("Got an error listing the queues:")
            fmt.Println(err)
            return
        }

    case "receive":
        if queue == "" {
            fmt.Println("You must supply the name of the queue to receive messages from")
            usage()
            return
        }

        queueURL, err := GetQueueURL(sess, queue)
        if err != nil {
            fmt.Println("Got an error retrieving URL for queue:")
            fmt.Println(err)
            return
        }

        fmt.Println("Receiving messages from queue with URL " + queueURL)
        // ReceiveMessages(sess *session.Session, queueURL string) ([]*sqs.Message, error)
        msgs, err := ReceiveMessages(sess, queueURL)
        if err != nil {
            fmt.Println("Got an error calling ReceiveMessages:")
            fmt.Println(err)
            return
        }
        // Barf out message receipt handles
        fmt.Println("Receipt handles:")
        for _, m := range msgs {
            fmt.Println(*m.ReceiptHandle)
        }

    case "receiveLp":
        if queue == "" {
            fmt.Println("You must supply the name of the queue to receive messages from")
            usage()
            return
        }

        queueURL, err := GetQueueURL(sess, queue)
        if err != nil {
            fmt.Println("Got an error retrieving URL for queue:")
            fmt.Println(err)
            return
        }

        fmt.Println("Receiving messages from queue with URL " + queueURL)
        // ReceiveLongPollingMessages(sess *session.Session, queueURL string, timeout int64) ([]*sqs.Message, error)
        msgs, err := ReceiveLongPollingMessages(sess, queueURL, int64(GlobalConfig.Timeout))
        if err != nil {
            fmt.Println("Got an error retrieving messages from long-polling queue:")
            fmt.Println(err)
            return
        }
        // Barf out message receipt handles
        fmt.Println("Receipt handles:")
        for _, m := range msgs {
            fmt.Println(m.ReceiptHandle)
        }

    case "sendMsg":
        if queue == "" {
            fmt.Println("You must supply the name of the queue to receive the message")
            usage()
            return
        }

        queueURL, err := GetQueueURL(sess, queue)
        if err != nil {
            fmt.Println("Got an error retrieving the URL for queue:")
            fmt.Println(err)
            return
        }

        // SendMessage(sess *session.Session, queueURL string) (string, error)
        msgID, err := SendMessage(sess, queueURL)
        if err != nil {
            fmt.Println("Got an error sending a message:")
            fmt.Println(err)
            return
        }

        fmt.Println("Sent message ID: " + msgID)

    default:
        // This should never happen
        fmt.Println("Unrecognized operation (typo?): " + op)
        return
    }
}
