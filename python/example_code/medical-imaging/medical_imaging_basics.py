# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage and invoke AWS HealthImaging
functions.
"""

import logging

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class MedicalImagingWrapper:
    def __init__(self, health_imaging_client):
        self.health_imaging_client = health_imaging_client

    # snippet-start:[python.example_code.medical-imaging.CreateDatastore]
    def create_datastore(self, name):
        """
        Create a data store.

        :param name: The name of the data store to create.
        :return: The data store ID.
        """
        try:
            data_store = self.health_imaging_client.create_datastore(datastoreName=name)
        except ClientError as err:
            logger.error(
                "Couldn't create data store %s. Here's why: %s: %s", name, err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return data_store['datastoreId']
    # snippet-end:[python.example_code.medical-imaging.CreateDatastore]

    # snippet-start:[python.example_code.medical-imaging.GetDatastore]
    def get_datastore_properties(self, datastore_id):
        """
        Get the properties of a data store.

        :param datastore_id: The ID of the data store to get.
        :return: The data store properties.
        """
        try:
            data_store = self.health_imaging_client.get_datastore(datastoreId=datastore_id)
        except ClientError as err:
            logger.error(
                "Couldn't get data store %s. Here's why: %s: %s", id, err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return data_store['datastoreProperties']
    # snippet-end:[python.example_code.medical-imaging.GetDatastore]

    # snippet-start:[python.example_code.medical-imaging.ListDatastores]
    def list_datastores(self):
        """
        List the data stores.

        :return: The list of data stores.
        """
        try:
            data_stores = self.health_imaging_client.list_datastores()
        except ClientError as err:
            logger.error(
                "Couldn't list data stores. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return data_stores['datastoreSummaries']
    # snippet-end:[python.example_code.medical-imaging.ListDatastores]

    # snippet-start:[python.example_code.medical-imaging.DeleteDatastore]
    def delete_datastore(self, datastore_id):
        """
        Delete a data store.

        :param datastore_id: The ID of the data store to delete.
        """
        try:
            self.health_imaging_client.delete_datastore(datastoreId=datastore_id)
        except ClientError as err:
            logger.error(
                "Couldn't delete data store %s. Here's why: %s: %s", datastore_id, err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.medical-imaging.DeleteDatastore]