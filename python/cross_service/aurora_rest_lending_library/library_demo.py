import argparse
import logging
import boto3
import yaml

from aurora_tools import create_resources, do_populate_database, do_deploy_rest, do_rest_demo

logger = logging.getLogger(__name__)

# Read YAML configuration
with open("config.yml") as file:
    config = yaml.safe_load(file)

# Define cluster_name and secret_name from config
cluster_name = config.get('cluster', {}).get('cluster_name')
secret_name = config.get('secret_name')


def find_api_url(stack_name):
    cloudformation = boto3.resource("cloudformation")
    stack = cloudformation.Stack(name=stack_name)
    try:
        api_url = next(
            output["OutputValue"]
            for output in stack.outputs
            if output["OutputKey"] == "EndpointURL"
        )
        logging.info(
            "Found API URL in %s AWS CloudFormation stack: %s", stack_name, api_url
        )
    except StopIteration:
        logger.warning(
            "Couldn't find the REST URL for your API. Try running the following "
            "at the command prompt:\n"
            "\taws cloudformation describe-stacks --stack-name {stack_name} "
            "--query \"Stacks[0].Outputs[?OutputKey=='EndpointURL'].OutputValue\" "
            "--output text"
        )
    else:
        return api_url


def main():
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    parser = argparse.ArgumentParser()
    parser.add_argument(
        "action", choices=["populate_database", "deploy_rest", "demo_rest"]
    )
    args = parser.parse_args()

    print("-" * 88)
    print("Welcome to the Amazon Relational Database Service (Amazon RDS) demo.")
    print("-" * 88)

    if args.action == "deploy_database":
        print("Deploying the serverless database and supporting resources.")
        rds_client = boto3.client('rds')
        secrets_client = boto3.client('secretsmanager')
        create_resources(
            cluster_name=config["cluster"]["cluster_name"],
            db_name=config["db_name"],
            admin_name=config["admin_name"],
            admin_password=config["admin_password"],
            rds_client=rds_client,
            secret_name=config["secret_name"],
            secrets_client=secrets_client,
        )
        print("Next, run 'python library_demo.py deploy_rest' to deploy the REST API.")
    elif args.action == "populate_database":
        print("Populating serverless database cluster with data.")
        do_populate_database(config["cluster"], config["db_name"], config["secret"])
        print("Next, run 'python library_demo.py deploy_rest' to deploy the REST API.")
    elif args.action == "deploy_rest":
        print("Deploying the REST API components.")
        api_url = do_deploy_rest(config["cluster"]["cluster_name"])
        print(
            f"Next, send HTTP requests to {api_url} or run "
            f"'python library_demo.py demo_rest' "
            f"to see a demonstration of how to call the REST API by using the "
            f"Requests package."
        )
    elif args.action == "demo_rest":
        print("Demonstrating how to call the REST API by using the Requests package.")
        try:
            do_rest_demo(config["cluster"]["cluster_name"])
        except TimeoutError as err:
            print(err)
        else:
            print(
                "Next, give it a try yourself or run 'python library_demo.py cleanup' "
                "to delete all demo resources."
            )
    print("-" * 88)


if __name__ == "__main__":
    main()
