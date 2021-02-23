# TypeScript environment for the Transcription App tutorial
This is a workspace where you can find an AWS SDK for JavaScript version 3 (v3) tutorial that demonstrates how to 
use Amazon API Gateway to invoke AWS Lambda functions.

The [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-scan-and-publish-message.html) contains these examples.

# Getting started

1. Clone the [AWS Code Examples Repository](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

1. Install the dependencies listed in the [root]/javascriptv3/example_code/cross-services/transcription-app/package.json.

**Note**: These include the AWS SDK for JavaScript v3 client modules for the AWS services required in this example, 
which are  *@aws-sdk/client-cognito-identity*, *@aws-sdk/credential-provider-cognito-identity*, *@aws-sdk/client-dynamodb*,
*@aws-sdk/client-iam*, *@aws-sdk/client-lambda*, *@aws-sdk/client-cloudformation*, *@aws-sdk/client-api-gateway*, and*@aws-sdk/client-sns*

They also include third-party Node.js modules, including webpack.
```
npm install ts-node -g # If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/cross-services/lamba-api-gateway
npm install
```


