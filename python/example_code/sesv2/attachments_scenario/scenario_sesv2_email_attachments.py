# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Shows how to use the AWS SDK for Python (Boto3) with Amazon SESv2 to
    send emails with attachments. This scenario demonstrates three use cases:
    1. Send a simple email with a file attachment.
    2. Send a simple email with an inline image rendered in the HTML body.
    3. Send bulk templated emails with attachments to multiple recipients.

    The new attachment support eliminates the need for developers to construct
    raw MIME messages — SES handles the MIME assembly automatically.
"""

import json
import logging
import sys

import boto3
from botocore.exceptions import ClientError

from sesv2_wrapper import SESv2Wrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append("../../..")
import demo_tools.question as q  # noqa

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sesv2.Scenario_EmailAttachments]
class SESv2EmailAttachmentsScenario:
    """
    Demonstrates sending emails with attachments using Amazon SESv2.

    This scenario walks through:
    1. Setting up an email identity and template.
    2. Sending a simple email with a file attachment.
    3. Sending a simple email with an inline image.
    4. Sending bulk templated emails with attachments.
    5. Cleaning up created resources.
    """

    TEMPLATE_NAME = "AttachmentDemoTemplate"

    def __init__(self, sesv2_wrapper: SESv2Wrapper) -> None:
        """
        :param sesv2_wrapper: An instance of the SESv2Wrapper class.
        """
        self.sesv2_wrapper = sesv2_wrapper
        self.sender_email = ""
        self.recipient_emails: list = []
        self.identity_was_created = False

    def run_scenario(self) -> None:
        """Runs the SESv2 email attachments scenario."""
        print("-" * 88)
        print("Welcome to the Amazon SESv2 Email Attachments Scenario!")
        print("-" * 88)
        print(
            "This scenario demonstrates how to send emails with attachments\n"
            "using the new SESv2 attachment support. SES handles MIME\n"
            "construction automatically, so you don't need to build raw\n"
            "MIME messages.\n"
        )

        try:
            self._setup()
            self._step1_send_email_with_attachment()
            self._step2_send_email_with_inline_image()
            self._step3_send_bulk_email_with_attachments()
        except Exception as e:
            logger.error("Scenario failed: %s", e)
            print(f"\nThe scenario encountered an error: {e}")
        finally:
            self._cleanup()

    # ---------- Setup ----------

    def _setup(self) -> None:
        """
        Prompts for configuration, verifies the sender identity, prepares a
        sample attachment, and creates an email template.
        """
        print("\n--- Setup ---\n")

        # Prompt for sender and recipient addresses.
        print(
            "Both sender and recipient addresses must be verified if your\n"
            "account is in the SES sandbox.\n"
        )
        self.sender_email = q.ask(
            "Enter a verified sender email address: "
        )
        recipient_input = q.ask(
            "Enter one or more recipient email addresses (comma-separated): "
        )
        self.recipient_emails = [
            addr.strip() for addr in recipient_input.split(",") if addr.strip()
        ]

        # Verify the sender identity.
        print(f"\nChecking identity for {self.sender_email}...")
        try:
            identity_info = self.sesv2_wrapper.get_email_identity(
                self.sender_email
            )
            verified = identity_info.get("VerifiedForSendingStatus", False)
            if verified:
                print(f"  {self.sender_email} is verified and ready to send.")
            else:
                print(
                    f"  {self.sender_email} exists but is not yet verified."
                )
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                print(
                    f"  Identity {self.sender_email} not found. "
                    "Creating it now..."
                )
                result = self.sesv2_wrapper.create_email_identity(
                    self.sender_email
                )
                self.identity_was_created = True
                print(
                    f"  Identity created. Verification status: "
                    f"{result.get('VerifiedForSendingStatus', False)}"
                )
                print(
                    "  Check your inbox and click the verification link "
                    "before continuing."
                )
                q.ask("Press Enter when you have verified the address...")
            else:
                raise

        # Create the email template for the bulk-send step.
        print("\nCreating email template for the bulk email step...")
        try:
            self.sesv2_wrapper.create_email_template(
                template_name=self.TEMPLATE_NAME,
                subject="Bulk Email with Attachment for {{name}}",
                html_body=(
                    "<h1>Hello {{name}}</h1>"
                    "<p>Please find the attached document.</p>"
                ),
                text_body=(
                    "Hello {{name}}, Please find the attached document."
                ),
            )
            print(f"  Template '{self.TEMPLATE_NAME}' created.\n")
        except ClientError as err:
            if err.response["Error"]["Code"] == "AlreadyExistsException":
                print(
                    f"  Template '{self.TEMPLATE_NAME}' already exists. "
                    "Using it.\n"
                )
            else:
                raise

    # ---------- Step 1: Simple email with file attachment ----------

    def _step1_send_email_with_attachment(self) -> None:
        """Sends a simple email with a text file attachment."""
        print("\n--- Step 1: Send a Simple Email with a File Attachment ---\n")
        print(
            "Creating a sample text file attachment and sending it with\n"
            "the Simple email content type. SES constructs the MIME message\n"
            "automatically.\n"
        )

        # Prepare a sample text file as bytes.
        sample_content = b"This is a sample report attachment."

        attachment = {
            "RawContent": sample_content,
            "FileName": "sample-report.txt",
            "ContentType": "text/plain",
            "ContentDisposition": "ATTACHMENT",
            "ContentDescription": "Sample report text file",
            "ContentTransferEncoding": "BASE64",
        }

        print(
            "Note: When using an AWS SDK, the SDK handles base64 encoding\n"
            "automatically. Direct API callers must encode content themselves.\n"
        )

        message_id = self.sesv2_wrapper.send_email(
            from_address=self.sender_email,
            to_addresses=self.recipient_emails,
            subject="SESv2 Attachment Demo — Simple Email with Attachment",
            html_body=(
                "<h1>Attachment Demo</h1>"
                "<p>Please see the attached <b>report document</b>.</p>"
            ),
            text_body="Please see the attached report document.",
            attachments=[attachment],
        )

        print(f"  Email sent! MessageId: {message_id}")
        print(
            "  SES automatically constructed the MIME message with the "
            "attachment.\n"
        )

    # ---------- Step 2: Simple email with inline image ----------

    def _step2_send_email_with_inline_image(self) -> None:
        """Sends a simple email with an inline image that renders in HTML."""
        print("\n--- Step 2: Send a Simple Email with an Inline Image ---\n")
        print(
            "This step demonstrates INLINE disposition. The image renders\n"
            "directly in the HTML body using a 'cid:' reference instead of\n"
            "appearing as a downloadable attachment.\n"
        )

        # Create a minimal 1x1 red PNG (valid PNG file).
        sample_image = (
            b"\x89PNG\r\n\x1a\n"  # PNG signature
            b"\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01"
            b"\x08\x02\x00\x00\x00\x90wS\xde"  # 1x1 RGB
            b"\x00\x00\x00\x0cIDATx\x9cc\xf8\x0f\x00\x00\x01\x01"
            b"\x00\x05\x18\xd8N"  # compressed data
            b"\x00\x00\x00\x00IEND\xaeB`\x82"  # IEND
        )

        attachment = {
            "RawContent": sample_image,
            "FileName": "logo.png",
            "ContentType": "image/png",
            "ContentDisposition": "INLINE",
            "ContentId": "logo123",
            "ContentDescription": "Company logo",
            "ContentTransferEncoding": "BASE64",
        }

        html_body = (
            "<html><body>"
            "<h1>Inline Image Demo</h1>"
            "<p>Here is our logo:</p>"
            '<img src="cid:logo123" alt="Company Logo">'
            "</body></html>"
        )

        message_id = self.sesv2_wrapper.send_email(
            from_address=self.sender_email,
            to_addresses=self.recipient_emails,
            subject="SESv2 Attachment Demo — Inline Image",
            html_body=html_body,
            text_body=(
                "Inline Image Demo — Please view this email in an "
                "HTML-capable client to see the embedded image."
            ),
            attachments=[attachment],
        )

        print(f"  Email sent! MessageId: {message_id}")
        print(
            "  The ContentId 'logo123' is referenced in the HTML body via\n"
            "  'cid:logo123', which lets the image render inline.\n"
        )

    # ---------- Step 3: Bulk templated email with attachments ----------

    def _step3_send_bulk_email_with_attachments(self) -> None:
        """Sends bulk templated emails with attachments to multiple recipients."""
        print("\n--- Step 3: Send Bulk Templated Emails with Attachments ---\n")
        print(
            "Using SendBulkEmail to send a templated email with an attachment\n"
            "to multiple recipients in a single API call. Each recipient gets\n"
            "personalized content via template data.\n"
        )

        sample_content = b"This is a sample report attachment."

        attachment = {
            "RawContent": sample_content,
            "FileName": "sample-report.txt",
            "ContentType": "text/plain",
            "ContentDisposition": "ATTACHMENT",
            "ContentDescription": "Sample report for bulk recipients",
            "ContentTransferEncoding": "BASE64",
        }

        # Build one entry per recipient with personalized names.
        names = ["Alice", "Bob", "Charlie", "Diana", "Eve"]
        bulk_entries = []
        for i, email in enumerate(self.recipient_emails):
            name = names[i] if i < len(names) else f"Recipient{i + 1}"
            bulk_entries.append(
                {
                    "Destination": {"ToAddresses": [email]},
                    "ReplacementEmailContent": {
                        "ReplacementTemplate": {
                            "ReplacementTemplateData": json.dumps(
                                {"name": name}
                            )
                        }
                    },
                }
            )

        results = self.sesv2_wrapper.send_bulk_email(
            from_address=self.sender_email,
            template_name=self.TEMPLATE_NAME,
            default_template_data='{"name": "Valued Customer"}',
            bulk_entries=bulk_entries,
            attachments=[attachment],
        )

        print("  Bulk email results:")
        for idx, result in enumerate(results):
            status = result.get("Status", "Unknown")
            msg_id = result.get("MessageId", "N/A")
            error = result.get("Error", "")
            recipient = (
                self.recipient_emails[idx]
                if idx < len(self.recipient_emails)
                else "Unknown"
            )
            print(f"    {recipient}: Status={status}, MessageId={msg_id}")
            if error:
                print(f"      Error: {error}")

        print(
            "\n  All recipients receive the same attachment(s) defined in\n"
            "  DefaultContent. Template data is personalized per recipient.\n"
        )

    # ---------- Cleanup ----------

    def _cleanup(self) -> None:
        """Deletes the email template and optionally the email identity."""
        print("\n--- Cleanup ---\n")

        # Delete the email template.
        try:
            self.sesv2_wrapper.delete_email_template(self.TEMPLATE_NAME)
            print(f"  Template '{self.TEMPLATE_NAME}' deleted.")
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                print(
                    f"  Template '{self.TEMPLATE_NAME}' was already deleted."
                )
            else:
                logger.error("Failed to delete template: %s", err)

        # Optionally delete the email identity.
        if self.identity_was_created and self.sender_email:
            delete_identity = q.ask(
                f"Delete the email identity '{self.sender_email}'? (y/n) ",
                q.is_yesno,
            )
            if delete_identity:
                try:
                    self.sesv2_wrapper.delete_email_identity(
                        self.sender_email
                    )
                    print(
                        f"  Email identity '{self.sender_email}' deleted."
                    )
                except ClientError as err:
                    if err.response["Error"]["Code"] == "NotFoundException":
                        print(
                            f"  Identity '{self.sender_email}' was "
                            "already deleted."
                        )
                    else:
                        logger.error(
                            "Failed to delete identity: %s", err
                        )
            else:
                print(
                    f"  Skipping identity deletion for {self.sender_email}."
                )
        else:
            print(
                "  Sender identity was pre-existing. Skipping deletion."
            )

        print("\nAll resources have been cleaned up.")
        print("-" * 88)


# snippet-end:[python.example_code.sesv2.Scenario_EmailAttachments]


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    try:
        scenario = SESv2EmailAttachmentsScenario(SESv2Wrapper.from_client())
        scenario.run_scenario()
    except Exception:
        logging.exception("Something went wrong with the scenario.")