# Create the SageMaker geospatial Lambda function using the Lambda Java API

This example demonstrates how to create a Lambda function for the Amazon SageMaker pipeline and geospatial job example. 

A [SageMaker pipeline](https://docs.aws.amazon.com/sagemaker/latest/dg/pipelines.html) is a series of 
interconnected steps that can be used to automate machine learning workflows. You can create and run pipelines from SageMaker Studio by using Python, but you can also do this by using AWS SDKs in other
languages. Using the SDKs, you can create and run SageMaker pipelines and also monitor operations for them.

You need to build this Lambda function in order to successfully complete the Java example. You can find the full example under **workflow_sagemaker_pipes**.

### Prerequisites

To use this tutorial, you need the following:

+ An AWS account.
+ A Java IDE. 
+ Java 1.8 JDK or later.
+ Maven 3.6 or later.
+ Set up your development environment. For more information, see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-basics.html).

### Create a .jar file

You can compile the project into a .jar file, which will serve as input for [Create and run a SageMaker geospatial pipeline using the SDK for Java V2](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/workflow_sagemaker_pipes). This can be achieved by using the following Maven command.

    mvn package

The .jar file is located in the target folder. 

## Additional resources

* [SageMaker Developer Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/whatis.html)
* [SageMaker API Reference](https://docs.aws.amazon.com/sagemaker/latest/APIReference/Welcome.html)
* [Java Developer Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
