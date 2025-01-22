# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import unittest
from itertools import cycle
from unittest.mock import patch

import boto3
import pytest
from auto_scaler import AutoScalingWrapper
from botocore.exceptions import ClientError
from load_balancer import ElasticLoadBalancerWrapper
from parameters import ParameterHelper
from recommendation_service import RecommendationService
from runner import Runner


@pytest.fixture(autouse=True)
def disable_capture(pytestconfig):
    pytestconfig.option.capture = "no"


class TestRunnerIntegration(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        # Set up AWS clients
        cls.ddb_client = boto3.client("dynamodb")
        cls.elb_client = boto3.client("elbv2")
        cls.autoscaling_client = boto3.client("autoscaling")
        cls.ec2_client = boto3.client("ec2")
        cls.ssm_client = boto3.client("ssm")
        cls.iam_client = boto3.client("iam")

        # Initialize the services and runner
        cls.prefix = "test-doc-example-resilience"
        cls.resource_path = "../../../scenarios/features/resilient_service/resources"
        cls.recommendation = RecommendationService(
            "test-recommendation-service", cls.ddb_client
        )
        cls.autoscaling_wrapper = AutoScalingWrapper(
            cls.prefix,
            "t3.micro",
            "/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2",
            cls.autoscaling_client,
            cls.ec2_client,
            cls.ssm_client,
            cls.iam_client,
        )
        cls.elb_wrapper = ElasticLoadBalancerWrapper(cls.elb_client)
        cls.param_helper = ParameterHelper(
            cls.recommendation.table_name, cls.ssm_client
        )
        cls.runner = Runner(
            cls.resource_path,
            cls.recommendation,
            cls.autoscaling_wrapper,
            cls.elb_wrapper,
            cls.param_helper,
        )

    @pytest.mark.integ
    @pytest.mark.usefixtures("disable_capture")
    @patch("builtins.input", side_effect=cycle(["3"]))
    def test_deploy_resources(self, mock_input):
        try:
            self.runner.deploy()
            # Verify that resources were created
            table = self.ddb_client.describe_table(
                TableName=self.recommendation.table_name
            )
            self.assertEqual(table["Table"]["TableStatus"], "ACTIVE")

            lb = self.elb_client.describe_load_balancers(
                Names=[self.runner.load_balancer_name]
            )
            self.assertEqual(lb["LoadBalancers"][0]["State"]["Code"], "active")
        except ClientError as e:
            self.fail(f"Deployment failed with error: {e}")

    @pytest.mark.integ
    @pytest.mark.usefixtures("disable_capture")
    @patch("builtins.input", side_effect=cycle(["3"]))
    def test_service_resilience(self, mock_input):
        self.runner.deploy()
        try:
            self.runner.demo()
        except Exception as e:
            self.fail(f"Service resilience test failed with error: {e}")
        finally:
            self.runner.destroy(automation=True)

    @classmethod
    def tearDownClass(cls):
        # Clean up in case any resources were left
        try:
            cls.runner.destroy(automation=True)
        except Exception as e:
            print(f"Cleanup failed in tearDown with error: {e}")


if __name__ == "__main__":
    unittest.main()
