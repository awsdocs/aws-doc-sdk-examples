import os
import boto3
import botocore
import subprocess
import tempfile
from urllib.parse import urlparse
import urllib.request
import tarfile

# Environment variables
AWS_NUKE_DRY_RUN = os.environ.get('AWS_NUKE_DRY_RUN', 'true')
AWS_NUKE_VERSION = os.environ.get('AWS_NUKE_VERSION', '2.21.2')
NUKE_S3_BUCKET = os.environ.get('NUKE_S3_BUCKET')
NUKE_CONFIG_KEY = os.environ.get('NUKE_CONFIG_KEY')


def handler(event, context):
    try:
        # Download AWS Nuke binary from GitHub release
        nuke_binary_path = download_nuke_binary(AWS_NUKE_VERSION)
        extract_binary(nuke_binary_path)

        # Download nuke-config.yaml from S3
        nuke_config_path = download_from_s3(NUKE_S3_BUCKET, NUKE_CONFIG_KEY)

        # Execute AWS Nuke
        execute_aws_nuke(nuke_binary_path, nuke_config_path, AWS_NUKE_DRY_RUN)

    except Exception as e:
        print(f"Error: {e}")
        raise e

def download_nuke_binary(version):
    """Download the AWS Nuke binary from the GitHub release"""
    binary_url = f"https://github.com/rebuy-de/aws-nuke/releases/download/v{version}/aws-nuke-v{version}-linux-amd64.tar.gz"
    tmp_file = tempfile.NamedTemporaryFile(delete=False)
    urllib.request.urlretrieve(binary_url, tmp_file.name)
    tmp_file.close()
    return tmp_file.name

def download_from_s3(bucket_name, object_key):
    """Download an object from S3 to a temporary file and return the file path"""
    s3 = boto3.client('s3')
    tmp_file = tempfile.NamedTemporaryFile(delete=False)
    s3.download_file(bucket_name, object_key, tmp_file.name)
    tmp_file.close()
    return tmp_file.name

def extract_binary(binary_path):
    """Extract the AWS Nuke binary archive"""
    with tarfile.open(binary_path, 'r:gz') as tar:
        tar.extractall('/tmp')
    binary_name = f'aws-nuke-v{AWS_NUKE_VERSION}-linux-amd64'
    binary_path = os.path.join('/tmp', binary_name)
    os.chmod(binary_path, 0o755)

def execute_aws_nuke(binary_path, config_path, dry_run_flag):
    """Execute the AWS Nuke command"""
    dry_run_arg = '--no-dry-run' if dry_run_flag.lower() == 'false' else ''
    binary_path = f'/tmp/aws-nuke-v{AWS_NUKE_VERSION}-linux-amd64'
    subprocess.run([binary_path, '-c', config_path, '--force', '--max-wait-retries', '10', dry_run_arg], check=True)