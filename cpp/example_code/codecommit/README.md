# AWS CodeCommit C++ SDK code examples

## Purpose
The code examples in this directory demonstrate how to work with the AWS CodeCommit 
using the AWS SDK for C++.

AWS CodeCommit is a secure, highly scalable, managed source control service that hosts private Git repositories.

## Code examples

### API examples
- [Create a pull request](./create_pull_request.cpp) (CreatePullRequest)
- [Create a repository](./create_repository.cpp) (CreateRepository)
- [Delete a branch](./delete_branch.cpp) (DeleteBranch)
- [Delete a repository](./delete_repository.cpp) (DeleteRepository)
- [List your builds](./list_branches.cpp) (ListBranches)
- [List your pull requests](./list_pull_requests.cpp) (ListPullRequests)
- [Update your pull requests](./update_pull_request.cpp) 
- [Update a repository](./update_repository.cpp) 


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

To run these code examples, your AWS user must have permissions to perform these actions with AWS CodeCommit.  
The AWS managed policy named "AWSCodeCommitFullAccess" may be used to bulk-grant the necessary permissions.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [AWS CodeCommit Documentation](https://docs.aws.amazon.com/codecommit/)
