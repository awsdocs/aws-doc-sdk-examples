# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for lambda_basics.py functions.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from medical_imaging_basics import MedicalImagingWrapper


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_function(make_stubber, error_code):
    medical_imaging_client = boto3.client('medical-imaging')
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_name = 'test-datastore'
    datastore_id = 'abcdedf1234567890abcdef123456789'

    medical_imaging_stubber.stub_create_datastore(
        datastore_name,
        datastore_id,
        error_code=error_code)

    if error_code is None:
        got_datastore_id = wrapper.create_datastore(datastore_name)
        assert got_datastore_id == datastore_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_datastore(datastore_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_properties_function(make_stubber, error_code):
    medical_imaging_client = boto3.client('medical-imaging')
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = 'abcdedf1234567890abcdef123456789'

    medical_imaging_stubber.stub_get_datastore_properties(
        datastore_id,
        error_code=error_code)

    if error_code is None:
        got_properties = wrapper.get_datastore_properties(datastore_id)
        assert got_properties['datastoreId'] == datastore_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_datastore_properties(datastore_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_function(make_stubber, error_code):
    medical_imaging_client = boto3.client('medical-imaging')
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = 'abcdedf1234567890abcdef123456789'
    medical_imaging_stubber.stub_list_datastores(
        datastore_id,
        error_code=error_code)

    if error_code is None:
        datastores = wrapper.list_datastores()
        assert datastores[0]['datastoreId'] == datastore_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_datastores()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_function(make_stubber, error_code):
    medical_imaging_client = boto3.client('medical-imaging')
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = 'abcdedf1234567890abcdef123456789'

    medical_imaging_stubber.stub_delete_data_store(
        datastore_id,
        error_code=error_code)

    if error_code is None:
        wrapper.delete_datastore(datastore_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_datastore(datastore_id)
        assert exc_info.value.response['Error']['Code'] == error_code
