# Amazon Lookout for Vision Java code examples

This README discusses how to run and test the Java code examples for Amazon Lookout for Vision.

## Running the Amazon Lookout for Vision Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials. Some examples run AWS services and might incur charge. For details about the charges you can expect for a given service and operation, see the [AWS Pricing page](https://aws.amazon.com/pricing/).

Some of these examples perform *destructive* operations on AWS resources. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you can set up your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).

Before running the examples and unit tests, we recommend that you read the [Amazon Lookout for Vision documentation](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/what-is.html). To help you understand the Amazon Lookout for Vision API, read [Getting started with the AWS SDK](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/getting-started-sdk.html). We provide example images that you can use. For more information, see [Step 8: (Optional) Prepare example images](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/su-prepare-example-images.html). 

## Example code structure ##

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
- **DetectAnomalies.java** - Shows how to analyze an image for anomalies with [DetectAnomalies](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_DetectAnomalies.html).
- **ListDatasetEntries.java** - Shows how to list the JSON lines in an Amazon Lookout for Vision dataset with [ListDatasetEntries](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_ListDatasetEntries.html).
- **ListModelPackagingJobs.java** - Shows how to list the Amazon Lookout for Vision model packaging jobs in a project with [ListModelPackagingJobs](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_ListModelPackagingJobs.html).
- **ListModelTags.java** - Shows how to list tags attached to an Amazon Lookout for Vision model with [ListTagsForResource](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_ListTagsForResource.html).
- **ListModels.java** - Shows how to list the Amazon Lookout for Vision models in a project with [ListModels](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_ListModels.html).
- **ListProjects.java** - Shows how to list the Amazon Lookout for Vision projects in the current AWS account and AWS Region with [ListProjects](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_ListProjects.html).
- **StartModel.java** - Shows how to start hosting an Amazon Lookout for Vision model with [StartModel](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_StartModel.html). You are charged for the amount of time that your model is hosted.
- **StartModelPackagingJob.java** - Shows how to start a Amazon Lookout for Vision model packaging job with [StartModelPackagingJob](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_StartModelPackagingJob.html). You specify the model packaging job settings in a JSON format file. We provide a template JSON file for a [target device](./src/main/resources/packaging-job-request-device-template.json) (Jetson Xavier) and a template JSON file for a [target platform](./src/main/resources/packaging-job-request-hardware-template.json). For information about the package settings that you can make, see [Packaging your model (SDK)](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/package-job-sdk.html).
- **StopModel.java** - Shows how to stop a hosted Amazon Lookout for Vision model with [StopModel](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_StopModel.html).
- **TagModel.java** - Shows how to attach a tag to an Amazon Lookout for Vision model with [TagResource](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_TagResource.html).
- **UntagModel.java** - Shows how to remove a tag from an Amazon Lookout for Vision model with [UntagResource](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_UntagResource.html).
- **UpdateDatasetEntries.java** - Shows how to update Amazon Lookout for Vision dataset with a manifest file with [UpdateDatasetEntries](https://docs.aws.amazon.com/lookout-for-vision/latest/APIReference/API_UpdateDatasetEntries.html).


 ## Testing the Amazon Lookout for Vision files

You can test the Java code examples for Amazon Lookout for Vision by running a test file named **LookoutVisionTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is run, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon Lookout for Vision models. You are charged for the amount of time it takes to successfully train the test model and for the amount of time the test model is hosted._

 ### Properties file
Before running the Amazon Lookout for Vision JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an instance name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:


- **projectName** -  The name of the project to create in the tests.
- **modelDescription** - A description for the model.
- **modelTrainingOutputBucket** - The Amazon S3 bucket in which to place the training results.
- **modelTrainingOutputFolder** - The folder in modelTrainingOutputBucket in which to place the training results.
- **photo** - The location of an image to analyze with the trained model. 
- **manifestFile** - The location of a local manifest file that is used to populate the training dataset. For more information, see [Creating a manifest file](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/manifest-files.html). 
- **modelPackageJobJsonFile** - The location of the edge packaging Job request JSON file. We provide a template JSON file for a [target device](./src/main/resources/packaging-job-request-device-template.json) (Jetson Xavier) and a template JSON file for a [target platform](./src/main/resources/packaging-job-request-hardware-template.json). To successfully run the model packaging job test, make sure that the value of **ModelVersion** is "1". Each time you run the test, you must change the value of **ComponentVersion** and **JobName**. For information about the package settings that you can make, see [Packaging your model (SDK)](https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/package-job-sdk.html).

If you want to use the project, dataset, and model that the testing creates, disable the following tests:
- **deleteDataset_thenNotFound()**
- **deleteModel_thenNotFound()**
- **deleteProject_thenNotFound()**


### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running LookoutVisionTest
	Test 1 passed
	Test 2 passed
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
	[INFO]
	INFO] --------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO]--------------------------------------------
	[INFO] Total time:  12.003 s
	[INFO] Finished at: 2020-02-10T14:25:08-05:00
	[INFO] --------------------------------------------

### Unsuccessful tests

If you do not define the correct values in the properties file, your JUnit tests are not successful. You will see an error message such as the following. You need to double-check the values that you set in the properties file and run the tests again.

	[INFO]
	[INFO] --------------------------------------
	[INFO] BUILD FAILURE
	[INFO] --------------------------------------
	[INFO] Total time:  19.038 s
	[INFO] Finished at: 2020-02-10T14:41:51-05:00
	[INFO] ---------------------------------------
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project S3J2Project:  There are test failures.
	[ERROR];
