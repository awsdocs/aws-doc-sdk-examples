# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Elastic Load Balancing v2 (ELB) unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from test_tools.example_stubber import ExampleStubber


class ELBv2Stubber(ExampleStubber):
    """
    A class that implements stub functions used by ELB v2 unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 ELB v2 client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_target_group(
        self, tg_name, protocol, port, vpc_id, healthcheck, tg_arn, error_code=None
    ):
        expected_params = {
            "Name": tg_name,
            "Protocol": protocol,
            "Port": port,
            "VpcId": vpc_id,
            "HealthCheckPath": healthcheck["path"],
            "HealthCheckIntervalSeconds": healthcheck["interval"],
            "HealthCheckTimeoutSeconds": healthcheck["timeout"],
            "HealthyThresholdCount": healthcheck["thresh_healthy"],
            "UnhealthyThresholdCount": healthcheck["thresh_unhealthy"],
        }
        response = {
            "TargetGroups": [
                {
                    "TargetGroupName": tg_name,
                    "TargetGroupArn": tg_arn,
                    "Protocol": protocol,
                    "Port": port,
                }
            ]
        }
        self._stub_bifurcator(
            "create_target_group", expected_params, response, error_code=error_code
        )

    def stub_describe_target_groups(self, tg_names, tg_arns, error_code=None):
        expected_params = {"Names": tg_names}
        response = {"TargetGroups": [{"TargetGroupArn": tg_arn} for tg_arn in tg_arns]}
        self._stub_bifurcator(
            "describe_target_groups", expected_params, response, error_code=error_code
        )

    def stub_create_load_balancer(
        self, lb_name, subnet_ids, protocol, port, lb_arn, lb_dns_name, error_code=None
    ):
        expected_params = {"Name": lb_name, "Subnets": subnet_ids}
        response = {
            "LoadBalancers": [{"LoadBalancerArn": lb_arn, "DNSName": lb_dns_name}]
        }
        self._stub_bifurcator(
            "create_load_balancer", expected_params, response, error_code=error_code
        )

    def stub_describe_load_balancers(
        self, names, dns_names=None, arns=None, error_code=None
    ):
        expected_params = {"Names": names}
        response = {"LoadBalancers": [{"State": {"Code": "active"}}]}
        if dns_names is not None:
            for index, dns_name in enumerate(dns_names):
                response["LoadBalancers"][index]["DNSName"] = dns_name
        if arns is not None:
            for index, arn in enumerate(arns):
                response["LoadBalancers"][index]["LoadBalancerArn"] = arn
        self._stub_bifurcator(
            "describe_load_balancers", expected_params, response, error_code=error_code
        )

    def stub_delete_load_balancer(self, arn, error_code=None):
        expected_params = {"LoadBalancerArn": arn}
        response = {}
        self._stub_bifurcator(
            "delete_load_balancer", expected_params, response, error_code=error_code
        )

    def stub_create_listener(self, lb_arn, protocol, port, tg_arn, error_code=None):
        expected_params = {
            "LoadBalancerArn": lb_arn,
            "Protocol": protocol,
            "Port": port,
            "DefaultActions": [{"Type": "forward", "TargetGroupArn": tg_arn}],
        }
        response = {}
        self._stub_bifurcator(
            "create_listener", expected_params, response, error_code=error_code
        )

    def stub_describe_target_health(self, tg_arn, tg_descs, error_code=None):
        expected_params = {"TargetGroupArn": tg_arn}
        response = {
            "TargetHealthDescriptions": [
                {
                    "TargetHealth": {
                        "State": desc["state"],
                        "Reason": desc["reason"],
                        "Description": desc["desc"],
                    },
                    "Target": {"Id": desc["id"], "Port": desc["port"]},
                }
                for desc in tg_descs
            ]
        }
        self._stub_bifurcator(
            "describe_target_health", expected_params, response, error_code=error_code
        )

    def stub_delete_target_group(self, tg_arn, error_code=None):
        expected_params = {"TargetGroupArn": tg_arn}
        response = {}
        self._stub_bifurcator(
            "delete_target_group", expected_params, response, error_code=error_code
        )
