# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Elastic Container Registry (Amazon ECR) to perform
basic operations.

This Boto3 code example requires an IAM Role that has permissions to interact with the Amazon ECR service.

To create an IAM role, see:

https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html


"""

import logging
import sys
import os
import docker
import argparse
import json
import base64

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative path to include ecrWrapper.
sys.path.append(os.path.dirname(script_dir))
from ecr_wrapper import ECRWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../.."))
import demo_tools.question as q

logger = logging.getLogger(__name__)

no_art = False  # 'no_art' suppresses 'art' to improve accessibility.


def print_dashes():
    """
    Print a line of dashes to separate sections of the output.
    """
    if not no_art:
        print("-" * 80)


use_press_enter_to_continue = False


def press_enter_to_continue():
    if use_press_enter_to_continue:
        q.ask("Press Enter to continue...")


# snippet-start:[python.example_code.ecr.FeatureScenario]
class ECRGettingStarted:
    """
    A scenario that demonstrates how to use Boto3 to perform basic operations using
    Amazon ECR.
    """

    def __init__(
        self,
        ecr_wrapper: ECRWrapper,
            docker_client: docker.DockerClient,
    ):
        self.ecr_wrapper = ecr_wrapper
        self.docker_client = docker_client
        self.docker_image_tag = "echo-text"
        self.repo_name = "echo-text"
        self.docker_image = None

    def run(self, role_arn: str) -> None:
        """
        Runs the scenario.
        """
        print("""
The Amazon Elastic Container Registry (ECR) is a fully-managed Docker container registry
service provided by AWS. It allows developers and organizations to securely
store, manage, and deploy Docker container images.
ECR provides a simple and scalable way to manage container images throughout their lifecycle,
from building and testing to production deployment.

The `ECRWrapper' class is a wrapper for the Boto3 'ecr' client. The 'ecr' client provides a set of methods to
programmatically interact with the Amazon ECR service. This allows developers to
automate the storage, retrieval, and management of container images as part of their application
deployment pipelines. With ECR, teams can focus on building and deploying their
applications without having to worry about the underlying infrastructure required to
host and manage a container registry.

This scenario walks you through how to perform key operations for this service.
Let's get started...
        """)
        press_enter_to_continue()
        print_dashes()
        print(f"""
* Create an ECR repository.

The first task is to create a local Docker image named echo-text if it does not already exist.
A Python Docker client is used to execute Docker commands.
You must have Docker installed and running.
A Docker image named "{self.docker_image_tag}" will be created if it does not already exist.
        """)

        self.docker_image = None

        try:
            self.docker_image = docker_client.images.get(self.docker_image_tag)
        except docker.errors.ImageNotFound:
            pass

        if self.docker_image is not None:
            print(f"The docker image with tag '{self.docker_image_tag}' already exists.")
        else:
            print(f"Building a docker image from 'docker_files/Dockerfile' with tag '{self.docker_image_tag}")
            self.docker_image = docker_client.images.build(path = "docker_files", tag = self.docker_image_tag)

        print("""
An ECR repository is a private Docker container repository provided
by Amazon Web Services (AWS). It is a managed service that makes it easy
to store, manage, and deploy Docker container images.
        """)
        print(f"Creating a repository named {self.repo_name}")
        self.repo_arn = ecr_wrapper.create_repository(self.repo_name)
        print(f"The ARN of the ECR repository is {self.repo_arn}")
        press_enter_to_continue()
        print_dashes()

        if role_arn is None:
            print("""
* Because an IAM role ARN was not provided, a role policy will not be set for this repository.
            """)
        else :
            print("""
* Set an ECR repository policy.

Setting an ECR repository policy using the `setRepositoryPolicy` function is crucial for maintaining
the security and integrity of your container images. The repository policy allows you to
define specific rules and restrictions for accessing and managing the images stored within your ECR
repository.
        """)
            self.grant_role_download_access(role_arn)
            press_enter_to_continue()
            print_dashes()

            print("""
* Display ECR repository policy.

Now we will retrieve the ECR policy to ensure it was successfully set.
            """)
            policyText = ecr_wrapper.get_repository_policy(self.repo_name)
            print("Policy Text:")
            print(f"{policyText}")
            press_enter_to_continue()
            print_dashes()

        print("""
* Retrieve an ECR authorization token.

You need an authorization token to securely access and interact with the Amazon ECR registry.
The `getAuthorizationToken` method of the `EcrAsyncClient` is responsible for securely accessing
and interacting with an Amazon ECR repository. This operation is responsible for obtaining a
valid authorization token, which is required to authenticate your requests to the ECR service.

Without a valid authorization token, you would not be able to perform any operations on the
ECR repository, such as pushing, pulling, or managing your Docker images.
        """)
        authorization_token = ecr_wrapper.get_authorization_token()
        press_enter_to_continue()
        print_dashes()
        print("""
* Get the ECR Repository URI.

The URI  of an Amazon ECR repository is important. When you want to deploy a container image to
a container orchestration platform like Amazon Elastic Kubernetes Service (EKS)
or Amazon Elastic Container Service (ECS), you need to specify the full image URI,
which includes the ECR repository URI. This allows the container runtime to pull the
correct container image from the ECR repository.
        """)
        repository_descriptions =  ecr_wrapper.describe_repositories([self.repo_name])
        repository_uri = repository_descriptions[0]["repositoryUri"]
        print(f"Repository URI found: {repository_uri}")
        press_enter_to_continue()
        print_dashes()


        print("""
* Set an ECR Lifecycle Policy.

An ECR Lifecycle Policy is used to manage the lifecycle of Docker images stored in your ECR repositories.
These policies allow you to automatically remove old or unused Docker images from your repositories,
freeing up storage space and reducing costs.

This example policy helps to maintain the size and efficiency of the container registry
by automatically removing older and potentially unused images, ensuring that the
storage is optimized and the registry remains up-to-date.
            """)
        press_enter_to_continue()
        print_dashes()

        print("""
* Push a docker image to the Amazon ECR Repository.

The method uses the authorization token to create an `AuthConfig` object, which is used to authenticate
the Docker client when pushing the image. Finally, the method tags the Docker image with the specified
repository name and image tag, and then pushes the image to the ECR repository using the Docker client.
        """)
        decoded_authorization = base64.b64decode(authorization_token).decode("utf-8")
        username, password = decoded_authorization.split(":")
        resp = self.docker_client.api.push(
            repository = repository_uri,
            auth_config= {"username": username, "password": password},
            tag=self.docker_image_tag,
            stream=True,
            decode=True,
        )
        for line in resp:
            print(line)

        print_dashes()

        if False:
            print("* Verify if the image is in the ECR Repository.")
            print_dashes()
            print("* As an optional step, you can interact with the image in Amazon ECR by using the CLI.")
            print("Would you like to view instructions on how to use the CLI to run the image? (y/n)")
            print(f"{instructions}")
            print_dashes()
            print("* Delete the ECR Repository.")
            print("")
            print("Would you like to delete the Amazon ECR Repository? (y/n)")
            print("You selected to delete the AWS ECR resources.")
            print_dashes()
            print("This concludes the Amazon ECR SDK scenario")
            print_dashes()
            print("")
            print("Enter 'c' followed by <ENTER> to continue:")
            print("Continuing with the program...")
            print("")
            print("Invalid input. Please try again.")

        self.cleanup()

    def cleanup(self):
        """
        Deletes the resources created in this scenario.
        """

        if self.repo_arn is not None:
            print("Deleting the ECR repository.")
            ecr_wrapper.delete_repository(self.repo_name)

        if self.docker_image is not None:
            print("Deleting the docker image.")
            self.docker_client.images.remove(self.docker_image_tag)

    def grant_role_download_access(self, role_arn: str):
        """
        Grants the specified role access to download images from the ECR repository.

        :param role_arn: The ARN of the role to grant access to.
        """
        policy_json = {
    "Version": "2008-10-17",
    "Statement": [
        {
            "Sid": "AllowDownload",
            "Effect": "Allow",
            "Principal": {
                "AWS": role_arn
            },
            "Action": [
                "ecr:BatchGetImage"
            ]
        }
    ]
}

        ecr_wrapper.set_repository_policy(self.repo_name, json.dumps(policy_json))

    def put_expiration_policy(self):
        """
        Puts an expiration policy on the ECR repository.
        """
        policy_json =            {
             "rules": [
                 {
                     "rulePriority": 1,
                     "description": "Expire images older than 14 days",
                     "selection": {
                         "tagStatus": "any",
                         "countType": "sinceImagePushed",
                         "countUnit": "days",
                         "countNumber": 14
                     },
                     "action": {
                         "type": "expire"
                     }
                 }
            ]
            }

        ecr_wrapper.put_lifecycle_policy(self.repo_name, json.dumps(policy_json))

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Run Amazon ECR getting started scenario."
    )
    parser.add_argument(
        "--iam-role-arn",
        type=str,
        default=None,
        help="an optional IAM role ARN that will be granted access to download images from a repository.",
        required=False
    )
    parser.add_argument(
        "--no-art",
        action="store_true",
        help="accessibility setting that suppresses art in the console output.",
    )
    args = parser.parse_args()
    no_art = args.no_art
    iam_role_arn = args.iam_role_arn
    demo = None
    docker_client = None
    try:
        docker_client = docker.from_env()
        if not docker_client.ping():
            raise docker.errors.DockerException("Docker is not running.")
    except docker.errors.DockerException as err:
        logging.error("""
        The Python Docker could not be created. 
        Do you have Docker installed and running?
        Here is the error message:
        %s
        """, err)
        sys.exit("Error with Docker.")
    try:
        ecr_wrapper = ECRWrapper.from_client()
        demo = ECRGettingStarted(
            ecr_wrapper,
            docker_client
        )
        demo.run(iam_role_arn)


    except Exception as exception:
        logging.exception("Something went wrong with the demo!")
        if demo is not None:
            demo.cleanup()

# snippet-end:[python.example_code.ecr.FeatureScenario]