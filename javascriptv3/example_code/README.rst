###############################
AWS SDK for JavaScript v3 examples
###############################

These are examples for the AWS SDK for JavaScript version 3 (v3) public documentation.

Prerequisites
=============
To build and run these examples, you need to:

- Set up the project environment to run these TypeScript examples with Node.js, and install the required AWS SDK for JavaScript and third-party modules. Follow the instructions in the README.md in the folder with the examples you're running; for example, `instructions for Amazon S3 examples <https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/javascriptv3/example_code/s3/README.md>`_.
- Create a shared configurations file with your user credentials. See the `AWS SDK for JavaScript v3 Developer Guide <https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/loading-node-credentials-shared.html>`_ for more information.

Running the examples
====================
Examples are written for either execution in a browser script or in Node.js, usually depending on the use case for each. The two scenarios typically differ in how you supply credentials to the code. For information on the differences, see the `AWS SDK for JavaScript v3 Developer Guide <https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/setting-credentials.html>`_.

The Node.js examples are all run from the command line.

Unit tests
=============
`Unit tests<./tests>`_ are provided for all examples, using the `Jest <https://jestjs.io/>`_ framework.

**Note**: Jest does not currently support jest.mock in a clean way in ECMAscript 6 (ES6) syntax, the JavaScript syntax used in our code examples.
We recommend converting the code examples you want to unit test to CommonJS syntax. For guidelines to convert ES6 syntax to CommonJS syntax, see
`JavaScript ES6/CommonJS syntax <https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html>`_.

Resources
=============
`AWS SDK for JavaScript v3 <https://github.com/aws/aws-sdk-js-v3>`_
`AWS SDK for JavaScript v3 Developer Guide <https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/>`_
`AWS SDK for JavaScript v3 API Reference <http://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/index.html>`_

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
