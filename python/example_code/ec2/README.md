# Amazon EC2 examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage Amazon Compute Cloud 
(Amazon EC2) resources. Learn to accomplish the following tasks:

* Create security keys, groups, and instances.
* Start and stop instances, use Elastic IP addresses, and update security 
groups.
* Clean up security keys and security groups.
* Permanently terminate instances. 

*Amazon EC2 is a web service that provides resizable computing capacity that you use 
to build and host your software systems.*

## Code examples

**Cross-service example**

* [Create and manage instances](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_basics_demo.py)

**API examples**

* [Allocate an Elastic IP address](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_instance_management.py)
(`allocate_address`)
* [Associate an Elastic IP address with an instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_instance_management.py)
(`associate_address`)
* [Change the security group on an instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_instance_management.py)
(`authorize_security_group_ingress`)
* [Create a security group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_setup.py)
(`create_security_group`)
* [Create a security key pair](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_setup.py)
(`create_key_pair`)
* [Create and run an instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_setup.py)
(`run_instances`)
* [Delete a security group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_teardown.py)
(`delete_security_group`)
* [Delete a security key pair](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_teardown.py)
(`delete_key_pair`)
* [Disassociate an Elastic IP address from an instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_instance_management.py)
(`disassociate_address`)
* [Get the console output of an instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_instance_management.py)
(`get_console_output`)
* [Release an Elastic IP address](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_instance_management.py)
(`release_address`)
* [Set inbound rules for a security group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_instance_management.py)
(`modify_network_interface_attribute`)
* [Start an instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_instance_management.py)
(`start_instances`)
* [Stop an instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_instance_management.py)
(`stop_instances`)
* [Terminate an instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/ec2/ec2_teardown.py)
(`terminate_instances`)


## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7 or later
- Boto3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)
- An SSH client, such as Open SSH (to connect to demo instances)

### Command

Start the demonstration by running the following command at a command prompt:

```
python ec2_basics_demo.py
```  

During the demonstration, you are prompted to open a second command prompt
window, which you use to connect to Amazon EC2 instances using SSH.

### Example structure

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

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
