// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

// snippet-start:[gov2.workflows.TopicsAndQueues]

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"strings"
	"topics_and_queues/actions"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/sns"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
	"github.com/aws/aws-sdk-go-v2/service/sqs/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

const FIFO_SUFFIX = ".fifo"
const TONE_KEY = "tone"

var ToneChoices = []string{"cheerful", "funny", "serious", "sincere"}

// MessageBody is used to deserialize the body of a message from a JSON string.
type MessageBody struct {
	Message string
}

// ScenarioRunner separates the steps of this scenario into individual functions so that
// they are simpler to read and understand.
type ScenarioRunner struct {
	questioner demotools.IQuestioner
	snsActor   *actions.SnsActions
	sqsActor   *actions.SqsActions
}

func (runner ScenarioRunner) CreateTopic(ctx context.Context) (string, string, bool, bool) {
	log.Println("SNS topics can be configured as FIFO (First-In-First-Out) or standard.\n" +
		"FIFO topics deliver messages in order and support deduplication and message filtering.")
	isFifoTopic := runner.questioner.AskBool("\nWould you like to work with FIFO topics? (y/n) ", "y")

	contentBasedDeduplication := false
	if isFifoTopic {
		log.Println(strings.Repeat("-", 88))
		log.Println("Because you have chosen a FIFO topic, deduplication is supported.\n" +
			"Deduplication IDs are either set in the message or are automatically generated\n" +
			"from content using a hash function. If a message is successfully published to\n" +
			"an SNS FIFO topic, any message published and determined to have the same\n" +
			"deduplication ID, within the five-minute deduplication interval, is accepted\n" +
			"but not delivered. For more information about deduplication, see:\n" +
			"\thttps://docs.aws.amazon.com/sns/latest/dg/fifo-message-dedup.html.")
		contentBasedDeduplication = runner.questioner.AskBool(
			"\nDo you want to use content-based deduplication instead of entering a deduplication ID? (y/n) ", "y")
	}
	log.Println(strings.Repeat("-", 88))

	topicName := runner.questioner.Ask("Enter a name for your SNS topic. ")
	if isFifoTopic {
		topicName = fmt.Sprintf("%v%v", topicName, FIFO_SUFFIX)
		log.Printf("Because you have selected a FIFO topic, '%v' must be appended to\n"+
			"the topic name.", FIFO_SUFFIX)
	}

	topicArn, err := runner.snsActor.CreateTopic(ctx, topicName, isFifoTopic, contentBasedDeduplication)
	if err != nil {
		panic(err)
	}
	log.Printf("Your new topic with the name '%v' and Amazon Resource Name (ARN) \n"+
		"'%v' has been created.", topicName, topicArn)

	return topicName, topicArn, isFifoTopic, contentBasedDeduplication
}

func (runner ScenarioRunner) CreateQueue(ctx context.Context, ordinal string, isFifoTopic bool) (string, string) {
	queueName := runner.questioner.Ask(fmt.Sprintf("Enter a name for the %v SQS queue. ", ordinal))
	if isFifoTopic {
		queueName = fmt.Sprintf("%v%v", queueName, FIFO_SUFFIX)
		if ordinal == "first" {
			log.Printf("Because you are creating a FIFO SQS queue, '%v' must "+
				"be appended to the queue name.\n", FIFO_SUFFIX)
		}
	}
	queueUrl, err := runner.sqsActor.CreateQueue(ctx, queueName, isFifoTopic)
	if err != nil {
		panic(err)
	}
	log.Printf("Your new SQS queue with the name '%v' and the queue URL "+
		"'%v' has been created.", queueName, queueUrl)

	return queueName, queueUrl
}

func (runner ScenarioRunner) SubscribeQueueToTopic(
	ctx context.Context, queueName string, queueUrl string, topicName string, topicArn string, ordinal string,
	isFifoTopic bool) (string, bool) {

	queueArn, err := runner.sqsActor.GetQueueArn(ctx, queueUrl)
	if err != nil {
		panic(err)
	}
	log.Printf("The ARN of your queue is: %v.\n", queueArn)

	err = runner.sqsActor.AttachSendMessagePolicy(ctx, queueUrl, queueArn, topicArn)
	if err != nil {
		panic(err)
	}
	log.Println("Attached an IAM policy to the queue so the SNS topic can send " +
		"messages to it.")
	log.Println(strings.Repeat("-", 88))

	var filterPolicy map[string][]string
	if isFifoTopic {
		if ordinal == "first" {
			log.Println("Subscriptions to a FIFO topic can have filters.\n" +
				"If you add a filter to this subscription, then only the filtered messages\n" +
				"will be received in the queue.\n" +
				"For information about message filtering, see\n" +
				"\thttps://docs.aws.amazon.com/sns/latest/dg/sns-message-filtering.html\n" +
				"For this example, you can filter messages by a \"tone\" attribute.")
		}

		wantFiltering := runner.questioner.AskBool(
			fmt.Sprintf("Do you want to filter messages that are sent to \"%v\"\n"+
				"from the %v topic? (y/n) ", queueName, topicName), "y")
		if wantFiltering {
			log.Println("You can filter messages by one or more of the following \"tone\" attributes.")

			var toneSelections []string
			askAboutTones := true
			for askAboutTones {
				toneIndex := runner.questioner.AskChoice(
					"Enter the number of the tone you want to filter by:\n", ToneChoices)
				toneSelections = append(toneSelections, ToneChoices[toneIndex])
				askAboutTones = runner.questioner.AskBool("Do you want to add another tone to the filter? (y/n) ", "y")
			}
			log.Printf("Your subscription will be filtered to only pass the following tones: %v\n", toneSelections)
			filterPolicy = map[string][]string{TONE_KEY: toneSelections}
		}
	}

	subscriptionArn, err := runner.snsActor.SubscribeQueue(ctx, topicArn, queueArn, filterPolicy)
	if err != nil {
		panic(err)
	}
	log.Printf("The queue %v is now subscribed to the topic %v with the subscription ARN %v.\n",
		queueName, topicName, subscriptionArn)

	return subscriptionArn, filterPolicy != nil
}

func (runner ScenarioRunner) PublishMessages(ctx context.Context, topicArn string, isFifoTopic bool, contentBasedDeduplication bool, usingFilters bool) {
	var message string
	var groupId string
	var dedupId string
	var toneSelection string
	publishMore := true
	for publishMore {
		groupId = ""
		dedupId = ""
		toneSelection = ""
		message = runner.questioner.Ask("Enter a message to publish: ")
		if isFifoTopic {
			log.Println("Because you are using a FIFO topic, you must set a message group ID.\n" +
				"All messages within the same group will be received in the order they were published.")
			groupId = runner.questioner.Ask("Enter a message group ID: ")
			if !contentBasedDeduplication {
				log.Println("Because you are not using content-based deduplication,\n" +
					"you must enter a deduplication ID.")
				dedupId = runner.questioner.Ask("Enter a deduplication ID: ")
			}
		}
		if usingFilters {
			if runner.questioner.AskBool("Add a tone attribute so this message can be filtered? (y/n) ", "y") {
				toneIndex := runner.questioner.AskChoice(
					"Enter the number of the tone you want to filter by:\n", ToneChoices)
				toneSelection = ToneChoices[toneIndex]
			}
		}

		err := runner.snsActor.Publish(ctx, topicArn, message, groupId, dedupId, TONE_KEY, toneSelection)
		if err != nil {
			panic(err)
		}
		log.Println(("Your message was published."))

		publishMore = runner.questioner.AskBool("Do you want to publish another messsage? (y/n) ", "y")
	}
}

func (runner ScenarioRunner) PollForMessages(ctx context.Context, queueUrls []string) {
	log.Println("Polling queues for messages...")
	for _, queueUrl := range queueUrls {
		var messages []types.Message
		for {
			currentMsgs, err := runner.sqsActor.GetMessages(ctx, queueUrl, 10, 1)
			if err != nil {
				panic(err)
			}
			if len(currentMsgs) == 0 {
				break
			}
			messages = append(messages, currentMsgs...)
		}
		if len(messages) == 0 {
			log.Printf("No messages were received by queue %v.\n", queueUrl)
		} else if len(messages) == 1 {
			log.Printf("One message was received by queue %v:\n", queueUrl)

		} else {
			log.Printf("%v messages were received by queue %v:\n", len(messages), queueUrl)
		}
		for msgIndex, message := range messages {
			messageBody := MessageBody{}
			err := json.Unmarshal([]byte(*message.Body), &messageBody)
			if err != nil {
				panic(err)
			}
			log.Printf("Message %v: %v\n", msgIndex+1, messageBody.Message)
		}

		if len(messages) > 0 {
			log.Printf("Deleting %v messages from queue %v.\n", len(messages), queueUrl)
			err := runner.sqsActor.DeleteMessages(ctx, queueUrl, messages)
			if err != nil {
				panic(err)
			}
		}
	}
}

// RunTopicsAndQueuesScenario is an interactive example that shows you how to use the
// AWS SDK for Go to create and use Amazon SNS topics and Amazon SQS queues.
//
// 1. Create a topic (FIFO or non-FIFO).
// 2. Subscribe several queues to the topic with an option to apply a filter.
// 3. Publish messages to the topic.
// 4. Poll the queues for messages received.
// 5. Delete the topic and the queues.
//
// This example creates service clients from the specified sdkConfig so that
// you can replace it with a mocked or stubbed config for unit testing.
//
// It uses a questioner from the `demotools` package to get input during the example.
// This package can be found in the ..\..\demotools folder of this repo.
func RunTopicsAndQueuesScenario(
	ctx context.Context, sdkConfig aws.Config, questioner demotools.IQuestioner) {
	resources := Resources{}
	defer func() {
		if r := recover(); r != nil {
			log.Println("Something went wrong with the demo.\n" +
				"Cleaning up any resources that were created...")
			resources.Cleanup(ctx)
		}
	}()
	queueCount := 2

	log.Println(strings.Repeat("-", 88))
	log.Printf("Welcome to messaging with topics and queues.\n\n"+
		"In this scenario, you will create an SNS topic and subscribe %v SQS queues to the\n"+
		"topic. You can select from several options for configuring the topic and the\n"+
		"subscriptions for the queues. You can then post to the topic and see the results\n"+
		"in the queues.\n", queueCount)

	log.Println(strings.Repeat("-", 88))

	runner := ScenarioRunner{
		questioner: questioner,
		snsActor:   &actions.SnsActions{SnsClient: sns.NewFromConfig(sdkConfig)},
		sqsActor:   &actions.SqsActions{SqsClient: sqs.NewFromConfig(sdkConfig)},
	}
	resources.snsActor = runner.snsActor
	resources.sqsActor = runner.sqsActor

	topicName, topicArn, isFifoTopic, contentBasedDeduplication := runner.CreateTopic(ctx)
	resources.topicArn = topicArn
	log.Println(strings.Repeat("-", 88))

	log.Printf("Now you will create %v SQS queues and subscribe them to the topic.\n", queueCount)
	ordinals := []string{"first", "next"}
	usingFilters := false
	for _, ordinal := range ordinals {
		queueName, queueUrl := runner.CreateQueue(ctx, ordinal, isFifoTopic)
		resources.queueUrls = append(resources.queueUrls, queueUrl)

		_, filtering := runner.SubscribeQueueToTopic(ctx, queueName, queueUrl, topicName, topicArn, ordinal, isFifoTopic)
		usingFilters = usingFilters || filtering
	}

	log.Println(strings.Repeat("-", 88))
	runner.PublishMessages(ctx, topicArn, isFifoTopic, contentBasedDeduplication, usingFilters)
	log.Println(strings.Repeat("-", 88))
	runner.PollForMessages(ctx, resources.queueUrls)

	log.Println(strings.Repeat("-", 88))

	wantCleanup := questioner.AskBool("Do you want to remove all AWS resources created for this scenario? (y/n) ", "y")
	if wantCleanup {
		log.Println("Cleaning up resources...")
		resources.Cleanup(ctx)
	}

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// snippet-end:[gov2.workflows.TopicsAndQueues]
