# Amazon EC2 Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Elastic Compute Cloud (Amazon EC2).

## Running the Amazon EC2 Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources,  such as deleting a security group. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **AllocateAddress** - Demonstrates how to allocate an elastic IP address for an Amazon EC2 instance.
- **CreateInstance** - Demonstrates how to create an Amazon EC2 instance.
- **CreateKeyPair** - Demonstrates how to create an Amazon EC2 key pair.
- **CreateSecurityGroup** - Demonstrates how to create an Amazon EC2 security group.
- **DeleteKeyPair** - Demonstrates how to delete an Amazon EC2 key pair.
- **DeleteSecurityGroup** - Demonstrates how to delete an Amazon EC2 security group.
- **DescribeAccount** - Demonstrates how to get information about the Amazon EC2 account.
- **DescribeAddresses** - Demonstrates how to get information about elastic IP addresses.
- **DescribeInstances** - Demonstrates how to get information about all the Amazon EC2 Instances associated with an AWS account.
- **DescribeInstanceTags** - Demonstrates how to describe the specified tags for your Amazon EC2 resource.
- **DescribeKeyPairs** - Demonstrates how to get information about all instance key pairs.
- **DescribeRegionsAndZones** - Demonstrates how to get information about all the Amazon EC2 regions and zones.
- **DescribeSecurityGroups** - Demonstrates how to get information about all the Amazon EC2 security groups.
- **DescribeVPCs** - Demonstrates how to get information about all the Amazon EC2 VPCs.
- **FindRunningInstances** - Demonstrates how to find running Amazon EC2 instances by using a filter.
- **TerminateInstance** - Demonstrates how to terminate an Amazon EC2 instance.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
