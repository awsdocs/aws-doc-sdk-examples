import boto3
import json


# snippet-start:[python.example_code.secretsmanager.GetSecretValue]
def get_secret(secret_name):
    """
    Retrieve individual secrets from AWS Secrets Manager using the get_secret_value API.
    This function assumes the stack mentioned in this directory's README has been successfully deployed.
    This stack includes 7 secrets, all of which have names beginning with "mySecret".

    @:param secret_name [String] The name of the secret fetched.
    """
    client = boto3.client("secretsmanager")

    try:
        get_secret_value_response = client.get_secret_value(SecretId=secret_name)
        print(json.loads(get_secret_value_response["SecretString"]))
    except client.exceptions.ResourceNotFoundException:
        return f"The requested secret {secret_name} was not found."
    except Exception as e:
        return f"An unknown error occurred: {str(e)}."


print(get_secret("mySecret1"))
# snippet-end:[python.example_code.secretsmanager.GetSecretValue]
