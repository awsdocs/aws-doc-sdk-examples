# Amazon EC2 C++ SDK code examples

## Purpose
The code examples in this directory demonstrate how to work with the Amazon Elastic Compute Cloud (Amazon EC2) 
using the AWS SDK for C++.

Amazon EC2 is a web service that provides resizable computing capacity—literally, 
servers in Amazon's data centers—that you use to build and host your software systems.

## Code examples

### API examples
- [Attach a address](./allocate_address.cpp) (GetAllocationId)
- [Create an instance](./create_instance.cpp) (GetInstances)
- [Create a key pair](./create_key_pair.cpp) (CreateKeyPair)
- [Create a security group](./create_security_group.cpp) (CreateSecurityGroup)
- [Delete a key pair](./delete_key_pair.cpp) (DeleteKeyPair)
- [Delete a security group](./delete_security_group.cpp) (DeleteSecurityGroup)
- [Describe your addresses](./describe_addresses.cpp) (DescribeAddresses)
- [Describe your instances](./describe_instances.cpp) (DescribeInstances)
- [Describe your key pairs](./describe_key_pairs.cpp) (DescribeKeyPairs)
- [Describe your security groups](./describe_security_groups.cpp) (DescribeSecurityGroups)
- [Monitor an instance](./monitor_instance.cpp) (MonitorInstances)
- [Reboot and instance](./reboot_instance.cpp) (RebootInstances)
- [Release an address](./release_address.cpp) (ReleaseAddress)
- [Stop/Start an instance](./start_stop_instance.cpp) (StartInstances/StopInstances)

## ⚠ Important
- We recommend that you grant your code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples

### Prerequisites
- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- Complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to obtain and build the SDK, and how to build your own code utilizing the SDK with a sample "Hello World"-style application. 
- See [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

To run these code examples, your AWS user must have permissions to perform these actions with Amazon EC2.  
The AWS managed policy named "AmazonEC2FullAccess" may be used to bulk-grant the necessary permissions.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [Amazon Elastic Compute Cloud Documentation](https://docs.aws.amazon.com/ec2/)
