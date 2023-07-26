import boto3


def generate_parameter_store_values(account_id_mappings):
    ssm = boto3.client('ssm')

    for key, value in account_id_mappings:
        try:
            response = ssm.get_parameter(Name=key, WithDecryption=True)
            print(f"Parameter '{key}' already exists. Updating the value...")
            ssm.put_parameter(Name=key, Value=value, Type='SecureString', Overwrite=True)
        except ssm.exceptions.ParameterNotFound:
            print(f"Parameter '{key}' does not exist. Creating a new parameter...")
            ssm.put_parameter(Name=key, Value=value, Type='SecureString')


if __name__ == "__main__":
    mappings = [
        # ('/account-mappings/swift', '123456789'),
        # ('/account-mappings/rust_dev_preview', '123456789'),
        # ('/account-mappings/kotlin', '123456789'),
        # ('/account-mappings/php', '123456789'),
        # ('/account-mappings/javascriptv3', '123456789'),
        # ('/account-mappings/python', '123456789'),
        # ('/account-mappings/gov2', '123456789'),
        # ('/account-mappings/javav2', '123456789'),
        # ('/account-mappings/dotnetv3', '123456789'),
        ('/account-mappings/ruby', '260778392212')
    ]
    generate_parameter_store_values(mappings)
