# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import sys
import os
import boto3

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from batch_get_secret_value import BatchGetSecretsWrapper
from get_secret_value import GetSecretWrapper


def test_batch_scenario():
    # Retrieve the secrets using the filter
    client = boto3.client("secretsmanager")
    wrapper = BatchGetSecretsWrapper(client)
    secrets = wrapper.batch_get_secrets("nonexistentSecret")
    assert not secrets


def test_scenario():
    # Retrieve the secrets using the filter
    client = boto3.client("secretsmanager")
    wrapper = GetSecretWrapper(client)
    secrets = wrapper.get_secret("nonexistentSecret")
    assert not secrets
