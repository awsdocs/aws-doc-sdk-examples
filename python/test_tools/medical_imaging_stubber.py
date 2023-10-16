# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS HealthImaging unit tests.
"""

from test_tools.example_stubber import ExampleStubber
import botocore
import io


class MedicalImagingStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS HealthImaging unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 medical imaging client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_datastore(self, datastore_name, datastore_id, error_code=None):
        expected_params = {"datastoreName": datastore_name}
        response = {
            "datastoreId": datastore_id,
            "datastoreStatus": "CREATING",
        }
        self._stub_bifurcator(
            "create_datastore", expected_params, response, error_code=error_code
        )

    def stub_get_datastore_properties(self, datastore_id, error_code=None):
        expected_params = {"datastoreId": datastore_id}
        response = {
            "datastoreProperties": {
                "datastoreId": datastore_id,
                "datastoreStatus": "ACTIVE",
                "datastoreName": "MyDataStore",
            }
        }

        self._stub_bifurcator(
            "get_datastore", expected_params, response, error_code=error_code
        )

    def stub_list_datastores(self, datastore_id, error_code=None):
        expected_params = {}
        response = {
            "datastoreSummaries": [
                {
                    "datastoreId": datastore_id,
                    "datastoreStatus": "ACTIVE",
                    "datastoreName": "MyDataStore1",
                }
            ]
        }

        self._stub_bifurcator(
            "list_datastores", expected_params, response, error_code=error_code
        )

    def stub_delete_data_store(self, datastore_id, error_code=None):
        expected_params = {"datastoreId": datastore_id}
        response = {"datastoreId": datastore_id, "datastoreStatus": "DELETING"}
        self._stub_bifurcator(
            "delete_datastore", expected_params, response, error_code=error_code
        )

    def stub_start_dicom_import_job(
        self,
        job_name,
        datastore_id,
        role_arn,
        input_s3_uri,
        output_s3_uri,
        job_id,
        error_code=None,
    ):
        expected_params = {
            "jobName": job_name,
            "datastoreId": datastore_id,
            "dataAccessRoleArn": role_arn,
            "inputS3Uri": input_s3_uri,
            "outputS3Uri": output_s3_uri,
        }
        response = {
            "jobId": job_id,
            "datastoreId": datastore_id,
            "jobStatus": "CREATING",
            "submittedAt": "2019-01-01T00:00:00.000Z",
        }

        self._stub_bifurcator(
            "start_dicom_import_job", expected_params, response, error_code=error_code
        )

    def stub_get_dicom_import_job(
        self, job_id, datastore_id, job_status, error_code=None
    ):
        expected_params = {"jobId": job_id, "datastoreId": datastore_id}
        response = {
            "jobProperties": {
                "jobId": job_id,
                "jobStatus": job_status,
                "jobName": "test_job",
                "datastoreId": "cccccc1234567890abcdef123456789",
                "dataAccessRoleArn": "arn:aws:iam::111111111111:role/dicom_import",
                "inputS3Uri": "s3://healthimaging-source/CRStudy/",
                "outputS3Uri": "s3://healthimaging-destination/CRStudy/",
            }
        }
        self._stub_bifurcator(
            "get_dicom_import_job", expected_params, response, error_code=error_code
        )

    def stub_list_dicom_import_jobs(self, datastore_id, error_code=None):
        expected_params = {"datastoreId": datastore_id}
        response = {
            "jobSummaries": [
                {
                    "jobId": "cccccc1234567890abcdef123456789",
                    "jobStatus": "TESTING",
                    "jobName": "test_job",
                    "datastoreId": "cccccc1234567890abcdef123456789",
                    "dataAccessRoleArn": "arn:aws:iam::111111111111:role/dicom_import",
                }
            ]
        }
        self._stub_bifurcator(
            "list_dicom_import_jobs", expected_params, response, error_code=error_code
        )

    def stub_search_image_sets(self, datastore_id, search_criteria, error_code=None):
        expected_params = {
            "datastoreId": datastore_id,
            "searchCriteria": search_criteria,
        }
        response = {
            "imageSetsMetadataSummaries": [
                {
                    "imageSetId": "cccccc1234567890abcdef123456789",
                    "version": 1,
                    "createdAt": "2023-09-13T14:13:39.302000-04:00",
                    "updatedAt": "2023-09-13T14:13:39.302000-04:00",
                }
            ]
        }
        self._stub_bifurcator(
            "search_image_sets", expected_params, response, error_code=error_code
        )

    def stub_get_image_set(
        self, datastore_id, image_set_id, version_id, error_code=None
    ):
        expected_params = {
            "datastoreId": datastore_id,
            "imageSetId": image_set_id,
            "versionId": version_id,
        }
        response = {
            "datastoreId": "12345678901234567890123456789012",
            "imageSetId": image_set_id,
            "versionId": "1",
            "imageSetState": "ACTIVE",
        }

        self._stub_bifurcator(
            "get_image_set", expected_params, response, error_code=error_code
        )

    def stub_get_image_set_metadata(self, datastore_id, image_set_id, error_code=None):
        expected_params = {"datastoreId": datastore_id, "imageSetId": image_set_id}

        data_string = b"akdelfaldkflakdflkajs"
        stream = botocore.response.StreamingBody(
            io.BytesIO(data_string), len(data_string)
        )
        response = {
            "contentType": " text/plain",
            "contentEncoding": "gzip",
            "imageSetMetadataBlob": stream,
        }

        self._stub_bifurcator(
            "get_image_set_metadata", expected_params, response, error_code=error_code
        )

    def stub_get_pixel_data(
        self, datastore_id, image_set_id, image_frame_id, error_code=None
    ):
        expected_params = {
            "datastoreId": datastore_id,
            "imageSetId": image_set_id,
            "imageFrameInformation": {"imageFrameId": image_frame_id},
        }

        data_string = b"akdelfaldkflakdflkajs"
        stream = botocore.response.StreamingBody(
            io.BytesIO(data_string), len(data_string)
        )
        response = {"contentType": "text/plain", "imageFrameBlob": stream}

        self._stub_bifurcator(
            "get_image_frame", expected_params, response, error_code=error_code
        )

    def stub_list_image_set_versions(self, datastore_id, image_set_id, error_code=None):
        expected_params = {"datastoreId": datastore_id, "imageSetId": image_set_id}

        response = {
            "imageSetPropertiesList": [
                {
                    "imageSetId": "cccccc1234567890abcdef123456789",
                    "versionId": "1",
                    "imageSetState": "TESTING",
                    "createdAt": "2023-09-13T14:13:39.302000-04:00",
                    "updatedAt": "2023-09-13T14:13:39.302000-04:00",
                }
            ]
        }
        self._stub_bifurcator(
            "list_image_set_versions", expected_params, response, error_code=error_code
        )

    def stub_update_image_set_metadata(
        self, datastore_id, image_set_id, version_id, metadata, error_code=None
    ):
        expected_params = {
            "datastoreId": datastore_id,
            "imageSetId": image_set_id,
            "latestVersionId": version_id,
            "updateImageSetMetadataUpdates": metadata,
        }

        response = {
            "imageSetId": "cccccc1234567890abcdef123456789",
            "latestVersionId": "1",
            "datastoreId": "12345678901234567890123456789012",
            "imageSetState": "ACTIVE",
        }

        self._stub_bifurcator(
            "update_image_set_metadata",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_copy_image_set_without_destination(
        self,
        datastore_id,
        image_set_id,
        version_id,
        copied_image_set_id,
        error_code=None,
    ):
        expected_params = {
            "datastoreId": datastore_id,
            "sourceImageSetId": image_set_id,
            "copyImageSetInformation": {
                "sourceImageSet": {"latestVersionId": version_id}
            },
        }

        response = {
            "datastoreId": datastore_id,
            "sourceImageSetProperties": {
                "imageSetId": image_set_id,
                "latestVersionId": version_id,
            },
            "destinationImageSetProperties": {
                "imageSetId": copied_image_set_id,
                "latestVersionId": "1",
            },
        }

        self._stub_bifurcator(
            "copy_image_set", expected_params, response, error_code=error_code
        )

    def stub_copy_image_set_with_destination(
        self,
        datastore_id,
        image_set_id,
        version_id,
        destination_image_set_id,
        destination_version_id,
        error_code=None,
    ):
        expected_params = {
            "datastoreId": datastore_id,
            "sourceImageSetId": image_set_id,
            "copyImageSetInformation": {
                "destinationImageSet": {
                    "imageSetId": destination_image_set_id,
                    "latestVersionId": destination_version_id,
                },
                "sourceImageSet": {"latestVersionId": version_id},
            },
        }

        response = {
            "datastoreId": datastore_id,
            "sourceImageSetProperties": {
                "imageSetId": image_set_id,
                "latestVersionId": version_id,
            },
            "destinationImageSetProperties": {
                "imageSetId": destination_image_set_id,
                "latestVersionId": destination_version_id,
            },
        }

        self._stub_bifurcator(
            "copy_image_set", expected_params, response, error_code=error_code
        )

    def stub_delete_image_set(self, datastore_id, image_set_id, error_code=None):
        expected_params = {"datastoreId": datastore_id, "imageSetId": image_set_id}

        response = {
            "datastoreId": datastore_id,
            "imageSetId": image_set_id,
            "imageSetWorkflowStatus": "DELETED",
            "imageSetState": "DELETED",
        }

        self._stub_bifurcator(
            "delete_image_set", expected_params, response, error_code=error_code
        )

    def stub_tag_resource(self, resource_arn, tags, error_code=None):
        expected_params = {"resourceArn": resource_arn, "tags": tags}

        response = {}

        self._stub_bifurcator(
            "tag_resource", expected_params, response, error_code=error_code
        )

    def stub_untag_resource(self, resource_arn, tag_keys, error_code=None):
        expected_params = {"resourceArn": resource_arn, "tagKeys": tag_keys}

        response = {}

        self._stub_bifurcator(
            "untag_resource", expected_params, response, error_code=error_code
        )

    def stub_list_tags_for_resource(self, resource_arn, error_code=None):
        expected_params = {"resourceArn": resource_arn}

        response = {"tags": {"test-key": "test-value"}}

        self._stub_bifurcator(
            "list_tags_for_resource", expected_params, response, error_code=error_code
        )
