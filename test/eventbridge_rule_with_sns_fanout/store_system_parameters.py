import boto3


# This file writes key-value mappings to AWS Parameter Store.
def generate_parameter_store_values(account_id_mappings):
    # Create an SSM client using boto3 library
    ssm = boto3.client('ssm')

    # Iterate through each key-value pair in the account_id_mappings list
    for key, value in account_id_mappings:
        try:
            # Check if the parameter with the specified name (key) already exists in Parameter Store
            ssm.get_parameter(Name=key, WithDecryption=True)

            # If the parameter already exists, update its value with the new value
            print(f"Parameter '{key}' already exists. Updating the value...")
            ssm.put_parameter(Name=key, Value=value, Type='SecureString', Overwrite=True)
        except ssm.exceptions.ParameterNotFound:
            # If the parameter does not exist, create a new parameter with the given key-value pair
            print(f"Parameter '{key}' does not exist. Creating a new parameter...")
            ssm.put_parameter(Name=key, Value=value, Type='SecureString')

if __name__ == "__main__":
    # List of key-value pairs representing parameter names and their corresponding values
    mappings = [
        ('/account-mappings/ruby', '616362385685'),
        ('/account-mappings/php', '733931915187'),
        ('/account-mappings/dotnetv3', '441997275833'),
        ('/account-mappings/javascriptv3', '875008041426'),
        ('/account-mappings/swift', '637397754108'),
        ('/account-mappings/rust_dev_preview', '050288538048'),
        ('/account-mappings/cpp', '770244195820'),
        ('/account-mappings/python', '664857444588'),
        ('/account-mappings/gov2', '234521034040'),
        ('/account-mappings/sap-abap', '099736152523'),
        ('/account-mappings/javav2', '814548047983'), # back-up 667348412466
        ('/account-mappings/kotlin', '814548047983') # back-up 667348412466
    ]

    # Call the function to generate or update the parameter values in Parameter Store
    generate_parameter_store_values(mappings)
