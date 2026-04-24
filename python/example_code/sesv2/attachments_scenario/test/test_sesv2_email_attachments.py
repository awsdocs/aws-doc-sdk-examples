# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the SESv2 Email Attachments scenario.

These tests call real AWS SESv2 APIs and require a verified email identity.
Set the environment variable SENDER_EMAIL to a verified SES email address.
"""

import os
import sys

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

import boto3
import pytest

from sesv2_wrapper import SESv2Wrapper
from scenario_sesv2_email_attachments import SESv2EmailAttachmentsScenario


@pytest.mark.integ
def test_sesv2_wrapper_get_email_identity():
    """Test that get_email_identity returns identity info for a verified sender."""
    sender_email = os.environ.get("SENDER_EMAIL")
    if not sender_email:
        pytest.skip("SENDER_EMAIL env var not set; skipping integration test.")

    wrapper = SESv2Wrapper.from_client()
    try:
        result = wrapper.get_email_identity(sender_email)
        assert "VerifiedForSendingStatus" in result
    finally:
        # No cleanup needed for read-only operation.
        pass


@pytest.mark.integ
def test_sesv2_wrapper_create_and_delete_template():
    """Test creating and deleting an email template."""
    wrapper = SESv2Wrapper.from_client()
    template_name = "IntegTestTemplate"
    try:
        wrapper.create_email_template(
            template_name=template_name,
            subject="Test Subject for {{name}}",
            html_body="<h1>Hello {{name}}</h1>",
            text_body="Hello {{name}}",
        )
    finally:
        try:
            wrapper.delete_email_template(template_name)
        except Exception:
            pass


@pytest.mark.integ
def test_sesv2_wrapper_send_email_with_attachment():
    """Test sending a simple email with a file attachment."""
    sender_email = os.environ.get("SENDER_EMAIL")
    recipient_email = os.environ.get("RECIPIENT_EMAIL", sender_email)
    if not sender_email:
        pytest.skip("SENDER_EMAIL env var not set; skipping integration test.")

    wrapper = SESv2Wrapper.from_client()
    sample_content = b"Integration test attachment content."

    attachment = {
        "RawContent": sample_content,
        "FileName": "integ-test.txt",
        "ContentType": "text/plain",
        "ContentDisposition": "ATTACHMENT",
        "ContentDescription": "Integration test file",
        "ContentTransferEncoding": "BASE64",
    }

    try:
        message_id = wrapper.send_email(
            from_address=sender_email,
            to_addresses=[recipient_email],
            subject="SESv2 Integration Test — Attachment",
            html_body="<p>Integration test with attachment.</p>",
            text_body="Integration test with attachment.",
            attachments=[attachment],
        )
        assert message_id is not None
        assert len(message_id) > 0
    finally:
        # No cleanup needed for sent emails.
        pass


@pytest.mark.integ
def test_sesv2_wrapper_send_email_with_inline_image():
    """Test sending a simple email with an inline image."""
    sender_email = os.environ.get("SENDER_EMAIL")
    recipient_email = os.environ.get("RECIPIENT_EMAIL", sender_email)
    if not sender_email:
        pytest.skip("SENDER_EMAIL env var not set; skipping integration test.")

    wrapper = SESv2Wrapper.from_client()

    # Minimal 1x1 PNG
    sample_image = (
        b"\x89PNG\r\n\x1a\n"
        b"\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01"
        b"\x08\x02\x00\x00\x00\x90wS\xde"
        b"\x00\x00\x00\x0cIDATx\x9cc\xf8\x0f\x00\x00\x01\x01"
        b"\x00\x05\x18\xd8N"
        b"\x00\x00\x00\x00IEND\xaeB`\x82"
    )

    attachment = {
        "RawContent": sample_image,
        "FileName": "test-logo.png",
        "ContentType": "image/png",
        "ContentDisposition": "INLINE",
        "ContentId": "testlogo123",
        "ContentDescription": "Test logo",
        "ContentTransferEncoding": "BASE64",
    }

    html_body = (
        '<html><body><h1>Test</h1>'
        '<img src="cid:testlogo123" alt="Test Logo">'
        '</body></html>'
    )

    try:
        message_id = wrapper.send_email(
            from_address=sender_email,
            to_addresses=[recipient_email],
            subject="SESv2 Integration Test — Inline Image",
            html_body=html_body,
            text_body="Inline image test — view in HTML client.",
            attachments=[attachment],
        )
        assert message_id is not None
        assert len(message_id) > 0
    finally:
        pass


@pytest.mark.integ
def test_sesv2_wrapper_send_bulk_email_with_attachment():
    """Test sending bulk templated email with an attachment."""
    sender_email = os.environ.get("SENDER_EMAIL")
    recipient_email = os.environ.get("RECIPIENT_EMAIL", sender_email)
    if not sender_email:
        pytest.skip("SENDER_EMAIL env var not set; skipping integration test.")

    wrapper = SESv2Wrapper.from_client()
    template_name = "IntegTestBulkTemplate"

    sample_content = b"Bulk test attachment content."
    attachment = {
        "RawContent": sample_content,
        "FileName": "bulk-test.txt",
        "ContentType": "text/plain",
        "ContentDisposition": "ATTACHMENT",
        "ContentDescription": "Bulk test file",
        "ContentTransferEncoding": "BASE64",
    }

    try:
        wrapper.create_email_template(
            template_name=template_name,
            subject="Bulk Test for {{name}}",
            html_body="<h1>Hello {{name}}</h1><p>Attached document.</p>",
            text_body="Hello {{name}}, attached document.",
        )

        bulk_entries = [
            {
                "Destination": {"ToAddresses": [recipient_email]},
                "ReplacementEmailContent": {
                    "ReplacementTemplate": {
                        "ReplacementTemplateData": '{"name": "TestUser"}'
                    }
                },
            }
        ]

        results = wrapper.send_bulk_email(
            from_address=sender_email,
            template_name=template_name,
            default_template_data='{"name": "Default"}',
            bulk_entries=bulk_entries,
            attachments=[attachment],
        )

        assert results is not None
        assert len(results) == 1
        assert results[0].get("Status") == "SUCCESS"
    finally:
        try:
            wrapper.delete_email_template(template_name)
        except Exception:
            pass


@pytest.mark.integ
def test_sesv2_hello(capsys):
    """Test the Hello SESv2 example."""
    from sesv2_hello import hello_sesv2  # noqa: resolved via sys.path

    try:
        hello_sesv2(boto3.client("sesv2"))
        captured = capsys.readouterr()
        assert "Hello, Amazon SESv2!" in captured.out
    finally:
        pass