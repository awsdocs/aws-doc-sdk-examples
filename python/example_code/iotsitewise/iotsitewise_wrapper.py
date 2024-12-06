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
        self.entry_id = 0 # Incremented to generate unique entry IDs for batch_put_asset_property_value.

    @classmethod
    def from_client(cls) -> "IoTSitewiseWrapper":
        """
        Creates an IoTSitewiseWrapper instance with a default AWS IoT SiteWise client.

        :return: An instance of IoTSitewiseWrapper initialized with the default AWS IoT SiteWise client.
        """
        iotsitewise_client = boto3.client("iotsitewise")
        return cls(iotsitewise_client)

    # snippet-end:[python.example_code.iotsitewise.IoTSitewiseWrapper.decl]

    # snippet-start:[python.example_code.iotsitewise.CreateAssetModel]
    def create_asset_model(
        self, asset_model_name: str, properties: List[Dict[str, Any]]
    ) -> str:
        """
        Creates an AWS IoT SiteWise Asset Model.

        :param asset_model_name: The name of the asset model to create.
        :param properties: The property definitions of the asset model.
        :return: The ID of the created asset model.
        """
        try:
            response = self.iotsitewise_client.create_asset_model(
                assetModelName=asset_model_name,
                assetModelDescription="This is a sample asset model description.",
                assetModelProperties=properties,
            )
            asset_model_id = response["assetModelId"]
            waiter = self.iotsitewise_client.get_waiter("asset_model_active")
            waiter.wait(assetModelId=asset_model_id)
            return asset_model_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceAlreadyExistsException":
                logger.error("Asset model %s already exists.", asset_model_name)
            else:
                logger.error(
                    "Error creating asset model %s. Here's why %s",
                    asset_model_name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iotsitewise.CreateAssetModel]

    # snippet-start:[python.example_code.iotsitewise.CreateAsset]
    def create_asset(self, asset_name: str, asset_model_id: str) -> str:
        """
        Creates an AWS IoT SiteWise Asset.

        :param asset_name: The name of the asset to create.
        :param asset_model_id: The ID of the asset model to associate with the asset.
        :return: The ID of the created asset.
        """
        try:
            response = self.iotsitewise_client.create_asset(
                assetName=asset_name, assetModelId=asset_model_id
            )
            asset_id = response["assetId"]
            waiter = self.iotsitewise_client.get_waiter("asset_active")
            waiter.wait(assetId=asset_id)
            return asset_id
        except ClientError as err:
            if err.response["Error"] == "ResourceNotFoundException":
                logger.error("Asset model %s does not exist.", asset_model_id)
            else:
                logger.error(
                    "Error creating asset %s. Here's why %s",
                    asset_name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iotsitewise.CreateAsset]

    # snippet-start:[python.example_code.iotsitewise.ListAssetModels]
    def list_asset_models(self) -> List[Dict[str, Any]]:
        """
        Lists all AWS IoT SiteWise Asset Models.

        :return: A list of dictionaries containing information about each asset model.

        """
        try:
            asset_models = []
            paginator = self.iotsitewise_client.get_paginator("list_asset_models")
            pages = paginator.paginate()
            for page in pages:
                asset_models.extend(page["assetModelSummaries"])
            return asset_models
        except ClientError as err:
            logger.error(
                "Error listing asset models. Here's why %s",
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.iotsitewise.ListAssetModels]

    # snippet-start:[python.example_code.iotsitewise.ListAssetModelProperties]
    def list_asset_model_properties(self, asset_model_id: str) -> List[Dict[str, Any]]:
        """
        Lists all AWS IoT SiteWise Asset Model Properties.

        :param asset_model_id: The ID of the asset model to list values for.
        :return: A list of dictionaries containing information about each asset model property.
        """
        try:
            asset_model_properties = []
            paginator = self.iotsitewise_client.get_paginator(
                "list_asset_model_properties"
            )
            pages = paginator.paginate(assetModelId=asset_model_id)
            for page in pages:
                asset_model_properties.extend(page["assetModelPropertySummaries"])
            return asset_model_properties
        except ClientError as err:
            logger.error(
                "Error listing asset model values. Here's why %s",
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.iotsitewise.ListAssetModelProperties]

    # snippet-start:[python.example_code.iotsitewise.BatchPutAssetPropertyValue]
    def batch_put_asset_property_value(
        self, asset_id: str, values: List[Dict[str, str]]
    ) -> None:
        """
        Sends data to an AWS IoT SiteWise Asset.

        :param asset_id: The asset ID.
        :param values: A list of dictionaries containing the values in the form
                        {propertyId : property_id,
                        valueType : [stringValue|integerValue|doubleValue|booleanValue],
                        value : the_value}.
        """
        try:
            entries = self.properties_to_values(asset_id, values)
            self.iotsitewise_client.batch_put_asset_property_value(entries=entries)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Asset %s does not exist.", asset_id)
            else:
                logger.error(
                    "Error sending data to asset. Here's why %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iotsitewise.BatchPutAssetPropertyValue]

    # snippet-start:[python.example_code.iotsitewise.BatchPutAssetPropertyValue.properties_to_values]
    def properties_to_values(
        self, asset_id: str, values: list[dict[str, Any]]
    ) -> list[dict[str, Any]]:
        """
        Utility function to convert a values list to the entries parameter for batch_put_asset_property_value.
        :param asset_id : The asset ID.
        :param values : A list of dictionaries containing the values in the form
                        {propertyId : property_id,
                        valueType : [stringValue|integerValue|doubleValue|booleanValue],
                        value : the_value}.
        :return: An entries list to pass as the 'entries' parameter to batch_put_asset_property_value.
        """
        entries = []
        for value in values:
            epoch_ns = time.time_ns()
            self.entry_id += 1
            if value["valueType"] == "stringValue":
                property_value = {"stringValue": value["value"]}
            elif value["valueType"] == "integerValue":
                property_value = {"integerValue": value["value"]}
            elif value["valueType"] == "booleanValue":
                property_value = {"booleanValue": value["value"]}
            elif value["valueType"] == "doubleValue":
                property_value = {"doubleValue": value["value"]}
            else:
                raise ValueError("Invalid valueType: %s", value["valueType"])
            entry = {
                "entryId": f"{self.entry_id}",
                "assetId": asset_id,
                "propertyId": value["propertyId"],
                "propertyValues": [
                    {
                        "value": property_value,
                        "timestamp": {
                            "timeInSeconds": int(epoch_ns / 1000000000),
                            "offsetInNanos": epoch_ns % 1000000000,
                        },
                    }
                ],
            }
            entries.append(entry)
        return entries

    # snippet-end:[python.example_code.iotsitewise.BatchPutAssetPropertyValue.properties_to_values]

    # snippet-start:[python.example_code.iotsitewise.GetAssetPropertyValue]
    def get_asset_property_value(
        self, asset_id: str, property_id: str
    ) -> Dict[str, Any]:
        """
        Gets the value of an AWS IoT SiteWise Asset Property.

        :param asset_id: The ID of the asset.
        :param property_id: The ID of the property.
        :return: A dictionary containing the value of the property.
        """
        try:
            response = self.iotsitewise_client.get_asset_property_value(
                assetId=asset_id, propertyId=property_id
            )
            return response["propertyValue"]
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Asset %s or property %s does not exist.", asset_id, property_id
                )
            else:
                logger.error(
                    "Error getting asset property value. Here's why %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iotsitewise.GetAssetPropertyValue]

    # snippet-start:[python.example_code.iotsitewise.CreatePortal]
    def create_portal(
        self, portal_name: str, iam_role_arn: str, portal_contact_email: str
    ) -> str:
        """
        Creates an AWS IoT SiteWise Portal.

        :param portal_name: The name of the portal to create.
        :param iam_role_arn: The ARN of an IAM role.
        :param portal_contact_email: The contact email of the portal.
        :return: The ID of the created portal.
        """
        try:
            response = self.iotsitewise_client.create_portal(
                portalName=portal_name,
                roleArn=iam_role_arn,
                portalContactEmail=portal_contact_email,
            )
            portal_id = response["portalId"]
            waiter = self.iotsitewise_client.get_waiter("portal_active")
            waiter.wait(portalId=portal_id, WaiterConfig={"MaxAttempts": 40})
            return portal_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceAlreadyExistsException":
                logger.error("Portal %s already exists.", portal_name)
            else:
                logger.error(
                    "Error creating portal %s. Here's why %s",
                    portal_name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iotsitewise.CreatePortal]

    # snippet-start:[python.example_code.iotsitewise.DescribePortal]
    def describe_portal(self, portal_id: str) -> Dict[str, Any]:
        """
        Describes an AWS IoT SiteWise Portal.

        :param portal_id: The ID of the portal to describe.
        :return: A dictionary containing information about the portal.
        """
        try:
            response = self.iotsitewise_client.describe_portal(portalId=portal_id)
            return response
        except ClientError as err:
            logger.error(
                "Error describing portal %s. Here's why %s",
                portal_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.iotsitewise.DescribePortal]

    # snippet-start:[python.example_code.iotsitewise.CreateGateway]
    def create_gateway(self, gateway_name: str, my_thing: str) -> str:
        """
        Creates an AWS IoT SiteWise Gateway.

        :param gateway_name: The name of the gateway to create.
        :param my_thing: The core device thing name.
        :return: The ID of the created gateway.
        """
        try:
            response = self.iotsitewise_client.create_gateway(
                gatewayName=gateway_name,
                gatewayPlatform={
                    "greengrassV2": {"coreDeviceThingName": my_thing},
                },
                tags={"Environment": "Production"},
            )
            gateway_id = response["gatewayId"]
            return gateway_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceAlreadyExistsException":
                logger.error("Gateway %s already exists.", gateway_name)
            else:
                logger.error(
                    "Error creating gateway %s. Here's why %s",
                    gateway_name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iotsitewise.CreateGateway]

    # snippet-start:[python.example_code.iotsitewise.DescribeGateway]
    def describe_gateway(self, gateway_id: str) -> Dict[str, Any]:
        """
        Describes an AWS IoT SiteWise Gateway.

        :param gateway_id: The ID of the gateway to describe.
        :return: A dictionary containing information about the gateway.
        """
        try:
            response = self.iotsitewise_client.describe_gateway(gatewayId=gateway_id)
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Gateway %s does not exist.", gateway_id)
            else:
                logger.error(
                    "Error describing gateway %s. Here's why %s",
                    gateway_id,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iotsitewise.DescribeGateway]

    # snippet-start:[python.example_code.iotsitewise.DeleteGateway]
    def delete_gateway(self, gateway_id: str) -> None:
        """
        Deletes an AWS IoT SiteWise Gateway.

        :param gateway_id: The ID of the gateway to delete.
        """
        try:
            self.iotsitewise_client.delete_gateway(gatewayId=gateway_id)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Gateway %s does not exist.", gateway_id)
            else:
                logger.error(
                    "Error deleting gateway %s. Here's why %s",
                    gateway_id,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iotsitewise.DeleteGateway]

    # snippet-start:[python.example_code.iotsitewise.DeletePortal]
    def delete_portal(self, portal_id: str) -> None:
        """
        Deletes an AWS IoT SiteWise Portal.

        :param portal_id: The ID of the portal to delete.
        """
        try:
            self.iotsitewise_client.delete_portal(portalId=portal_id)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Portal %s does not exist.", portal_id)
            else:
                logger.error(
                    "Error deleting portal %s. Here's why %s",
                    portal_id,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iotsitewise.DeletePortal]

    # snippet-start:[python.example_code.iotsitewise.DeleteAsset]
    def delete_asset(self, asset_id: str) -> None:
        """
        Deletes an AWS IoT SiteWise Asset.

        :param asset_id: The ID of the asset to delete.
        """
        try:
            self.iotsitewise_client.delete_asset(assetId=asset_id)
        except ClientError as err:
            logger.error(
                "Error deleting asset %s. Here's why %s",
                asset_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.iotsitewise.DeleteAsset]

    # snippet-start:[python.example_code.iotsitewise.DeleteAssetModel]
    def delete_asset_model(self, asset_model_id: str) -> None:
        """
        Deletes an AWS IoT SiteWise Asset Model.

        :param asset_model_id: The ID of the asset model to delete.
        """
        try:
            self.iotsitewise_client.delete_asset_model(assetModelId=asset_model_id)
        except ClientError as err:
            logger.error(
                "Error deleting asset model %s. Here's why %s",
                asset_model_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.iotsitewise.DeleteAssetModel]

    def wait_asset_deleted(self, asset_id: str) -> None:
        """
        Waits for an AWS IoT SiteWise Asset to be deleted.

        :param asset_id: The ID of the asset to wait for.
        """
        try:
            waiter = self.iotsitewise_client.get_waiter("asset_not_exists")
            waiter.wait(assetId=asset_id)
        except ClientError as err:
            logger.error(
                "Error waiting for asset %s to be deleted. Here's why %s",
                asset_id,
                err.response["Error"]["Message"],
            )
            raise


# snippet-end:[python.example_code.iotsitewise.IoTSitewiseWrapper.class]

if __name__ == "__main__":
    first = {
        "entries": [
            {
                "assetId": "a1b2c3d4-5678-90ab-cdef-33333EXAMPLE",
                "entryId": "1",
                "propertyId": "01234567-1234-0123-1234-0123456789ae",
                "propertyValues": [
                    {
                        "timestamp": {
                            "offsetInNanos": 508329000,
                            "timeInSeconds": 1731685525,
                        },
                        "value": {"doubleValue": 60.0},
                    }
                ],
            },
            {
                "assetId": "a1b2c3d4-5678-90ab-cdef-33333EXAMPLE",
                "entryId": "2",
                "propertyId": "12345678-1234-0123-1234-0123456789ae",
                "propertyValues": [
                    {
                        "timestamp": {
                            "offsetInNanos": 508329000,
                            "timeInSeconds": 1731685525,
                        },
                        "value": {"doubleValue": 23.5},
                    }
                ],
            },
        ]
    }

    second = {
        "entries": [
            {
                "assetId": "a1b2c3d4-5678-90ab-cdef-33333EXAMPLE",
                "entryId": "1",
                "propertyId": "01234567-1234-0123-1234-0123456789ae",
                "propertyValues": [
                    {
                        "timestamp": {
                            "offsetInNanos": 508329000,
                            "timeInSeconds": 1731685525,
                        },
                        "value": {"doubleValue": 65.0},
                    }
                ],
            },
            {
                "assetId": "a1b2c3d4-5678-90ab-cdef-33333EXAMPLE",
                "entryId": "2",
                "propertyId": "12345678-1234-0123-1234-0123456789ae",
                "propertyValues": [
                    {
                        "timestamp": {
                            "offsetInNanos": 508329000,
                            "timeInSeconds": 1731685525,
                        },
                        "value": {"doubleValue": 23.5},
                    }
                ],
            },
        ]
    }

    entries1 = first["entries"]
    entries2 = second["entries"]
    entries1 = sorted(entries1, key=lambda d: d["assetId"])
    entries2 = sorted(entries2, key=lambda d: d["assetId"])

    # compare each element
    for idx, val in enumerate(entries1):
        if val["assetId"] != entries2[idx]["assetId"]:
            print(
                f"Asset ID mismatch at index {idx}: {val['assetId']} != {entries2[idx]['assetId']}"
            )
        if val["entryId"] != entries2[idx]["entryId"]:
            print(
                f"Entry ID mismatch at index {idx}: {val['entryId']} != {entries2[idx]['entryId']}"
            )
        if val["propertyId"] != entries2[idx]["propertyId"]:
            print(
                f"Property ID mismatch at index {idx}: {val['propertyId']} != {entries2[idx]['propertyId']}"
            )
        if (
            val["propertyValues"][0]["timestamp"]
            != entries2[idx]["propertyValues"][0]["timestamp"]
        ):
            print(
                f"Timestamp mismatch at index {idx}: {val['propertyValues'][0]['timestamp']} != {entries2[idx]['propertyValues'][0]['timestamp']}"
            )
        if (
            val["propertyValues"][0]["value"]
            != entries2[idx]["propertyValues"][0]["value"]
        ):
            print(
                f"Value mismatch at index {idx}: {val['propertyValues'][0]['value']} != {entries2[idx]['propertyValues'][0]['value']}"
            )
