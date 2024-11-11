# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import logging
from typing import Any, Dict, List
import time

import boto3
from boto3 import client
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.iotsitewise.IoTSitewiseWrapper.class]
# snippet-start:[python.example_code.iotsitewise.IoTSitewiseWrapper.decl]
class IoTSitewiseWrapper:
    """Encapsulates AWS IoT SiteWise actions using the client interface."""


    def __init__(self, iotsitewise_client: client) -> None:
        """
        Initializes the IoTSitewiseWrapper with an AWS IoT SiteWise client.

        :param iotsitewise_client: A Boto3 AWS IoT SiteWise client. This client provides low-level
                           access to AWS IoT SiteWise services.
        """
        self.iotsitewise_client = iotsitewise_client
        self.entry_id = 0


    @classmethod
    def from_client(cls) -> "IoTSitewiseWrapper":
        """
        Creates an IoTSitewiseWrapper instance with a default AWS IoT SiteWise client.

        :return: An instance of IoTSitewiseWrapper initialized with the default AWS IoT SiteWise client.
        """
        iotsitewise_client = boto3.client("iotsitewise")
        return cls(iotsitewise_client)

    # snippet-end:[python.example_code.iotsitewise.IoTSitewiseWrapper.decl]

    # snipped-start:[python.example_code.iotsitewise.CreateAssetModel]
    def create_asset_model(self, asset_model_name: str) -> str:
        """
        Creates an AWS IoT SiteWise Asset Model.

        :param asset_model_name: The name of the asset model to create.
        :return: The ID of the created asset model.
        """
        try:
            properties = [
                {
                    "name": "temperature",
                    "dataType": "DOUBLE",
                    "type": {
                        "measurement": {},
                    },
                },
                {
                    "name": "humidity",
                    "dataType": "DOUBLE",
                    "type": {
                        "measurement": {},
                    },
                }
            ]
            response = self.iotsitewise_client.create_asset_model(
                assetModelName=asset_model_name,
                assetModelDescription="This is a sample asset model description.",
                assetModelProperties=properties
            )
            asset_model_id = response["assetModelId"]
            return asset_model_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceAlreadyExistsException":
                logger.error("Asset model %s already exists.", asset_model_name)
            else:
                logger.error("Error creating asset model %s. Here's why %s",
                         asset_model_name, err.response["Error"]["Message"])
            raise
    # snipped-end:[python.example_code.iotsitewise.CreateAssetModel]

    # snipped-start:[python.example_code.iotsitewise.CreateAsset]
    def create_asset(self, asset_name: str, asset_model_id: str) -> str:
        """
        Creates an AWS IoT SiteWise Asset.

        :param asset_name: The name of the asset to create.
        :param asset_model_id: The ID of the asset model to associate with the asset.
        :return: The ID of the created asset.
        """
        try:
            response = self.iotsitewise_client.create_asset(
                assetName=asset_name,
                assetModelId=asset_model_id
            )
            asset_id = response["assetId"]
            return asset_id
        except ClientError as err:
            if err.response["Error"] == "ResourceNotFoundException":
                logger.error("Asset model %s does not exist.", asset_model_id)
            else:
                logger.error("Error creating asset %s. Here's why %s",
                         asset_name, err.response["Error"]["Message"])
            raise
    # snipped-end:[python.example_code.iotsitewise.CreateAsset]

    # snipped-start:[python.example_code.iotsitewise.ListAssetModels]
    def list_asset_models(self) -> List[Dict[str, Any]]:
        """
        Lists all AWS IoT SiteWise Asset Models.

        :return: A list of dictionaries containing information about each asset model.

        """
        try:
            asset_models = []
            paginator = self.iotsitewise_client.get_paginator('list_asset_models')
            pages = paginator.paginate()
            for page in pages:
                asset_models.extend(page["assetModelSummaries"])
            return asset_models
        except ClientError as err:
            logger.error("Error listing asset models. Here's why %s",
                         err.response["Error"]["Message"])
            raise
    # snipped-end:[python.example_code.iotsitewise.ListAssetModels]

    # snipped-start:[python.example_code.iotsitewise.ListAssetModelProperties]
    def list_asset_model_properties(self, asset_model_id: str) -> List[Dict[str, Any]]:
        """
        Lists all AWS IoT SiteWise Asset Model Properties.

        :param asset_model_id: The ID of the asset model to list double_properties for.
        :return: A list of dictionaries containing information about each asset model property.
        """
        try:
            asset_model_properties = []
            paginator = self.iotsitewise_client.get_paginator('list_asset_model_properties')
            pages = paginator.paginate(assetModelId=asset_model_id)
            for page in pages:
                asset_model_properties.extend(page["assetModelPropertySummaries"])
            return asset_model_properties
        except ClientError as err:
            logger.error("Error listing asset model double_properties. Here's why %s",
                         err.response["Error"]["Message"])
            raise

    # snipped-start:[python.example_code.iotsitewise.BatchPutAssetPropertyValue]
    def batch_put_asset_property_value(self, asset_id: str, double_properties: List[Dict[str, str]]) -> None:
        """
        Sends data to an AWS IoT SiteWise Asset.

        :param asset_id: The asset ID.
        :param double_properties: A list of dictionaries containing the values in the form
                        {propertyId : property_id,
                        value_type : [stringValue|integerValue|doubleValue|booleanValue],
                        value : the_value}.
        """
        try:

            entries = []
            for value in double_properties:
                epoch_ns = time.time_ns()
                self.entry_id += 1
                if value["value_type"] == "stringValue":
                    value = {"stringValue": value["value"]}
                elif value["value_type"] == "integerValue":
                    value = {"integerValue": value["value"]}
                elif value["value_type"] == "booleanValue":
                    value = {"booleanValue": value["value"]}
                elif value["value_type"] == "doubleValue":
                    value = {"doubleValue": value["value"]}
                else:
                    raise ValueError("Invalid value_type: %s", value["value_type"])
                entry = {
                        "entryId": f"{self.entry_id}",
                        "assetId": asset_id,
                        "propertyId": value["propertyId"],
                        "propertyValues": [
                            {
                                "value": value,
                                "timestamp": {
                                    "timeInSeconds": int(epoch_ns / 1000000000),
                                    "offsetInNanos": epoch_ns % 1000000000
                                }
                            }
                        ]
                }
                entries.append(entry)
            self.iotsitewise_client.batch_put_asset_property_value(entries=entries)
        except ClientError as err:
            logger.error("Error sending data to asset. Here's why %s",
                         err.response["Error"]["Message"])
            raise

    # snipped-end:[python.example_code.iotsitewise.BatchPutAssetPropertyValue]

    # snipped-start:[python.example_code.iotsitewise.DeleteAsset]
    def delete_asset(self, asset_id: str) -> None:
        """
        Deletes an AWS IoT SiteWise Asset.

        :param asset_id: The ID of the asset to delete.
        """
        try:
            self.iotsitewise_client.delete_asset(assetId=asset_id)
        except ClientError as err:
            logger.error("Error deleting asset %s. Here's why %s",
                         asset_id, err.response["Error"]["Message"])
            raise
    # snipped-end:[python.example_code.iotsitewise.DeleteAsset]

    # snipped-start:[python.example_code.iotsitewise.DeleteAssetModel]
    def delete_asset_model(self, asset_model_id: str) -> None:
        """
        Deletes an AWS IoT SiteWise Asset Model.

        :param asset_model_id: The ID of the asset model to delete.
        """
        try:
            self.iotsitewise_client.delete_asset_model(assetModelId=asset_model_id)
        except ClientError as err:
            logger.error("Error deleting asset model %s. Here's why %s",
                         asset_model_id, err.response["Error"]["Message"])
            raise
    # snipped-end:[python.example_code.iotsitewise.DeleteAssetModel]

    def wait_asset_model_active(self, asset_model_id: str) -> None:
        """
        Waits for an AWS IoT SiteWise Asset Model to become active.

        :param asset_model_id: The ID of the asset model to wait for.
        """
        try:
            waiter = self.iotsitewise_client.get_waiter('asset_model_active')
            waiter.wait(assetModelId=asset_model_id)
        except ClientError as err:
            logger.error("Error waiting for asset model %s to become active. Here's why %s",
                         asset_model_id, err.response["Error"]["Message"])
            raise

    def wait_asset_active(self, asset_id: str) -> None:
        """
        Waits for an AWS IoT SiteWise Asset to become active.

        :param asset_id: The ID of the asset to wait for.
        """
        try:
            waiter = self.iotsitewise_client.get_waiter('asset_active')
            waiter.wait(assetId=asset_id)
        except ClientError as err:
            logger.error("Error waiting for asset %s to become active. Here's why %s",
                         asset_id, err.response["Error"]["Message"])
            raise

    def wait_asset_deleted(self, asset_id: str) -> None:
        """
        Waits for an AWS IoT SiteWise Asset to be deleted.

        :param asset_id: The ID of the asset to wait for.
        """
        try:
            waiter = self.iotsitewise_client.get_waiter('asset_not_exists')
            waiter.wait(assetId=asset_id)
        except ClientError as err:
            logger.error("Error waiting for asset %s to be deleted. Here's why %s",
                         asset_id, err.response["Error"]["Message"])
            raise
