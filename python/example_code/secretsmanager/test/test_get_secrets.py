# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import sys
import os
import boto3
import pytest

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from batch_get_secret_value import BatchGetSecretsWrapper
from get_secret_value import GetSecretWrapper


@pytest.mark.integ
def test_scenario():
    """
    This test assumes the stack mentioned in the source code README has been successfully deployed.
    This stack includes 7 secrets, all of which have names beginning with "mySecret".
    """
    # Retrieve secret using secret name
    client = boto3.client("secretsmanager")
    wrapper = GetSecretWrapper(client)
    secret = wrapper.get_secret("nonexistentSecret")
    assert "The requested secret nonexistentSecret was not found" in secret
    secret = wrapper.get_secret("mySecret1")
    assert secret


@pytest.mark.integ
def test_batch_scenario():
    """
    This test assumes the stack mentioned in the source code README has been successfully deployed.
    This stack includes 7 secrets, all of which have names beginning with "mySecret".
    """
    # Retrieve the secrets using the filter
    client = boto3.client("secretsmanager")
    wrapper = BatchGetSecretsWrapper(client)
    secrets = wrapper.batch_get_secrets("nonexistentSecrets")
    assert not secrets
    secrets = wrapper.batch_get_secrets("mySecret")
    assert secrets
