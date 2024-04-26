# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import pytest
import os
from botocore.config import Config

from redshift import RedshiftWrapper
from redshift_data import RedshiftDataWrapper
from redshift_scenario import RedshiftScenario


@pytest.fixture
def mock_wait(monkeypatch):
    return


@pytest.mark.skip(
    reason="Skip until shared resources are part of the Docker environment."
)
@pytest.mark.integ
def test_run_get_started_redshift_integ(input_mocker, capsys):
    my_config = Config(
        region_name="us-east-1",
    )

    redshift_client = boto3.client("redshift", config=my_config)
    redshift_data_client = boto3.client("redshift-data", config=my_config)
    redshift_wrapper = RedshiftWrapper(redshift_client)
    redshift_data_wrapper = RedshiftDataWrapper(redshift_data_client)
    redshift_scenario = RedshiftScenario(redshift_wrapper, redshift_data_wrapper)

    input_mocker.mock_answers(
        [
            "testuser",  # Username.
            "testUser1000",  # User password.
            "test-workflow-movies",  # Cluster name.
            "",  # Press Enter to continue...
            "",  # Press Enter to continue...
            "",  # Press Enter to continue...
            "",  # Press Enter to continue...
            "51",  # Please enter a value between 50 and 200.
            "2014",  # Query the Movies table by year. Enter a value between 2012-2014
            "",  # Press Enter to continue...
            "y",  # Do you want to delete the cluster? (y/n)
            "",  # Press Enter to continue..
        ]
    )
    redshift_scenario.redhift_scenario(
        f"{os.path.dirname(__file__)}/../../../../resources/sample_files/movies.json"
    )

    capt, err = capsys.readouterr()

    assert "This concludes the Amazon Redshift SDK Getting Started scenario." in capt
