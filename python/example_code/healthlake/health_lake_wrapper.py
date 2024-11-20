# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage and invoke AWS HealthImaging
functions.
"""

from boto3 import client
import logging

import boto3
from botocore.exceptions import ClientError

import time

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.medical-imaging.HealthLakeWrapper]
class HealthLakeWrapper:
    def __init__(self, health_lake_client):
        self.health_lake_client = health_lake_client


    # snippet-start:[python.example_code.medical-imaging.HealthLakeWrapper.decl]
    @classmethod
    def from_client(cls) -> "HealthLakeWrapper":
        """
        Creates a HealthLakeWrapper instance with a default AWS HealthLake client.

        :return: An instance of HealthLakeWrapper initialized with the default HealthLake client.
        """
        kms_client = boto3.client("healthlake")
        return cls(kms_client)

    # snippet-end:[python.example_code.medical-imaging.HealthLakeWrapper.decl]

    # snippet-start:[python.example_code.medical-imaging.CreateFHIRDatastore]
    def create_fihr_datastore(self, datastore_name: str, sse_configuration : dict[str, any] = None,
                              identity_provider_configuration : dict[str, any] = None) -> str:
        """
        Creates a new HealthLake datastore.
        When creating a SMART on FHIR datastore, the following parameters are required:
        - sse_configuration: The server-side encryption configuration for a SMART on FHIR-enabled data store.
        - identity_provider_configuration: The identity provider configuration for a SMART on FHIR-enabled data store.

        :param datastore_name: The name of the data store.
        :param sse_configuration: The server-side encryption configuration for a SMART on FHIR-enabled data store.
        :param identity_provider_configuration: The identity provider configuration for a SMART on FHIR-enabled data store.
        :return: The datastore ID.
        """
        try:
            parameters = {
                'DatastoreName': datastore_name,
                'DatastoreTypeVersion' : 'R4'
            }
            if sse_configuration is not None and identity_provider_configuration is not None:
                # Creating a SMART on FHIR-enabled data store
                parameters['SseConfiguration'] = sse_configuration
                parameters['IdentityProviderConfiguration'] = identity_provider_configuration

            response = self.health_lake_client.create_fhir_datastore(**parameters)
            return response['datastoreId']
        except ClientError as err:
            logger.exception("Couldn't create datastore %s. Here's why",
                             datastore_name, err.response["Error"]["Message"])
            raise

    # snippet-end:[python.example_code.medical-imaging.CreateFHIRDatastore]

    # snippet-start:[python.example_code.medical-imaging.DescribeFHIRDatastore]
    def describe_fhir_datastore(self, datastore_id: str) -> dict[str, any]:
        """
        Describes a HealthLake datastore.
        :param datastore_id: The datastore ID.
        :return: The datastore description.
        """
        try:
            response = self.health_lake_client.describe_fhir_datastore(
                DatastoreId=datastore_id)
            return response['DatastoreProperties']
        except ClientError as err:
            logger.exception("Couldn't describe datastore with ID %s. Here's why",
                             datastore_id, err.response["Error"]["Message"])
            raise

    # snippet-end:[python.example_code.medical-imaging.HealthLakeWrapper]

    def wait_datastore_active(self, datastore_id: str) -> None:
        """
        Waits for a HealthLake datastore to become active.
        :param datastore_id: The datastore ID.
        """
        counter = 0
        max_count_minutes = 40 # It can take a while to create a datastore, so we'll wait up to 40 minutes.
        data_store_active = False
        while counter < max_count_minutes:
            datastore = self.health_lake_client.describe_fhir_datastore(
                DatastoreId=datastore_id)
            if datastore["DatastoreProperties"]["DatastoreStatus"] == "ACTIVE":
                data_store_active = True
                break
            else:
                counter += 1
                time.sleep(60)

        if data_store_active :
            logger.info("Datastore with ID %s is active after %d minutes.", datastore_id, counter)
        else:
            raise ClientError("Datastore with ID %s is not active after %d minutes.", datastore_id, counter)

        try:
            waiter = self.health_lake_client.get_waiter("datastore_active")
            waiter.wait(DatastoreId=datastore_id)
        except ClientError as err:
            logger.exception("Data store with ID %s failed to become active. Here's why",
                             datastore_id, err.response["Error"]["Message"])
            raise

    def health_lake_demo(self) -> None:
        use_smart_data_store = False
        testing_code = True

        datastore_name = "health_imaging_datastore"
        if use_smart_data_store:
            pass
        else:
            data_store_id = self.health_imaging_client.list_datastores(
                maxResults=1
            )['datastoreResults'][0]['datastoreId']


if __name__ == "__main__":
    health_lake_wrapper = HealthLakeWrapper.from_client()

