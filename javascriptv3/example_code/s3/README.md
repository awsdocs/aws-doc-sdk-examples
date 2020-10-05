# TypeScript environment for Amazon S3 examples
Environment for AWS SDK for JavaScript version 3 (v3) Amazon S3 examples. 

The [preview version of the AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) is available. 

Once it's released, see the [AWS documentation for these examples](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-examples.html) for the topic containing these examples.

Amazon Simple Storage Service (Amazon S3) is an object storage service that offers industry-leading scalability, data availability, security, and performance.

This is a workspace where you can find working AWS SDK for JavaScript v3 S3 examples. 

**NOTE:** The AWS SDK for JavaScript v3 is written in TypeScript so, for consistency, these examples are also in TypeScript. TypeScript extends of JavaScript so these examples can also be run as JavaScript. For more information, see [TypeScript homepage](https://www.typescriptlang.org/).


# Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These dependencies include the client modules for the AWS services that this example requires, 
which is *@aws-sdk/client-s3*, *@aws-sdk/client-cognito-identity*, and 
*@aws-sdk/credential-provider-cognito-identity*.
```
npm install ts-node -g // If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/s3
npm install
```
3. If you're using JavaScript, change the sample file extension from ```.ts``` to ```.js```.


4. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

5. Run sample code:
```
cd src
ts-node [example name].ts // e.g., ts-node s3.ts
```
