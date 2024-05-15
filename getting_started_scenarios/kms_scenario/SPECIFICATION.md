# AWS Key Management Service Scenario Specification

## Overview
This SDK getting started scenario demonstrates how to interact with AWS Key Management Service (AWS KMS) using the AWS SDK. It demonstrates various tasks such as creating AWS KMS Keys, encrypting data, digitally signing data, creating aliases, tagging keys, and so on.  Finally this scenario demonstrates how to clean up resources. Its purpose is to demonstrate how to get up and running with AWS KMS and the AWS SDK.

## Resources and User Input
The only required resource for this SDK getting started scenario is the grantee principal. The `GranteePrincipal` is the AWS Identity and Access Management (IAM) entity (user, role, or service) that is granted permission to perform specific actions. 

## Hello AWS KSM
This program is intended for users not familiar with the AWS KSM SDK to easily get up an running. The logic is to show use of `KmsClient.listKeysPaginator()`.

### Program execution
The following shows the output of the program in the console. 

``` java 
The key ARN is: arn:aws:kms:us-west-2:814548047983:key/0279e5fa-2a8f-42af-ac0a-9f7f83cfc871. The key Id is: 0279e5fa-2a8f-42af-ac0a-9f7f83cfc871
 The key ARN is: arn:aws:kms:us-west-2:814548047983:key/0448ed21-078e-4712-9e69-5aced6282fcf. The key Id is: 0448ed21-078e-4712-9e69-5aced6282fcf 
```

## Scenario Program Flow
The AWS Key Management SDK getting started scenario executes the following steps:
1. Creates an AWS KMS client.
2. Prompts the user to create an AWS KMS Symmetric key for encrypting and decrypting data.
3. Describes the newly created key.
4. Enables the AWS KMS key.
5. Encrypts the plaintext data using the KMS key.
6. Allows the user to create a custom alias for the KMS key.
7. Enables automatic rotation of the KMS key.
8. Grants permissions to an IAM principal to use the KMS key.
9. Lists the grants for the KMS key.
10. Revokes the previously created grant.
11. Decrypts the encrypted data using the KMS key.
12. Creates a key policy for the KMS key.
13. Retrieves the key policy.
14. Creates an AWS KMS Asymmetric key for signing and verifying data.
15. Signs and verifies data using the newly created key.
16. Tags the KMS key.
17. Prompts the user to delete the KMS resources.
18. If confirmed, deletes the custom alias, disables, and deletes the KMS key.
19. Concludes the AWS Key Management SDK getting started scenario.

### Program execution
The following shows the output of the program in the console. 

``` java
--------------------------------------------------------------------------------
Welcome to the AWS Key Management SDK Getting Started scenario.
This program demonstrates how to interact with AWS Key Management using the AWS SDK for Java (v2).
The AWS Key Management Service (KMS) is a secure and highly available service that allows you to create
and manage AWS KMS keys and control their use across a wide range of AWS services and applications.
KMS provides a centralized and unified approach to managing encryption keys, making it easier to meet your
data protection and regulatory compliance requirements.

This Getting Started scenario creates two key types. A symmetric encryption key is used to encrypt and decrypt data,
and an asymmetric key used to digitally sign data.
Let's get started...

Please enter 'c' to continue:
c
Continuing with the program...
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
First, we will create an AWS KMS key that is used to encrypt and decrypt data.
Please enter 'c' to continue:
c
Continuing with the program...
Created a customer key with ARN arn:aws:kms:us-west-2:814548047983:key/fe57938c-21ec-4ea7-a68f-5cbe2f617c80
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
By default, when you create an AWS key, it is enabled. Check to make sure if the key is enabled. If not,
then enabled it.

Please enter 'c' to continue:
c
Continuing with the program...
The key is enabled.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Hello, AWS KMS!
Lets encrypt this data.
Please enter 'c' to continue:
c
Continuing with the program...
The encryption algorithm is SYMMETRIC_DEFAULT
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Please enter an alias name for the key that is prefixed by alias/ (default is alias/dev-encryption-key.

The alias name is: alias/Scott
The alias name is: alias/alias/myAlias
The alias name is: alias/alias/s3-kms-key
The alias name is: alias/aws/cloud9
The alias name is: alias/aws/connect
The alias name is: alias/aws/dynamodb
The alias name is: alias/aws/ebs
The alias name is: alias/aws/elasticfilesystem
The alias name is: alias/aws/es
The alias name is: alias/aws/glue
The alias name is: alias/aws/kendra
The alias name is: alias/aws/kinesisvideo
The alias name is: alias/aws/lex
The alias name is: alias/aws/rds
The alias name is: alias/aws/redshift
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Enable automatic rotation of the KMS key.

By default, when you enable automatic rotation of a KMS key,
KMS rotates the key material of the KMS key one year (approximately 365 days) from the enable date and every year
thereafter.

Please enter 'c' to continue:
c
Continuing with the program...
You have enabled key rotation for key fe57938c-21ec-4ea7-a68f-5cbe2f617c80
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
A grant is a policy instrument that allows Amazon Web Services principals to use KMS keys.

It also can allow them to view a KMS key (DescribeKey) and create and manage grants.
When authorizing access to a KMS key, grants are considered along with key policies and IAM policies.

Please enter 'c' to continue:
c
Continuing with the program...
The grant id is ed5ddb49cae3e2d99be93fd8a1eb39b2607902b6f880c3705ebc291622a10855
List grants for the KMS key.
Please enter 'c' to continue:
c
Continuing with the program...
The grant Id is : ed5ddb49cae3e2d99be93fd8a1eb39b2607902b6f880c3705ebc291622a10855
Revoke the grant.
Please enter 'c' to continue:
c
Continuing with the program...
ed5ddb49cae3e2d99be93fd8a1eb39b2607902b6f880c3705ebc291622a10855 was successfully revoked!
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Lets decrypt the data that was encrypted in an early step.
Please enter 'c' to continue:
c
Continuing with the program...
Decrypted text is: Hello, AWS KMS!
--------------------------------------------------------------------------------
A key policy is a resource policy for an KMS key. Key policies are the primary way to control
access to KMS keys. Every KMS key must have exactly one key policy. The statements in the key policy
determine who has permission to use the KMS key and how they can use it.
You can also use IAM policies and grants to control access to the KMS key, but every KMS key
must have a key policy..

Set a Key policy.
Please enter 'c' to continue:
c
Continuing with the program...
Policy Name: default
Only one policy per key is supported. Unable to create the policy.
Lets get the key policy to make sure it exists. 
Please enter 'c' to continue:
c
Continuing with the program...
The policy cannot be found. Error message: No such policy exists (Service: Kms, Status Code: 400, Request ID: 0a25d66d-e544-4d7b-a9ec-afc75e8f34c9)
--------------------------------------------------------------------------------
 Signing your data with an AWS key can provide several benefits that make it an attractive option
 for your data signing needs. By using an AWS KMS key, you can leverage the
 security controls and compliance features provided by AWS,
 which can help you meet various regulatory requirements and enhance the overall security posture
 of your organization.

Please enter 'c' to continue:
c
Continuing with the program...
Created KMS key with ID: 85772333-f623-428e-9afc-19f659e29997
Signature verification result: true
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Tag your KMS Key.

By using tags, you can improve the overall management, security, and governance of your
KMS keys, making it easier to organize, track, and control access to your encrypted data within
your AWS environment

Please enter 'c' to continue:
c
Continuing with the program...
Tagged KMS key with key-value pair
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Schedules the deletion of a KMS key. By default, KMS applies a waiting period of 30 days,
but you can specify a waiting period of 7-30 days. When this operation is successful,
the key state of the KMS key changes to PendingDeletion and the key can't be used in any
cryptographic operations. It remains in this state for the duration of the waiting period.

Deleting a KMS key is a destructive and potentially dangerous operation. When a KMS key is deleted,
all data that was encrypted under the KMS key is unrecoverable. 

Would you like to delete the Key Management resources? (y/n)
y
You selected to delete the AWS KMS resources.
Please enter 'c' to continue:
c
Continuing with the program...
The key will be deleted in 7 days.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
This concludes the AWS Key Management SDK Getting Started scenario
--------------------------------------------------------------------------------


```

## SOS Tags

The following table describes the metadata used in this SDK Getting Started Scenario.


| action                       | metadata file                | metadata key                            |
|------------------------------|------------------------------|---------------------------------------- |
| `createKey  `                | kms_metadata.yaml            | kms_CreateKey                           |
| `decrypt`                    | kms_metadata.yaml            | kms_Decrypt                             |
| `encrypt`                    | kms_metadata.yaml            | kms_Encrypt                             |
| `listAliases`                | kms_metadata.yaml            | kms_ListAliases                         |
| `createAlias`                | kms_metadata.yaml            | kms_CreateAlias                         |
| `createGrant`                | kms_metadata.yaml            | kms_CreateGrant                         |
| `describeKey`                | kms_metadata.yaml            | kms_DescribeKey                         |
| `disableKey `                | kms_metadata.yaml            | kms_DisableKey                          |
| `enableKey `                 | kms_metadata.yaml            | kms_EnableKey                           |
| `listKeysPaginator `         | kms_metadata.yaml            | kms_Hello                               |
| `listGrants `                | kms_metadata.yaml            | kms_ListGrants                          |
| `getKeyPolicy `              | kms_metadata.yaml            | kms_GetKeyPolicy                        |
| `revokeGrant`                | kms_metadata.yaml            | kms_RevokeGrant                         |
| `listGrants `                | kms_metadata.yaml            | kms_ListGrants                          |
| `scheduleKeyDeletion `       | kms_metadata.yaml            | kms_ScheduleKeyDeletion                 |
| `tagResource `               | kms_metadata.yaml            | kms_Tags                                |
| `sign`                       | kms_metadata.yaml            | kms_Sign                                |
| `scenario                    | kms_metadata.yaml            | kms_Scenario                            |

