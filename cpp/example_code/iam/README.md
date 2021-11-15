# Amazon IAM C++ SDK code examples

## Purpose
The code examples in this directory demonstrate how to work with the AWS Identity and Access Management (IAM) 
using the AWS SDK for C++.

IAM is a web service for securely controlling access to AWS services. With IAM, you can
centrally manage users, security credentials such as access keys, and permissions that control 
which AWS resources users and applications can access.

## Code examples

### API examples
- [Get the last use access key](./access_key_last_used.cpp) (GetAccessKeyLastUsed)
- [Assume a role](./assume_role.cpp)(AssumeRole)
- [Attach a role policy](./attach_role_policy.cpp)
- [Create an access key](./create_access_key.cpp)(CreateAccessKey)
- [Create an account alias](./create_account_alias.cpp)(CreateAccountAlias)
- [Create a policy](./create_policy.cpp) (CreatePolicy)
- [Create a role](./create_role.cpp)(CreateRole)
- [Create a user ](./create_user.cpp)(CreateUser)
- [Delete an access key](./delete_access_key.cpp)(DeleteAccessKey)
- [Delete an account alias](./delete_account_alias.cpp)(DeleteAccountAlias)
- [Delete a policy](./delete_policy.cpp) (DeletePolicy)
- [Delete a server certificated](./delete_server_cert.cpp)(DeleteServerCertificate)
- [Delete a user](./delete_user.cpp)(DeleteUser)
- [Detach a role policy](./detach_role_policy.cpp)(DeleteRolePolicy)
- [Get a policy](get_policy.cpp)(GetPolicy)
- [Get a server certificate](./get_server_cert.cpp) (GetServerCertificate)
- [List your access keys](list_access_keys.cpp)(ListAccessKeys)
- [List your account aliases](list_account_aliases.cpp)(ListAccountAliases)
- [List your policies](list_policies.cpp)(ListPolicies)
- [List server certificates](list_server_certs.cpp)(ListServerCertificates)
- [List your users](list_users.cpp)(ListUsers)
- [Put a role policy](put_role_policy.cpp)(PutRolePolicy)
- [Update an acess key](update_access_key.cpp)(UpdateAccessKey)
- [Update a server certificate](update_server_cert.cpp)(UpdateServerCertificate)
- [Update a user](update_user.cpp)(UpdateUser)


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

To run these code examples, your AWS user must have permissions to perform these actions with IAM.  
The AWS managed policy named "IAMFullAccess" may be used to bulk-grant the necessary permissions.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [AWS Identity and Access Management Documentation](https://docs.aws.amazon.com/iam/)
