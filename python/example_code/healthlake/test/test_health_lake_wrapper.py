# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for health_lake_wrapper functions.
"""

import os
import sys

import boto3
import pytest
from botocore.exceptions import ClientError

script_dir = os.path.dirname(os.path.abspath(__file__))

# Append parent directory to import health_lake_wrapper.
sys.path.append(os.path.join(script_dir, ".."))
from health_lake_wrapper import HealthLakeWrapper


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_fhir_datastore(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    datastore_name = "test-datastore"
    datastore_id = "abcdedf1234567890abcdef123456789"

    healthlake_stubber.stub_create_fhir_datastore(
        datastore_name, datastore_id, error_code=error_code
    )

    if error_code is None:
        response = wrapper.create_fhir_datastore(datastore_name)
        assert response["DatastoreId"] == datastore_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_fhir_datastore(datastore_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_fhir_datastore(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    datastore_id = "abcdedf1234567890abcdef123456789"

    healthlake_stubber.stub_describe_fhir_datastore(datastore_id, error_code=error_code)

    if error_code is None:
        response = wrapper.describe_fhir_datastore(datastore_id)
        assert response["DatastoreId"] == datastore_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_fhir_datastore(datastore_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_fhir_datastores(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)

    healthlake_stubber.stub_list_fhir_datastores(error_code=error_code)

    if error_code is None:
        response = wrapper.list_fhir_datastores()
        assert len(response) == 1
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_fhir_datastores()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_fhir_datastore(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    datastore_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

    healthlake_stubber.stub_delete_fhir_datastore(datastore_id, error_code=error_code)

    if error_code is None:
        wrapper.delete_fhir_datastore(datastore_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_fhir_datastore(datastore_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_start_fhir_import_job(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    job_name = "test-job"
    datastore_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    input_s3_uri = "s3://amzn-s3-demo-bucket/test-data"
    job_output_s3_uri = "s3://amzn-s3-demo-bucket/test-output"
    kms_key_id = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
    data_access_role_arn = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

    healthlake_stubber.stub_start_fhir_import_job(
        job_name,
        datastore_id,
        input_s3_uri,
        job_output_s3_uri,
        kms_key_id,
        data_access_role_arn,
        error_code=error_code,
    )

    if error_code is None:
        wrapper.start_fhir_import_job(
            job_name,
            datastore_id,
            input_s3_uri,
            job_output_s3_uri,
            kms_key_id,
            data_access_role_arn,
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.start_fhir_import_job(
                job_name,
                datastore_id,
                input_s3_uri,
                job_output_s3_uri,
                kms_key_id,
                data_access_role_arn,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_fhir_import_job(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    datastore_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    job_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

    healthlake_stubber.stub_describe_fhir_import_job(
        datastore_id, job_id, error_code=error_code
    )

    if error_code is None:
        wrapper.describe_fhir_import_job(datastore_id, job_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_fhir_import_job(datastore_id, job_id)
        assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_fhir_import_jobs(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    datastore_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

    healthlake_stubber.stub_list_fhir_import_jobs(datastore_id, error_code=error_code)

    if error_code is None:
        wrapper.list_fhir_import_jobs(datastore_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_fhir_import_jobs(datastore_id)
        assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_start_fhir_export_job(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    job_name = "test-job"
    datastore_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    output_s3_uri = "s3://amzn-s3-demo-bucket/test-output"
    data_access_role_arn = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    kms_key_id = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"

    healthlake_stubber.stub_start_fhir_export_job(
        job_name,
        datastore_id,
        output_s3_uri,
        kms_key_id,
        data_access_role_arn,
        error_code=error_code,
    )

    if error_code is None:
        wrapper.start_fhir_export_job(
            job_name,
            datastore_id,
            output_s3_uri,
            kms_key_id,
            data_access_role_arn,
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.start_fhir_export_job(
                job_name,
                datastore_id,
                output_s3_uri,
                kms_key_id,
                data_access_role_arn,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_fhir_export_jobs(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    datastore_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

    healthlake_stubber.stub_list_fhir_export_jobs(datastore_id, error_code=error_code)

    if error_code is None:
        wrapper.list_fhir_export_jobs(datastore_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_fhir_export_jobs(datastore_id)
        assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_fhir_export_job(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    datastore_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    job_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

    healthlake_stubber.stub_describe_fhir_export_job(
        datastore_id, job_id, error_code=error_code
    )

    if error_code is None:
        wrapper.describe_fhir_export_job(datastore_id, job_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_fhir_export_job(datastore_id, job_id)
        assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_tag_resource(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    resource_arn = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    tags = [{"Key" :"test-key", "Value" : "test-value"}]
    healthlake_stubber.stub_tag_resource(resource_arn, tags, error_code=error_code)
    if error_code is None:
        wrapper.tag_resource(resource_arn, tags)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.tag_resource(resource_arn, tags)
        assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_untag_resource(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    resource_arn = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    tag_keys = ["test-key"]
    healthlake_stubber.stub_untag_resource(resource_arn, tag_keys, error_code=error_code)
    if error_code is None:
        wrapper.untag_resource(resource_arn, tag_keys)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.untag_resource(resource_arn, tag_keys)
        assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_tags_for_resource(make_stubber, error_code):
    healthlake_client = boto3.client("healthlake")
    healthlake_stubber = make_stubber(healthlake_client)
    wrapper = HealthLakeWrapper(healthlake_client)
    resource_arn = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    healthlake_stubber.stub_list_tags_for_resource(resource_arn, error_code=error_code)
    if error_code is None:
        wrapper.list_tags_for_resource(resource_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_tags_for_resource(resource_arn)
        assert exc_info.value.response["Error"]["Code"] == error_code
