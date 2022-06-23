# JavaScript environment for tutorial to create an AWS serverless workflow using AWS SDK for JavaScript and AWS Step Functions
This is a workspace where you can find an AWS SDK for JavaScript version 3 (v3) tutorial that demonstrates how to 
create AWS serverless workflows using AWS Step Functions.

For instructions on implementing this example, see [Creating AWS serverless workflows using AWS SDK for JavaScript](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/serverless-step-functions-example.html) in the *AWS SDK for JavaScript v3 Developer Guide*.

# Getting started

1. Clone the [AWS Code Examples Repository](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

1. Install the dependencies listed in the [root]/javascriptv3/example_code/cross-services/lambda-step-functions/package.json.

**Note**: These include the AWS SDK for JavaScript v3 client modules for the AWS services required in this example, 
which include *@aws-sdk/client-dynamodb* and *@aws-sdk/client-ses*.

They also include third-party Node.js modules, including webpack.
```
npm install node -g 
cd javascriptv3/example_code/cross-services/lambda-step-functions
npm install
```


