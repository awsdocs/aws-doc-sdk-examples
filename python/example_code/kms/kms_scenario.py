# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import logging
import sys
from key_management import KeyManager
from key_encryption import KeyEncrypt
from alias_management import AliasManager
from grant_management import GrantManager

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append("../..")
import demo_tools.question as q

DASHES = "-" * 80


# snippet-start:[python.example_code.redshift.redshift_scenario.RedshiftScenario]
class KMSScenario:
    """Runs an interactive scenario that shows how to get started with KMS."""

    def __init__(
        self,
        key_manager: KeyManager,
        key_encryption: KeyEncrypt,
        alias_manager: AliasManager,
        grant_manager: GrantManager,
    ):
        self.key_manager = key_manager
        self.key_encryption = key_encryption
        self.alias_manager = alias_manager
        self.grant_manager = grant_manager
        self.key_id = ""
        self.alias_name = ""

    def kms_scenario(self):
        key_description = "Created by the AWS KMS API"

        print(DASHES)
        print(
            """
Welcome to the AWS Key Management SDK Basics scenario.

This program demonstrates how to interact with AWS Key Management using the AWS SDK for Java (v2).
The AWS Key Management Service (KMS) is a secure and highly available service that allows you to create
and manage AWS KMS keys and control their use across a wide range of AWS services and applications.
KMS provides a centralized and unified approach to managing encryption keys, making it easier to meet your
data protection and regulatory compliance requirements.

This Basics scenario creates two key types:

- A symmetric encryption key is used to encrypt and decrypt data.
- An asymmetric key used to digitally sign data.

Let's get started...
        """
        )
        q.ask("Press Enter to continue...")

        print(DASHES)
        print(f"1. Create a symmetric KMS key\n")
        print(
            f"First, the program will creates a symmetric KMS key that you can used to encrypt and decrypt data."
        )
        q.ask("Press Enter to continue...")
        self.key_id = self.key_manager.create_key(key_description)
        print(
            f"Key created with ID: {self.key_id} and description: '{key_description}'"
        )
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(
            """
2. Enable a KMS key

By default, when the SDK creates an AWS key, it is enabled. The next bit of code checks to
determine if the key is enabled.
        """
        )
        q.ask("Press Enter to continue...")
        is_enabled = self.is_key_enabled(self.key_id)
        print(f"Is the key enabled? {is_enabled}")
        if not is_enabled:
            self.key_manager.enable_key(self.key_id)
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(f"3. Encrypt data using the symmetric KMS key")
        plain_text = "Hello, AWS KMS!"
        print(
            """
One of the main uses of symmetric keys is to encrypt and decrypt data.
Next, the code encrypts the string {} with the SYMMETRIC_DEFAULT encryption algorithm.
        """,
            plain_text,
        )
        q.ask("Press Enter to continue...")
        encrypted_text = self.key_encryption.encrypt(self.key_id, key_description)
        print(DASHES)
        print(f"4. Create an alias")
        print(
            """
Now, the program will create an alias for the KMS key. An alias is a friendly name that you
can associate with a KMS key. The alias name should be prefixed with 'alias/'.
        """
        )
        alias_name = q.ask("Enter an alias name: ", q.non_empty)
        self.alias_manager.create_alias(self.key_id, alias_name)
        self.alias_name = alias_name
        print(DASHES)
        print(f"5. List all of your aliases")
        q.ask("Press Enter to continue...")
        self.alias_manager.list_aliases(10)
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(f"6. Enable automatic rotation of the KMS key")
        print(
            """

By default, when the SDK enables automatic rotation of a KMS key,
KMS rotates the key material of the KMS key one year (approximately 365 days) from the enable date and every year
thereafter.
        """
        )
        q.ask("Press Enter to continue...")
        self.key_manager.enable_key_rotation(self.key_id)
        print(DASHES)
        print(
            """
7. Create a grant

A grant is a policy instrument that allows Amazon Web Services principals to use KMS keys.
It also can allow them to view a KMS key (DescribeKey) and create and manage grants.
When authorizing access to a KMS key, grants are considered along with key policies and IAM policies.
        """
        )
        print(
            """
To create a grant you must specify a principal. To specify the grantee principal, use the Amazon Resource Name (ARN) 
of an AWS principal. Valid principals include AWS accounts, IAM users, IAM roles, federated users, 
and assumed role users. 
        """
        )
        principal = q.ask(
            "Enter a name for a principal, or press enter to skip creating a grant... "
        )
        grant = None
        if principal != "":
            grant = self.grant_manager.create_grant(
                self.key_id,
                principal,
                [
                    "Encrypt",
                    "Decrypt",
                    "DescribeKey",
                ],
            )
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(DASHES)
        print(f"8. List grants for the KMS key")
        q.ask("Press Enter to continue...")
        self.grant_manager.list_grants(self.key_id)
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(f"9. Revoke the grant")
        print(
            """
The revocation of a grant immediately removes the permissions and access that the grant had provided.
This means that any principal (user, role, or service) that was granted access to perform specific
KMS operations on a KMS key will no longer be able to perform those operations.
        """
        )
        q.ask("Press Enter to continue...")
        if grant is not None:
            self.grant_manager.revoke_grant(self.key_id, grant["GrantId"])
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(f"10. Decrypt the data\n")
        print(
            """
Lets decrypt the data that was encrypted in an early step.
The code uses the same key to decrypt the string that we encrypted earlier in the program.
        """
        )
        q.ask("Press Enter to continue...")
        decrypted_text = self.key_encryption.decrypt(self.key_id, encrypted_text)
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(f"11. Replace a key policy\n")
        print(
            """
A key policy is a resource policy for a KMS key. Key policies are the primary way to control
access to KMS keys. Every KMS key must have exactly one key policy. The statements in the key policy
determine who has permission to use the KMS key and how they can use it.
You can also use IAM policies and grants to control access to the KMS key, but every KMS key
must have a key policy.

By default, when you create a key by using the SDK, a policy is created that
gives the AWS account that owns the KMS key full access to the KMS key.

Let's try to replace the automatically created policy with the following policy.
{
"Version": "2012-10-17",
"Statement": [{
"Effect": "Allow",
"Principal": {"AWS": "arn:aws:iam::0000000000:root"},
"Action": "kms:*",
"Resource": "*"
}]
}
        """
        )
        q.ask("Press Enter to continue...")
        # print(f"Key policy replacement succeeded.")
        # logging.error("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(),
        #               kmsEx.awsErrorDetails().errorCode());
        # logging.error("An unexpected error occurred: " + rt.getMessage());
        # q.ask("Press Enter to continue...")
        # print(DASHES)
        # print(f"12. Get the key policy\n")
        # print(f"The next bit of code that runs gets the key policy to make sure it exists.")
        # q.ask("Press Enter to continue...")
        # logging.error("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(),
        #               kmsEx.awsErrorDetails().errorCode());
        # logging.error("An unexpected error occurred: " + rt.getMessage());
        # q.ask("Press Enter to continue...")
        # print(DASHES)
        # print(f"13. Create an asymmetric KMS key and sign your data\n")
        # print("""
        # Signing your data with an AWS key can provide several benefits that make it an attractive option
        # for your data signing needs. By using an AWS KMS key, you can leverage the
        # security controls and compliance features provided by AWS,
        # which can help you meet various regulatory requirements and enhance the overall security posture
        # of your organization.
        # """);
        # q.ask("Press Enter to continue...")
        # print(f"Sign and verify data operation succeeded.")
        # logging.error("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(),
        #               kmsEx.awsErrorDetails().errorCode());
        # logging.error("An unexpected error occurred: " + rt.getMessage());
        # q.ask("Press Enter to continue...")
        # print(DASHES)
        # print(f"14. Tag your symmetric KMS Key\n")
        # print("""
        # By using tags, you can improve the overall management, security, and governance of your
        # KMS keys, making it easier to organize, track, and control access to your encrypted data within
        # your AWS environment
        # """);
        # q.ask("Press Enter to continue...")
        # logging.error("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(),
        #               kmsEx.awsErrorDetails().errorCode());
        # logging.error("An unexpected error occurred: " + rt.getMessage());
        # q.ask("Press Enter to continue...")
        # print(DASHES)
        # print(f"15. Schedule the deletion of the KMS key\n")
        # print("""
        # By default, KMS applies a waiting period of 30 days,
        # but you can specify a waiting period of 7-30 days. When this operation is successful,
        # the key state of the KMS key changes to PendingDeletion and the key can't be used in any
        # cryptographic operations. It remains in this state for the duration of the waiting period.
        #
        # Deleting a KMS key is a destructive and potentially dangerous operation. When a KMS key is deleted,
        # all data that was encrypted under the KMS key is unrecoverable.
        # """);
        # print(f"Would you like to delete the Key Management resources? (y/n)")
        # print(f"You selected to delete the AWS KMS resources.")
        # q.ask("Press Enter to continue...")
        # logging.error("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(),
        #               kmsEx.awsErrorDetails().errorCode());
        # logging.error("An unexpected error occurred: " + rt.getMessage());
        # q.ask("Press Enter to continue...")
        # logging.error("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(),
        #               kmsEx.awsErrorDetails().errorCode());
        # logging.error("An unexpected error occurred: " + rt.getMessage());
        # logging.error("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(),
        #               kmsEx.awsErrorDetails().errorCode());
        # logging.error("An unexpected error occurred: " + rt.getMessage());
        # print(f"The Key Management resources will not be deleted")
        # print(DASHES)
        # print(f"This concludes the AWS Key Management SDK scenario")
        # print(DASHES)

    def is_key_enabled(self, key_id: str) -> bool:
        """
        Check if the key is enabled or not.

        :param key_id: The key to check.
        :return: True if the key is enabled, otherwise False.
        """
        response = self.key_manager.describe_key(key_id)
        return response["Enabled"] == "True"

    def clean_up(self):
        if self.alias_name != "":
            print(f"Deleting the alias {self.alias_name}.")
            self.alias_manager.delete_alias(self.alias_name)

        if self.key_id != "":
            print(
                """
Warning:
Deleting a KMS key is a destructive and potentially dangerous operation. When a KMS key is deleted,
all data that was encrypted under the KMS key is unrecoverable.
                """
            )
            if q.ask(
                f"Do you want to delete the key with ID {self.key_id} (y/n)?",
                q.is_yesno,
            ):
                window = 7
                print(
                    f"The key will be deleted with a window of {window} days. You can cancel the deletion before"
                )
                print("the window expires.")
                self.key_manager.delete_key(self.key_id, window)


if __name__ == "__main__":
    kms_scenario = None
    try:
        kms_client = boto3.client("kms")
        a_key_manager = KeyManager(kms_client)
        a_key_encrypt = KeyEncrypt(kms_client)
        an_alias_manager = AliasManager(kms_client)
        a_grant_manager = GrantManager(kms_client)
        kms_scenario = KMSScenario(
            a_key_manager, a_key_encrypt, an_alias_manager, a_grant_manager
        )
        kms_scenario.kms_scenario()
    except Exception:
        logging.exception("Something went wrong with the demo!")
    finally:
        if kms_scenario is not None:
            kms_scenario.clean_up()
