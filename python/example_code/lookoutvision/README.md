# Lookout for Vision code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Lookout for Vision.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Lookout for Vision enables you to find visual defects in industrial products, accurately and at scale._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Lookout for Vision](hello.py#L4) (`ListProjects`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDataset](datasets.py#L32)
- [CreateModel](models.py#L29)
- [CreateProject](projects.py#L31)
- [DeleteDataset](datasets.py#L201)
- [DeleteModel](models.py#L167)
- [DeleteProject](projects.py#L54)
- [DescribeDataset](datasets.py#L227)
- [DescribeModel](models.py#L102)
- [DetectAnomalies](inference.py#L23)
- [ListModels](models.py#L143)
- [ListProjects](projects.py#L73)
- [StartModel](hosting.py#L30)
- [StopModel](hosting.py#L87)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a manifest file](datasets.py)
- [Create, train, and start a model](../../example_code/lookoutvision)
- [Export the datasets from a project](export_datasets.py)
- [Find a project with a specific tag](find_tag.py)
- [List models that are currently hosted](hosting.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
There are three demonstrations in this set of examples:

* Create and host a model.
* Detect anomalies in images using a model.
* Find tags attached to a model.

Before running these demonstrations do the following:
- Follow the [setup instructions](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/su-set-up.html).
- Read [Getting started with the SDK](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/getting-started-sdk.html).
- Create an Amazon S3 bucket in your AWS account. You'll use the bucket to store your
  training images, manifest files, and training output.
- Copy your training and test images to your S3 bucket. To try this code with example
  images, You can use the example [circuit board dataset](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/su-prepare-example-images.html).

The folder structures for the training and test images must be as follows:
```
s3://amzn-s3-demo-bucket/<train or test>/
    normal/
    anomaly/
```
`train` and `test` can be any folder path.
Place normal images in the `normal` folder. Anomalous images in the `anomaly` folder.

##### Example structure

The example contains the following files.

###### datasets.py

A class that shows how to create and manage datasets. Also shows how to create a
manifest file based on images found in an Amazon S3 bucket. Used by `train_host.py`.

Manifest files are used to create training and test datasets. `train_host.py` uses
`datasets.py` to create training and (optionally) test manifest files, and upload them
to an Amazon S3 bucket location that you specify. For more information about manifest
files, see [Creating a dataset using an Amazon SageMaker Ground Truth manifest file](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/create-dataset-ground-truth.html).

###### find_tag.py

Shows how to find a tag attached to a Lookout for Vision model.

###### hello.py

Confirms that you can call Amazon Lookout for Vision operations.
If you haven't previously created a project in the current AWS Region,
the response is an empty list, however it confirms that you can call the
Lookout for Vision API.

###### hosting.py

A class that shows how to start and stop a Lookout for Vision project. Also shows how
to list hosted models. Used by `train_host.py`.

###### inference.py

A class that shows how to analyze an image (JPEG/PNG) with a hosted Lookout for Vision
model. You can also analyze an image stored in an Amazon S3 bucket.
The example shows how you can classify images as normal or anomalous. It also shows how to
use segmentation information returned from a segmentation model. For more information,
see [Detecting anomalies in an image](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/inference-detect-anomalies.html).
To run the example, supply an image file name and a configuration JSON file with the following format.

    {
        "project" : "The Lookout for Vision project name.",
        "model_version" : "The model version.",
        "confidence_limit" : The minimum acceptable confidence. (Float 0 - 1).,
        "coverage_limit" : The maximum acceptable percentage coverage of an anomaly (Float 0 - 1).,
        "anomaly_types_limit" : The maximum number of allowable anomaly types. (Integer),
        "anomaly_label" : "The anomaly label for the type of anomaly that you want to check."
    }

We provide a template JSON configuration file in config.json.

###### models.py

A class that shows how to train and manage a Lookout for Vision model. Used by
`train_host.py`.

###### projects.py

A class that shows how to create and manage a Lookout for Vision project. Used by
`train_host.py`.

###### train_host.py

Shows how to create and host a model. The code creates a project, creates a manifest
file, creates a dataset using the manifest file, and trains a model. Finally, if
desired, the example shows how to host the model. Used by `train_host.py`.

###### find_running_models.py
You are charged for the amount of time that an Amazon Lookout for Vision model is
running (hosted). Use this script to find the running models in the commercial AWS
partition. You can stop a model by calling the [StopModel](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/run-stop-model.html) operation.

###### update_dataset.py
Shows how to add or update images in an Amazon Lookout for Vision dataset.

###### export_datasets.py

Shows how to export the datasets from an Amazon Lookout for Vision project to an
Amazon S3 location.
Run this example at a command prompt with the following command.

```
python export_datasets.py <project> <destination>
``` 

- `project` - The project that you want to export the datasets from.
- `destination` - The Amazon S3 path that you want to copy the datasets to.

<!--custom.instructions.end-->

#### Hello Lookout for Vision

This example shows you how to get started using Lookout for Vision.

```
python hello.py
```


#### Create a manifest file

This example shows you how to create a Lookout for Vision manifest file and upload it to Amazon S3.


<!--custom.scenario_prereqs.lookoutvision_Scenario_CreateManifestFile.start-->
<!--custom.scenario_prereqs.lookoutvision_Scenario_CreateManifestFile.end-->

Start the example by running the following at a command prompt:

```
python datasets.py
```


<!--custom.scenarios.lookoutvision_Scenario_CreateManifestFile.start-->
<!--custom.scenarios.lookoutvision_Scenario_CreateManifestFile.end-->

#### Create, train, and start a model

This example shows you how to create, train, and start a Lookout for Vision model.


<!--custom.scenario_prereqs.lookoutvision_Scenario_CreateTrainStartModel.start-->


Start the example by running the following at a command prompt:

<!--custom.scenario_prereqs.lookoutvision_Scenario_CreateTrainStartModel.end-->


<!--custom.scenarios.lookoutvision_Scenario_CreateTrainStartModel.start-->
- `project` - A name for your project.
- `bucket` - The name of the Amazon S3 bucket in which to store your manifest files and
  training output. The bucket must be in your AWS account and in the same AWS Region as
  the Amazon S3 path supplied for `train` and `test`. For example, `amzn-s3-demo-bucket`.
- `train` - The Amazon S3 path where your training images are stored. For example,
  `s3://amzn-s3-demo-bucket/circuitboard/train/`.
- `test` - (Optional) the Amazon S3 path where your test images are stored. For example,
  `s3://amzn-s3-demo-bucket/circuitboard/test/`. If you don't supply a value,
  Lookout for Vision splits the training dataset to create a test dataset.

After training completes, use the performance metrics to decide if the model's
performance is acceptable. For more information, see
[Improving your model](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/improve.html).
If you are satisfied with the model's performance, the code allows you to start the
model. After the model starts, use `inference.py` to analyze an image.

**You are charged for the amount of time that your model is running and for the time
taken to successfully train your model.**

<!--custom.scenarios.lookoutvision_Scenario_CreateTrainStartModel.end-->

#### Export the datasets from a project

This example shows you how to export the datasets from a Lookout for Vision project.


<!--custom.scenario_prereqs.lookoutvision_Scenario_ExportDatasets.start-->
<!--custom.scenario_prereqs.lookoutvision_Scenario_ExportDatasets.end-->

Start the example by running the following at a command prompt:

```
python export_datasets.py
```


<!--custom.scenarios.lookoutvision_Scenario_ExportDatasets.start-->
<!--custom.scenarios.lookoutvision_Scenario_ExportDatasets.end-->

#### Find a project with a specific tag

This example shows you how to find a Lookout for Vision project with a specific tag.


<!--custom.scenario_prereqs.lookoutvision_Scenario_FindTagInProjects.start-->
<!--custom.scenario_prereqs.lookoutvision_Scenario_FindTagInProjects.end-->

Start the example by running the following at a command prompt:

```
python find_tag.py
```


<!--custom.scenarios.lookoutvision_Scenario_FindTagInProjects.start-->
- tag - The key of the tag that you want to find.
- value - The value of the tag that you want to find.

For more information about tags, see [Tagging models](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/tagging-model.html).

<!--custom.scenarios.lookoutvision_Scenario_FindTagInProjects.end-->

#### List models that are currently hosted

This example shows you how to list Lookout for Vision models that are currently hosted.


<!--custom.scenario_prereqs.lookoutvision_Scenario_ListHostedModels.start-->
<!--custom.scenario_prereqs.lookoutvision_Scenario_ListHostedModels.end-->

Start the example by running the following at a command prompt:

```
python hosting.py
```


<!--custom.scenarios.lookoutvision_Scenario_ListHostedModels.start-->
<!--custom.scenarios.lookoutvision_Scenario_ListHostedModels.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Lookout for Vision Developer Guide](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/what-is.html)
- [Lookout for Vision API Reference](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/Welcome.html)
- [SDK for Python Lookout for Vision reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/lookoutvision.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0