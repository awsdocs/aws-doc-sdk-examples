# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys

import boto3
import pytest
import os


script_dir = os.path.dirname(os.path.abspath(__file__))

sys.path.append(script_dir)
import ecr_getting_started
from ecr_wrapper import ECRWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../.."))

from test_tools.fixtures.common import *


class MockDockerImage:
    def __init__(self):
        self.repository = None
        self.image_tag = (None,)
        self.path = None

    def build(self, path: str, tag: str) -> tuple["MockDockerImage", None]:
        assert path == self.path, "MockDockerImage.build"
        assert tag == f"{self.repository}:{self.image_tag}", "MockDockerImage.build"
        return self, None

    def remove(self, tag: str) -> None:
        assert tag == f"{self.repository}:{self.image_tag}", "MockDockerImage.remove"
        pass


class MockDockerAPI:
    def __init__(self):
        self.repository = None
        self.tag = (None,)
        self.username = None
        self.password = None

    def push(
        self,
        repository: str,
        auth_config: dict[str, str],
        tag: str,
        stream: bool,
        decode: bool,
    ) -> list[str]:
        assert repository == self.repository, "MockDockerAPI.push"
        assert auth_config["username"] == self.username, "MockDockerAPI.push"
        assert auth_config["password"] == self.password, "MockDockerAPI.push"
        assert tag == self.tag, "MockDockerAPI.push"
        assert stream == True, "MockDockerAPI.push"
        assert decode == True, "MockDockerAPI.push"
        return ["pushing", "push complete"]


class MockDockerClient:
    def __init__(self):
        self.images = MockDockerImage()
        self.api = MockDockerAPI()

    def set_mocking_values(self, repository, tag, path, username, password):
        self.images.repository = repository
        self.images.image_tag = tag
        self.images.path = path
        self.api.repository = repository
        self.api.tag = tag
        self.api.username = username
        self.api.password = password


class ScenarioData:
    def __init__(
        self,
        ecr_client,
        mock_docker_client,
        ecr_stubber,
    ):
        self.ecr_client = ecr_client
        self.mock_docker_client = mock_docker_client
        self.ecr_stubber = ecr_stubber
        self.scenario = ecr_getting_started.ECRGettingStarted(
            ecr_wrapper=ECRWrapper(self.ecr_client),
            docker_client=self.mock_docker_client,
        )


@pytest.fixture
def scenario_data(make_stubber):
    ecr_client = boto3.client("ecr")
    mock_docker_client = MockDockerClient()
    ecr_stubber = make_stubber(ecr_client)
    return ScenarioData(
        ecr_client,
        mock_docker_client,
        ecr_stubber,
    )
