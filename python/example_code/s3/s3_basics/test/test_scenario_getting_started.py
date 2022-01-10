# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import os
import boto3
from botocore.stub import ANY
import pytest

from scenario_getting_started import do_scenario


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_bucket'),
    ('TestException', 'stub_put_object'),
    ('TestException', 'stub_get_object'),
    ('TestException', 'stub_copy_object'),
    ('TestException', 'stub_list_objects'),
    ('TestException', 'stub_delete_bucket'),
])
def test_do_scenario(
        make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    upload_file = __file__
    upload_key = os.path.basename(upload_file)
    want_to_download = 'y'
    want_to_copy = 'y'
    want_to_delete = 'y'
    keys = [upload_file]

    test_content = b'Test object content.'

    inputs = [
        'non_existent_file.txt',
        upload_file,
        want_to_download,
        want_to_copy,
        want_to_delete
    ]
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            s3_stubber.stub_create_bucket, ANY, s3_resource.meta.client.meta.region_name)
        runner.add(s3_stubber.stub_put_object, ANY, upload_key, raise_and_continue=True)
        if want_to_download == 'y':
            runner.add(
                s3_stubber.stub_head_object, ANY, upload_key,
                content_length=len(test_content))
            runner.add(
                s3_stubber.stub_get_object, ANY, upload_key, test_content,
                raise_and_continue=True)
        if want_to_copy == 'y':
            copy_key = f'demo-folder/{upload_key}'
            keys.append(copy_key)
            runner.add(
                s3_stubber.stub_head_object, ANY, upload_key,
                content_length=len(test_content))
            runner.add(
                s3_stubber.stub_copy_object, ANY, upload_key, ANY, copy_key,
                raise_and_continue=True)
        runner.add(s3_stubber.stub_list_objects, ANY, keys, raise_and_continue=True)
        if want_to_delete == 'y':
            runner.add(s3_stubber.stub_list_objects, ANY, keys)
            runner.add(s3_stubber.stub_delete_objects, ANY, keys)
            runner.add(s3_stubber.stub_delete_bucket, ANY, raise_and_continue=True)

    do_scenario(s3_resource)
