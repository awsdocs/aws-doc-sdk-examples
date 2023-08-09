# Create the SageMaker geospatial Lambda function using the Lambda Java rumtime API

This example demonstrates how to create a Lambda function for the Amazon SageMaker pipeline and geospatial job example. 

A [SageMaker pipeline](https://docs.aws.amazon.com/sagemaker/latest/dg/pipelines.html) is a series of 
interconnected steps that can be used to automate machine learning workflows. You can create and run pipelines from SageMaker Studio by using Python, but you can also do this by using AWS SDKs in other
languages. Using the SDKs, you can create and run SageMaker pipelines and also monitor operations for them.

You need to build this Lambda function in order to successfully complete the Java example. You can find the full example under **workflow_sagemaker_pipes**.

### Create a .jar file

You can package up the project into a .jar file that you can use as input to [Create and run a SageMaker geospatial pipeline using the SDK for Java V2](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/workflow_sagemaker_pipes) by using the following Maven command.

    mvn package

The .jar file is located in the target folder. 