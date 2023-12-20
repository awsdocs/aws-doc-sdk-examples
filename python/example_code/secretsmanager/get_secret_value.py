import boto3
import json
from botocore.exceptions import NoCredentialsError

# snippet-start:[python.example_code.secretsmanager.GetSecretValue]

def get_secret(secret_name):
    """
    Retrieve individual secrets from AWS Secrets Manager using the get_secret_value API.
    This function assumes the stack mentioned in this directory's README has been successfully deployed.
    This stack includes 7 secrets, all of which have names beginning with "mySecret".

    @:param secret_name [String] The name of the secret fetched.
    """

    # Create SecretsManager client
    client = boto3.client(service_name='secretsmanager')

    try:
        # Retrieve secret
        get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    except NoCredentialsError:
        return "No AWS credentials available"
    except client.exceptions.ResourceNotFoundException:
        return f"The requested secret {secret_name} was not found"
    except Exception as e:
        return f"An unknown error occurred: {str(e)}"

    # Decode and return secret
    if 'SecretString' in get_secret_value_response:
        secret = get_secret_value_response['SecretString']
        return json.loads(secret)
    else:
        return "Binary secrets are not supported in this example"


print(get_secret("mySecret1"))
# snippet-end:[python.example_code.secretsmanager.GetSecretValue]
