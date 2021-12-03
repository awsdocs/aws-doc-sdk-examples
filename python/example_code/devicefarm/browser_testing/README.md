# AWS Device Farm browser testing example

## Purpose

Uses PyTest with the AWS Device Farm browser testing feature. This example demonstrates:

* Using the Device Farm API to start browser sessions.
* Using the Resource Tagging API to tag browser sessions.
* Retrieving screenshots from the browser session.

This example gets you started using Selenium to test web applications using 
Device Farm as part of a CI system. The example must be run inside a Git repository 
and output is saved into a directory based on the most recent Git commit hash.

*Device Farm is an app testing service that enables you to test your iOS, Android and 
Fire OS apps on real, physical phones and tablets that are hosted by AWS.* 

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code works in `us-west-2`, the only AWS Region where Device Farm is located.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

* A Device Farm browser testing project. For more information on creating and managing 
projects in Device Farm, see 
[Projects in Device Farm Browser Testing](https://docs.aws.amazon.com/devicefarm/latest/testgrid/managing-projects.html).
* AWS programmatic credentials allowed to call these actions:
  * devicefarm:createTestGridUrl
  * devicefarm:getTestGridSession
  * resourcetaggingapi:tagResources
* `pipenv`: This example uses Pipenv to contain its dependencies. The following 
dependencies are used:
    * boto3
    * selenium
    * pytest
    * pytest-xdist (optional, if you want multiple parallel tests)

### Command

To run this example from a command line, use the following commands:

1. Install the dependencies into the pipenv-generated virtual environment.

    ```
    pipenv install
    ```

1. Run the example test suite.

    ```
    PROJECT_ARN="aws:arn:devicefarm:us-west-2:..." pipenv run pytest -s
    ```

Note: Not all tests are designed to pass.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
