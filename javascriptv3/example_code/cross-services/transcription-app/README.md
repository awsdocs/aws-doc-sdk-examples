# TypeScript environment for the Transcription App tutorial
This is a workspace where you can find working AWS SDK for JavaScript version 3 (v3) 'Transcription app' tutorial.

The [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/tarnscription-app.html) contains these examples.

# Getting started

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

1. Install the dependencies listed in the [root]/javascriptv3/example_code/cross-services/transcription-app/package.json.

**Note**: These include the AWS SDK for JavaScript v3 client modules for the AWS services required in this example, 
which are *@aws-sdk/client-iam*, *@aws-sdk/client-cognito-identity*, *@aws-sdk/credential-provider-cognito-identity*, *@aws-sdk/s3-request-presigner*,
*@aws-sdk/client-dynamodb*, *@aws-sdk/util-create-request*, *@aws-sdk/util-format-url*, *@aws-sdk/client-transcribe* and *@aws-sdk/client-s3*.
They also include third-party Node.js modules, path, node-fetch, and webpack.
```
npm install ts-node -g // If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/cross-services/transcription-app 
npm install
```

