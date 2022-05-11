// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package main

import (
	"github.com/aws/aws-cdk-go/awscdk"
	"github.com/aws/aws-cdk-go/awscdk/awsecs"
	"github.com/aws/aws-cdk-go/awscdk/awsecspatterns"
	"github.com/aws/aws-cdk-go/awscdk/awsiam"
	"github.com/aws/aws-cdk-go/awscdk/awss3"
	"github.com/aws/aws-cdk-go/awscdk/awss3notifications"
	"github.com/aws/aws-cdk-go/awscdk/awssqs"
	"github.com/aws/constructs-go/constructs/v3"
	"github.com/aws/jsii-runtime-go"
)

type WordfreqStackProps struct {
	awscdk.StackProps
}

func NewWordfreqStack(scope constructs.Construct, id string, props *WordfreqStackProps) awscdk.Stack {
	var sprops awscdk.StackProps
	if props != nil {
		sprops = props.StackProps
	}

	stack := awscdk.NewStack(scope, &id, &sprops)

	/*

		The following resources need to be created:

		- a bucket to hold incoming data in
		- a queue to hold incoming notifications in
		- a queue to hold outgoing notifications in

		- a service to process the data
			- read/delete items in the bucket
			- consume items from the input queue
			- add items to the output queue
		- a role to submit data and collect results.

		The bucket needs to self-expire contents.
	*/

	// The bucket that input data comes in on
	inputBucket := awss3.NewBucket(stack, jsii.String("contentBucket"), &awss3.BucketProps{
		AutoDeleteObjects: jsii.Bool(true),              // Automatically delete objects on CDK teardown
		RemovalPolicy:     awscdk.RemovalPolicy_DESTROY, // Destroy the files during CDK teardown
		LifecycleRules: &[]*awss3.LifecycleRule{
			{ // Delete objects after 14 days
				Id:         jsii.String("delete after 14 days"),
				Expiration: awscdk.Duration_Days(jsii.Number(14)),
			},
		},
		BlockPublicAccess: awss3.BlockPublicAccess_BLOCK_ALL(), // There is no public access
		Versioned:         jsii.Bool(false),                    // we do not need versioned objects.
	})

	inputQueue := awssqs.NewQueue(stack, jsii.String("WorkQueue"), &awssqs.QueueProps{})

	inputBucket.AddEventNotification(awss3.EventType_OBJECT_CREATED,
		awss3notifications.NewSqsDestination(inputQueue))

	outputQueue := awssqs.NewQueue(stack, jsii.String("resultQueue"), &awssqs.QueueProps{
		RetentionPeriod: awscdk.Duration_Hours(jsii.Number(2)),
	})

	workerImage := awsecs.EcrImage_FromAsset(jsii.String("./wordfreq/"), &awsecs.AssetImageProps{})

	workerCluster := awsecs.NewCluster(stack, jsii.String("WorkerCluster"), &awsecs.ClusterProps{
		ClusterName: jsii.String("WordfreqCluster"),
	})

	workerService := awsecspatterns.NewQueueProcessingFargateService(stack, jsii.String("workerService"), &awsecspatterns.QueueProcessingFargateServiceProps{
		Queue:            inputQueue,
		Cluster:          workerCluster,
		ServiceName:      jsii.String("WordfreqProcessor"),
		DesiredTaskCount: jsii.Number(2),
		Environment: &map[string]*string{
			"WORKER_QUEUE_URL":        inputQueue.QueueUrl(),
			"WORKER_RESULT_QUEUE_URL": outputQueue.QueueUrl(),
			"AWS_REGION":              stack.Region(),
			"WORKER_DEBUG":            jsii.String("0"),
		},
		MinScalingCapacity: jsii.Number(0),
		MaxReceiveCount:    jsii.Number(5),
		Image:              workerImage,
		EnableLogging:      jsii.Bool(true),
	})

	// The worker has to be able to
	// * Consume messages from the input queue
	// * Publish messages to the output queue
	// * Read/delete from S3

	inputQueue.GrantConsumeMessages(workerService.TaskDefinition().TaskRole())
	outputQueue.GrantSendMessages(workerService.TaskDefinition().TaskRole())
	inputBucket.GrantRead(workerService.TaskDefinition().TaskRole(), "*")
	inputBucket.GrantDelete(workerService.TaskDefinition().TaskRole(), "*")

	// Now, the submission tool needs to have a few things lined up to know what's up.
	// The submission tool needs a role to assume when taking on
	submitterRole := awsiam.NewRole(stack, jsii.String("QueueSubmitterRole"), &awsiam.RoleProps{
		Description: jsii.String("Role to submit items into the queue"),
		AssumedBy:   awsiam.NewAccountRootPrincipal(),
	})

	// The submission tool needs to
	// * Write to the submission queue
	// * Put S3 objects
	// * Read from the results queue

	inputQueue.GrantSendMessages(submitterRole)
	outputQueue.GrantConsumeMessages(submitterRole)
	inputBucket.GrantPut(submitterRole, jsii.String("*"))

	//	resultTable.GrantReadData(submitterRole)

	awscdk.NewCfnOutput(stack, jsii.String("submitRoleArn"), &awscdk.CfnOutputProps{
		Value:      submitterRole.RoleArn(),
		ExportName: jsii.String("submitRole"),
	})

	awscdk.NewCfnOutput(stack, jsii.String("bucketName"), &awscdk.CfnOutputProps{
		Value:      inputBucket.BucketName(),
		ExportName: jsii.String("bucketName"),
	})

	awscdk.NewCfnOutput(stack, jsii.String("resultQueueUrl"), &awscdk.CfnOutputProps{
		Value:      outputQueue.QueueUrl(),
		ExportName: jsii.String("resultQueueUrl"),
	})

	awscdk.NewCfnOutput(stack, jsii.String("InputQueueUrl"), &awscdk.CfnOutputProps{
		Value:      inputQueue.QueueUrl(),
		ExportName: jsii.String("inputQueueUrl"),
	})

	return stack
}

func main() {
	app := awscdk.NewApp(nil)

	NewWordfreqStack(app, "WordfreqStack", &WordfreqStackProps{
		awscdk.StackProps{
			Env: env(),
		},
	})

	app.Synth(nil)

}

// env determines the AWS environment (account+region) in which our stack is to
// be deployed. For more information see: https://docs.aws.amazon.com/cdk/latest/guide/environments.html
func env() *awscdk.Environment {
	// If unspecified, this stack will be "environment-agnostic".
	// Account/Region-dependent features and context lookups will not work, but a
	// single synthesized template can be deployed anywhere.
	//---------------------------------------------------------------------------
	return nil

	// Uncomment if you know exactly what account and region you want to deploy
	// the stack to. This is the recommendation for production stacks.
	//---------------------------------------------------------------------------
	// return &awscdk.Environment{
	//  Account: jsii.String("123456789012"),
	//  Region:  jsii.String("us-east-1"),
	// }

	// Uncomment to specialize this stack for the AWS Account and Region that are
	// implied by the current CLI configuration. This is recommended for dev
	// stacks.
	//---------------------------------------------------------------------------
	// return &awscdk.Environment{
	//  Account: jsii.String(os.Getenv("CDK_DEFAULT_ACCOUNT")),
	//  Region:  jsii.String(os.Getenv("CDK_DEFAULT_REGION")),
	// }
}
