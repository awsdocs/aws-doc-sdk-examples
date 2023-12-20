import pytest
import boto3
import os
import sys
from moto import mock_secretsmanager
sys.path.append(os.path.dirname(os.path.dirname(__file__)))
from batch_get_secret_value import batch_get_secrets
from get_secret_value import get_secret

@mock_secretsmanager
def create_secret(secret_name, secret_value):
    """Utility function to create a secret in the mocked Secrets Manager."""
    client = boto3.client('secretsmanager')
    client.create_secret(Name=secret_name, SecretString=secret_value)

@pytest.fixture
def aws_credentials():
    """Mocked AWS Credentials for moto."""
    return {
        "aws_access_key_id": "testing",
        "aws_secret_access_key": "testing",
        "aws_session_token": "testing",
    }

@pytest.fixture
def secrets_manager(aws_credentials):
    """Secrets Manager mock fixture."""
    with mock_secretsmanager():
        yield boto3.client('secretsmanager', region_name='us-east-1')

def test_batch_get_secrets_success(secrets_manager):
    # Setup
    secret_names = [f"mySecret{i}" for i in range(3)]
    for name in secret_names:
        create_secret(name, f"secret-value-{name}")

    # Test
    secrets = get_secret("mySecret1")
    breakpoint()
    assert len(secrets) > 3
    for name in secret_names:
        assert name == "mySecret0"

def test_batch_get_secrets_no_credentials():
    with pytest.raises(NoCredentialsError):
        batch_get_secrets("mySecret")

def test_batch_get_secrets_resource_not_found(secrets_manager):
    result = batch_get_secrets("nonexistentSecret")
    assert result == "One or more requested secrets were not found"
