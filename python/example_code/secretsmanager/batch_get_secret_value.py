import boto3
import json


# snippet-start:[python.example_code.secretsmanager.BatchGetSecretValue]
def batch_get_secrets(filter_name):
    """
    Retrieve multiple secrets from AWS Secrets Manager using the batch_get_secret_value API.
    This function assumes the stack mentioned in this directory's README has been successfully deployed.
    This stack includes 7 secrets, all of which have names beginning with "mySecret".

    @:param filter_name [String] The full or partial name of secrets to be fetched.
    """
    client = boto3.client("secretsmanager")

    try:
        secrets = []
        response = client.batch_get_secret_value(
            Filters=[{"Key": "name", "Values": [f"{filter_name}"]}]
        )
        for secret in response["SecretValues"]:
            print(json.loads(secret["SecretString"]))
            secrets.append(json.loads(secret["SecretString"]))
        return secrets
    except client.exceptions.ResourceNotFoundException:
        return f"One or more requested secrets were not found."
    except Exception as e:
        return f"An unknown error occurred:\n{str(e)}."


print(batch_get_secrets("mySecret"))
# snippet-end:[python.example_code.secretsmanager.BatchGetSecretValue]
