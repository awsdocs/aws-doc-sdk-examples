# AWS Storage Gateway C++ SDK code examples

## Purpose
The code examples in this directory demonstrate how to work with the AWS Storage Gateway 
using the AWS SDK for C++.

AWS Storage Gateway is a service that connects an on-premises software appliance with cloud-based storage 
to provide seamless and secure integration between your on-premises IT environment and the AWS storage 
infrastructure in the cloud.

## Code examples

### API examples
- [Create a Network File System (NFS) file share on an AWS Storage Gateway resource](./create_nfs_file_share.cpp) (CreateNFSFileShare)
- [Delete a volume ARN from an AWS Storage Gateway resource](./delete_volume.cpp) (DeleteVolume)
- [Retrieve information pertaining to an AWS Storage Gateway resource](./describe_gateway_information.cpp) (DescribeGatewayInformation)
- [Retrieve information about the Server Message Block (SMB) settings for a resource](./describe_smb_settings.cpp) (DescribeSMBSettings)
- [Retrieve a list of the file shares for an AWS Storage Gateway resource](./list_file_shares.cpp) (ListFileShares)
- [Start an AWS Storage Gateway resource that was previously shut down](./start_gateway.cpp) (StorageGateway)

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

To run these code examples, your AWS user must have permissions to perform these actions with AWS Storage Gateway.  
The AWS managed policy named "AmazonStorageGatewayFullAccess" may be used to bulk-grant the necessary permissions.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [AWS Storage Gateway Documentation](https://docs.aws.amazon.com/storagegateway/)
