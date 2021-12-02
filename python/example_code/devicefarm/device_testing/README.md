# AWS Device Farm CI runner example

## Purpose

Shows how to upload a compiled Android (or iOS) application and test package to 
AWS Device Farm, start a test, wait for test completion, and report the results. 

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

* A test package.
* A compiled application executable.
* A project Amazon Resource Name (ARN) in Device Farm device testing.
* A device pool ARN.
* A prefix to use to distinguish runs of the test (such as a Git branch).
* `pipenv`: This example uses Pipenv to contain its dependencies.  
    The following dependencies are used:
    * boto3
    * requests

### Command

Modify the `run_tests.py` script to use your ARNs and test and application packages in 
this directory. 

Make sure that Pipenv has installed the appropriate packages:

```
pipenv lock
pipenv install
```

Run the example:

```
pipenv run python run_tests.py
```

The results of your tests are written to the `results` folder. 

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
