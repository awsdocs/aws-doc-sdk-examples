# AWS SDK for JavaScript v3 examples

These are examples for the AWS SDK for JavaScript version 3 (v3) public documentation.

## Prerequisites
To build and run these examples, you need to:

* Set up the project environment to run these TypeScript examples with Node.js, and install the required AWS SDK for JavaScript and third-party modules. Follow the instructions in the README.md in the folder with the examples you're running; for example, [instructions for Amazon S3 examples](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/javascriptv3/example_code/s3/README.md).
* Create a shared configurations file with your user credentials. See the [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/loading-node-credentials-shared.html) for more information.

## Running the examples
Examples are written for either execution in a browser script or in Node.js, usually depending on the use case for each. The two scenarios typically differ in how you supply credentials to the code. For information on the differences, see the [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/setting-credentials.html).

The Node.js examples are all run from the command line.

## Unit tests
[Unit tests](./tests) are provided for many examples, using the [Jest](https://jestjs.io/) framework.

To run tests, navigate to the **tests** folder.

For example, to run tests on the S3 folder, enter the following sequence of commands at the command prompt:

```
npm install node -g
cd javascriptv3/example_code/s3/tests
npm install
npm test

```
## Resources
* [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3)
* [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/)
* [AWS SDK for JavaScript v3 API Reference](http://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
