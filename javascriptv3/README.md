# AWS SDK for JavaScript (v3) code examples

## Overview

The code examples in this topic show you how to use the AWS SDK for JavaScript (v3) with AWS.

The AWS SDK for JavaScript (v3) provides a JavaScript API for AWS infrastructure services. Using the SDK, you can build applications on top of Amazon S3, Amazon EC2, Amazon DynamoDB, and more.

## Types of code examples

- **Single-service actions** - Code examples that show you how to call individual service functions.

- **Single-service scenarios** - Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- **Cross-service examples** - Sample applications that work across multiple AWS services.

### Find code examples

Single-service actions and scenarios are organized by AWS service in the `example_code` folder. A README in each folder lists and describes how to run the examples.

Cross-service examples are located in the [_cross-services folder_](./example_code/cross-services). A README in each folder describes how to run the example.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code the least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites

- Install the latest stable version of Node.js.
- Set up a shared configuration file with your credentials. For more information, see the [AWS SDK for JavaScript (v3) Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/loading-node-credentials-shared.html).
- Install dependencies by running `npm i` from the same path as this document.

## Tests

**Note**: Running the tests might result in charges to your AWS account.

You can run tests for a specific service, or for every service in this repository. Choose whether to run unit tests, integration tests, or both.

- To run unit tests, use the following command:

  `npm test`

- To run integration tests, use the following command:

  `npm run integration-test`

- To run tests for a specific service, follow the instructions in the service's README.

### Output

If you run tests using the preceding commands, output will be stored in `unit_test.log` or `integration_test.log`. Errors are still logged to the console.

## Docker image (Beta)

This example is available in a container image hosted on [Amazon Elastic Container Registry (ECR)](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html). This image will be pre-loaded with all JavaScript v3 examples with dependencies pre-resolved. It is used for running tests.

- [SDK for JavaScript v3 image](https://gallery.ecr.aws/b4v4v1s0/javascriptv3)

### Build the Docker image

1. Install and run Docker on your machine.
2. Navigate to the root directory of this repository.
3. Run `docker build -t <image_name> -f javascriptv3/Dockerfile .` and replace `<image_name>` with a name for the image.

### Launch the Docker container

1. Run `docker run -it -v /Users/corepyle/.aws/credentials:/home/automation/.aws/credentials <image_name>`. `-it` launches an interactive terminal. `-v ~/.aws...` is optional but recommended. It will mount your local credentials file to the container.
2. The Dockerfile is configured to automatically run integration tests when the container is run.

## Contribute

Contributions are welcome. To increase the likelihood of your contribution
being accepted, adhere to the following guidelines.

### Maintain directory structure

- `javascriptv3` is considered the project root.
- All examples exist under `example_code`.
- Each directory under `example_code` corresponds to an AWS service.
- Directory names should be lowercase with underscores.
- File names should be lowercase with dashes.
- `cross-services` is a special directory for examples that use multiple services.
- A service directory has the following structure:
  - ```
    actions/
      {action-name}.js
    scenarios/
      web/
        {web-scenario-name}/
      {scenario-name}.js
    tests/
      {integ-test-name}.integration.test.js
      {unit-test-name}.unit.test.js
    client.js
    package.json
    README.md
    vite.config.js
    ```

### Create runnable actions and scenarios

Place the following code at the bottom of each example
to make it runnable from the command line.

```
// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  functionName();
}
```

### Create testable actions and scenarios

Export one primary function from each example. Do not create examples
that require input or cannot run without human intervention.

Create function names that match the action name. `CreateUserCommand` becomes
`createUser`.

```
export const createUser = (name) => {
  const command = new CreateUserCommand({ UserName: name });
  return client.send(command);
};
```

### Create tests

Every example should be covered by an integration test. Each integration test must
run the example and verify that it ran correctly.

## Configure Visual Studio Code (VS Code)

### ESLint

To configure ESLint in VS Code, add the following to `settings.json`:

```
  "eslint.workingDirectories": ["javascriptv3/example_code/reactnative/ReactNativeApp", "javascriptv3"],
```

## Additional resources

- [AWS SDK for JavaScript (v3)](https://github.com/aws/aws-sdk-js-v3)
- [AWS SDK for JavaScript (v3) Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/)
- [AWS SDK for JavaScript (v3) API Reference](http://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
