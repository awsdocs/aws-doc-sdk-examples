#  SageMaker workflow technical specification

This document contains the technical specifications for the Amazon SageMaker Geospatial Pipelines example, a sample workflow that showcases SageMaker pipelines using SDKs.

This document explains the following:

- Application inputs and outputs
- Underlying AWS components and their configurations
- Implementation details and sample output
- Troubleshooting information

### Table of contents

- [Architecture](#architecture)
- [Common resources](#common-resources)
- [Metadata](#metadata)
- [Implementation](#implementation)
- [Troubleshooting](#troubleshooting)

## Architecture
This workflow uses a pre-defined SageMaker pipeline to execute a geospatial job in SageMaker. The pipeline uses [Pipeline steps](https://docs.aws.amazon.com/sagemaker/latest/dg/build-and-manage-steps.html) to define the actions and relationships of the pipeline operations. The pipeline in this example includes an [AWS Lambda step](https://docs.aws.amazon.com/sagemaker/latest/dg/build-and-manage-steps.html#step-type-lambda)
and a [callback step](https://docs.aws.amazon.com/sagemaker/latest/dg/build-and-manage-steps.html#step-type-callback).
Both steps are processed by the same example Lambda function.

The Lambda function handler should be written as part of the example, with the following functionality:
- Starts a [SageMaker Vector Enrichment Job](https://docs.aws.amazon.com/sagemaker/latest/dg/geospatial-vej.html) with the provided job configuration.
- Processes Amazon SQS queue messages from the SageMaker pipeline.
- Starts the export function with the provided export configuration.
- Completes the pipeline when the export is complete.

This diagram represents the relationships between key components.
![relational diagram](resources/workflow.png)

Amazon SageMaker is a managed machine learning service. Developers can build and train machine learning models and deploy them into a production-ready hosted environment. This example focuses on the pipeline capabilities rather than the model training and building capabilities, since those are more likely to be useful to an SDK developer.

The example uses a geospatial job because it allows for a fast processing time, and it's simple to verify that the pipeline executed correctly. We expect that the user would replace this job with processing steps of their own, but be able to use the SDK pipeline operations for creating or updating a pipeline, handling callback and execution steps in an AWS Lambda function, and using pipeline parameters to set up input and output.

The geospatial job itself is a Vector Enrichment Job (VEJ) that reverse geocodes a set of coordinates. Other job types are much slower to complete, and this job type has an easy-to-read output. Note that you should use **us-west-2 region** to use this job type. This particular job type is powered by Amazon Location Service, although you will not need to call that service directly. You can read more [about geospatial capabilities here].(https://docs.aws.amazon.com/sagemaker/latest/dg/geospatial.html).

The AWS Lambda function handles the callback and the parameter-based queue messages from the pipeline. This example includes writing this Lambda function and deploying it as part of the pipeline, and also connecting it to the SQS queue that is used by the pipeline.

There are multiple ways to handle pipeline operations, but in the interest of consistency, the C# implementation is based on [this pipeline example reference](https://github.com/aws/amazon-sagemaker-examples/blob/main/sagemaker-geospatial/geospatial-pipeline/assets/eoj_pipeline_lambda.py). This logic checks for the existence of parameters in the message to determine which type of processing to start. Other languages do not need to mimic the exact logic shown here, that functionality is left up to the language developer.

The pipeline in this example is defined through a [JSON file](resources/GeoSpatialPipeline.json). Each language might want to name the steps and parameters in a way that makes sense for their implementation, but you can use the file here as a guide.

## Common resources
This example has a set of common resources that are stored in the [resources](resources) folder.
- GeoSpatialPipeline.json defines the pipeline steps and parameters for the SageMaker pipeline. 
- latlongtest.csv is a sample set of coordinates for geocoding.
- pipeline.png is a pipeline image to use in language-specific READMEs.
- workflow.png is a workflow image to use in language-specific READMEs.

## Metadata
Service actions can either be pulled out as individual functions or can be incorporated into the scenario, but each service action must be included as an excerpt.

### SageMaker actions
- CreatePipeline
- UpdatePipeline
- StartPipelineExecution
- DescribePipelineExecution
- DeletePipeline
- Hello Service

### Metadata tags
```
sagemaker_Hello
sagemaker_CreatePipeline
sagemaker_ExecutePipeline
sagemaker_DeletePipeline
sagemaker_DescribePipelineExecution
sagemaker_Scenario_Pipelines
```

## Implementation

_Reminder:_ A scenario runs at a command prompt and prints output to the user on the result of each service action. Because of the choices in this workflow scenario, it must be run interactively.

1. Set up any missing resources needed for the example if they don’t already exist.
   1. Create a Lambda role with the following: `iamClient CreateRole, AttachRolePolicy`
      1. AssumeRolePolicy:
      ```
      {
         Version: "2012-10-17",
         Statement: [
            {
               Effect: "Allow",
            Action: ["sts:AssumeRole"],
            Principal: { Service: ["lambda.amazonaws.com"] },
            },
         ],
      }
      ```
        b. ExecutionPolicy:
   ```
      {
          Version: "2012-10-17",
          Statement: [
            {
              Effect: "Allow",
              Action: [
                "sqs:ReceiveMessage",
                "sqs:DeleteMessage",
                "sqs:GetQueueAttributes",
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents",
                "sagemaker-geospatial:StartVectorEnrichmentJob",
                "sagemaker-geospatial:GetVectorEnrichmentJob",
                "sagemaker:SendPipelineExecutionStepFailure",
                "sagemaker:SendPipelineExecutionStepSuccess",
                "sagemaker-geospatial:ExportVectorEnrichmentJob"
              ],
              Resource: "*",
            },
            {
              Effect: "Allow",
              Action: ["iam:PassRole"],
              Resource: `${pipelineExecutionRoleArn}`,
              Condition: {
                StringEquals: {
                  "iam:PassedToService": [
                    "sagemaker.amazonaws.com",
                    "sagemaker-geospatial.amazonaws.com",
                  ],
                },
              },
            },
          ],
        }
      ```

   1. Create a SageMaker role with the following: `iamClient CreateRole, AttachRolePolicy`
      1. AssumeRolePolicy:
      ```
      {
         Version: "2012-10-17",
         Statement: [
            {
               Effect: "Allow",
               Action: ["sts:AssumeRole"],
               Principal: {
                  Service: [
                  "sagemaker.amazonaws.com",
                  "sagemaker-geospatial.amazonaws.com",
                  ],
               },
            },
         ],
      }
      ```   
       b. ExecutionPolicy:
       ```
        {
           Version: "2012-10-17",
           Statement: [
           {
               Effect: "Allow",
               Action: ["lambda:InvokeFunction"],
               Resource: lambdaArn,
           },
           {
               Effect: "Allow",
               Action: ["s3:*"],
                Resource: [
                    `arn:aws:s3:::${s3BucketName}`,
                    `arn:aws:s3:::${s3BucketName}/*`,
                ],
           },
           {
               Effect: "Allow",
               Action: ["sqs:SendMessage"],
               Resource: sqsQueueArn,
           },
          ],
        }
       ```
   1. Create an SQS queue for the pipeline. SqsClient CreateQueue, GetQueueUrl
       You will need the queue URL for the pipeline execution.
   1. Create a bucket and upload a .csv file that includes Latitude and Longitude columns (see [the resources section](#common-resources)) for reverse geocoding. `s3 client PutBucket and PutObject`
       1. You can add an /input directory for this file. The pipeline will create a /output directory for the output file.
1. Add a Lambda handler, with code included and written in your language, that handles callback functionality and connect it to the queue. If the Lambda already exists, you can prompt the user if they would like to update it. Suggested timeout for the Lambda is 30 seconds. `lambdaClient CreateFunction, UpdateFunction, ListEventSourceMappings, CreateEventSourceMapping`
   1. The lambda performs the following tasks, based on the input:
       1. If queue records are present, processes the records to check the job status of the geospatial job.
           COMPLETED: call SendPipelineExecutionStepSuccess
           FAILED: call SendPipelineExeuctionStepFailure
           IN_PROGRESS: log that the job is still running
       1. If export configuration is present, call ExportVectorEnrichmentJob
       1. If job name is present, call StartVectorEnrichmentJob
   1. The queue must be added to the event source mappings for the Lambda, and the event source mapping must be enabled.
1. Create a pipeline using the SDK with the following characteristics. If the pipeline already exists, use an Update call to update it. You can use the JSON referenced here as a guide for the pipeline definition. `sagemakerClient UpdatePipeline, CreatePipeline`
   1. Pipeline parameters for the job, input, and export steps.
   1. A Lambda processing step: a Lambda that kicks off a vector enrichment job that takes in a set of coordinates for reverse geocode.
   1. A callback step to check the progress of the processing job.
   1. An export step for the results of the VEJ.
   1. A callback step to finish the pipeline.
1. Execute the pipeline using the SDK with some input and poll for the execution status. `sagemakerClient StartPipelineExecution, DescribePipelineExecution`
1. When the execution is complete, fetch the latest output file and display some of the output data to the user. `s3client ListObjects, GetObject`
1. Provide instructions for optionally viewing the pipeline and executions in SageMaker Studio.
1. Clean up the pipeline and resources – the user gets to decide if they want to clean these up or not.
   1. Clean up pipeline. DeletePipeline
   1. Clean up the queue. DeleteQueue
   1. Clean up the bucket. DeleteObjects, DeleteBucket
   1. Clean up the Lambda. DeleteFunction
   1. Clean up the pipeline. DeletePipeline

### Sample output

```
--------------------------------------------------------------------------------
Welcome to the Amazon SageMaker pipeline example scenario.

This example workflow will guide you through setting up and executing a
Amazon SageMaker pipeline. The pipeline uses an AWS Lambda function and an
Amazon SQS queue, and runs a vector enrichment reverse geocode job to
reverse geocode addresses in an input file and store the results in an export file.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
First, we will set up the roles, functions, and queue needed by the SageMaker pipeline.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Checking for role named SageMakerExampleLambdaRole.
--------------------------------------------------------------------------------
Checking for role named SageMakerExampleRole.
--------------------------------------------------------------------------------
Setting up the Lambda function for the pipeline.
        The Lambda function SageMakerExampleFunction already exists, do you want to update it?
n
        Lambda ready with ARN arn:aws:lambda:us-west-2:1234567890:function:SageMakerExampleFunction.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Setting up queue sagemaker-sdk-example-queue-test.
--------------------------------------------------------------------------------
Setting up bucket sagemaker-sdk-test-bucket-test.
        Bucket sagemaker-sdk-test-bucket-test ready.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Now we can create and execute our pipeline.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Setting up the pipeline.
        Pipeline set up with ARN arn:aws:sagemaker:us-west-2:1234567890:pipeline/sagemaker-sdk-example-pipeline.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Starting pipeline execution.
        Execution started with ARN arn:aws:sagemaker:us-west-2:1234567890:pipeline/sagemaker-sdk-example-pipeline/execution/f8xmafpxx3ke.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Waiting for pipeline execution to finish.
        Execution status is Executing.
        Execution status is Executing.
        Execution status is Executing.
        Execution status is Succeeded.
        Execution finished with status Succeeded.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Getting output results sagemaker-sdk-test-bucket-test.
        Output file: outputfiles/qyycwuuxwc9w/results_0.csv
        Output file contents:

        -149.8935557,"61.21759217
        ",601,USA,"601 W 5th Ave, Anchorage, AK, 99501, USA",Anchorage,,99501 6301,Alaska,Valid Data
        -149.9054948,"61.19533942
        ",2794,USA,"2780-2798 Spenard Rd, Anchorage, AK, 99503, USA",Anchorage,North Star,99503,Alaska,Valid Data
        -149.7522,"61.2297
        ",,USA,"Enlisted Hero Dr, Jber, AK, 99506, USA",Jber,,99506,Alaska,Valid Data
        -149.8643361,"61.19525062
        ",991,USA,"959-1069 E Northern Lights Blvd, Anchorage, AK, 99508, USA",Anchorage,Rogers Park,99508,Alaska,Valid Data
        -149.8379726,"61.13751355
        ",2372,USA,"2276-2398 Abbott Rd, Anchorage, AK, 99507, USA",Anchorage,,99507,Alaska,Valid Data
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
The pipeline has completed. To view the pipeline and executions in SageMaker Studio, follow these instructions:
https://docs.aws.amazon.com/sagemaker/latest/dg/pipelines-studio.html
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Finally, let's clean up our resources.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Clean up resources.
        Delete pipeline sagemaker-sdk-example-pipeline? (y/n)
y
        Delete queue https://sqs.us-west-2.amazonaws.com/565846806325/sagemaker-sdk-example-queue-rlhagerm? (y/n)
y
        Delete S3 bucket sagemaker-sdk-test-bucket-rlhagerm2? (y/n)
y
        Delete role SageMakerExampleLambdaRole? (y/n)
y
        Delete role SageMakerExampleRole? (y/n)
y
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
SageMaker pipeline scenario is complete.
--------------------------------------------------------------------------------
```
### Hello SageMaker
The Hello Service example should demonstrate how to set up the client and make an example call using the SDK.

Initialize the client and call ListNotebookInstances to list up to 5 of the account's notebook instances. If no instances are found, you can direct the user to instructions on how to add one.

Sample output:

```
Hello Amazon SageMaker! Let's list some of your notebook instances:
    Instance: test-notebook
    Arn: arn:aws:sagemaker:us-west-2:123456789:notebook-instance/test-notebook
    Creation Date: 6/7/2023
```

_General info for Hello Service example snippets:_
This section of the workflow should be a streamlined, simple example with enough detail to be as close to “copy/paste” runnable as possible. This example may include namespaces and other setup in order to focus on getting the user up and running with the new service.

### README

This is a workflow scenario. As such, the READMEs should be standardized.
This is the [.NET reference README](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/dotnetv3/SageMaker/Scenarios/README.md). When a language implementation is completed, update the [parent README](README.md) to include the new language SDK.

## Troubleshooting
- You might want to view your pipeline in SageMaker studio, which will require a domain.
This will provide better debugging for the pipeline execution steps. You can use a default domain or create a custom domain.
- Amazon CloudWatch Logs will help you with this task. SageMaker studio should link each step to relevant logs.
- When testing your Lambda function, you might find it useful to log out the function input. Serialization differences between languages (capitalization, nulls) can cause the function to fail.
- When pipelines are failing, don't delete the queue until the pipeline execution has stopped.
- Pipelines can only be deleted through the SDK.
- For an example, see the [.NET implementation](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/dotnetv3/SageMaker). You might find it useful to begin with the .NET Lambda and get the rest of the workflow working, then work on the language-specific Lambda function handler.
- Geospatial jobs are supported in `region us-west-2`. All operations should use this Region unless otherwise specified.
- Pipeline callbacks won't resolve until SendPipelineExecutionStepSuccess or SendPipelineExecutionStepFailure are called. There's a risk of having to reach out to support if these aren't called.

### SageMaker documentation references:

- [SageMaker Developer Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/whatis.html)
- [SageMaker API Reference](https://docs.aws.amazon.com/sagemaker/latest/APIReference/Welcome.html?icmpid=docs_sagemaker_lp)
- [Sagemaker examples and example notebooks](https://github.com/aws/amazon-sagemaker-examples/tree/main/sagemaker-geospatial/geospatial-pipeline)
