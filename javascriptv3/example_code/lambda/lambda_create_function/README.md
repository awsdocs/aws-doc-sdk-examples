# JavaScript environment for AWS Lambda example
## Purpose
This is a workspace where you can find the files required for a tutorial that demonstrates how to use the 
AWS SDK for JavaScript version 3 (v3) to create an AWS Lambda function that creates an Amazon DynamoDB 
table from the browser. 

For instructions on implementing this example, see [Creating and using Lambda functions](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-lambda/index.html) in the *AWS SDK for JavaScript v3 Developer Guide*.

# Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These dependencies include the client module for the AWS services required in these example, 
which are *@aws-sdk/client-cognito-identity*, *@aws-sdk/credential-provider-cognito-identity*, *@aws-sdk/client-dynamodb*, 
*@aws-sdk/client-iam*, *@aws-sdk/client-cloudformation* and *@aws-sdk/client-lambda*.


```
npm install node -g' instead
cd javascriptv3/example_code/lambda/lambda_create_function/src
npm install
```


