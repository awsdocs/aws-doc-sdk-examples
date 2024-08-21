# JavaScript environment for the Transcription App tutorial

This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) 'Transcription app' tutorial.

For instructions on implementing this example, see [Build a transcription app with authenticated users](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/transcribe-app.html) in the
_AWS SDK for JavaScript v3 Developer Guide_.

# Getting started

1. Clone the [AWS Code Examples Repository](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment.
   See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for
   instructions.

1. Install the dependencies listed in the [root]/javascriptv3/example_code/cross-services/transcription-app/package.json.

**Note**: These include the AWS SDK for JavaScript v3 client modules for the AWS services required in this example,
which are _@aws-sdk/client-iam_, _@aws-sdk/client-cognito-identity_, _@aws-sdk/credential-provider-cognito-identity_, _@aws-sdk/s3-request-presigner_,
_@aws-sdk/client-dynamodb_, _@aws-sdk/util-create-request_, _@aws-sdk/util-format-url_, _@aws-sdk/client-transcribe_ and _@aws-sdk/client-s3_.
They also include third-party Node.js modules, path, node-fetch, and webpack.

```
npm install node -g
cd javascriptv3/example_code/cross-services/transcription-app
npm install
```
