# Amazon EC2 examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage Amazon Compute Cloud 
(Amazon EC2) resources.

The examples are divided into the following files:

**ec2_basics_demo.py**

Shows how to create an Amazon EC2 instance, perform management tasks on the instance, 
and clean up everything created during the demo.

* Creates, stops, starts, and terminates instances.
* Creates a security key pair that is used to connect to instances using SSH.
* Creates security groups that grant access to instances. Uses the Amazon EC2 API to 
  change security groups to dynamically update access to instances.
* Creates an Elastic IP address and associates it with an instance.
* Cleans up all resources created during the demo.

**ec2_setup.py**

Shows how to create the following security resources and instances:

* Security keys
* Security groups and inbound rules
* Instances using an Amazon Machine Image (AMI)

**ec2_instance_management.py**

Shows how to manage the following aspects of an Amazon EC2 instance:

* Start and stop instances.
* Create, associate, and release an Elastic IP address.
* Get an instance's console output.
* Update the security group associated with an instance.

**ec2_teardown.py**

Shows how to terminate instances and clean up the following security resources:

* Security key pairs
* Security groups

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.6 or later
- Boto3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)
- An SSH client, such as Open SSH (to connect to demo instances)

## Running the code

Start the demonstration by running the following command at a command prompt:

```
python ec2_basics_demo.py
```  

During the demonstration, you are prompted to open a second command prompt
window, which you use to connect to Amazon EC2 instances using SSH.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/ec2/ec2_basics 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon EC2 examples](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/ec2-examples.html)
- [Boto3 Amazon EC2 service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/ec2.html)
- [Amazon Elastic Compute Cloud Documentation](https://docs.aws.amazon.com/ec2/index.html)
- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Regional Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
