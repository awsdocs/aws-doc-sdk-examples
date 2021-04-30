# TypeScript environment for the Video Analyzer App tutorial

This is a workspace where you can find working AWS SDK for JavaScript version 3 (v3) 'Video analyzer app' tutorial.

The [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/video-analyzer.html) contains this examples.

# Getting started

1. Clone the [AWS Code Examples Repository](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment.
   See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for
   instructions.

2. Install the dependencies listed in the [root]/javascriptv3/example_code/cross-services/transcription-app/package.json.

**Note**: These include the AWS SDK for JavaScript v3 client modules for the AWS services required in this example,
which are _@aws-sdk/client-cognito-identity_, _@aws-sdk/client-rekognition_, _@aws-sdk/client-s3_,
_@aws-sdk/client-ses_, and _@aws-sdk/credential-provider-cognito-identity_.

They also include third-party Node.js modules, path, node-fetch, and webpack.

```
npm install ts-node -g # If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/cross-services/video-analyzer
npm install
```

3. Follow the instruction in the [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/video-analyzer.html).
