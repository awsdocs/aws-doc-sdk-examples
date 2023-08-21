# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage and invoke AWS HealthImaging
functions.
"""

import logging
import boto3

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class MedicalImagingWrapper:
    def __init__(self, health_imaging_client):
        self.health_imaging_client = health_imaging_client

    def create_data_store(self, name):
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


if __name__ == '__main__':
    try:
        medical_imaging_wrapper = MedicalImagingWrapper(boto3.client('medical-imaging'))
        data_store = medical_imaging_wrapper.create_data_store("MyDataStore")
    except Exception:
        logging.exception("Something went wrong with the demo!")
