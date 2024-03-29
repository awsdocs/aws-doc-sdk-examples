# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import json
import sys
from botocore.exceptions import ClientError
from io import StringIO
import pytest
from unittest.mock import patch

from newsletter import (
    SESv2Workflow,
    get_subaddress_variants,
    CONTACT_LIST_NAME,
    TEMPLATE_NAME,
)

# Run tests with `python -m unittest`


class TestSESv2WorkflowPrepareApplication:
    @patch("boto3.client")
    def setup_method(self, method, mock_client):
        self.ses_client = mock_client()
        self.workflow = SESv2Workflow(self.ses_client)

    # Tests for prepare_application
    @patch("builtins.input", return_value="verified@example.com")
    def test_prepare_application_success(self, mock_input):
        self.ses_client.create_email_identity.return_value = None
        self.ses_client.create_contact_list.return_value = None

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.prepare_application()
        sys.stdout = sys.__stdout__  # Reset standard output

        expected_output = "Email identity 'verified@example.com' created successfully.\nContact list 'weekly-coupons-newsletter' created successfully.\nEmail template 'weekly-coupons' created successfully.\n"
        assert captured_output.getvalue() == expected_output

    @patch("builtins.input", return_value="verified@example.com")
    def test_prepare_application_error_identity_already_exists(self, mock_input):
        self.ses_client.create_email_identity.side_effect = ClientError(
            error_response={"Error": {"Code": "AlreadyExistsException"}},
            operation_name="CreateEmailIdentity",
        )
        self.ses_client.create_contact_list.return_value = None

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.prepare_application()
        sys.stdout = sys.__stdout__

        expected_output = "Email identity 'verified@example.com' already exists.\nContact list 'weekly-coupons-newsletter' created successfully.\nEmail template 'weekly-coupons' created successfully.\n"
        assert captured_output.getvalue() == expected_output

    @patch("builtins.input", return_value="invalid@example.com")
    def test_prepare_application_error_identity_not_found(self, mock_input):
        self.ses_client.create_email_identity.side_effect = ClientError(
            error_response={"Error": {"Code": "NotFoundException"}},
            operation_name="CreateEmailIdentity",
        )
        self.ses_client.create_contact_list.return_value = None

        captured_output = StringIO()
        sys.stdout = captured_output
        with pytest.raises(ClientError):
            self.workflow.prepare_application()
        sys.stdout = sys.__stdout__

        expected_output = ""
        assert captured_output.getvalue() == expected_output

    @patch("builtins.input", return_value="verified@example.com")
    def test_prepare_application_error_identity_limit_exceeded(self, mock_input):
        self.ses_client.create_email_identity.side_effect = ClientError(
            error_response={"Error": {"Code": "LimitExceededException"}},
            operation_name="CreateEmailIdentity",
        )
        self.ses_client.create_contact_list.return_value = None

        captured_output = StringIO()
        sys.stdout = captured_output
        with pytest.raises(ClientError):
            self.workflow.prepare_application()
        sys.stdout = sys.__stdout__

        expected_output = ""
        assert captured_output.getvalue() == expected_output

    @patch("builtins.input", return_value="verified@example.com")
    def test_prepare_application_error_contact_list_limit_exceeded(self, mock_input):
        self.ses_client.create_email_identity.return_value = None
        self.ses_client.create_contact_list.side_effect = ClientError(
            error_response={"Error": {"Code": "LimitExceededException"}},
            operation_name="CreateContactList",
        )

        captured_output = StringIO()
        sys.stdout = captured_output
        with pytest.raises(ClientError):
            self.workflow.prepare_application()
        sys.stdout = sys.__stdout__

        expected_output = (
            "Email identity 'verified@example.com' created successfully.\n"
        )
        assert captured_output.getvalue() == expected_output


class TestSESv2WorkflowGatherSubscribers:
    @patch("boto3.client")
    def setup_method(self, method, mock_client):
        self.ses_client = mock_client()
        self.workflow = SESv2Workflow(self.ses_client)
        self.workflow.verified_email = "verified@example.com"

    # Tests for gather_subscriber_email_addresses
    @patch("builtins.input", return_value="user@example.com")
    def test_gather_subscriber_email_addresses_success(self, mock_input):
        self.ses_client.create_contact.return_value = None
        self.ses_client.send_email.return_value = None

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.gather_subscriber_email_addresses()
        sys.stdout = sys.__stdout__

        email_variants = get_subaddress_variants("user@example.com", 3)
        expected_output = (
            "\n".join(
                [
                    f"Contact with email '{email}' created successfully.\nWelcome email sent to '{email}'."
                    for email in email_variants
                ]
            )
            + "\n"
        )
        assert captured_output.getvalue() == expected_output

    @patch("builtins.input", return_value="user@example.com")
    def test_gather_subscriber_email_addresses_error_contact_exists(self, mock_input):
        self.ses_client.create_contact.side_effect = [
            None,
            None,
            ClientError(
                error_response={"Error": {"Code": "AlreadyExistsException"}},
                operation_name="CreateContact",
            ),
        ]
        self.ses_client.send_email.return_value = None

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.gather_subscriber_email_addresses()
        sys.stdout = sys.__stdout__

        email_variants = get_subaddress_variants("user@example.com", 3)
        expected_output = f"Contact with email '{email_variants[0]}' created successfully.\nWelcome email sent to '{email_variants[0]}'.\nContact with email '{email_variants[1]}' created successfully.\nWelcome email sent to '{email_variants[1]}'.\nContact with email '{email_variants[2]}' already exists. Skipping...\n"
        assert captured_output.getvalue() == expected_output

    @patch("builtins.input", return_value="user@example.com")
    def test_gather_subscriber_email_addresses_error_send_email_failed(
        self, mock_input
    ):
        self.ses_client.create_contact.return_value = None
        self.ses_client.send_email.side_effect = [
            None,
            ClientError(
                error_response={"Error": {"Code": "MessageRejected"}},
                operation_name="SendEmail",
            ),
            None,
        ]

        captured_output = StringIO()
        sys.stdout = captured_output
        with pytest.raises(ClientError):
            self.workflow.gather_subscriber_email_addresses()
        sys.stdout = sys.__stdout__

        email_variants = get_subaddress_variants("user@example.com", 3)
        expected_output = f"Contact with email '{email_variants[0]}' created successfully.\nWelcome email sent to '{email_variants[0]}'.\nContact with email '{email_variants[1]}' created successfully.\n"
        assert captured_output.getvalue() == expected_output


class TestSESv2WorkflowSendCouponNewsletter:
    @patch("boto3.client")
    def setup_method(self, method, mock_client):
        self.ses_client = mock_client()
        self.workflow = SESv2Workflow(self.ses_client)
        self.workflow.verified_email = "verified@example.com"

    # Tests for send_coupon_newsletter
    @patch(
        "newsletter.load_file_content",
        side_effect=[
            "<html>Template Content</html>",
            "Plain Text Template Content",
            '[{"details": "20% off on all electronics"}]',
        ],
    )
    def test_send_coupon_newsletter_success(self, mock_load_file_content):
        self.ses_client.list_contacts.return_value = {
            "Contacts": [
                {"EmailAddress": "user+1@example.com"},
                {"EmailAddress": "user+2@example.com"},
                {"EmailAddress": "user+3@example.com"},
            ]
        }
        self.ses_client.send_email.return_value = None

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.send_coupon_newsletter()
        sys.stdout = sys.__stdout__

        expected_output = "Newsletter sent to 'user+1@example.com'.\nNewsletter sent to 'user+2@example.com'.\nNewsletter sent to 'user+3@example.com'.\n"
        assert captured_output.getvalue() == expected_output

    def test_send_coupon_newsletter_error_contact_list_not_found(self):
        self.ses_client.list_contacts.side_effect = ClientError(
            error_response={"Error": {"Code": "NotFoundException"}},
            operation_name="ListContacts",
        )

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.send_coupon_newsletter()
        sys.stdout = sys.__stdout__

        expected_output = f"Contact list '{CONTACT_LIST_NAME}' does not exist.\n"
        assert captured_output.getvalue() == expected_output

    @patch(
        "newsletter.load_file_content",
        return_value=json.dumps(["Coupon 1", "Coupon 2"]),
    )
    def test_send_coupon_newsletter_error_send_email_failed(
        self, mock_load_file_content
    ):
        self.ses_client.list_contacts.return_value = {
            "Contacts": [
                {"EmailAddress": "user1@example.com"},
                {"EmailAddress": "user2@example.com"},
                {"EmailAddress": "user3@example.com"},
            ]
        }
        self.ses_client.send_email.side_effect = [
            None,
            ClientError(
                error_response={"Error": {"Code": "MessageRejected"}},
                operation_name="SendEmail",
            ),
            None,
        ]

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.send_coupon_newsletter()
        sys.stdout = sys.__stdout__

        expected_output = "Newsletter sent to 'user1@example.com'.\nError: An error occurred (MessageRejected) when calling the SendEmail operation: Unknown\nNewsletter sent to 'user3@example.com'.\n"
        assert captured_output.getvalue() == expected_output


class TestSESv2WorkflowCleanUp:
    @patch("boto3.client")
    def setup_method(self, method, mock_client):
        self.ses_client = mock_client()
        self.workflow = SESv2Workflow(self.ses_client)
        self.workflow.verified_email = "verified@example.com"

    # Tests for clean_up
    @patch("builtins.input", return_value="n")
    def test_clean_up_success(self, mock_input):
        self.ses_client.delete_contact_list.return_value = None
        self.ses_client.delete_email_template.return_value = None
        self.ses_client.delete_email_identity.return_value = None

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.clean_up()
        sys.stdout = sys.__stdout__

        expected_output = f"Contact list '{CONTACT_LIST_NAME}' deleted successfully.\nEmail template '{TEMPLATE_NAME}' deleted successfully.\nSkipping email identity deletion.\n"
        assert captured_output.getvalue() == expected_output

    @patch("builtins.input", return_value="n")
    def test_clean_up_error_contact_list_not_found(self, mock_input):
        self.ses_client.delete_contact_list.side_effect = ClientError(
            error_response={"Error": {"Code": "NotFoundException"}},
            operation_name="DeleteContactList",
        )
        self.ses_client.delete_email_template.return_value = None
        self.ses_client.delete_email_identity.return_value = None

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.clean_up()
        sys.stdout = sys.__stdout__

        expected_output = f"Contact list '{CONTACT_LIST_NAME}' does not exist.\nEmail template '{TEMPLATE_NAME}' deleted successfully.\nSkipping email identity deletion.\n"
        assert captured_output.getvalue() == expected_output

    @patch("builtins.input", return_value="n")
    def test_clean_up_error_template_not_found(self, mock_input):
        self.ses_client.delete_contact_list.return_value = None
        self.ses_client.delete_email_template.side_effect = ClientError(
            error_response={"Error": {"Code": "NotFoundException"}},
            operation_name="DeleteEmailTemplate",
        )
        self.ses_client.delete_email_identity.return_value = None

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.clean_up()
        sys.stdout = sys.__stdout__

        expected_output = f"Contact list '{CONTACT_LIST_NAME}' deleted successfully.\nEmail template '{TEMPLATE_NAME}' does not exist.\nSkipping email identity deletion.\n"
        assert captured_output.getvalue() == expected_output

    @patch("builtins.input", return_value="y")
    def test_clean_up_error_identity_not_found(self, mock_input):
        self.ses_client.delete_contact_list.return_value = None
        self.ses_client.delete_email_template.return_value = None
        self.ses_client.delete_email_identity.side_effect = ClientError(
            error_response={"Error": {"Code": "NotFoundException"}},
            operation_name="DeleteEmailIdentity",
        )

        captured_output = StringIO()
        sys.stdout = captured_output
        self.workflow.clean_up()
        sys.stdout = sys.__stdout__

        expected_output = f"Contact list '{CONTACT_LIST_NAME}' deleted successfully.\nEmail template '{TEMPLATE_NAME}' deleted successfully.\nEmail identity '{self.workflow.verified_email}' does not exist.\n"
        assert captured_output.getvalue() == expected_output
