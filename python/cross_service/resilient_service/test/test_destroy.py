# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError, WaiterError
import pytest

from auto_scaler import AutoScalerError
from load_balancer import LoadBalancerError
from recommendation_service import RecommendationServiceError


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.scenario_args = []
        self.scenario_out = {}
        answers = ["y"]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                self.scenario_data.elb.stubber.stub_describe_load_balancers,
                [self.scenario_data.lb_name],
                arns=[self.scenario_data.lb_arn],
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_delete_load_balancer,
                self.scenario_data.lb_arn,
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_describe_load_balancers,
                [self.scenario_data.lb_name],
                error_code="LoadBalancerNotFound",
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_describe_target_groups,
                [self.scenario_data.tg_name],
                [self.scenario_data.tg_arn],
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_delete_target_group,
                self.scenario_data.tg_arn,
            )
            runner.add(
                self.scenario_data.auto_scaling.stubber.stub_describe_auto_scaling_groups,
                [self.scenario_data.asg_name],
                [self.scenario_data.asg_group],
            )
            runner.add(
                self.scenario_data.auto_scaling.stubber.stub_update_auto_scaling_group,
                self.scenario_data.asg_name,
                0,
            )
            runner.add(
                self.scenario_data.auto_scaling.stubber.stub_terminate_instance_in_auto_scaling_group,
                self.scenario_data.instance["InstanceId"],
                True,
                None,
            )
            runner.add(
                self.scenario_data.auto_scaling.stubber.stub_delete_auto_scaling_group,
                self.scenario_data.asg_name,
            )
            runner.add(
                self.scenario_data.ec2.stubber.stub_delete_launch_template,
                self.scenario_data.lt_name,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_remove_role_from_instance_profile,
                self.scenario_data.profile_name,
                self.scenario_data.role_name,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_delete_instance_profile,
                self.scenario_data.profile_name,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_list_attached_role_policies,
                self.scenario_data.role_name,
                {"1": self.scenario_data.policy_arn},
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_detach_role_policy,
                self.scenario_data.role_name,
                self.scenario_data.policy_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_delete_policy,
                self.scenario_data.policy_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_delete_role,
                self.scenario_data.role_name,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_remove_role_from_instance_profile,
                self.scenario_data.bad_prof_name,
                self.scenario_data.bad_role_name,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_delete_instance_profile,
                self.scenario_data.bad_prof_name,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_list_attached_role_policies,
                self.scenario_data.bad_role_name,
                {"1": self.scenario_data.bad_policy_arn},
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_detach_role_policy,
                self.scenario_data.bad_role_name,
                self.scenario_data.bad_policy_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_delete_policy,
                self.scenario_data.bad_policy_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_delete_role,
                self.scenario_data.bad_role_name,
            )
            runner.add(
                self.scenario_data.ddb.stubber.stub_delete_table,
                self.scenario_data.table_name,
            )
            runner.add(
                self.scenario_data.ddb.stubber.stub_describe_table,
                self.scenario_data.table_name,
                error_code="ResourceNotFoundException",
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_destroy(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None)

    mock_mgr.scenario_data.scenario.destroy()


@pytest.mark.parametrize(
    "error, stub_name, stop_on_index",
    [
        (LoadBalancerError, "stub_describe_load_balancers", 0),
        (LoadBalancerError, "stub_delete_load_balancer", 1),
        (WaiterError, "stub_describe_load_balancers", 2),
        (LoadBalancerError, "stub_describe_target_groups", 3),
        (LoadBalancerError, "stub_delete_target_group", 4),
        (AutoScalerError, "stub_describe_auto_scaling_groups", 5),
        (AutoScalerError, "stub_update_auto_scaling_group", 6),
        (AutoScalerError, "stub_terminate_instance_in_auto_scaling_group", 7),
        (AutoScalerError, "stub_delete_auto_scaling_group", 8),
        (AutoScalerError, "stub_delete_launch_template", 9),
        (AutoScalerError, "stub_remove_role_from_instance_profile", 10),
        (AutoScalerError, "stub_delete_instance_profile", 11),
        (AutoScalerError, "stub_list_attached_role_policies", 12),
        (AutoScalerError, "stub_detach_role_policy", 13),
        (AutoScalerError, "stub_delete_policy", 14),
        (AutoScalerError, "stub_delete_role", 15),
        (AutoScalerError, "stub_remove_role_from_instance_profile", 16),
        (AutoScalerError, "stub_delete_instance_profile", 17),
        (AutoScalerError, "stub_list_attached_role_policies", 18),
        (AutoScalerError, "stub_detach_role_policy", 19),
        (AutoScalerError, "stub_delete_policy", 20),
        (AutoScalerError, "stub_delete_role", 21),
        (RecommendationServiceError, "stub_delete_table", 22),
        (WaiterError, "stub_describe_table", 23),
    ],
)
def test_destroy_error(mock_mgr, caplog, error, stub_name, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index)

    with pytest.raises(error):
        mock_mgr.scenario_data.scenario.destroy()
