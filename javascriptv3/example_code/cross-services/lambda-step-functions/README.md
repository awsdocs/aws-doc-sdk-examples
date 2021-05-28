# TypeScript environment for tutorial to create an AWS serverless workflow using AWS SDK for JavaScript and AWS Step Functions
This is a workspace where you can find an AWS SDK for JavaScript version 3 (v3) tutorial that demonstrates how to 
create AWS serverless workflows using AWS Step Functions.

The [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/serverless-workflows-using-step-functions.html) contains these examples.

# Getting started

1. Clone the [AWS Code Examples Repository](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

1. Install the dependencies listed in the [root]/javascriptv3/example_code/cross-services/lambda-step-functions/package.json.

**Note**: These include the AWS SDK for JavaScript v3 client modules for the AWS services required in this example, 
which are *@aws-sdk/client-dynamodb*, *@aws-sdk/client-lambda*, *@aws-sdk/client-ses*, *@aws-sdk/client-api-gateway*, and*@aws-sdk/client-sns*

They also include third-party Node.js modules, including webpack.
```
npm install ts-node -g # If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/cross-services/lambda-step-functions
npm install
```


