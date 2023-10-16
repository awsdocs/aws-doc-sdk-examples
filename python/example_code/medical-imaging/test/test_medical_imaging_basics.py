# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for medical_imaging_basics functions.
"""

import boto3
from botocore.exceptions import ClientError
import pytest
import os

from medical_imaging_basics import MedicalImagingWrapper


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_datastore(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_name = "test-datastore"
    datastore_id = "abcdedf1234567890abcdef123456789"

    medical_imaging_stubber.stub_create_datastore(
        datastore_name, datastore_id, error_code=error_code
    )

    if error_code is None:
        got_datastore_id = wrapper.create_datastore(datastore_name)
        assert got_datastore_id == datastore_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_datastore(datastore_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_datastore_properties(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"

    medical_imaging_stubber.stub_get_datastore_properties(
        datastore_id, error_code=error_code
    )

    if error_code is None:
        got_properties = wrapper.get_datastore_properties(datastore_id)
        assert got_properties["datastoreId"] == datastore_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_datastore_properties(datastore_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_datastores(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    medical_imaging_stubber.stub_list_datastores(datastore_id, error_code=error_code)

    if error_code is None:
        datastores = wrapper.list_datastores()
        assert datastores[0]["datastoreId"] == datastore_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_datastores()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_datastore(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"

    medical_imaging_stubber.stub_delete_data_store(datastore_id, error_code=error_code)

    if error_code is None:
        wrapper.delete_datastore(datastore_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_datastore(datastore_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_start_dicom_import_job(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    job_id = "cccccc1234567890abcdef123456789"
    job_name = "job_name"
    datastore_id = "abcdedf1234567890abcdef123456789"
    role_arn = "arn:aws:iam::111111111111:role/dicom_import"
    input_s3_uri = "s3://healthimaging-source/CRStudy/"
    output_s3_uri = "s3://healthimaging-destination/CRStudy/"

    medical_imaging_stubber.stub_start_dicom_import_job(
        job_name,
        datastore_id,
        role_arn,
        input_s3_uri,
        output_s3_uri,
        job_id,
        error_code=error_code,
    )

    if error_code is None:
        result = wrapper.start_dicom_import_job(
            job_name, datastore_id, role_arn, input_s3_uri, output_s3_uri
        )
        assert result == job_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.start_dicom_import_job(
                job_name, datastore_id, role_arn, input_s3_uri, output_s3_uri
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_dicom_import_job(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    job_id = "cccccc1234567890abcdef123456789"
    job_status = "TESTING"

    medical_imaging_stubber.stub_get_dicom_import_job(
        job_id, datastore_id, job_status, error_code=error_code
    )

    if error_code is None:
        result = wrapper.get_dicom_import_job(datastore_id, job_id)
        assert result["jobStatus"] == job_status
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_dicom_import_job(datastore_id, job_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_dicom_import_jobs(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"

    medical_imaging_stubber.stub_list_dicom_import_jobs(
        datastore_id, error_code=error_code
    )

    if error_code is None:
        wrapper.list_dicom_import_jobs(datastore_id)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_dicom_import_jobs(datastore_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_search_mage_sets(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    search_filter = {
        "filters": [
            {
                "values": [
                    {"createdAt": "2023-09-13T14:13:39.302000-04:00"},
                    {"createdAt": "2023-09-13T14:13:39.302000-04:00"},
                ],
                "operator": "BETWEEN",
            }
        ]
    }
    medical_imaging_stubber.stub_search_image_sets(
        datastore_id, search_filter, error_code=error_code
    )

    if error_code is None:
        wrapper.search_image_sets(datastore_id, search_filter)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.search_image_sets(datastore_id, search_filter)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_image_set(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    image_set_id = "cccccc1234567890abcdef123456789"
    version_id = "1"

    medical_imaging_stubber.stub_get_image_set(
        datastore_id, image_set_id, version_id, error_code=error_code
    )

    if error_code is None:
        wrapper.get_image_set(datastore_id, image_set_id, version_id)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_image_set(datastore_id, image_set_id, version_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_image_set_metadata(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    image_set_id = "cccccc1234567890abcdef123456789"
    test_file = "med-imag-test_file_1234.gzip"
    medical_imaging_stubber.stub_get_image_set_metadata(
        datastore_id, image_set_id, error_code=error_code
    )

    if error_code is None:
        wrapper.get_image_set_metadata(test_file, datastore_id, image_set_id)
        assert os.path.exists(test_file)
        os.remove(test_file)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_image_set_metadata(test_file, datastore_id, image_set_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_pixel_data(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    image_set_id = "cccccc1234567890abcdef123456789"
    image_frame_id = "cccccc1234567890abcdef123456789"
    test_file = "med-imag-test_file_789654.jph"
    medical_imaging_stubber.stub_get_pixel_data(
        datastore_id, image_set_id, image_frame_id, error_code=error_code
    )

    if error_code is None:
        wrapper.get_pixel_data(test_file, datastore_id, image_set_id, image_frame_id)
        assert os.path.exists(test_file)
        os.remove(test_file)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_pixel_data(
                test_file, datastore_id, image_set_id, image_frame_id
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_image_set_versions(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    image_set_id = "cccccc1234567890abcdef123456789"

    medical_imaging_stubber.stub_list_image_set_versions(
        datastore_id, image_set_id, error_code=error_code
    )

    if error_code is None:
        wrapper.list_image_set_versions(datastore_id, image_set_id)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_image_set_versions(datastore_id, image_set_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_update_image_set_metadata(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    image_set_id = "cccccc1234567890abcdef123456789"
    version_id = "1"
    metadata = {
        "DICOMUpdates": {
            "updatableAttributes": '{"SchemaVersion":1.1,"Patient":{"DICOM":{"PatientName":"Garcia^Gloria"}}}'
        }
    }

    medical_imaging_stubber.stub_update_image_set_metadata(
        datastore_id, image_set_id, version_id, metadata, error_code=error_code
    )

    if error_code is None:
        wrapper.update_image_set_metadata(
            datastore_id, image_set_id, version_id, metadata
        )

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.update_image_set_metadata(
                datastore_id, image_set_id, version_id, metadata
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_copy_image_set_without_destination(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    image_set_id = "cccccc1234567890abcdef123456789"
    version_id = "1"
    new_image_set_id = "cccccc1234567890abcdef123456789"

    medical_imaging_stubber.stub_copy_image_set_without_destination(
        datastore_id, image_set_id, version_id, new_image_set_id, error_code=error_code
    )

    if error_code is None:
        wrapper.copy_image_set(datastore_id, image_set_id, version_id)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.copy_image_set(datastore_id, image_set_id, version_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_copy_image_set_with_destination(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    image_set_id = "cccccc1234567890abcdef123456789"
    version_id = "1"
    destination_image_set_id = "cccccc1234567890abcdef123456789"
    destination_version_id = "1"

    medical_imaging_stubber.stub_copy_image_set_with_destination(
        datastore_id,
        image_set_id,
        version_id,
        destination_image_set_id,
        destination_version_id,
        error_code=error_code,
    )

    if error_code is None:
        wrapper.copy_image_set(
            datastore_id,
            image_set_id,
            version_id,
            destination_image_set_id,
            destination_version_id,
        )

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.copy_image_set(
                datastore_id,
                image_set_id,
                version_id,
                destination_image_set_id,
                destination_version_id,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_image_set(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    image_set_id = "cccccc1234567890abcdef123456789"

    medical_imaging_stubber.stub_delete_image_set(
        datastore_id, image_set_id, error_code=error_code
    )

    if error_code is None:
        wrapper.delete_image_set(datastore_id, image_set_id)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_image_set(datastore_id, image_set_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_tag_resource(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    resource_arn = (
        "arn:aws:medical-imaging:us-east-1:123456789012:datastore/abcdedf1234567890abcdef123456789/image"
        "-set/cccccc1234567890abcdef123456789 "
    )
    tags = {"test-key": "test-value"}

    medical_imaging_stubber.stub_tag_resource(resource_arn, tags, error_code=error_code)

    if error_code is None:
        wrapper.tag_resource(resource_arn, tags)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.tag_resource(resource_arn, tags)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_untag_resource(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    resource_arn = (
        "arn:aws:medical-imaging:us-east-1:123456789012:datastore/abcdedf1234567890abcdef123456789/image"
        "-set/cccccc1234567890abcdef123456789 "
    )
    tag_keys = ["test-key"]

    medical_imaging_stubber.stub_untag_resource(
        resource_arn, tag_keys, error_code=error_code
    )

    if error_code is None:
        wrapper.untag_resource(resource_arn, tag_keys)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.untag_resource(resource_arn, tag_keys)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_tags_for_resource(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client)
    resource_arn = (
        "arn:aws:medical-imaging:us-east-1:123456789012:datastore/abcdedf1234567890abcdef123456789/image"
        "-set/cccccc1234567890abcdef123456789 "
    )

    medical_imaging_stubber.stub_list_tags_for_resource(
        resource_arn, error_code=error_code
    )

    if error_code is None:
        wrapper.list_tags_for_resource(resource_arn)

    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_tags_for_resource(resource_arn)
        assert exc_info.value.response["Error"]["Code"] == error_code
