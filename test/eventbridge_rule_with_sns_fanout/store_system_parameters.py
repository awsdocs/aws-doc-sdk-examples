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
        ('/account-mappings/ruby', '260778392212')
    ]

    # Call the function to generate or update the parameter values in Parameter Store
    generate_parameter_store_values(mappings)
