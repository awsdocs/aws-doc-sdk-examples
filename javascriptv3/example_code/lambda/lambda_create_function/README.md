# TypeScript environment for AWS Lambda examples
This is a workspace where you can find the files required for a tutorial that demonstrates how to use the 
AWS SDK for JavaScript version 3 (v3) to create an AWS Lambda function that creates an Amazon DynamoDB 
table from the browser. You can find this example in the [AWS SDK for JavaScript v3 Developer Guide (v3)](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-provision-resources.html) 

The [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) is available. 

The [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-lambda/index.html) contains the API operations for the AWS SDK for JavaScript v3 Amazon Polly client module.

AWS Lambda is a serverless compute service that lets you run code without provisioning or managing servers, 
creating workload-aware cluster scaling logic, maintaining event integrations, or managing runtimes. 
 
**NOTE:** The AWS SDK for JavaScript v3 is written in TypeScript so, for consistency, these examples are also 
in TypeScript. TypeScript extends JavaScript so these examples can also be run as JavaScript. 
For more information, see [TypeScript homepage](https://www.typescriptlang.org/).

# Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These dependencies include the client module for the AWS services required in these example, 
which are *@aws-sdk/client-cognito-identity*, *@aws-sdk/credential-provider-cognito-identity*, *@aws-sdk/client-dynamodb*, 
*@aws-sdk/client-iam*, *@aws-sdk/client-cloudformation* and *@aws-sdk/client-lambda*.


```
npm install ts-node -g # If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/lambda/lambda_create_function/src
npm install
```


