import boto3
import yaml


def load_yaml(file_path):
    """Load key-value mappings from a YAML file."""
    with open(file_path, "r") as file:
        data = yaml.safe_load(file)
        return data


def add_parameter(key, value):
    ssm = boto3.client("ssm")
    try:
        ssm.get_parameter(Name=key, WithDecryption=True)
        print(f"Parameter {key} already exists. Updating the value...")
        ssm.put_parameter(Name=key, Value=value, Type="SecureString", Overwrite=True)
    except ssm.exceptions.ParameterNotFound:
        print(f"Parameter {key} does not exist. Creating a new parameter...")
        ssm.put_parameter(Name=key, Value=value, Type="SecureString")


def generate_parameter_store_values(mappings):
    languages = mappings.keys()
    for language in languages:
        add_parameter(f"/{language}/account_id", str(mappings[language]["account_id"]))
        add_parameter(f"/{language}/enabled", str(mappings[language]["enabled"]))


if __name__ == "__main__":
    mappings = load_yaml("config/targets.yaml")
    generate_parameter_store_values(mappings)
