// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package main

import (
	"context"
	"encoding/json"
	"fmt"
	"os"
	"time"

	"example.aws/go-v2/examples/cross_service/wordfreq/service/shared"
	parser "example.aws/go-v2/examples/cross_service/wordfreq/service/shared/s3"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
	"go.uber.org/zap"
)

// This is our central logger
var log *zap.Logger

// Early wakeup: Make sure that the logger is running.
func init() {

	mLog, err := zap.NewProduction()

	if err != nil {
		// Something VERY bad has happened.
		fmt.Printf("Failed to initialize logging; something is very bad. Error returned: \n------\n%v\n------\n", err)
		os.Exit(1)
	}
	log = mLog
	log.Debug("main.init: we're live, folks!")

	defer log.Sync()
}

// Worker service which reads from an SQS queue pulls off job messages, processes
// the jobs, and records the results. The service uses environment variables for
// its configuration.
//
// Requires the following environment variables to be set.
//
// * WORKER_QUEUE_URL - The SQS queue URL where the service will read job messages
// from. Job messages are created when S3 notifies the SQS queue that a file has
// been uploaded to a particular bucket.
//
// * WORKER_RESULT_QUEUE_URL - The SQS queue URL where the job results will be
// sent to.
//
// Optionally the follow environment variables can be provided.
//
// * AWS_REGION - The AWS region the worker will use for signing and making all
// requests to. This parameter is only optional if the service is running within
// an EC2 instance. If not running in an EC2 instance AWS_REGION is required.
//
// * WORKER_DEBUG: Set this to T or 1 or True to enable debugging.
//   Doing so will change the behavior to try to parse messages until there are no more
//   for roughly a minute.
//

// ----------------------------------------------------------------------------

// This gofunc brings in jobs from the queue.
// Internally, it's a big loop
func JobParser(config Config, JobQueue chan<- shared.JobBatch) {

	log.Debug("starting SQS connection...")

	// Set up an SQS client
	client := sqs.NewFromConfig(*config.AwsConfig)

	// Continuously consume messages
	for {

		// Get an item from the queue
		// We use a large timeout and visibility timeout here for two reasons:
		// We're looking for larger batches at once, with possibly long times between batches.
		queueItem, err := client.ReceiveMessage(context.TODO(), &sqs.ReceiveMessageInput{
			QueueUrl:            &config.WorkerQueueURL,
			VisibilityTimeout:   *aws.Int32(30),
			WaitTimeSeconds:     *aws.Int32(15),
			MaxNumberOfMessages: 5,
		})

		// This is the one point where we really exit.
		// If for some reason, the call fails, we bail out.
		if err != nil {
			// Something horribly wrong has happened.
			log.Error("Couldn't receive messages", zap.Error(err))
			break
		}

		// If we've ended up with no messages, we have no more things to add to the queue
		if len(queueItem.Messages) == 0 {
			time.Sleep(10 * time.Second)
			log.Debug("Waiting for more messages...")
		} else {
			log.Info("Processing message from SQS", zap.Int("messageCount", len(queueItem.Messages)))
			for _, queueMessage := range queueItem.Messages {
				log.Info("Processing queue item", zap.String("messageId", *queueMessage.MessageId))

				// The body of the queueMessage is going to contain at least one message.
				// S3 occasionally sends us Test messages. If we get one of those, we should drop it from the
				// queue and move on. Otherwise, we should take each of the items in the message and turn that
				// into a job set

				// First, we want to get records.

				s3records, err := parser.ParseEvent(*queueMessage.Body)

				// We get zero records and an error if it's a test message
				if s3records != nil && len(s3records) == 0 && err != nil {
					// This is a test message and should be discarded.
					client.DeleteMessage(context.TODO(), &sqs.DeleteMessageInput{
						ReceiptHandle: queueMessage.ReceiptHandle,
						QueueUrl:      &config.WorkerQueueURL,
					})
					log.Debug("Dropping zero-record/test message from the queue", zap.String("receiptHandle", *queueMessage.ReceiptHandle))
					continue
				}

				batch := shared.JobBatch{
					Message: shared.JobMessage{},
					Jobs:    []shared.Job{},
				}

				batch.FromSQSMessage(*queueMessage.MessageId, *queueMessage.ReceiptHandle, config.WorkerQueueURL, s3records)

				log.Info("Adding batch to queue", zap.String("receiptHandle", batch.Message.ReceiptHandle))
				JobQueue <- batch
			}
		}

	}
	close(JobQueue)
}

func sendJobResponse(config Config, result shared.JobResult) {
	sqsClient := sqs.NewFromConfig(*config.AwsConfig)

	// get the JSON version of the job.

	log.Info("Sending result",
		zap.String("JobID", result.JobID),
		zap.String("result", string(result.Status)),
		zap.String("message", result.StatusMessage),
	)

	data, err := json.Marshal(result)
	if err != nil {
		log.Error("Failed to marshal JSON out", zap.Error(err))
		return
	}
	dataStr := string(data)

	_, err = sqsClient.SendMessage(context.TODO(), &sqs.SendMessageInput{
		QueueUrl:    &config.ResultQueueURL,
		MessageBody: aws.String(dataStr),
	})
	if err != nil {
		log.Error("Failed to send response", zap.Error(err))
	}
}

// This gofunc actually processes each batch of jobs.
func JobRunner(config Config, jobQueue <-chan shared.JobBatch, done chan<- bool) {

	// our S3 client, used for getting objects out of S3
	sclient := s3.NewFromConfig(*config.AwsConfig)
	// Loop forever until the Job queue is exhausted and closed by the input management queue.
	for {
		batch, open := <-jobQueue
		if !open {
			break
		}

		// Consume each job from the batch
		for _, job := range batch.Jobs {

			// The result that we'll send to the uploader (Or whoever is looking)
			result := shared.JobResult{
				FileKey: job.Key,
				JobID:   job.SnowflakeId,
			}

			log.Debug("getting file out of s3", zap.String("snowflake", job.SnowflakeId), zap.String("key", job.Key), zap.String("bucket", job.Bucket))

			// Get the object out of S3
			request := &s3.GetObjectInput{
				Bucket: &job.Bucket,
				Key:    &job.Key,
			}
			// make the request; err will tell us a lot of things. Most of them we don't care about, and failure to pull the object
			// out of S3 is generally a failure to fly.
			response, err := sclient.GetObject(context.TODO(), request)
			if err != nil {
				// Log it with enough information to pick up the parts afterwards
				log.Error("Failed to get the object out of s3",
					zap.String("bucket", job.Bucket),
					zap.String("key", job.Key),
					zap.String("requestid", batch.Message.ID),
					zap.String("snowflake", job.SnowflakeId),
					zap.Error(err),
				)
				// Set the status information on the result
				result.Status = shared.JobCompleteFailure
				result.StatusMessage = "Failed to retreive bucket data"
				// Send the response
				sendJobResponse(config, result)
				// Pick up the next job in the batch.
				continue

			}

			log.Debug("Got contents out of S3", zap.String("snowflake", job.SnowflakeId), zap.String("contentEtag", *response.ETag))

			// Get the words
			words, err := countTopWords(response.Body, 10)

			// If something failed, return an error on the queue.
			if err != nil {
				log.Error("failed to count words", zap.String("snowflake", job.SnowflakeId), zap.Error(err))
				result.Status = shared.JobCompleteFailure
				result.StatusMessage = fmt.Sprintf("Failed to count words: %v", err)
				sendJobResponse(config, result)
				continue
			}

			// If nothing has gone wrong so far, mark the job as being successful.
			result.Status = shared.JobCompleteSuccess
			result.StatusMessage = "Success"
			// store our word count results
			result.Words = words
			// Say how long it took to get to this
			result.Duration = time.Since(job.StartedAt)
			// Log this for metrics
			log.Info("Counted words in job", zap.String("snowflake", job.SnowflakeId), zap.Duration("rtt", result.Duration))
			// send off the results.
			sendJobResponse(config, result)

			// Also, clean up the s3 object that's left over:
			_, err = sclient.DeleteObject(context.TODO(), &s3.DeleteObjectInput{Bucket: &job.Bucket, Key: &job.Key})
			// it's possible, but unlikely, that we can't delete the object.
			// There's legitimate reasons (such as legal holds) but it's not an error on our part.
			if err != nil {
				log.Warn("Couldn't delete bucket object at end of job processing...", zap.Error(err), zap.String("bucket", job.Bucket), zap.String("key", job.Key), zap.String("snowflake", job.SnowflakeId))
			}
			// ... And we reach the end of the inner loop, taking another job off the batch.
		}

		// When we have no more jobs within the batch to manage, it's safe to remove the source item off the queue.

		// We need an SQS client...
		sqsclient := sqs.NewFromConfig(*config.AwsConfig)
		// mention in the debug log that we're going to delete a message.
		log.Debug("Deleting message from queue", zap.String("receiptHandle", batch.Message.ReceiptHandle))
		_, err := sqsclient.DeleteMessage(context.TODO(), &sqs.DeleteMessageInput{
			QueueUrl:      &config.WorkerQueueURL,
			ReceiptHandle: &batch.Message.ReceiptHandle,
		})
		// This is highly unlikely, but possible.
		if err != nil {
			log.Warn("somehow, deletion of an item from the queue failed!", zap.String("queueUrl", config.WorkerQueueURL), zap.String("receiptHandle", batch.Message.ReceiptHandle), zap.Error(err))
		}
		// ... And take another message.
	}

	// The done channel is a simple way to synchronize the end of execution.
	done <- true
}

func main() {

	cfg, err := GetConfig()
	if err != nil {
		log.Fatal(
			"Unable to get config",
			zap.Error(err),
		)
	}
	// Check if we need to reset the logger to a debugging logger
	if cfg.Debug {
		tLogger, err := zap.NewDevelopment()
		if err != nil {
			log.Error("Couldn't get a debug logger, WTF?", zap.Error(err))
			os.Exit(1)
		} else {
			log = tLogger
			log.Debug("Debug logging is go")
		}
	}

	holdChan := make(chan bool)
	jobQueueChan := make(chan shared.JobBatch)
	go JobParser(cfg, jobQueueChan)
	go JobRunner(cfg, jobQueueChan, holdChan)
	<-holdChan

}
