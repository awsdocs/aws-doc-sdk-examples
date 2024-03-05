# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import base64
import boto3
import pytest

from botocore.exceptions import ClientError
from actions.invoke_claude_3_with_text import invoke_claude_3_with_text
from actions.invoke_claude_3_multimodal import invoke_claude_3_multimodal

client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")


def test_invoke_claude_3_with_text(make_stubber):
    make_stubber(client).stub_invoke_claude_3_with_text()
    response = invoke_claude_3_with_text({"client": client})
    assert len(response["outputs"]) > 0


def test_invoke_claude_3_multimodal(make_stubber):
    image_base64 = "FakeBase64String=="
    make_stubber(client).stub_invoke_claude_3_multimodal(image_base64)
    response = invoke_claude_3_multimodal({"client": client, "image": image_base64})
    assert len(response["outputs"]) > 0
