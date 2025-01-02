# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for prepare_application in ecr_getting_started.py.
"""

import pytest
from botocore.exceptions import ClientError
import ecr_getting_started
import json
import base64


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        ecr_getting_started.use_press_enter_to_continue = False
        self.role_arn = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
        self.repository_name = "ecr-basics"
        self.tag = "echo-text"
        self.repository_uri = (
            f"123456789012.dkr.ecr.us-east-1.amazonaws.com/{self.repository_name}"
        )
        self.policy_str = json.dumps(
            {
                "Version": "2008-10-17",
                "Statement": [
                    {
                        "Sid": "AllowDownload",
                        "Effect": "Allow",
                        "Principal": {"AWS": self.role_arn},
                        "Action": ["ecr:BatchGetImage"],
                    }
                ],
            }
        )
        self.user = "AWS"
        self.password = "password"
        self.authorization_token = base64.b64encode(
            f"{self.user}:{self.password}".encode("utf-8")
        ).decode("utf-8")
        self.repositories = [
            {
                "registryId": "012345678910",
                "repositoryName": self.repository_name,
                "repositoryArn": "arn:aws:ecr:us-west-2:012345678910:repository/ubuntu",
                "repositoryUri": self.repository_uri,
            }
        ]
        self.lifecycle_policy = json.dumps(
            {
                "rules": [
                    {
                        "rulePriority": 1,
                        "description": "Expire images older than 14 days",
                        "selection": {
                            "tagStatus": "any",
                            "countType": "sinceImagePushed",
                            "countUnit": "days",
                            "countNumber": 14,
                        },
                        "action": {"type": "expire"},
                    }
                ]
            }
        )
        self.image_details = [
            {
                "registryId": "012345678910",
                "repositoryName": self.repository_name,
                "imageDigest": "sha256:4a1c6567c38904384ebc64e35b7eeddd8451110c299e3368d2210066487d97e5",
                "imageTags": [self.tag],
                "imageSizeInBytes": 48318255,
                "imagePushedAt": 1565128275.0,
            }
        ]

        scenario_data.mock_docker_client.set_mocking_values(
            self.repository_uri, self.tag, "docker_files", self.user, self.password
        )

        answers = ["y", "y", "y"]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(
        self,
        error,
        stop_on,
        ecr_stubber,
    ):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                ecr_stubber.stub_create_repository,
                self.repository_name,
            )
            runner.add(
                ecr_stubber.stub_set_repository_policy,
                self.repository_name,
                self.policy_str,
            )
            runner.add(
                ecr_stubber.stub_get_repository_policy,
                self.repository_name,
                self.policy_str,
            )
            runner.add(
                ecr_stubber.stub_get_authorization_token,
                self.authorization_token,
            )
            runner.add(
                ecr_stubber.stub_describe_repositories,
                [self.repository_name],
                self.repositories,
            )
            runner.add(
                ecr_stubber.stub_put_lifecycle_policy,
                self.repository_name,
                self.lifecycle_policy,
            )
            runner.add(
                ecr_stubber.stub_describe_images,
                self.repository_name,
                [self.tag],
                self.image_details,
            )
            runner.add(
                ecr_stubber.stub_delete_repository,
                self.repository_name,
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.integ
def test_ecr_getting_started(mock_mgr):
    mock_mgr.setup_stubs(
        None,
        None,
        mock_mgr.scenario_data.ecr_stubber,
    )

    mock_mgr.scenario_data.scenario.run(mock_mgr.role_arn)


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_create_repository", 0),
        ("TESTERROR-stub_set_repository_policy", 1),
        ("TESTERROR-stub_get_repository_policy", 2),
        ("TESTERROR-stub_get_authorization_token", 3),
        ("TESTERROR-stub_describe_repositories", 4),
        ("TESTERROR-stub_put_lifecycle_policy", 5),
        ("TESTERROR-stub_describe_images", 6),
        ("TESTERROR-stub_delete_repository", 7),
    ],
)
@pytest.mark.integ
def test_ecr_getting_started_error(mock_mgr, error, stop_on_index):
    mock_mgr.setup_stubs(
        error,
        stop_on_index,
        mock_mgr.scenario_data.ecr_stubber,
    )

    with pytest.raises(ClientError):
        mock_mgr.scenario_data.scenario.run(mock_mgr.role_arn)
