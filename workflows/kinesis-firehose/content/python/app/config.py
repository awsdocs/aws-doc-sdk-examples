import os

def get_config():
    config = {
        'aws_access_key_id': os.environ.get('AWS_ACCESS_KEY_ID'),
        'aws_secret_access_key': os.environ.get('AWS_SECRET_ACCESS_KEY'),
        'aws_region': os.environ.get('AWS_REGION', 'us-east-1')
    }
    return config