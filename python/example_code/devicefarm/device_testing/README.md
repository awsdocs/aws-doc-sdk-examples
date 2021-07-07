# CI Runner example for Device Farm

## Purpose
This example covers a common use case of Device Farm: uploading a compiled Android (or iOS) application and test package to Device Farm, starting a test, waiting for pass/fail, and reporting those results. 

## ⚠️ Important

* We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see Grant Least Privilege (https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
* This code works only in `us-west-2`, the only region Device Farm is located.
* Running this code might result in charges to your AWS account. 

## prequisites

This example requires

* A test package
* A compiled application executable
* A project ARN in Device Farm device testing
* A device pool ARN
* A prefix to use to distinguish runs of the test (such as a git branch)

## Running the sample

Modify the `run_tests.py` script to use your ARNs and test/application packages in this directory. 

Make sure that pipenv has installed the appropriate packages:

```
pipenv lock
pipenv install
```

and run the sample:

```
pipenv run python run_tests.py
```

The results of your tests will be in `results/`. 