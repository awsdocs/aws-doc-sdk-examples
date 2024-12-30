# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Elastic Container Registry (Amazon ECR)
unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class EcrStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    ECR unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 ECR client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_repository(self, repository_name, error_code=None):
        expected_params = {"repositoryName": repository_name}
        response = {
            "repository": {
                "repositoryArn": f"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
                "registryId": "XXXXXXXXXXXX",
                "repositoryName": repository_name,
                "repositoryUri": f"123456789012.dkr.ecr.us-east-1.amazonaws.com/{repository_name}",
                "createdAt": "2022-01-01 00:00:00.000000000",
                "imageTagMutability": "MUTABLE",
                "imageScanningConfiguration": {"scanOnPush": False},
            }
        }
        self._stub_bifurcator(
            "create_repository", expected_params, response, error_code=error_code
        )

    def stub_set_repository_policy(self, repository_name, policy, error_code=None):
        expected_params = {
            "repositoryName": repository_name,
            "policyText": policy,
        }
        response = {}
        self._stub_bifurcator(
            "set_repository_policy", expected_params, response, error_code=error_code
        )

    def stub_get_repository_policy(self, repository_name, policy, error_code=None):
        expected_params = {"repositoryName": repository_name}
        response = {"policyText": policy}
        self._stub_bifurcator(
            "get_repository_policy", expected_params, response, error_code=error_code
        )

    def stub_get_authorization_token(self, authorization_token, error_code=None):
        expected_params = {}
        response = {
            "authorizationData": [
                {
                    "authorizationToken": authorization_token,
                    "proxyEndpoint": "https://123456789012.dkr.ecr.us-east-1.amazonaws.com",
                }
            ]
        }
        self._stub_bifurcator(
            "get_authorization_token", expected_params, response, error_code=error_code
        )

    def stub_describe_repositories(
        self, repository_names, repositories, error_code=None
    ):
        expected_params = {"repositoryNames": repository_names}
        response = {"repositories": repositories}
        self._stub_bifurcator(
            "describe_repositories", expected_params, response, error_code=error_code
        )

    def stub_put_lifecycle_policy(self, repository_name, policy, error_code=None):
        expected_params = {
            "repositoryName": repository_name,
            "lifecyclePolicyText": policy,
        }
        response = {}
        self._stub_bifurcator(
            "put_lifecycle_policy", expected_params, response, error_code=error_code
        )

    def stub_describe_images(self, repository_name, image_ids, images, error_code=None):
        expected_params = {"repositoryName": repository_name}
        if image_ids is not None:
            expected_params["imageIds"] = [{"imageTag": tag} for tag in image_ids]
        response = {"imageDetails": images}
        self._stub_bifurcator(
            "describe_images", expected_params, response, error_code=error_code
        )

    def stub_delete_repository(self, repository_name, error_code=None):
        expected_params = {"repositoryName": repository_name, "force": True}
        response = {
            "repository": {
                "registryId": "123456789012",
                "repositoryName": "ubuntu",
                "repositoryArn": "arn:aws:ecr:us-west-2:123456789012:repository/ubuntu",
            }
        }
        self._stub_bifurcator(
            "delete_repository", expected_params, response, error_code=error_code
        )
