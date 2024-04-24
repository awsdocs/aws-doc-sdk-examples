# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for imaging_set_and_frames workflow.
"""

import boto3
from botocore.exceptions import ClientError
import pytest
import os

from medicalimaging import MedicalImagingWrapper
from imaging_set_and_frames import MedicalImagingWorkflowScenario


@pytest.mark.skip(
    reason="Skip until shared resources are part of the Docker environment."
)
@pytest.mark.integ
def test_run_imaging_set_and_frames_scenario_integ(input_mocker, capsys):
    s3 = boto3.client("s3")
    cf = boto3.resource("cloudformation")
    scenario = MedicalImagingWorkflowScenario(
        MedicalImagingWrapper.from_client(), s3, cf
    )

    input_mocker.mock_answers(
        [
            "stacktest0",  # Stack name.
            "storetest0",  # Datastore name.
            1,  # Select folder.
            "",  # Press enter.
            "",  # Press enter.
            "",  # Press enter.
            "",  # Press enter.
            "",  # Press enter.
            "y",  # Cleanup.
        ]
    )

    scenario.run_scenario()

    capt = capsys.readouterr()
    assert "Thanks for watching!" in capt.out


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_image_set_metadata(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    s3_client = boto3.client("s3")
    wrapper = MedicalImagingWrapper(medical_imaging_client, s3_client)
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
def test_start_dicom_import_job(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    s3_client = boto3.client("s3")
    wrapper = MedicalImagingWrapper(medical_imaging_client, s3_client)
    job_id = "cccccc1234567890abcdef123456789"
    job_name = "examplejob"
    datastore_id = "abcdedf1234567890abcdef123456789"
    role_arn = "arn:aws:iam::111111111111:role/dicom_import"
    input_bucket_name = "healthimaging-source"
    input_directory = "input"
    output_bucket_name = "healthimaging-destination"
    output_directory = "output"
    input_uri = f"s3://{input_bucket_name}/{input_directory}/"
    output_uri = f"s3://{output_bucket_name}/{output_directory}/"

    medical_imaging_stubber.stub_start_dicom_import_job(
        job_name,
        datastore_id,
        role_arn,
        input_uri,
        output_uri,
        job_id,
        error_code=error_code,
    )

    if error_code is None:
        result = wrapper.start_dicom_import_job(
            datastore_id,
            input_bucket_name,
            input_directory,
            output_bucket_name,
            output_directory,
            role_arn,
        )
        assert result == job_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.start_dicom_import_job(
                datastore_id,
                input_bucket_name,
                input_directory,
                output_bucket_name,
                output_directory,
                role_arn,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_image_sets_for_dicom_import_job(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    s3_client = boto3.client("s3")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    s3_stubber = make_stubber(s3_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client, s3_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    job_id = "cccccc1234567890abcdef123456789"
    job_status = "TESTING"
    bucket = "healthimaging-destination"
    key = "CRStudy/job-output-manifest.json"
    test_content = b"Test object body"

    medical_imaging_stubber.stub_get_dicom_import_job(
        job_id, datastore_id, job_status, error_code=error_code
    )

    if error_code is None:
        s3_stubber.stub_get_object(bucket, key, test_content, error_code=error_code)
        result = wrapper.get_image_sets_for_dicom_import_job(datastore_id, job_id)
        assert result["jobStatus"] == job_status
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_image_sets_for_dicom_import_job(datastore_id, job_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_search_mage_sets(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    s3_client = boto3.client("s3")
    wrapper = MedicalImagingWrapper(medical_imaging_client, s3_client)
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
def test_get_image_frames_for_image_set(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    s3_client = boto3.client("s3")
    wrapper = MedicalImagingWrapper(medical_imaging_client, s3_client)
    datastore_id = "abcdedf1234567890abcdef123456789"
    image_set_id = "cccccc1234567890abcdef123456789"
    directory = "output"

    if error_code is None:
        medical_imaging_stubber.stub_get_image_set_metadata(
            datastore_id, image_set_id, error_code=error_code
        )
        wrapper.get_image_frames_for_image_set(datastore_id, image_set_id, directory)


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_pixel_data(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    s3_client = boto3.client("s3")
    wrapper = MedicalImagingWrapper(medical_imaging_client, s3_client)
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
def test_delete_image_set(make_stubber, error_code):
    medical_imaging_client = boto3.client("medical-imaging")
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    s3_client = boto3.client("s3")
    wrapper = MedicalImagingWrapper(medical_imaging_client, s3_client)
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
