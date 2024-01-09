# Lookout for Vision code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Lookout for Vision.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

<!--custom.examples.start-->

## Example code structure

The following files provide utility classes for managing Amazon Lookout for Vision resources.

- **Models.java** - A class of static functions that manage Amazon Lookout for Vision models.
- **Projects.java** - A class of static functions that manage Amazon Lookout for Vision projects.
- **Datasets.java** - A class of static functions that manage Amazon Lookout for Vision datasets.
- **Hosting.java** - A class of static functions that manage the hosting of Amazon Lookout for Vision models.
- **EdgePackages.java** - A class of static functions that manage Amazon Lookout for Vision edge packaging jobs.

The following examples use the utility classes to show how to use the Amazon Lookout for Vision API.

- **CreateDataset.java** - Shows how to create an Amazon Lookout for Vision dataset with [CreateDataset](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_CreateDataset.html). You must have a manifest file to train the model. We provide a [script](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/ex-csv-manifest.html) that creates a manifest file from a .csv file. For more information, see [Creating a manifest file](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/manifest-files.html).
- **CreateModel.java** - Shows how to create an Amazon Lookout for Vision model with [CreateModel](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_CreateModel.html). You are charged for the amount of time it takes to successfully train a model.
- **CreateProject.java** - Shows how to create an Amazon Lookout for Vision project with [CreateProject](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_CreateProject.html).
- **DeleteDataset.java** - Shows how to delete an Amazon Lookout for Vision dataset with [DeleteDataset](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_DeleteDataset.html).
- **DeleteModel.java** - Shows how to delete an Amazon Lookout for Vision model with [DeleteModel](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_DeleteModel.html).
- **DeleteProject.java** - Shows how to delete an Amazon Lookout for Vision project with [DeleteProject](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_DeleteProject.html).
- **DescribeDataset.java** - Shows how to get information about an Amazon Lookout for Vision dataset with [DescribeDataset](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_DescribeDataset.html).
- **DescribeModel.java** - Shows how to get information about an Amazon Lookout for Vision model with [DescribeModel](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_DescribeModel.html).
- **DescribeModelPackagingJob.java** - Shows how to get information about an Amazon Lookout for Vision model packaging job with [DescribeModelPackagingJob](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_DescribeModelPackagingJob.html).
- **DescribeProject.java** - Shows how to get information about an Amazon Lookout for Vision project with [DescribeProject](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_DescribeProject.html).
- **DetectAnomalies.java** - Shows how to analyze an image for anomalies with [DetectAnomalies](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_DetectAnomalies.html). The configuration file is
  is **analysis-config.json** in the **resources** folder. For more information, see [Determining if an image is anomalous](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/inference-determine-anomaly-state.html).

- **ListDatasetEntries.java** - Shows how to list the JSON lines in an Amazon Lookout for Vision dataset with [ListDatasetEntries](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_ListDatasetEntries.html).
- **ListModelPackagingJobs.java** - Shows how to list the Amazon Lookout for Vision model packaging jobs in a project with [ListModelPackagingJobs](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_ListModelPackagingJobs.html).
- **ListModelTags.java** - Shows how to list tags attached to an Amazon Lookout for Vision model with [ListTagsForResource](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_ListTagsForResource.html).
- **ListModels.java** - Shows how to list the Amazon Lookout for Vision models in a project with [ListModels](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_ListModels.html).
- **ListProjects.java** - Shows how to list the Amazon Lookout for Vision projects in the current AWS account and AWS Region with [ListProjects](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_ListProjects.html).
- **ShowAnomalies.java** - Shows how to display classification and segmentation (image masks and anomaly labels) information on an image analyzed by [DetectAnomalies](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_DetectAnomalies.html). For more information,
  see [Showing classification and segmentation](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/inference-display-information.html).
- **StartModel.java** - Shows how to start hosting an Amazon Lookout for Vision model with [StartModel](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_StartModel.html). You are charged for the amount of time that your model is hosted.
- **StartModelPackagingJob.java** - Shows how to start a Amazon Lookout for Vision model packaging job with [StartModelPackagingJob](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_StartModelPackagingJob.html). You specify the model packaging job settings in a JSON format file. We provide a template JSON file for a [target device](./src/main/resources/packaging-job-request-device-template.json) (Jetson Xavier) and a template JSON file for a [target platform](./src/main/resources/packaging-job-request-hardware-template.json). For information about the package settings that you can make, see [Packaging your model (SDK)](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/package-job-sdk.html).
- **StopModel.java** - Shows how to stop a hosted Amazon Lookout for Vision model with [StopModel](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_StopModel.html).
- **TagModel.java** - Shows how to attach a tag to an Amazon Lookout for Vision model with [TagResource](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_TagResource.html).
- **UntagModel.java** - Shows how to remove a tag from an Amazon Lookout for Vision model with [UntagResource](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_UntagResource.html).
- **UpdateDatasetEntries.java** - Shows how to update Amazon Lookout for Vision dataset with a manifest file with [UpdateDatasetEntries](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_UpdateDatasetEntries.html).

<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->

#### Properties file

Before running the Amazon Lookout for Vision JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an instance name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **projectName** - The name of the project to create in the tests.
- **modelDescription** - A description for the model.
- **modelTrainingOutputBucket** - The Amazon S3 bucket in which to place the training results.
- **modelTrainingOutputFolder** - The folder in modelTrainingOutputBucket in which to place the training results.
- **photo** - The location of an image to analyze with the trained model.
- **anomalousPhoto** = The location of an anomalous image to analyze with the trained model.
- **anomalyLabel** = The label for an anomaly in the project.
- **manifestFile** - The location of a local manifest file that is used to populate the training dataset. For more information, see [Creating a manifest file](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/manifest-files.html).
- **modelPackageJobJsonFile** - The location of the edge packaging Job request JSON file. We provide a template JSON file for a [target device](./src/main/resources/packaging-job-request-device-template.json) (Jetson Xavier) and a template JSON file for a [target platform](./src/main/resources/packaging-job-request-hardware-template.json). To successfully run the model packaging job test, make sure that the value of **ModelVersion** is "1". Each time you run the test, you must change the value of **ComponentVersion** and **JobName**. For information about the package settings that you can make, see [Packaging your model (SDK)](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/package-job-sdk.html).

If you want to use the project, dataset, and model that the testing creates, disable the following tests:

- **deleteDataset_thenNotFound()**
- **deleteModel_thenNotFound()**
- **deleteProject_thenNotFound()**
<!--custom.tests.end-->

## Additional resources

- [Lookout for Vision Developer Guide](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/what-is.html)
- [Lookout for Vision API Reference](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Lookout for Vision reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/lookoutvision/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0