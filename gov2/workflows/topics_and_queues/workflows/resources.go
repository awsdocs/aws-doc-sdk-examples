// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

// snippet-start:[gov2.workflows.TopicsAndQueues.Resources.complete]

import (
	"context"
	"fmt"
	"log"
	"topics_and_queues/actions"
)

// Resources keeps track of AWS resources created during an example and handles
// cleanup when the example finishes.
type Resources struct {
	topicArn  string
	queueUrls []string
	snsActor  *actions.SnsActions
	sqsActor  *actions.SqsActions
}

// Cleanup deletes all AWS resources created during an example.
func (resources Resources) Cleanup(ctx context.Context) {
	defer func() {
		if r := recover(); r != nil {
			fmt.Println("Something went wrong during cleanup. Use the AWS Management Console\n" +
				"to remove any remaining resources that were created for this scenario.")
		}
	}()

	var err error
	if resources.topicArn != "" {
		log.Printf("Deleting topic %v.\n", resources.topicArn)
		err = resources.snsActor.DeleteTopic(ctx, resources.topicArn)
		if err != nil {
			panic(err)
		}
	}

	for _, queueUrl := range resources.queueUrls {
		log.Printf("Deleting queue %v.\n", queueUrl)
		err = resources.sqsActor.DeleteQueue(ctx, queueUrl)
		if err != nil {
			panic(err)
		}
	}
}

// snippet-end:[gov2.workflows.TopicsAndQueues.Resources.complete]
