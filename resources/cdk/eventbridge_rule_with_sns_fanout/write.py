import boto3

# Set up the AWS client
client = boto3.client('ssm')

# Define the parameters to write
parameters = {
    'ruby': '260778392212',
    'weathertop_central': '808326389482',
    # 'javav2': '000000000000',
    # 'javascriptv3': '000000000000',
    # 'python': '000000000000',
    # 'dotnetv3': '000000000000',
    # 'kotlin': '000000000000',
    # 'rust_dev_preview': '000000000000',
    # 'swift': '000000000000',
    # 'cpp': '000000000000',
    # 'gov2': '000000000000',
    # 'sap-abap': '000000000000',
}

# Method to write parameters to Parameter Store
def write_params():
    for name, value in parameters.items():
        client.put_parameter(
            Name=name,
            Value=value,
            Type='SecureString',
            Overwrite=True
        )
        print(f"Parameter written to Parameter Store: {name}:{value}")

write_params()
