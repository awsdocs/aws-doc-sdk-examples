# Amazon S3 Batch Basics Scenario

## Introduction
This scenario demonstrates how to use the AWS SDK to interact with S3 Express One Zone operations. 

## Setting up Resources
To successfully run this basic scenario, the program requires an IAM (Identity and Access Management) role. The program creates the IAM role by using an AWS CloudFormation template. 

## Service Operations Invoked
The program performs the following tasks:

1. **Set up a VPC and VPC Endpoint**:
   - EC2 Operation: `CreateVpc`
   - EC2 Operation: `DescribeRouteTables`
   - EC2 Operation: `CreateVpcEndpoint`
   
2. **Set up the S3 Express Policies, Roles, and User to work with S3 Express buckets**:
   - CDK Operation to load a CloudFormation template

3. **Create *two* S3 Clients**:
   - S3 instantiate client, once with elevated Express permissions

4. **Create two buckets**:
   - S3 Operation`CreateBucket`

5. **Create an object and copy it over**:
   - S3 Operation`CreateSession`
   - S3 Operation`PutObject`
   - S3 Operation`CopyObject`
   
6. **Demonstrate performance difference**:
   - S3 Operation`GetObject`
   
7. **Populate the buckets to show the lexicographical difference**:
   - S3 Operation `PutObject`
   - S3 Operation `ListObjectsV2`
   
8. **Prompt the user to see if they want to clean up the resources **:
   - S3 Operation `DeleteObject`
   - S3 Operation `DeleteBucket`
   - EC2 Operation `DeleteVpcEndpoint`
   - EC2 Operation `DeleteVpc`

## Usage
1. Clone the repository or download the source code.
2. Open the code in your preferred IDE.
3. Invoke the main method in the `S3ExpressBasics` class.
