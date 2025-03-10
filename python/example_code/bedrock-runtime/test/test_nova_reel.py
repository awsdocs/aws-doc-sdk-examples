# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the text-to-video generation example using Amazon Nova Reel.
Uses mocking to test the code without making actual API calls to AWS.
"""

from unittest.mock import MagicMock, patch

import pytest
from models.amazon_nova.amazon_nova_reel import text_to_video


@pytest.fixture
def mock_bedrock_runtime():
    """A mocked bedrock runtime client."""
    with patch("boto3.client") as mock_client:
        mock_bedrock = MagicMock()
        mock_client.return_value = mock_bedrock
        yield mock_bedrock


@pytest.fixture
def successful_job_responses(mock_bedrock_runtime):
    """Mock responses for a successful job."""
    mock_bedrock_runtime.start_async_invoke.return_value = {
        "invocationArn": "arn:aws:bedrock:us-east-1:123456789012:invocation/abcdef123456"
    }
    mock_bedrock_runtime.get_async_invoke.side_effect = [
        {"status": "InProgress"},
        {
            "status": "Completed",
            "outputDataConfig": {"s3OutputDataConfig": {"s3Uri": "s3://test-bucket"}},
        },
    ]

    return mock_bedrock_runtime


@pytest.fixture
def failed_job_responses(mock_bedrock_runtime):
    """Mock responses for a failed job."""
    mock_bedrock_runtime.start_async_invoke.return_value = {
        "invocationArn": "arn:aws:bedrock:us-east-1:123456789012:invocation/abcdef123456"
    }
    mock_bedrock_runtime.get_async_invoke.return_value = {
        "status": "Failed",
        "failureMessage": "Test failure message",
    }

    return mock_bedrock_runtime


def test_start_text_to_video_generation_job(mock_bedrock_runtime):
    # Set up mock return value
    mock_bedrock_runtime.start_async_invoke.return_value = {
        "invocationArn": "arn:aws:bedrock:us-east-1:123456789012:invocation/abcdef123456"
    }

    # Test parameters
    prompt = "Test prompt"
    output_s3_uri = "s3://test-bucket"

    # Call the function
    result = text_to_video.start_text_to_video_generation_job(
        mock_bedrock_runtime, prompt, output_s3_uri
    )

    # Verify the client was called correctly
    mock_bedrock_runtime.start_async_invoke.assert_called_once()

    # Check the parameters
    call_args = mock_bedrock_runtime.start_async_invoke.call_args[1]
    assert call_args["modelId"] == "amazon.nova-reel-v1:0"
    assert call_args["modelInput"]["textToVideoParams"]["text"] == prompt
    assert call_args["outputDataConfig"]["s3OutputDataConfig"]["s3Uri"] == output_s3_uri

    # Verify the return value
    assert result == "arn:aws:bedrock:us-east-1:123456789012:invocation/abcdef123456"


def test_query_job_status(mock_bedrock_runtime):
    """Test the job status query function with pytest."""
    # Set up mock return value
    mock_job_status = {"status": "InProgress"}
    mock_bedrock_runtime.get_async_invoke.return_value = mock_job_status

    # Test parameter
    invocation_arn = "arn:aws:bedrock:us-east-1:123456789012:invocation/abcdef123456"

    # Call the function
    result = text_to_video.query_job_status(mock_bedrock_runtime, invocation_arn)

    # Verify the client was called correctly
    mock_bedrock_runtime.get_async_invoke.assert_called_once_with(
        invocationArn=invocation_arn
    )

    # Verify the return value
    assert result == mock_job_status


@patch("time.sleep", return_value=None)  # Prevent actual sleep in tests
def test_main_success(mock_sleep, successful_job_responses, monkeypatch):
    # Patch boto3.client to use our fixture
    monkeypatch.setattr(
        "boto3.client", lambda service_name, region_name: successful_job_responses
    )

    # Patch the OUTPUT_S3_URI constant to use a valid test value
    monkeypatch.setattr(text_to_video, "OUTPUT_S3_URI", "s3://test-bucket")

    # Capture print output
    printed_messages = []
    monkeypatch.setattr(
        "builtins.print", lambda *args: printed_messages.append(args[0])
    )

    # Call the main function
    text_to_video.main()

    # Verify the printed messages
    assert "Submitting video generation job..." in printed_messages
    assert (
        "\nSuccess! The video is available at: s3://test-bucket/output.mp4"
        in printed_messages
    )


@patch("time.sleep", return_value=None)  # Prevent actual sleep in tests
def test_main_failure(mock_sleep, failed_job_responses, monkeypatch):
    # Patch boto3.client to use our fixture
    monkeypatch.setattr(
        "boto3.client", lambda service_name, region_name: failed_job_responses
    )

    # Patch the OUTPUT_S3_URI constant to use a valid test value
    monkeypatch.setattr(text_to_video, "OUTPUT_S3_URI", "s3://test-bucket")

    # Capture print output
    printed_messages = []
    monkeypatch.setattr(
        "builtins.print", lambda *args: printed_messages.append(args[0])
    )

    # Call the main function
    text_to_video.main()

    # Verify the printed messages
    assert "Submitting video generation job..." in printed_messages
    assert "\nVideo generation failed: Test failure message" in printed_messages


def test_main_invalid_s3_uri(monkeypatch):
    """Test the main function with an invalid S3 URI."""
    # Mock boto3.client to ensure no real AWS calls are made
    mock_client = MagicMock()
    monkeypatch.setattr("boto3.client", lambda service_name, region_name: mock_client)

    # Ensure the OUTPUT_S3_URI is the default placeholder value
    monkeypatch.setattr(
        text_to_video, "OUTPUT_S3_URI", "s3://REPLACE-WITH-YOUR-S3-BUCKET-NAME"
    )

    # Capture print output
    printed_messages = []
    monkeypatch.setattr(
        "builtins.print", lambda *args: printed_messages.append(args[0])
    )

    # Call the main function
    text_to_video.main()

    # Verify the error message is printed
    assert (
        "ERROR: You must replace the OUTPUT_S3_URI with your own S3 bucket URI"
        in printed_messages
    )

    # Verify that no AWS API calls were made
    mock_client.start_async_invoke.assert_not_called()
