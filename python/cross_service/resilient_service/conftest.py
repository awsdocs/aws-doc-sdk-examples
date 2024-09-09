# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

from datetime import datetime

import boto3
import pytest

import runner
from auto_scaler import AutoScalingWrapper
from load_balancer import ElasticLoadBalancerWrapper
from parameters import ParameterHelper
from recommendation_service import RecommendationService


class ScenarioData:
    def __init__(self, auto_scaling, elb, ddb, ec2, ssm, iam):
        self.auto_scaling = auto_scaling
        self.elb = elb
        self.ddb = ddb
        self.ec2 = ec2
        self.ssm = ssm
        self.iam = iam

        self.test_resource_path = "test/resources"
        self.table_name = "doc-example-test-rec-table"
        self.resource_prefix = "doc-example-test-resilience"
        self.policy_arn = "arn:aws:iam:us-west-2:123456789012:policy/test-policy"
        self.role_name = f"{self.resource_prefix}-role"
        self.profile_name = f"{self.resource_prefix}-prof"
        self.lt_name = f"{self.resource_prefix}-template"
        self.inst_type = "test-inst-type"
        self.ami_param = "test-ami-param"
        self.instance = {
            "InstanceId": "test-instance-id",
            "AvailabilityZone": "test-zone",
            "LifecycleState": "active",
            "HealthStatus": "healthy",
            "ProtectedFromScaleIn": True,
        }
        self.asg_name = f"{self.resource_prefix}-group"
        self.asg_group = {
            "AutoScalingGroupName": self.asg_name,
            "MinSize": 3,
            "MaxSize": 3,
            "DesiredCapacity": 0,
            "DefaultCooldown": 0,
            "AvailabilityZones": ["test-zone"],
            "HealthCheckType": "EC2",
            "CreatedTime": datetime.now(),
            "Instances": [self.instance],
        }
        self.tg_name = f"{self.resource_prefix}-tg"
        self.tg_arn = "arn:aws:elasticloadbalancing:test-region:123456789012:targetgroup/test-group"
        self.lb_name = f"{self.resource_prefix}-lb"
        self.lb_arn = (
            "arn:aws:elasticloadbalancing:test-region:123456789012:loadbalancer/test-lb"
        )
        self.bad_policy_arn = (
            "arn:aws:iam:us-west-2:123456789012:policy/test-bad-policy"
        )
        self.bad_role_name = f"{self.resource_prefix}-bc-role"
        self.bad_prof_name = f"{self.resource_prefix}-bc-prof"
        self.scenario = runner.Runner(
            self.test_resource_path,
            RecommendationService(self.table_name, self.ddb.client),
            AutoScalingWrapper(
                self.resource_prefix,
                self.inst_type,
                self.ami_param,
                self.auto_scaling.client,
                self.ec2.client,
                self.ssm.client,
                self.iam.client,
            ),
            ElasticLoadBalancerWrapper(self.elb.client),
            ParameterHelper(self.table_name, self.ssm.client),
        )


class TestClient:
    def __init__(self, service, make_stubber):
        self.client = boto3.client(service)
        self.stubber = make_stubber(self.client)


@pytest.fixture
def scenario_data(make_stubber):
    auto_scaling = TestClient("autoscaling", make_stubber)
    elb = TestClient("elbv2", make_stubber)
    ddb = TestClient("dynamodb", make_stubber)
    ec2 = TestClient("ec2", make_stubber)
    ssm = TestClient("ssm", make_stubber)
    iam = TestClient("iam", make_stubber)
    return ScenarioData(auto_scaling, elb, ddb, ec2, ssm, iam)
