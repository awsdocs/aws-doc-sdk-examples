# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for emr_emr_usage_demo.py functions.
"""

from unittest.mock import MagicMock

import pytest
import boto3
from boto3.s3.transfer import S3UploadFailedError
from botocore.exceptions import ClientError

import emr_usage_demo


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_bucket'),
    ('TestException', 'stub_put_object'),
])
def test_setup_bucket(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = make_unique_name('bucket-')
    script_file_name = __file__
    script_key = 'test-key'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            s3_stubber, 'stub_create_bucket', bucket_name,
            s3_resource.meta.client.meta.region_name)
        runner.add(s3_stubber, 'stub_head_bucket', bucket_name)
        runner.add(s3_stubber, 'stub_put_object', bucket_name, script_key)

    if error_code is None:
        bucket = emr_usage_demo.setup_bucket(
            bucket_name, script_file_name, script_key, s3_resource)
        assert bucket.name == bucket_name
    elif stop_on_method == 'stub_put_object':
        with pytest.raises(S3UploadFailedError):
            emr_usage_demo.setup_bucket(
                bucket_name, script_file_name, script_key, s3_resource)
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_usage_demo.setup_bucket(
                bucket_name, script_file_name, script_key, s3_resource)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_list_objects'),
    ('TestException', 'stub_delete_objects'),
    ('TestException', 'stub_delete_bucket')
])
def test_delete_bucket(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket = s3_resource.Bucket(make_unique_name('bucket-'))
    obj_keys = ['test-key-1', 'test-key-2']

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(s3_stubber, 'stub_list_objects', bucket.name, object_keys=obj_keys)
        runner.add(s3_stubber, 'stub_delete_objects', bucket.name, obj_keys)
        runner.add(s3_stubber, 'stub_delete_bucket', bucket.name)

    if error_code is None:
        emr_usage_demo.delete_bucket(bucket)
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_usage_demo.delete_bucket(bucket)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    'error_code_jf,stop_on_method_jf,error_code_svc,stop_on_method_svc', [
     (None, None, None, None),
     ('TestException', 'stub_create_role', None, None),
     ('TestException', 'stub_attach_role_policy', None, None),
     ('TestException', 'stub_create_instance_profile', None, None),
     ('TestException', 'stub_add_role_to_instance_profile', None, None),
     (None, None, 'TestException', 'stub_create_role'),
     (None, None, 'TestException', 'stub_attach_role_policy'),
    ])
def test_create_roles(
        make_stubber, make_unique_name, stub_runner, error_code_jf, stop_on_method_jf,
        error_code_svc, stop_on_method_svc):
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    job_flow_role_name = make_unique_name('jfrole-')
    service_role_name = make_unique_name('srole-')

    with stub_runner(error_code_jf, stop_on_method_jf) as runner_jf:
        runner_jf.add(iam_stubber, 'stub_create_role', job_flow_role_name)
        runner_jf.add(iam_stubber, 'stub_get_role', job_flow_role_name)
        runner_jf.add(
            iam_stubber, 'stub_attach_role_policy', job_flow_role_name,
            "arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceforEC2Role")
        runner_jf.add(iam_stubber, 'stub_create_instance_profile', job_flow_role_name)
        runner_jf.add(
            iam_stubber, 'stub_add_role_to_instance_profile', job_flow_role_name,
            job_flow_role_name)

    if error_code_jf is None:
        with stub_runner(error_code_svc, stop_on_method_svc) as runner_svc:
            runner_svc.add(iam_stubber, 'stub_create_role', service_role_name)
            runner_svc.add(iam_stubber, 'stub_get_role', service_role_name)
            runner_svc.add(
                iam_stubber, 'stub_attach_role_policy', service_role_name,
                'arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceRole')

    if error_code_jf is None and error_code_svc is None:
        job_flow_role, service_role = emr_usage_demo.create_roles(
            job_flow_role_name, service_role_name, iam_resource)
        assert job_flow_role.name == job_flow_role_name
        assert service_role.name == service_role_name
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_usage_demo.create_roles(
                job_flow_role_name, service_role_name, iam_resource)
        assert (exc_info.value.response['Error']['Code'] == error_code_jf or
                exc_info.value.response['Error']['Code'] == error_code_svc)


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_list_attached_role_policies'),
    ('TestException', 'stub_detach_role_policy'),
    ('TestException', 'stub_list_instance_profiles_for_role'),
    ('TestException', 'stub_remove_role_from_instance_profile'),
    ('TestException', 'stub_delete_instance_profile'),
    ('TestException', 'stub_delete_role')
])
def test_delete_roles(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    roles = [iam_resource.Role(make_unique_name('role-')) for _ in range(2)]
    policy_arn = 'arn:aws:iam:::policy/test-policy'
    policy = {'test-policy': policy_arn}
    inst_profile = 'test-profile'

    with stub_runner(error_code, stop_on_method) as runner:
        for role in roles:
            runner.add(
                iam_stubber, 'stub_list_attached_role_policies', role.name,
                policy)
            runner.add(iam_stubber, 'stub_detach_role_policy', role.name, policy_arn)
            runner.add(
                iam_stubber, 'stub_list_instance_profiles_for_role', role.name,
                [inst_profile])
            runner.add(
                iam_stubber, 'stub_remove_role_from_instance_profile', inst_profile,
                role.name)
            runner.add(iam_stubber, 'stub_delete_instance_profile', inst_profile)
            runner.add(iam_stubber, 'stub_delete_role', role.name)

    if error_code is None:
        emr_usage_demo.delete_roles(roles)
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_usage_demo.delete_roles(roles)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_describe_vpcs'),
    ('TestException', 'stub_create_security_group'),
])
def test_create_security_groups(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    ec2_resource = boto3.resource('ec2')
    ec2_stubber = make_stubber(ec2_resource.meta.client)
    vpc_id = 'test-vpc'
    sec_groups = {kind: f'sg-{kind}' for kind in ['manager', 'worker']}

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            ec2_stubber, 'stub_describe_vpcs', {vpc_id: True},
            [{'Name': 'isDefault', 'Values': ['true']}])
        runner.add(
            ec2_stubber, 'stub_create_security_group', 'test-manager',
            'EMR manager group.', vpc_id, sec_groups['manager'])
        runner.add(
            ec2_stubber, 'stub_create_security_group', 'test-worker',
            'EMR worker group.', vpc_id, sec_groups['worker'])

    if error_code is None:
        got_groups = emr_usage_demo.create_security_groups('test', ec2_resource)
        assert [group.id for group in got_groups.values()] == list(sec_groups.values())
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_usage_demo.create_security_groups('test', ec2_resource)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_describe_security_groups'),
    ('TestException', 'stub_revoke_security_group_ingress'),
    ('TestException', 'stub_delete_security_group'),
])
def test_delete_security_groups(
        make_stubber, stub_runner, error_code, stop_on_method):
    ec2_resource = boto3.resource('ec2')
    ec2_stubber = make_stubber(ec2_resource.meta.client)
    sec_group_info = {
        kind: {
            'sg': ec2_resource.SecurityGroup(f'sg-{kind}'),
            'id': f'sg-{kind}',
            'ip_permissions': [],
            'group_name': f'test-{kind}'}
        for kind in ['manager', 'worker']}

    with stub_runner(error_code, stop_on_method) as runner:
        for sg in sec_group_info.values():
            runner.add(ec2_stubber, 'stub_describe_security_groups', [sg])
            runner.add(ec2_stubber, 'stub_revoke_security_group_ingress', sg)
        for sg in sec_group_info.values():
            runner.add(ec2_stubber, 'stub_delete_security_group', sg['id'])

    if error_code is None:
        emr_usage_demo.delete_security_groups(
            {key: value['sg'] for key, value in sec_group_info.items()})
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_usage_demo.delete_security_groups(
                {key: value['sg'] for key, value in sec_group_info.items()})
        assert exc_info.value.response['Error']['Code'] == error_code
