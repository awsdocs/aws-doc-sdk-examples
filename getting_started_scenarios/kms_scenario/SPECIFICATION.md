# AWS Key Management Service Scenario Specification

## Overview
This SDK getting started scenario demonstrates how to interact with AWS Key Management Service (AWS KMS) using the AWS SDK. It demonstrates various tasks such as creating AWS KMS Keys, encrypting data, digitally signing data, creating aliases, tagging keys, and so on.  Finally this scenario demonstrates how to clean up resources. Its purpose is to demonstrate how to get up and running with AWS KMS and the AWS SDK.

## Resources and User Input
The only required resource for this SDK getting started scenario is the grantee principal. The `GranteePrincipal` is the AWS Identity and Access Management (IAM) entity (user, role, or service) that is granted permission to perform specific actions. 

## Hello AWS KSM
This program is intended for users not familiar with the AWS KSM SDK to easily get up and running. The logic is to show use of `KmsClient.listKeysPaginator()`.

### Program execution
The following shows the output of the program in the console. 

``` java 
The key ARN is: arn:aws:kms:us-west-2:123456789012:key/12345678-abcd-abcd-abcd-123456789012. The key Id is: 2345678-abcd-abcd-abcd-1234 

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

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
1. Create a symmetric KMS key
First, we will create a symmetric KMS key that is used to encrypt and decrypt data by invoking createKey().

Press <ENTER> to continue:
Continuing with the program...

Created a customer key with ARN arn:aws:kms:us-west-2:123456789012:key/11223344-aaaa-bbbb-cccc-111222233344

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
2. Enable a KMS key

By default when you create an AWS key, it is enabled. The code checks to
determine if the key is enabled. If it is not enabled, the code enables it.

Press <ENTER> to continue:
Continuing with the program...

The key is enabled.

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
3. Encrypt data using the symmetric KMS key
One of the main uses of symmetric keys is to encrypt and decrypt data.
Next, you encrypt the string 'Hello, AWS KMS!' with the SYMMETRIC_DEFAULT encryption algorithm.


Press <ENTER> to continue:
Continuing with the program...

The encryption algorithm is SYMMETRIC_DEFAULT

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
4. Create an alias
Enter an alias name for the key. The name should be prefixed with 'alias/'.
For example, 'alias/myFirstKey'.


alias/dev-encryption-key was successfully created.

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
5. List all of your aliases.

Press <ENTER> to continue:
Continuing with the program...

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

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
6. Enable automatic rotation of the KMS key
By default, when you enable automatic rotation of a KMS key,
KMS rotates the key material of the KMS key one year (approximately 365 days) from the enable date and every year
thereafter.

Press <ENTER> to continue:
Continuing with the program...

You have enabled key rotation for key 11223344-aaaa-bbbb-cccc-111222233344

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
7. Create a grant.

A grant is a policy instrument that allows Amazon Web Services principals to use KMS keys.
It also can allow them to view a KMS key (DescribeKey) and create and manage grants.
When authorizing access to a KMS key, grants are considered along with key policies and IAM policies.

Press <ENTER> to continue:
Continuing with the program...

The grant id is 1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
8. List grants for the KMS key.

Press <ENTER> to continue:
Continuing with the program...

The grant Id is: 8f1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
9. Revoke the grant.

Press <ENTER> to continue:
Continuing with the program...

1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef was successfully revoked!

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
10. Decrypt the data.
Lets decrypt the data that was encrypted in an early step.
We'll use the same key to decrypt the string that we encrypted earlier in the program.


Press <ENTER> to continue:
Continuing with the program...

Decrypted text is: Hello, AWS KMS!

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
10. Create a key policy.
A key policy is a resource policy for an KMS key. Key policies are the primary way to control
access to KMS keys. Every KMS key must have exactly one key policy. The statements in the key policy
determine who has permission to use the KMS key and how they can use it.
You can also use IAM policies and grants to control access to the KMS key, but every KMS key
must have a key policy.

We will set a key policy.
    "Version": "2012-10-17",
    "Statement": [{
    "Effect": "Allow",
    "Principal": {"AWS": "arn:aws:iam::0000000000:root"},
    "Action": "kms:*",
    "Resource": "*"
    }]


Press <ENTER> to continue:
Continuing with the program...

Policy Name: default
The Key already has a policy.

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
11. Get the key policy.
Lets get the key policy to make sure it exists. 

Press <ENTER> to continue:
Continuing with the program...

The response is {
  "Version" : "2012-10-17",
  "Id" : "key-default-1",
  "Statement" : [ {
    "Sid" : "Enable IAM User Permissions",
    "Effect" : "Allow",
    "Principal" : {
      "AWS" : "arn:aws:iam::123456789012:root"
    },
    "Action" : "kms:*",
    "Resource" : "*"
  } ]
}

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
12. Sign your data with the asymmetric KMS key.
 Signing your data with an AWS key can provide several benefits that make it an attractive option
 for your data signing needs. By using an AWS KMS key, you can leverage the
 security controls and compliance features provided by AWS,
 which can help you meet various regulatory requirements and enhance the overall security posture
 of your organization.


Press <ENTER> to continue:
Continuing with the program...

Created KMS key with ID: 12345678-abcd-abcd-abcd-123456789012
Signature verification result: true

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
13. Tag your symmetric KMS Key.
By using tags, you can improve the overall management, security, and governance of your
KMS keys, making it easier to organize, track, and control access to your encrypted data within
your AWS environment


Press <ENTER> to continue:
Continuing with the program...

Tagged KMS key with key-value pair

Press <ENTER> to continue:
Continuing with the program...

--------------------------------------------------------------------------------
14. Schedule the deletion of the KMS key.
By default, KMS applies a waiting period of 30 days,
but you can specify a waiting period of 7-30 days. When this operation is successful,
the key state of the KMS key changes to PendingDeletion and the key can't be used in any
cryptographic operations. It remains in this state for the duration of the waiting period.

Deleting a KMS key is a destructive and potentially dangerous operation. When a KMS key is deleted,
all data that was encrypted under the KMS key is unrecoverable. 

Would you like to delete the Key Management resources? (y/n)
y
You selected to delete the AWS KMS resources.

Press <ENTER> to continue:
Continuing with the program...

The key will be deleted in 7 days.
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
| `listAliasesPaginator`       | kms_metadata.yaml            | kms_ListAliases                         |
| `createAlias`                | kms_metadata.yaml            | kms_CreateAlias                         |
| `createGrant`                | kms_metadata.yaml            | kms_CreateGrant                         |
| `describeKey`                | kms_metadata.yaml            | kms_DescribeKey                         |
| `disableKey `                | kms_metadata.yaml            | kms_DisableKey                          |
| `enableKey `                 | kms_metadata.yaml            | kms_EnableKey                           |
| `listKeysPaginator `         | kms_metadata.yaml            | kms_Hello                               |
| `listGrantsPaginator`        | kms_metadata.yaml            | kms_ListGrants                          |
| `getKeyPolicy `              | kms_metadata.yaml            | kms_GetKeyPolicy                        |
| `revokeGrant`                | kms_metadata.yaml            | kms_RevokeGrant                         |
| `listGrants `                | kms_metadata.yaml            | kms_ListGrants                          |
| `scheduleKeyDeletion `       | kms_metadata.yaml            | kms_ScheduleKeyDeletion                 |
| `tagResource `               | kms_metadata.yaml            | kms_Tags                                |
| `sign`                       | kms_metadata.yaml            | kms_Sign                                |
| `scenario                    | kms_metadata.yaml            | kms_Scenario                            |

