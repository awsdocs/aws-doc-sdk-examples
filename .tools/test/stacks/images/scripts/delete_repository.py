# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import logging
import sys

import boto3


def delete_selected_public_ecr_repos(delete_images=False):
    """
    Function to delete selected public ECR repositories and optionally their images.
    """
    # Initialize logging
    logging.basicConfig(level=logging.INFO)

    # Create an ECR public client
    client = boto3.client("ecr")

    # Paginate through all public repositories
    paginator = client.get_paginator("describe_repositories")
    for page in paginator.paginate():
        for repo in page["repositories"]:
            repo_name = repo["repositoryName"]
            logging.info(f"Found repository {repo_name}")

            # If the script is run with the argument "delete_images", delete images in the repository
            if delete_images:
                try:
                    # Get a list of image details for the repository
                    images = client.describe_images(repositoryName=repo_name)[
                        "imageDetails"
                    ]
                    # Iterate through images and delete them
                    for image in images:
                        image_digest = image["imageDigest"]
                        client.batch_delete_image(
                            repositoryName=repo_name,
                            imageIds=[{"imageDigest": image_digest}],
                        )
                        logging.info(
                            f"Deleted image {image_digest} from repository {repo_name}"
                        )
                except Exception as e:
                    logging.error(
                        f"Failed to delete images from repository {repo_name}: {e}"
                    )

            # Check if 'examples' is in the repository name
            if "examples" not in repo_name:
                try:
                    # Attempt to delete the repository
                    response = client.delete_repository(repositoryName=repo_name)
                    logging.info(f"Deleted repository {repo_name}: {response}")
                except Exception as e:
                    logging.error(f"Failed to delete repository {repo_name}: {e}")


if __name__ == "__main__":
    # Check if the script is run with the argument "delete_images"
    if len(sys.argv) > 1 and sys.argv[1] == "delete_images":
        delete_selected_public_ecr_repos(delete_images=True)
    else:
        delete_selected_public_ecr_repos()
