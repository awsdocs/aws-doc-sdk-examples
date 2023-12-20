import boto3
import json
from botocore.exceptions import NoCredentialsError

# snippet-start:[python.example_code.secretsmanager.BatchGetSecretValue]
def batch_get_secrets(filter_name):
    """
    Retrieve multiple secrets from AWS Secrets Manager using the batch_get_secret_value API.
    This function assumes the stack mentioned in this directory's README has been successfully deployed.
    This stack includes 7 secrets, all of which have names beginning with "mySecret".

    @:param filter_name [String] The full or partial name of secrets to be fetched.
    """
    # Create SecretsManager client
    client = boto3.client(service_name='secretsmanager')

    try:
        # Retrieve secrets
        response = client.batch_get_secret_value(Filters=[
            {
                'Key': 'name',
                'Values': [
                    f'{filter_name}',
                ]
            },
        ])
    except NoCredentialsError:
        return "No AWS credentials available"
    except client.exceptions.ResourceNotFoundException:
        return f"One or more requested secrets were not found"
    except Exception as e:
        return f"An unknown error occurred: {str(e)}"

    secrets = {}
    for secret in response['SecretValues']:
        secret_name = secret['ARN'].split(':')[-1]
        if 'SecretString' in secret:
            secrets[secret_name] = json.loads(secret['SecretString'])
        else:
            secrets[secret_name] = "Binary secrets not supported in this example"

    return secrets


print(batch_get_secrets("mySecret"))
# snippet-end:[python.example_code.secretsmanager.BatchGetSecretValue]
