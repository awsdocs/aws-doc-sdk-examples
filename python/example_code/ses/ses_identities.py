# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Simple Email Service
(Amazon SES) to verify and manage email and domain identities.
"""

import logging
import boto3
from botocore.exceptions import ClientError, WaiterError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ses.SesIdentity]
class SesIdentity:
    """Encapsulates Amazon SES identity functions."""

    def __init__(self, ses_client):
        """
        :param ses_client: A Boto3 Amazon SES client.
        """
        self.ses_client = ses_client

    # snippet-end:[python.example_code.ses.SesIdentity]

    # snippet-start:[python.example_code.ses.VerifyDomainIdentity]
    def verify_domain_identity(self, domain_name):
        """
        Starts verification of a domain identity. To complete verification, you must
        create a TXT record with a specific format through your DNS provider.

        For more information, see *Verifying a domain with Amazon SES* in the
        Amazon SES documentation:
            https://docs.aws.amazon.com/ses/latest/DeveloperGuide/verify-domain-procedure.html

        :param domain_name: The name of the domain to verify.
        :return: The token to include in the TXT record with your DNS provider.
        """
        try:
            response = self.ses_client.verify_domain_identity(Domain=domain_name)
            token = response["VerificationToken"]
            logger.info("Got domain verification token for %s.", domain_name)
        except ClientError:
            logger.exception("Couldn't verify domain %s.", domain_name)
            raise
        else:
            return token

    # snippet-end:[python.example_code.ses.VerifyDomainIdentity]

    # snippet-start:[python.example_code.ses.VerifyEmailIdentity]
    def verify_email_identity(self, email_address):
        """
        Starts verification of an email identity. This function causes an email
        to be sent to the specified email address from Amazon SES. To complete
        verification, follow the instructions in the email.

        :param email_address: The email address to verify.
        """
        try:
            self.ses_client.verify_email_identity(EmailAddress=email_address)
            logger.info("Started verification of %s.", email_address)
        except ClientError:
            logger.exception("Couldn't start verification of %s.", email_address)
            raise

    # snippet-end:[python.example_code.ses.VerifyEmailIdentity]

    # snippet-start:[python.example_code.ses.helper.wait_until_identity_exists]
    def wait_until_identity_exists(self, identity):
        """
        Waits until an identity exists. The waiter polls Amazon SES until the
        identity has been successfully verified or until it exceeds its maximum time.

        :param identity: The identity to wait for.
        """
        try:
            waiter = self.ses_client.get_waiter("identity_exists")
            logger.info("Waiting until %s exists.", identity)
            waiter.wait(Identities=[identity])
        except WaiterError:
            logger.error("Waiting for identity %s failed or timed out.", identity)
            raise

    # snippet-end:[python.example_code.ses.helper.wait_until_identity_exists]

    # snippet-start:[python.example_code.ses.GetIdentityVerificationAttributes]
    def get_identity_status(self, identity):
        """
        Gets the status of an identity. This can be used to discover whether
        an identity has been successfully verified.

        :param identity: The identity to query.
        :return: The status of the identity.
        """
        try:
            response = self.ses_client.get_identity_verification_attributes(
                Identities=[identity]
            )
            status = response["VerificationAttributes"].get(
                identity, {"VerificationStatus": "NotFound"}
            )["VerificationStatus"]
            logger.info("Got status of %s for %s.", status, identity)
        except ClientError:
            logger.exception("Couldn't get status for %s.", identity)
            raise
        else:
            return status

    # snippet-end:[python.example_code.ses.GetIdentityVerificationAttributes]

    # snippet-start:[python.example_code.ses.DeleteIdentity]
    def delete_identity(self, identity):
        """
        Deletes an identity.

        :param identity: The identity to remove.
        """
        try:
            self.ses_client.delete_identity(Identity=identity)
            logger.info("Deleted identity %s.", identity)
        except ClientError:
            logger.exception("Couldn't delete identity %s.", identity)
            raise

    # snippet-end:[python.example_code.ses.DeleteIdentity]

    # snippet-start:[python.example_code.ses.ListIdentities]
    def list_identities(self, identity_type, max_items):
        """
        Gets the identities of the specified type for the current account.

        :param identity_type: The type of identity to retrieve, such as EmailAddress.
        :param max_items: The maximum number of identities to retrieve.
        :return: The list of retrieved identities.
        """
        try:
            response = self.ses_client.list_identities(
                IdentityType=identity_type, MaxItems=max_items
            )
            identities = response["Identities"]
            logger.info("Got %s identities for the current account.", len(identities))
        except ClientError:
            logger.exception("Couldn't list identities for the current account.")
            raise
        else:
            return identities


# snippet-end:[python.example_code.ses.ListIdentities]


# snippet-start:[python.example_code.ses.Scenario_EmailIdentity]
def usage_demo():
    print("-" * 88)
    print("Welcome to the Amazon Simple Email Service (Amazon SES) identities demo!")
    print("-" * 88)

    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    ses_identity = SesIdentity(boto3.client("ses"))
    email = input(
        "Enter an email address to verify with Amazon SES. This address will "
        "receive a verification email: "
    )
    ses_identity.verify_email_identity(email)

    print(f"Follow the steps in the email to {email} to complete verification.")
    print("Waiting for verification...")
    try:
        ses_identity.wait_until_identity_exists(email)
        print(f"Identity verified for {email}.")
    except WaiterError:
        print(
            f"Verification timeout exceeded. You must complete the "
            f"steps in the email sent to {email} to verify the address."
        )

    identities = ses_identity.list_identities("EmailAddress", 10)
    print("The identities in the account are:")
    print(*identities, sep="\n")

    status = ses_identity.get_identity_status(email)
    print(f"{email} has status: {status}.")

    answer = input(f"Do you want to remove {email} from Amazon SES (y/n)? ")
    if answer.lower() == "y":
        ses_identity.delete_identity(email)
        print(f"{email} removed from Amazon SES.")

    print("Thanks for watching!")
    print("-" * 88)


# snippet-end:[python.example_code.ses.Scenario_EmailIdentity]


if __name__ == "__main__":
    usage_demo()
