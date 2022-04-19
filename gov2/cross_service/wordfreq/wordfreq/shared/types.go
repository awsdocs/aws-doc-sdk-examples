// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package shared

import (
	"log"
	"math/rand"
	"time"

	s3events "example.aws/go-v2/examples/cross_service/wordfreq/service/shared/s3"
	"github.com/bwmarrin/snowflake"
)

var SnowflakeGen *snowflake.Node

func init() {
	// Snowflakes are used to identify jobs as they are created
	// Snowflakes are unique IDs, similar to UUIDs, containing their creation
	// timestamp and a few bits of metadata about what "node" was used to make
	// the snowflake. Here, we use a random node number.
	snow, err := snowflake.NewNode(rand.Int63n(1023))
	if err != nil {
		// throw up our hands
		log.Fatal("Couldn't get a random int..." + err.Error())
	}
	SnowflakeGen = snow
}

// A batch of jobs created from an SQS message.
type JobBatch struct {
	// The minimal information for tracing the job back to an SQS message
	Message JobMessage
	// Jobs that were extracted from that message.
	Jobs []Job
}

// A job created from an SQS message and S3 record.
type Job struct {
	StartedAt           time.Time
	Region, Bucket, Key string
	SnowflakeId         string
}

// Minimal information to identify an SQS message
type JobMessage struct {
	ID            string
	ReceiptHandle string
	QueueUrl      string
}

// Turn an SQS message into a batch
// NOTE: This does not filter out s3:objectDelete events.
func (batch *JobBatch) FromSQSMessage(BatchID string, ReceiptHandle string, QueueUrl string, Records []s3events.Record) error {

	batch.Message = JobMessage{
		ID:            BatchID,
		ReceiptHandle: ReceiptHandle,
		QueueUrl:      QueueUrl,
	}

	// We know the number of jobs since jobs map 1:1 to records.
	batch.Jobs = make([]Job, len(Records))

	for idx, rec := range Records {
		job := Job{
			Bucket:      rec.EventData.Bucket.Name,
			Region:      rec.AwsRegion,
			Key:         rec.EventData.Object.Key,
			StartedAt:   rec.EventTime,
			SnowflakeId: SnowflakeGen.Generate().Base36(),
		}
		batch.Jobs[idx] = job
	}

	return nil
}

type JobResult struct {
	// This maps to the key of the file uploaded to S3
	FileKey string
	// A unique value just for the reply
	JobID string
	// top words
	Words Words
	// Time to count them, dequeue, etc.
	Duration time.Duration
	// Did it succeed?
	Status JobCompleteStatus
	// How badly?
	StatusMessage string
}

type JobCompleteStatus string

const (
	JobCompleteSuccess JobCompleteStatus = "success"
	JobCompleteFailure JobCompleteStatus = "failure"
)

type Word struct {
	Word  string
	Count int
}

type Words []Word

func (w Words) Len() int {
	return len(w)
}
func (w Words) Less(i, j int) bool {
	return w[i].Count > w[j].Count
}
func (w Words) Swap(i, j int) {
	w[i], w[j] = w[j], w[i]
}

type OrigMessage struct {
	ID            string
	ReceiptHandle string
	Body          string
}
