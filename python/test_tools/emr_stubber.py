# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon EMR unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from test_tools.example_stubber import ExampleStubber


class EmrStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon EMR unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 EMR client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_run_job_flow(
            self, name, log_uri, release, instance_type, instance_count, keep_alive,
            steps, applications, job_flow_role_name, service_role_name, security_groups,
            cluster_id, error_code=None):
        expected_params = {
            'Name': name,
            'LogUri': log_uri,
            'ReleaseLabel': release,
            'Instances': {
                'MasterInstanceType': instance_type,
                'SlaveInstanceType': instance_type,
                'InstanceCount': instance_count,
                'KeepJobFlowAliveWhenNoSteps': keep_alive,
                'EmrManagedMasterSecurityGroup': security_groups['manager'].id,
                'EmrManagedSlaveSecurityGroup': security_groups['worker'].id,
            },
            'Steps': [{
                'Name': step['name'],
                'ActionOnFailure': 'CONTINUE',
                'HadoopJarStep': {
                    'Jar': 'command-runner.jar',
                    'Args': ['spark-submit', '--deploy-mode', 'cluster',
                             step['script_uri'], *step['script_args']]
                }
            } for step in steps],
            'Applications': [{
                'Name': app
            } for app in applications],
            'JobFlowRole': job_flow_role_name,
            'ServiceRole': service_role_name,
            'EbsRootVolumeSize': 10,
            'VisibleToAllUsers': True
        }
        response = {'JobFlowId': cluster_id}
        self._stub_bifurcator(
            'run_job_flow', expected_params, response, error_code=error_code)

    def stub_describe_cluster(self, cluster_id, cluster, error_code=None):
        expected_params = {'ClusterId': cluster_id}
        response = {'Cluster': cluster}
        self._stub_bifurcator(
            'describe_cluster', expected_params, response, error_code=error_code)

    def stub_terminate_job_flows(self, cluster_ids, error_code=None):
        expected_params = {'JobFlowIds': cluster_ids}
        self._stub_bifurcator(
            'terminate_job_flows', expected_params, error_code=error_code)

    def stub_list_steps(self, cluster_id, steps, error_code=None):
        expected_params = {'ClusterId': cluster_id}
        response = {'Steps': steps}
        self._stub_bifurcator(
            'list_steps', expected_params, response, error_code=error_code)

    def stub_add_job_flow_steps(self, cluster_id, steps, step_ids, error_code=None):
        expected_params = {
            'JobFlowId': cluster_id,
            'Steps': []
        }
        for step in steps:
            if step['type'] == 'emrfs':
                expected_params['Steps'].append({
                    'Name': step['name'],
                    'ActionOnFailure': 'CONTINUE',
                    'HadoopJarStep': {
                        'Jar': 'command-runner.jar',
                        'Args': ['/usr/bin/emrfs', step['command'], step['bucket_url']]
                    }
                })
            elif step['type'] == 'spark':
                expected_params['Steps'].append({
                    'Name': step['name'],
                    'ActionOnFailure': 'CONTINUE',
                    'HadoopJarStep': {
                        'Jar': 'command-runner.jar',
                        'Args': ['spark-submit', '--deploy-mode', 'cluster',
                                 step['script_uri'], *step['script_args']]
                    }
                })
        response = {'StepIds': step_ids}
        self._stub_bifurcator(
            'add_job_flow_steps', expected_params, response, error_code=error_code)

    def stub_describe_step(self, cluster_id, step, error_code=None):
        expected_params = {'ClusterId': cluster_id, 'StepId': step['Id']}
        response = {'Step': step}
        self._stub_bifurcator(
            'describe_step', expected_params, response, error_code=error_code)

    def stub_list_instances(self, cluster_id, types, instance_ids, error_code=None):
        expected_params = {'ClusterId': cluster_id, 'InstanceGroupTypes': types}
        response = {'Instances': [{
            'Ec2InstanceId': inst_id
        } for inst_id in instance_ids]}
        self._stub_bifurcator(
            'list_instances', expected_params, response, error_code=error_code)
