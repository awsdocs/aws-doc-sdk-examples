# Create the SageMaker geospatial Lambda function using the SDK for Kotlin

This example demonstrates how to create an AWS Lambda function for the Amazon SageMaker pipeline and geospatial job example. 

A [SageMaker pipeline](https://docs.aws.amazon.com/sagemaker/latest/dg/pipelines.html) is a series of 
interconnected steps that can be used to automate machine learning workflows. You can create and run pipelines from SageMaker Studio by using Python, but you can also do this by using AWS SDKs in other
languages. Using the SDKs, you can create and run SageMaker pipelines and also monitor operations for them.

You need to build this Lambda function in order to successfully complete the Java example. You can find the full example under **workflow_sagemaker_pipes**.

### Prerequisites

To use this tutorial, you need the following:

+ An AWS account.
+ A Kotlin IDE. (This tutorial uses the IntelliJ IDE with the Kotlin plugin).
+ Java JDK 11.
+ Gradle 6.8 or higher.
+ You must also set up your Kotlin development environment. For more information, see [Get started with the SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html).


### Create a .jar file

You can compile the project into a .jar file, which will serve as input for [Create and run a SageMaker geospatial pipeline using the SDK for Kotlin](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/usecases/workflow_sagemaker_pipes). 

After you create the Kotlin Lambda project, you can build the required .jar file by using the shadowJar plugin.

![AWS App](images/shawdow.png)

Perform the following tasks:

1. Create a new Kotlin project using an IDE and import the project from GitHub. 
2. Create the FAT JAR by running shadowJar.
3. Use the AWS Management Console to place the FAT JAR into the Amazon Simple Storage Service (Amazon S3) bucket. This S3 bucket name is input to the [Create and run a SageMaker geospatial pipeline using the SDK for Kotlin](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/usecases/workflow_sagemaker_pipes).  

## Additional resources

* [SageMaker Developer Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/whatis.html)
* [SageMaker API Reference](https://docs.aws.amazon.com/sagemaker/latest/APIReference/Welcome.html)
* [Kotlin Developer Guide](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/home.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.