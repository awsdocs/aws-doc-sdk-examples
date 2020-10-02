# TypeScript environment for Amazon Lambda examples
Environment for AWS SDK for JavaScript (v3) Amazon Lambda tutorial. 

The preview version of the SDK is available [here](https://github.com/aws/aws-sdk-js-v3). 

For more information about these examples, see the [AWS documentation for this tutorial](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-functions.html) (release pending).

AWS Lambda lets you run code without provisioning or managing servers. You pay only for the compute time you consume.

This is a workspace where you can find working AWS SDK for JavaScript (v3) Lambda examples. 

**NOTE:** The AWS SDK for JavaScript (v3) is written in TypeScript so, for consistency, these examples are also in TypeScript. TypeScript extends of JavaScript so these examples can also be run as JavaScript.

# Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) repo to your local environment. See [here](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These dependencies include the client modules for the AWS services that this example requires, 
which are *@aws-sdk/client-dynamodb*, *@aws-sdk/client-lambda*, *@aws-sdk/client-iam*, 
*@aws-sdk/client-s3*, *@aws-sdk/client-dynamoDB*, *@aws-sdk/client-cognito-identity*, 
*@aws-sdk/credential-provider-cognito-identity*, and *@aws-sdk/client-lambda*.
```
npm install ts-node -g // If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/lambda
npm install
```
