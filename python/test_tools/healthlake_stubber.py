# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS HealthLake unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

import io
import json
from botocore.stub import ANY
from boto3 import client

from test_tools.example_stubber import ExampleStubber

from datetime import timedelta, timezone, datetime


class HealthLakeStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS HealthLake unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, healthlake_client: client, use_stubs=True) -> None:
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param healthlake_client: A Boto 3 AWS HealthLake client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(healthlake_client, use_stubs)

    def stub_create_fhir_datastore(
        self, data_store_name: str, data_store_id: str, error_code: str = None
    ) -> None:
        expected_params = {
            "DatastoreName": data_store_name,
            "DatastoreTypeVersion": "R4",
        }

        response = {
            "DatastoreId": data_store_id,
            "DatastoreArn": "datastore_arn",
            "DatastoreStatus": "CREATING",
            "DatastoreEndpoint": f"https://healthlake.us-east-1.amazonaws.com/datastore/{data_store_id}/r4/",
        }

        self._stub_bifurcator(
            "create_fhir_datastore", expected_params, response, error_code=error_code
        )

    def stub_describe_fhir_datastore(
        self, data_store_id, error_code: str = None
    ) -> None:
        expected_params = {"DatastoreId": data_store_id}

        response = {
            "DatastoreProperties": {
                "DatastoreId": data_store_id,
                "DatastoreArn": "datastore_arn",
                "DatastoreStatus": "ACTIVE",
                "DatastoreEndpoint": f"https://healthlake.us-east-1.amazonaws.com/datastore/{data_store_id}/r4/",
                "CreatedAt": datetime.now(timezone.utc),
                "DatastoreName": "datastore_name",
                "DatastoreTypeVersion": "R4",
            }
        }

        self._stub_bifurcator(
            "describe_fhir_datastore", expected_params, response, error_code=error_code
        )

    def stub_list_fhir_datastores(self, error_code: str = None) -> None:
        expected_params = {}

        response = {
            "DatastorePropertiesList": [
                {
                    "DatastoreId": "6407b9ae4c2def3cb6f1a46a0Example",
                    "DatastoreArn": "datastore_arn",
                    "DatastoreStatus": "ACTIVE",
                    "DatastoreEndpoint": f"https://healthlake.us-east-1.amazonaws.com/datastore/6407b9ae4c2def3cb6f1a46a0Example/r4/",
                    "CreatedAt": datetime.now(timezone.utc),
                    "DatastoreName": "datastore_name",
                    "DatastoreTypeVersion": "R4",
                }
            ]
        }

        self._stub_bifurcator(
            "list_fhir_datastores", expected_params, response, error_code=error_code
        )

    def stub_delete_fhir_datastore(self, data_store_id, error_code: str = None) -> None:
        expected_params = {"DatastoreId": data_store_id}

        response = {
            "DatastoreId": data_store_id,
            "DatastoreArn": "datastore_arn",
            "DatastoreStatus": "DELETING",
            "DatastoreEndpoint": f"https://healthlake.us-east-1.amazonaws.com/datastore/{data_store_id}/r4/",
        }

        self._stub_bifurcator(
            "delete_fhir_datastore", expected_params, response, error_code=error_code
        )

    def stub_start_fhir_import_job(
        self,
        job_name: str,
        data_store_id: str,
        input_s3_uri: str,
        output_s3_uri: str,
        kms_key_id: str,
        data_access_role_arn: str,
        error_code: str = None,
    ) -> None:
        expected_params = {
            "JobName": job_name,
            "InputDataConfig": {
                "S3Uri": input_s3_uri,
            },
            "DatastoreId": data_store_id,
            "JobOutputDataConfig": {
                "S3Configuration": {"S3Uri": output_s3_uri, "KmsKeyId": kms_key_id}
            },
            "DatastoreId": data_store_id,
            "DataAccessRoleArn": data_access_role_arn,
        }

        response = {
            "JobId": "my_import_job",
            "JobStatus": "SUBMITTED",
            "DatastoreId": data_store_id,
        }

        self._stub_bifurcator(
            "start_fhir_import_job", expected_params, response, error_code=error_code
        )

    def stub_describe_fhir_import_job(
        self, datastore_id, job_id, error_code: str = None
    ):
        expected_params = {"DatastoreId": datastore_id, "JobId": job_id}

        response = {
            "ImportJobProperties": {
                "JobId": job_id,
                "JobName": "my_import_job",
                "JobStatus": "COMPLETED",
                "DatastoreId": datastore_id,
                "SubmitTime": datetime.now(timezone.utc),
                "EndTime": datetime.now(timezone.utc),
                "InputDataConfig": {
                    "S3Uri": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
                },
                "JobOutputDataConfig": {
                    "S3Configuration": {
                        "S3Uri": "s3://amzn-s3-demo-bucket-827365/import/output/",
                        "KmsKeyId": "kms_key_id",
                    }
                },
                "JobProgressReport": {
                    "TotalNumberOfScannedFiles": 123,
                    "TotalSizeOfScannedFilesInMB": 123.0,
                    "TotalNumberOfImportedFiles": 123,
                    "TotalNumberOfResourcesScanned": 123,
                    "TotalNumberOfResourcesImported": 123,
                    "TotalNumberOfResourcesWithCustomerError": 123,
                    "TotalNumberOfFilesReadWithCustomerError": 123,
                    "Throughput": 123.0,
                },
                "DataAccessRoleArn": "data_access_role_arn",
                "Message": "Import job completed successfully",
            }
        }

        self._stub_bifurcator(
            "describe_fhir_import_job", expected_params, response, error_code=error_code
        )

    def stub_list_fhir_import_jobs(self, data_store_id, error_code: str = None):
        expected_params = {"DatastoreId": data_store_id}

        response = {
            "ImportJobPropertiesList": [
                {
                    "JobId": "my_import_job",
                    "JobName": "my_import_job",
                    "JobStatus": "COMPLETED",
                    "DatastoreId": data_store_id,
                    "SubmitTime": datetime.now(timezone.utc),
                    "EndTime": datetime.now(timezone.utc),
                    "InputDataConfig": {
                        "S3Uri": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
                    },
                    "JobOutputDataConfig": {
                        "S3Configuration": {
                            "S3Uri": "s3://amzn-s3-demo-bucket-827365/import/output/",
                            "KmsKeyId": "kms_key_id",
                        }
                    },
                    "JobProgressReport": {
                        "TotalNumberOfScannedFiles": 123,
                        "TotalSizeOfScannedFilesInMB": 123.0,
                        "TotalNumberOfImportedFiles": 123,
                        "TotalNumberOfResourcesScanned": 123,
                        "TotalNumberOfResourcesImported": 123,
                        "TotalNumberOfResourcesWithCustomerError": 123,
                        "TotalNumberOfFilesReadWithCustomerError": 123,
                        "Throughput": 123.0,
                    },
                    "DataAccessRoleArn": "data_access_role_arn",
                    "Message": "Import job completed successfully",
                }
            ]
        }

        self._stub_bifurcator(
            "list_fhir_import_jobs", expected_params, response, error_code=error_code
        )

    def stub_start_fhir_export_job(
        self,
        job_name: str,
        data_store_id: str,
        output_s3_uri: str,
        kms_key_id: str,
        data_access_role_arn: str,
        error_code: str = None,
    ) -> None:
        expected_params = {
            "JobName": job_name,
            "OutputDataConfig": {
                "S3Configuration": {"S3Uri": output_s3_uri, "KmsKeyId": kms_key_id}
            },
            "DatastoreId": data_store_id,
            "DataAccessRoleArn": data_access_role_arn,
        }

        response = {
            "JobId": "my_export_job",
            "JobStatus": "SUBMITTED",
            "DatastoreId": data_store_id,
        }

        self._stub_bifurcator(
            "start_fhir_export_job", expected_params, response, error_code=error_code
        )

    def stub_list_fhir_export_jobs(self, data_store_id, error_code: str = None):
        expected_params = {"DatastoreId": data_store_id}

        response = {
            "ExportJobPropertiesList": [
                {
                    "JobId": "my_export_job",
                    "JobName": "my_export_job",
                    "JobStatus": "COMPLETED",
                    "DatastoreId": data_store_id,
                    "SubmitTime": datetime.now(timezone.utc),
                    "EndTime": datetime.now(timezone.utc),
                    "OutputDataConfig": {
                        "S3Configuration": {
                            "S3Uri": "s3://amzn-s3-demo-bucket-827365/export/output/",
                            "KmsKeyId": "kms_key_id",
                        }
                    },
                    "DataAccessRoleArn": "data_access_role_arn",
                    "Message": "Export job completed successfully",
                }
            ]
        }

        self._stub_bifurcator(
            "list_fhir_export_jobs", expected_params, response, error_code=error_code
        )
    
    def stub_describe_fhir_export_job(
        self, datastore_id, job_id, error_code: str = None
    ):
        expected_params = {"DatastoreId": datastore_id, "JobId": job_id}

        response = {
            "ExportJobProperties": {
                "JobId": job_id,
                "JobName": "my_export_job",
                "JobStatus": "COMPLETED",
                "DatastoreId": datastore_id,
                "SubmitTime": datetime.now(timezone.utc),
                "EndTime": datetime.now(timezone.utc),
                "OutputDataConfig": {
                    "S3Configuration": {
                        "S3Uri": "s3://amzn-s3-demo-bucket-827365/export/output/",
                        "KmsKeyId": "kms_key_id",
                    }
                },
                "DataAccessRoleArn": "data_access_role_arn",
                "Message": "Export job completed successfully",
            }
        }

        self._stub_bifurcator(
            "describe_fhir_export_job", expected_params, response, error_code=error_code
        )

    def stub_tag_resource(self, resource_arn: str, tags: dict[str, str], error_code: str = None) -> None:
        expected_params = {
            "ResourceARN": resource_arn,
            "Tags": tags,
        }

        response = {}

        self._stub_bifurcator("tag_resource", expected_params, response, error_code=error_code)

    def stub_untag_resource(self, resource_arn: str, tag_keys: list[str], error_code: str = None) -> None:
        expected_params = {
            "ResourceARN": resource_arn,
            "TagKeys": tag_keys,
        }
        response = {}
        self._stub_bifurcator("untag_resource", expected_params, response, error_code=error_code)
        
    def stub_list_tags_for_resource(self, resource_arn: str, error_code: str = None) -> dict[str, str]:
        expected_params = {
            "ResourceARN": resource_arn,
        }

        response = {
            "Tags": [{"Key" :"test-key", "Value" : "test-value"}]
        }

        self._stub_bifurcator(
            "list_tags_for_resource", expected_params, response, error_code=error_code
        )