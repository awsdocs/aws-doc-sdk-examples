# TypeScript environment for Amazon Rekognition examples
This is a workspace where you can find working AWS SDK for JavaScript version 3 (v3) Amazon Rekognition examples. 

The [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) is available. 

The [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-rekognition/index.html) contains the API operations for the AWS SDK for JavaScript v3 Amazon Polly client module.

Amazon Rekognition makes it easy to add image and video analysis to your applications. 
You just provide an image or video to the Amazon Rekognition API, and the service can identify objects, 
people, text, scenes, and activities. It can detect any inappropriate content as well. 
Amazon Rekognition also provides highly accurate facial analysis, face comparison, and face search 
capabilities. You can detect, analyze, and compare faces for a wide variety of use cases,
including user verification, cataloging, people counting, and public safety. 
 
**NOTE:** The AWS SDK for JavaScript v3 is written in TypeScript so, for consistency, these examples are also 
in TypeScript. TypeScript extends of JavaScript so these examples can also be run as JavaScript. 
For more information, see [TypeScript homepage](https://www.typescriptlang.org/).

# Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which are *@aws-sdk/client-cognito-identity*, *@aws-sdk/credential-provider-cognito-identity*, and *@aws-sdk/polly-kinesis*.

```
npm install ts-node -g # If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/rekognition
npm install
```
3. Follow the instructions in the [Using JavaScript](https://docs.aws.amazon.com/rekognition/latest/dg/image-bytes-javascript.html) in the *Amazon Rekognition developer guide*.


