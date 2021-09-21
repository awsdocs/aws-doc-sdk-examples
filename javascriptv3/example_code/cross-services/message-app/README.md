# JavaScript environment for tutorial building an Amazon Messaging app
This is a workspace where you can find an AWS SDK for JavaScript version 3 (v3) tutorial demonstrating how to build an app 
that sends and receives messages using Amazon Simple Queue Service (Amazon SQS).

For instructions on implementing this example, see [Creating an example messaging application](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/messaging-app.html) in the *AWS SDK for JavaScript v3 Developer Guide*.

# Getting started

1. Clone the [AWS Code Examples Repository](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

1. Install the dependencies listed in the [root]/javascriptv3/example_code/cross-services/message-app/package.json.

**Note**: These include the AWS SDK for JavaScript v3 client modules for the AWS services required in this example, 
which are *@aws-sdk/client-cognito-identity*,  *@aws-sdk/credential-provider-cognito-identity*, and *@aws-sdk/client-sqs"*.

They also include third-party Node.js modules, including webpack.
```
npm install node -g 
cd javascriptv3/example_code/cross-services/message-app/
npm install
```
