# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage and invoke AWS HealthLake
functions.
"""
from datetime import datetime, timedelta
from importlib.metadata import metadata

from boto3 import client
import logging
import json

import boto3
from botocore.exceptions import ClientError
import time

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.healthlake.HealthLakeWrapper]
class HealthLakeWrapper:
    def __init__(self, health_lake_client: client):
        self.health_lake_client = health_lake_client

    # snippet-start:[python.example_code.healthlake.HealthLakeWrapper.decl]
    @classmethod
    def from_client(cls) -> "HealthLakeWrapper":
        """
        Creates a HealthLakeWrapper instance with a default AWS HealthLake client.

        :return: An instance of HealthLakeWrapper initialized with the default HealthLake client.
        """
        health_lake_client = boto3.client("healthlake")
        return cls(health_lake_client)

    # snippet-end:[python.example_code.healthlake.HealthLakeWrapper.decl]

    # snippet-start:[python.example_code.healthlake.CreateFHIRDatastore]
    def create_fhir_datastore(
        self,
        datastore_name: str,
        sse_configuration: dict[str, any] = None,
        identity_provider_configuration: dict[str, any] = None,
    ) -> dict[str, str]:
        """
        Creates a new HealthLake data store.
        When creating a SMART on FHIR data store, the following parameters are required:
        - sse_configuration: The server-side encryption configuration for a SMART on FHIR-enabled data store.
        - identity_provider_configuration: The identity provider configuration for a SMART on FHIR-enabled data store.

        :param datastore_name: The name of the data store.
        :param sse_configuration: The server-side encryption configuration for a SMART on FHIR-enabled data store.
        :param identity_provider_configuration: The identity provider configuration for a SMART on FHIR-enabled data store.
        :return: A dictionary containing the data store information.
        """
        try:
            parameters = {"DatastoreName": datastore_name, "DatastoreTypeVersion": "R4"}
            if (
                sse_configuration is not None
                and identity_provider_configuration is not None
            ):
                # Creating a SMART on FHIR-enabled data store
                parameters["SseConfiguration"] = sse_configuration
                parameters[
                    "IdentityProviderConfiguration"
                ] = identity_provider_configuration

            response = self.health_lake_client.create_fhir_datastore(**parameters)
            return response
        except ClientError as err:
            logger.exception(
                "Couldn't create data store %s. Here's why %s",
                datastore_name,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.CreateFHIRDatastore]

    # snippet-start:[python.example_code.healthlake.DescribeFHIRDatastore]
    def describe_fhir_datastore(self, datastore_id: str) -> dict[str, any]:
        """
        Describes a HealthLake data store.
        :param datastore_id: The data store ID.
        :return: The data store description.
        """
        try:
            response = self.health_lake_client.describe_fhir_datastore(
                DatastoreId=datastore_id
            )
            return response["DatastoreProperties"]
        except ClientError as err:
            logger.exception(
                "Couldn't describe data store with ID %s. Here's why %s",
                datastore_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.DescribeFHIRDatastore]

    # snippet-start:[python.example_code.healthlake.ListFHIRDatastores]
    def list_fhir_datastores(self) -> list[dict[str, any]]:
        """
        Lists all HealthLake data stores.
        :return: A list of data store descriptions.
        """
        try:
            next_token = None
            datastores = []

            # Loop through paginated results.
            while True:
                parameters = {}
                if next_token is not None:
                    parameters["NextToken"] = next_token
                response = self.health_lake_client.list_fhir_datastores(**parameters)
                datastores.extend(response["DatastorePropertiesList"])
                if "NextToken" in response:
                    next_token = response["NextToken"]
                else:
                    break

            return datastores
        except ClientError as err:
            logger.exception(
                "Couldn't list data stores. Here's why %s", err.response["Error"]["Message"]
            )
            raise
    # snippet-end:[python.example_code.healthlake.ListFHIRDatastores]

    # snippet-start:[python.example_code.healthlake.DeleteFHIRDatastore]
    def delete_fhir_datastore(self, datastore_id: str) -> None:
        """
        Deletes a HealthLake data store.
        :param datastore_id: The data store ID.
        """
        try:
            self.health_lake_client.delete_fhir_datastore(DatastoreId=datastore_id)
        except ClientError as err:
            logger.exception(
                "Couldn't delete data store with ID %s. Here's why %s",
                datastore_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.DeleteFHIRDatastore]

    # snippet-start:[python.example_code.healthlake.StartFHIRImportJob]
    def start_fhir_import_job(
        self,
        job_name: str,
        datastore_id: str,
        input_s3_uri: str,
        job_output_s3_uri: str,
        kms_key_id: str,
        data_access_role_arn: str,
    ) -> dict[str, str]:
        """
        Starts a HealthLake import job.
        :param job_name: The import job name.
        :param datastore_id: The data store ID.
        :param input_s3_uri: The input S3 URI.
        :param job_output_s3_uri: The job output S3 URI.
        :param kms_key_id: The KMS key ID associated with the output S3 bucket.
        :param data_access_role_arn: The data access role ARN.
        :return: The import job.
        """
        try:
            response = self.health_lake_client.start_fhir_import_job(
                JobName=job_name,
                InputDataConfig={"S3Uri": input_s3_uri},
                JobOutputDataConfig={
                    "S3Configuration": {
                        "S3Uri": job_output_s3_uri,
                        "KmsKeyId": kms_key_id,
                    }
                },
                DataAccessRoleArn=data_access_role_arn,
                DatastoreId=datastore_id,
            )
            return response
        except ClientError as err:
            logger.exception(
                "Couldn't start import job. Here's why %s",
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.StartFHIRImportJob]

    # snippet-start:[python.example_code.healthlake.DescribeFHIRImportJob]
    def describe_fhir_import_job(
        self, datastore_id: str, job_id: str
    ) -> dict[str, any]:
        """
        Describes a HealthLake import job.
        :param datastore_id: The data store ID.
        :param job_id: The import job ID.
        :return: The import job description.
        """
        try:
            response = self.health_lake_client.describe_fhir_import_job(
                DatastoreId=datastore_id, JobId=job_id
            )
            return response["ImportJobProperties"]
        except ClientError as err:
            logger.exception(
                "Couldn't describe import job with ID %s. Here's why %s",
                job_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.DescribeFHIRImportJob]

    # snippet-start:[python.example_code.healthlake.ListFHIRImportJobs]
    def list_fhir_import_jobs(
        self,
        datastore_id: str,
        job_name: str = None,
        job_status: str = None,
        submitted_before: datetime = None,
        submitted_after: datetime = None,
    ) -> list[dict[str, any]]:
        """
        Lists HealthLake import jobs satisfying the conditions.
        :param datastore_id: The data store ID.
        :param job_name: The import job name.
        :param job_status: The import job status.
        :param submitted_before: The import job submitted before the specified date.
        :param submitted_after: The import job submitted after the specified date.
        :return: A list of import jobs.
        """
        try:
            parameters = {"DatastoreId": datastore_id}
            if job_name is not None:
                parameters["JobName"] = job_name
            if job_status is not None:
                parameters["JobStatus"] = job_status
            if submitted_before is not None:
                parameters["SubmittedBefore"] = submitted_before
            if submitted_after is not None:
                parameters["SubmittedAfter"] = submitted_after
            next_token = None
            jobs = []
            # Loop through paginated results.
            while True:
                if next_token is not None:
                    parameters["NextToken"] = next_token
                response = self.health_lake_client.list_fhir_import_jobs(**parameters)
                jobs.extend(response["ImportJobPropertiesList"])
                if "NextToken" in response:
                    next_token = response["NextToken"]
                else:
                    break
            return jobs
        except ClientError as err:
            logger.exception(
                "Couldn't list import jobs. Here's why %s",
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.ListFHIRImportJobs]

    # snippet-start:[python.example_code.healthlake.StartFHIRExportJob]
    def start_fhir_export_job(
        self,
        job_name: str,
        datastore_id: str,
        output_s3_uri: str,
        kms_key_id: str,
        data_access_role_arn: str,
    ) -> dict[str, str]:
        """
        Starts a HealthLake export job.
        :param job_name: The export job name.
        :param datastore_id: The data store ID.
        :param output_s3_uri: The output S3 URI.
        :param kms_key_id: The KMS key ID associated with the output S3 bucket.
        :param data_access_role_arn: The data access role ARN.
        :return: The export job.
        """
        try:
            response = self.health_lake_client.start_fhir_export_job(
                OutputDataConfig={
                    "S3Configuration": {"S3Uri": output_s3_uri, "KmsKeyId": kms_key_id}
                },
                DataAccessRoleArn=data_access_role_arn,
                DatastoreId=datastore_id,
                JobName=job_name,
            )

            return response
        except ClientError as err:
            logger.exception(
                "Couldn't start export job. Here's why %s",
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.StartFHIRExportJob]

    # snippet-start:[python.example_code.healthlake.DescribeFHIRExportJob]
    def describe_fhir_export_job(
        self, datastore_id: str, job_id: str
    ) -> dict[str, any]:
        """
        Describes a HealthLake export job.
        :param datastore_id: The data store ID.
        :param job_id: The export job ID.
        :return: The export job description.
        """
        try:
            response = self.health_lake_client.describe_fhir_export_job(
                DatastoreId=datastore_id, JobId=job_id
            )
            return response["ExportJobProperties"]
        except ClientError as err:
            logger.exception(
                "Couldn't describe export job with ID %s. Here's why %s",
                job_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.DescribeFHIRExportJob]

    # snippet-start:[python.example_code.healthlake.ListFHIRExportJobs]
    def list_fhir_export_jobs(
        self,
        datastore_id: str,
        job_name: str = None,
        job_status: str = None,
        submitted_before: datetime = None,
        submitted_after: datetime = None,
    ) -> list[dict[str, any]]:
        """
        Lists HealthLake export jobs satisfying the conditions.
        :param datastore_id: The data store ID.
        :param job_name: The export job name.
        :param job_status: The export job status.
        :param submitted_before: The export job submitted before the specified date.
        :param submitted_after: The export job submitted after the specified date.
        :return: A list of export jobs.
        """
        try:
            parameters = {"DatastoreId": datastore_id}
            if job_name is not None:
                parameters["JobName"] = job_name
            if job_status is not None:
                parameters["JobStatus"] = job_status
            if submitted_before is not None:
                parameters["SubmittedBefore"] = submitted_before
            if submitted_after is not None:
                parameters["SubmittedAfter"] = submitted_after
            next_token = None
            jobs = []
            # Loop through paginated results.
            while True:
                if next_token is not None:
                    parameters["NextToken"] = next_token
                response = self.health_lake_client.list_fhir_export_jobs(**parameters)
                jobs.extend(response["ExportJobPropertiesList"])
                if "NextToken" in response:
                    next_token = response["NextToken"]
                else:
                    break
            return jobs
        except ClientError as err:
            logger.exception(
                "Couldn't list export jobs. Here's why %s",
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.ListFHIRExportJobs]

    # snippet-start:[python.example_code.healthlake.TagResource]
    def tag_resource(self, resource_arn: str, tags: list[dict[str, str]]) -> None:
        """
        Tags a HealthLake resource.
        :param resource_arn: The resource ARN.
        :param tags: The tags to add to the resource.
        """
        try:
            self.health_lake_client.tag_resource(ResourceARN=resource_arn, Tags=tags)
        except ClientError as err:
            logger.exception(
                "Couldn't tag resource %s. Here's why %s",
                resource_arn,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.TagResource]

    # snippet-start:[python.example_code.healthlake.ListTagsForResource]
    def list_tags_for_resource(self, resource_arn: str) -> dict[str, str]:
        """
        Lists the tags for a HealthLake resource.
        :param resource_arn: The resource ARN.
        :return: The tags for the resource.
        """
        try:
            response = self.health_lake_client.list_tags_for_resource(
                ResourceARN=resource_arn
            )
            return response["Tags"]
        except ClientError as err:
            logger.exception(
                "Couldn't list tags for resource %s. Here's why %s",
                resource_arn,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.ListTagsForResource]

    # snippet-start:[python.example_code.healthlake.UntagResource]
    def untag_resource(self, resource_arn: str, tag_keys: list[str]) -> None:
        """
        Untags a HealthLake resource.
        :param resource_arn: The resource ARN.
        :param tag_keys: The tag keys to remove from the resource.
        """
        try:
            self.health_lake_client.untag_resource(
                ResourceARN=resource_arn, TagKeys=tag_keys
            )
        except ClientError as err:
            logger.exception(
                "Couldn't untag resource %s. Here's why %s",
                resource_arn,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.healthlake.UntagResource]

    # snippet-end:[python.example_code.healthlake.HealthLakeWrapper]

    def wait_datastore_active(self, datastore_id: str) -> None:
        """
        Waits for a HealthLake data store to become active.
        :param datastore_id: The data store ID.
        """
        counter = 0
        max_count_minutes = 40  # It can take a while to create a data store, so we'll wait up to 40 minutes.
        status = "CREATING"
        while counter < max_count_minutes:
            datastore = self.health_lake_client.describe_fhir_datastore(
                DatastoreId=datastore_id
            )
            status = datastore["DatastoreProperties"]["DatastoreStatus"]
            if status == "ACTIVE" or status == "CREATE_FAILED":
                break
            else:
                print(f"data store {status}, minutes {counter}")
                counter += 1
                time.sleep(60)

        if status == "ACTIVE":
            print(
                f"Data store with ID {datastore_id} is active after {counter} minutes."
            )
        elif status == "CREATE_FAILED":
            raise ClientError(
                "Create data store with ID %s failed after %d minutes.",
                datastore_id,
                counter,
            )
        else:
            raise ClientError(
                "Data store with ID %s is not active after %d minutes.",
                datastore_id,
                counter,
            )

    def wait_import_job_complete(self, datastore_id: str, job_id: str) -> None:
        """
        Waits for a HealthLake import job to complete.
        :param datastore_id: The data store ID.
        :param job_id: The import job ID.
        """
        counter = 0
        max_count_minutes = (
            20
        )
        status = "IN_PROGRESS"
        while counter < max_count_minutes:
            job = self.describe_fhir_import_job(datastore_id, job_id)
            status = job["JobStatus"]
            if status == "COMPLETED" or status == "COMPLETED_WITH_ERRORS":
                break
            else:
                print(f"Import job {status}, minutes {counter}")
                counter += 1
                time.sleep(60)

        if status == "COMPLETED":
            print(f"Import job with ID {job_id} is completed after {counter} minutes.")
        elif status == "COMPLETED_WITH_ERRORS":
            print(
                f"Import job with ID {job_id} is completed with errors after {counter} minutes."
            )
        else:
            raise ClientError(
                "Import job with ID %s is not completed after %d minutes.",
                job_id,
                counter,
            )

    def wait_export_job_complete(self, datastore_id: str, job_id: str) -> None:
        """
        Waits for a HealthLake export job to complete.
        :param datastore_id: The data store ID.
        :param job_id: The export job ID.
        """
        counter = 0
        max_count_minutes = (
            20
        )
        status = "IN_PROGRESS"
        while counter < max_count_minutes:
            job = self.describe_fhir_export_job(datastore_id, job_id)
            status = job["JobStatus"]
            if status == "COMPLETED" or status == "COMPLETED_WITH_ERRORS":
                break
            else:
                print(f"Export job {status}, minutes {counter}")
                counter += 1
                time.sleep(60)
        if status == "COMPLETED":
            print(f"Export job with ID {job_id} is completed after {counter} minutes.")
        elif status == "COMPLETED_WITH_ERRORS":
            print(
                f"Export job with ID {job_id} is completed with errors after {counter} minutes."
            )
        else:
            raise ClientError(
                "Job with ID %s is not completed after %d minutes.", job_id, counter
            )

    def health_lake_demo(self) -> None:
        use_smart_on_fhir_data_store = False

        # Change the following variables to match your environment.
        datastore_name = "health_imaging_datastore"
        import_job_name = "my_import_job"
        input_s3_uri = (
            "s3://amzn-s3-demo-bucket/import/examples/patient_example.json"
        )
        kms_key_id = "arn:aws:kms:us-east-1:123456789012:key/b7f645cb-e564-4981-8672-9e012d1ff1a0"
        data_access_role_arn = (
            "arn:aws:iam::123456789012:role/healthlake_access"
        )
        export_job_name = "my_export_job"
        output_s3_uri = "s3://amzn-s3-demo-bucket/export/output/"


        if use_smart_on_fhir_data_store:
            # snippet-start:[python.example_code.healthlake.CreateFHIRDatastore.smart]
            sse_configuration = {
                "KmsEncryptionConfig": {"CmkType": "AWS_OWNED_KMS_KEY"}
            }
            # TODO: Update the metadata to match your environment.
            metadata = {
                "issuer": "https://ehr.example.com",
                "jwks_uri": "https://ehr.example.com/.well-known/jwks.json",
                "authorization_endpoint": "https://ehr.example.com/auth/authorize",
                "token_endpoint": "https://ehr.token.com/auth/token",
                "token_endpoint_auth_methods_supported": [
                    "client_secret_basic",
                    "foo",
                ],
                "grant_types_supported": ["client_credential", "foo"],
                "registration_endpoint": "https://ehr.example.com/auth/register",
                "scopes_supported": ["openId", "profile", "launch"],
                "response_types_supported": ["code"],
                "management_endpoint": "https://ehr.example.com/user/manage",
                "introspection_endpoint": "https://ehr.example.com/user/introspect",
                "revocation_endpoint": "https://ehr.example.com/user/revoke",
                "code_challenge_methods_supported": ["S256"],
                "capabilities": [
                    "launch-ehr",
                    "sso-openid-connect",
                    "client-public",
                ],
            }
            # TODO: Update the IdpLambdaArn.
            identity_provider_configuration = {
                "AuthorizationStrategy": "SMART_ON_FHIR_V1",
                "FineGrainedAuthorizationEnabled": True,
                "IdpLambdaArn": "arn:aws:lambda:your-region:your-account-id:function:your-lambda-name",
                "Metadata": json.dumps(metadata),
            }
            data_store = self.create_fhir_datastore(
                datastore_name, sse_configuration, identity_provider_configuration
            )
            # snippet-end:[python.example_code.healthlake.CreateFHIRDatastore.smart]
        else:
            data_store = self.create_fhir_datastore(datastore_name)

        data_store_id = data_store["DatastoreId"]
        data_store_arn = data_store["DatastoreArn"]

        self.wait_datastore_active(data_store_id)
        data_stores = self.list_fhir_datastores()

        print(f"{len(data_stores)} data store(s) found.")
        for data_store in data_stores:
            if data_store["DatastoreId"] == data_store_id:
                logger.info(
                    "Data store with ID %s is %s.",
                    data_store_id,
                    data_store["DatastoreStatus"],
                )
                break
        tags = [{"Key": "TagKey", "Value": "TagValue"}]

        self.tag_resource(data_store_arn, tags)

        tags = self.list_tags_for_resource(data_store_arn)
        print(f"{len(tags)} tag(s) found.")
        for tag in tags:
            print(f"Tag key: {tag['Key']}, value: {tag['Value']}")

        keys = []
        for tag in tags:
            keys.append(tag["Key"])

        self.untag_resource(data_store_arn, keys)

        import_job = self.start_fhir_import_job(
            import_job_name,
            data_store_id,
            input_s3_uri,
            output_s3_uri,
            kms_key_id,
            data_access_role_arn,
        )

        import_job_id = import_job["JobId"]
        print(f"Started import job with ID: {import_job_id}")

        self.wait_import_job_complete(data_store_id, import_job_id)

        import_jobs = self.list_fhir_import_jobs(
            data_store_id, submitted_after=datetime.now() - timedelta(days=1)
        )
        print(f"{len(import_jobs)} import job(s) found.")
        for import_job in import_jobs:
            print(
                f"Job id: {import_job['JobId']}, status: {import_job['JobStatus']}, submit time: {import_job['SubmitTime']}"
            )

        export_job = self.start_fhir_export_job(
            export_job_name, data_store_id, output_s3_uri, kms_key_id, data_access_role_arn
        )

        export_job_id = export_job["JobId"]
        print(f"Started export job with ID: {export_job_id}")
        self.wait_export_job_complete(data_store_id, export_job_id)

        export_jobs = self.list_fhir_export_jobs(
            data_store_id, submitted_after=datetime.now() - timedelta(days=1)
        )
        print(f"{len(export_jobs)} export job(s) found.")
        for export_job in export_jobs:
            print(
                f"Job id: {export_job['JobId']}, status: {export_job['JobStatus']}, submit time: {export_job['SubmitTime']}"
            )

        self.delete_fhir_datastore(data_store_id)
        print(f"Data store with ID {data_store_id} deleted.")


if __name__ == "__main__":
    health_lake_wrapper = HealthLakeWrapper.from_client()
    health_lake_wrapper.health_lake_demo()
