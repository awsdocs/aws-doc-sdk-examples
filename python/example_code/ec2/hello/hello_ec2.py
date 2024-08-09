import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.ec2.Hello]
def hello_ec2(ec2_client):
    """
    Use the AWS SDK for Python (Boto3) to list the security groups in your account.
    This example uses the default settings specified in your shared credentials
    and config files.

    :param ec2_client: A Boto3 EC2 client. This client provides low-level
                       access to AWS EC2 services.
    """
    # Use the Client class instead of Resource to interact with the SDK
    # The Client class provides more granular control and flexibility,
    # which is better suited for this use case.
    try:
        # Use the paginator to handle pagination of the list_security_groups operation
        paginator = ec2_client.get_paginator("describe_security_groups")
        response_iterator = paginator.paginate(MaxResults=10)
        print("Hello, Amazon EC2! Let's list up to 10 of your security groups:")
        for page in response_iterator:
            for sg in page["SecurityGroups"]:
                print(f"\t{sg['GroupId']}: {sg['GroupName']}")
    except ClientError as err:
        # Catch and handle any specific errors that may occur, such as AccessDeniedException
        if err.response["Error"]["Code"] == "AccessDeniedException":
            logger.error("You do not have permission to list security groups.")
        else:
            logger.error(
                "Couldn't list security groups. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
        raise err

if __name__ == "__main__":
    hello_ec2(boto3.client("ec2"))
# snippet-end:[python.example_code.ec2.Hello]