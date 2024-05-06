# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for:
- models/amazon/titan_embeddings_text/g1_invoke_model_scenarios.py
- models/amazon/titan_embeddings_text/v2_invoke_model_scenarios.py
"""

import boto3
import io
import json
import pytest

from models.amazon.titan_embeddings_text.g1_invoke_model_scenarios import (
    invoke_model as invoke_embeddings_g1,
)
from models.amazon.titan_embeddings_text.v2_invoke_model_scenarios import (
    invoke_model as invoke_embeddings_v2,
)

EMBEDDINGS_G1_MODEL_ID = "amazon.titan-embed-text-v1"
EMBEDDINGS_V2_MODEL_ID = "amazon.titan-embed-text-v2:0"

client = boto3.client(service_name="bedrock-runtime", region_name="us-west-2")


def test_invoke_embeddings_g1(make_stubber):
    input_text = "An input text."
    expected_params = {
        "modelId": EMBEDDINGS_G1_MODEL_ID,
        "body": json.dumps({"inputText": input_text}),
    }
    response = {
        "contentType": "application/json",
        "body": io.BytesIO(
            json.dumps({"embedding": [], "inputTextTokenCount": 1}).encode("utf-8")
        ),
    }
    make_stubber(client).stub_invoke_model(expected_params, response)
    result = invoke_embeddings_g1(input_text, client)
    assert result is not None


def test_invoke_embeddings_v2(make_stubber):
    input_text = "An input text."
    dimensions = 1024
    normalize = True
    expected_params = {
        "modelId": EMBEDDINGS_V2_MODEL_ID,
        "body": json.dumps(
            {"inputText": input_text, "dimensions": dimensions, "normalize": normalize}
        ),
    }
    response = {
        "contentType": "application/json",
        "body": io.BytesIO(
            json.dumps({"embedding": [], "inputTextTokenCount": 1}).encode("utf-8")
        ),
    }
    make_stubber(client).stub_invoke_model(expected_params, response)
    result = invoke_embeddings_v2(input_text, dimensions, normalize, client)
    assert result is not None
