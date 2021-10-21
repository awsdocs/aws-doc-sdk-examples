# AWS Device Farm Browser Testing Sample Test Suite

## Purpose
This is an example of using PyTest with the AWS Device Farm browser testing feature. Device Farm is a service that provides real-world environments to test software and web applications with, from phones to desktop browsers. This example demonstrates:

* Using the Device Farm API to start browser sessions
* Using the Resource Tagging API to tag browser sessions
* Retrieving screenshots from the browser session

This example is intended to give you a start to using Selenium to test web applications using Device Farm as a part of a CI system. To that end, this example expects to be run inside a Git repository, and output is saved into a directory based on the most recent Git commit hash. 

## ⚠️ Important

* We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see Grant Least Privilege (https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
* This code works only in `us-west-2`, the only region Device Farm is located.
* Running this code might result in charges to your AWS account. 

## Prerequisites

1. A Device Farm browser testing project. For more information on creating and managing projects in Device Farm, see [Projects in Device Farm Browser Testing](https://docs.aws.amazon.com/devicefarm/latest/testgrid/managing-projects.html) from the guide.
2. AWS programmatic credentials allowed to call
  * devicefarm:createTestGridUrl
  * devicefarm:getTestGridSession
  * resourcetaggingapi:tagResources
3. `pipenv`: This example uses Pipenv to contain its dependencies. If you are uncomfortable or unable to use such, 
    The following dependencies are used:
    
    * boto3
    * selenium
    * pytest
    * pytest-xdist (optional, if you want multiple parallel tests)

## Running the sample

To run this sample, from a command line, use the following commands:

```
# This will install the dependencies into the pipenv-generated virtual environment
pipenv install
# This will run the sample test suite
PROJECT_ARN="aws:arn:devicefarm:us-west-2:..." pipenv run pytest -s
```

Note: Not all tests are designed to pass.

--
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
