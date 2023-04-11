import boto3

### The purpose of this script is to customize an AWS environment
### with specific langauges and AWS Account ID's where language-specific
### integration tests will be executed. You must update this file with at least 1
### AWS Acount ID and language to test on Line 14.

# Set up the AWS client
client = boto3.client('ssm')

# Define the parameters to write
parameters = {
    # 'language_name': '000000000000'
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
