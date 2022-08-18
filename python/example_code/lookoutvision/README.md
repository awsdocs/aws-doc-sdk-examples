# Amazon Lookout for Vision example code

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Lookout for Vision to
create a model that detects anomalies in images. Additional instructions can be found 
in [Amazon Lookout for Vision Developer Guide](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/what-is.html).

* Create a project to manage your model.
* Add a training dataset (and optional test dataset) that's used to train the model.
* Train the model.
* Detect anomalies in images using the model.
* Other project management tasks, such as tagging, model deletion, and project listing.

*Lookout for Vision enables you to find visual defects in industrial products, 
accurately and at scale.*

## Code examples

### Scenario examples

* [Create a manifest file](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/datasets.py)
* [Create, train, and start a model](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/train_host.py)
* [Find a project with a specific tag](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/find_tag.py)
* [List models that are currently hosted](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/find_running_models.py)

### API examples

* [Create a dataset](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/datasets.py)
(`CreateDataset`)
* [Create a model](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/models.py)
(`CreateModel`)
* [Create a project](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/projects.py)
(`CreateProject`)
* [Delete a dataset](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/datasets.py)
(`DeleteDataset`)
* [Delete a model](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/models.py)
(`DeleteModel`)
* [Delete a project](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/projects.py)
(`DeleteProject`)
* [Describe a dataset](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/datasets.py)
(`DescribeDataset`)
* [Update a dataset](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/datasets.py)
(`UpdateDatasetEntries`)
* [Describe a model](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/models.py)
(`DescribeModel`)
* [Detect anomalies in an image with a trained model](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/inference.py)
(`DetectAnomalies`)
* [List models](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/models.py)
(`ListModels`)
* [List projects](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/projects.py)
(`ListProjects`)
* [Start a model](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/models.py)
(`StartModel`)
* [Stop a model](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/lookoutvision/models.py)
(`StopModel`)

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Successfully training a model and hosting a model results in charges to your AWS account.

## Running the code

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7 or later
- Boto3 1.17.47 or later
- PyTest 5.3.5 or later (to run unit tests)

### Command

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
s3://doc-example-bucket/<train or test>/
    normal/
    anomaly/
```
`train` and `test` can be any folder path.
Place normal images in the `normal` folder. Anomalous images in the `anomaly` folder.

### Create and host a model

This example creates and trains a model. Optionally, you can host the model after 
training completes. Run this example at the command prompt with the following command.

```
python train_host.py <project> <bucket> <train> <test>
``` 

- `project` - A name for your project.
- `bucket` - The name of the Amazon S3 bucket in which to store your manifest files and 
training output. The bucket must be in your AWS account and in the same AWS Region as 
the Amazon S3 path supplied for `train` and `test`. For example, `doc-example-bucket`.
- `train` - The Amazon S3 path where your training images are stored. For example, 
`s3://doc-example-bucket/circuitboard/train/`.
- `test` - (Optional) the Amazon S3 path where your test images are stored. For example, 
`s3://doc-example-bucket/circuitboard/test/`. If you don't supply a value, 
Lookout for Vision splits the training dataset to create a test dataset.

After training completes, use the performance metrics to decide if the model's 
performance is acceptable. For more information, see 
[Improving your model](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/improve.html).
If you are satisfied with the model's performance, the code allows you to start the 
model. After the model starts, use `inference.py` to analyze an image.

**You are charged for the amount of time that your model is running and for the time 
taken to successfully train your model.**

### Detect anomalies in images using a trained model

Shows how to detect anomalies in an image by using a trained model. 
Run this example at a command prompt with the following command.

```
python inference.py <project> <version> <image>
``` 

- `project` - The project that contains the model that you want to use.
- `version` - The version of the model that you want to use.
- `image` - The image that you want to analyze. You can supply a JPEG or PNG format 
file. You can also supply the Amazon S3 path of an image stored in an Amazon S3 bucket. 
If you are using the example circuit board dataset, you can find extra images in the 
`extra_images` folder. 

### Find tags
This example searches all of your projects for models tagged with a specific tag and tag value. Run this example at a 
command prompt with the following command.

```
python find_tag.py <tag> <value>
``` 

- tag - The key of the tag that you want to find.
- value - The value of the tag that you want to find.

 For more information about tags, see [Tagging models](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/tagging-model.html). 

## Example structure

The example contains the following files.

### datasets.py

A class that shows how to create and manage datasets. Also shows how to create a 
manifest file based on images found in an Amazon S3 bucket. Used by `train_host.py`.

Manifest files are used to create training and test datasets. `train_host.py` uses 
`datasets.py` to create training and (optionally) test manifest files, and upload them 
to an Amazon S3 bucket location that you specify. For more information about manifest 
files, see [Creating a dataset using an Amazon SageMaker Ground Truth manifest file](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/create-dataset-ground-truth.html). 

### find_tag.py

Shows how to find a tag attached to a Lookout for Vision model.

### hosting.py

A class that shows how to start and stop a Lookout for Vision project. Also shows how 
to list hosted models. Used by `train_host.py`.

### inference.py

A class that shows how to analyze an image (JPEG/PNG) with a hosted Lookout for Vision 
model. You can also analyze an image stored in an Amazon S3 bucket.
The example shows how you can classify images as normal or anomalous. It also shows how to 
use segmentation information returned from a segmentation model. For more information,
 see [Detecting anomalies in an image](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/inference-detect-anomalies.html).
 To run the example, supply an image file name and a configuration JSON file with the following format. 

    {
        "project":"The Lookout for Vision project name.",
        "model_version" : "The model version.",
        "confidence_limit" : The minimum acceptable confidence. (Float 0 - 1).,
        "coverage_limit" : The maximum acceptable percentage coverage of an anomaly (Float 0-1).,
        "anomaly_types_limit" : The maximum number of allowable anomaly types. (Integer),
        "anomaly_label" : "The anomaly label for the type of anomaly that you want to check."
    }

We provide a template JSON configuration file in config.json.

### models.py

A class that shows how to train and manage a Lookout for Vision model. Used by 
`train_host.py`.

### projects.py

A class that shows how to create and manage a Lookout for Vision project. Used by 
`train_host.py`.

### train_host.py

Shows how to create and host a model. The code creates a project, creates a manifest 
file, creates a dataset using the manifest file, and trains a model. Finally, if 
desired, the example shows how to host the model. Used by `train_host.py`.

### find_running_models.py
You are charged for the amount of time that an Amazon Lookout for Vision model is 
running (hosted). Use this script to find the running models in the commercial AWS 
partition. You can stop a model by calling the [StopModel](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/run-stop-model.html) operation. 

### update_dataset.py
Shows how to add or update images in an Amazon Lookout for Vision dataset.


## Additional information

- [Boto3 Amazon Lookout for Vision service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/lookoutvision.html)
- [Amazon Lookout for Vision documentation](https://docs.aws.amazon.com/lookout-for-vision)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
