# AWS Key Management Service Getting Started Scenario

## Overview

 This AWS Key Management Service (AWS KMS) getting started scenario demonstrates how to interact with the AWS KMS service using an AWS SDK.  The scenario covers various operations such as creating and managing AWS KMS keys, encrpting data, digitally signing data, tagging keys, and so on. 

Here are the 5-6 high-level steps of the most important KMS operations used in this scenario:

1. **Create a Symmetric Encryption Key**: The program creates a symmetric encryption key using the AWS KMS service.

2. **Enable and Verify the Key's State**: It checks if the created key is enabled, and if not, enables it.

3. **Encrypt and Decrypt Data**: The program encrypts a plaintext message using the created key, and then decrypts the ciphertext to retrieve the original message.

4. **Manage Key Grants and Policies**: The program demonstrates how to create, list, and revoke grants for the KMS key, as well as how to create and retrieve a key policy.

5. **Enable Key Rotation**: The program enables automatic rotation of the KMS key, which allows the key material to be rotated periodically.

6. **Tag and Delete the Key**: The program adds a tag to the KMS key and then provides an option to schedule the deletion of the key.

These steps cover the core KMS operations, including creating and managing keys, encrypting and decrypting data, controlling access through grants and policies, and managing the key's lifecycle. 

Note: These steps are not the complete program, but summarizes the 5-6 high-level steps. 

### Resources

The getting started scenario requires an IAM role for a grantee principal. Grants are often used for temporary permissions because you can create one, use its permissions, and delete it without changing your key policies or IAM policies. No additional resources are required.

## Implementations

This example will be implemented in the following languages:

- Java
- Python
- JavaScript

## Additional reading

- [AWS Key Management Service](https://docs.aws.amazon.com/kms/latest/developerguide/overview.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
