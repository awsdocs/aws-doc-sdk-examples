# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS IoT SiteWise unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

import datetime
import random
import string
from botocore.stub import ANY
import time

from test_tools.example_stubber import ExampleStubber


def random_string(length):
    return "".join([random.choice(string.ascii_lowercase) for _ in range(length)])


class IoTSitewiseStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    IAM unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 AWS IoT SiteWise client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_asset_model(
        self,
        asset_model_name,
        assetModelDescription,
        assetModelProperties,
        assetModelId,
        error_code=None,
    ):
        expected_params = {
            "assetModelName": asset_model_name,
            "assetModelDescription": assetModelDescription,
            "assetModelProperties": assetModelProperties,
        }

        response = {
            "assetModelId": assetModelId,
            "assetModelArn": "arn:aws:iotsitewise:us-west-2:123456789012:asset-model/a1b2c3d4-5678-90ab-cdef-11111EXAMPLE",
            "assetModelStatus": {"state": "CREATING"},
        }

        self._stub_bifurcator(
            "create_asset_model", expected_params, response, error_code=error_code
        )

    def stub_create_asset(self, asset_name, asset_model_id, asset_id, error_code=None):
        expected_params = {"assetName": asset_name, "assetModelId": asset_model_id}

        response = {
            "assetId": asset_id,
            "assetArn": "arn:aws:iotsitewise:us-west-2:123456789012:asset/a1b2c3d4-5678-90ab-cdef-33333EXAMPLE",
            "assetStatus": {"state": "CREATING"},
        }
        self._stub_bifurcator(
            "create_asset", expected_params, response, error_code=error_code
        )

    def stub_list_asset_model_properties(
        self,
        asset_model_id,
        property_name,
        property_id,
            data_type,
            nextToken=None,
            truncated=False,
            error_code=None,
    ):
        expected_params = {"assetModelId": asset_model_id}
        if nextToken is not None:
            expected_params["nextToken"] = nextToken
        response = {
            "assetModelPropertySummaries": [
                {
                    "id": property_id,
                    "name": property_name,
                    "dataType": data_type,
                    "type": {},
                    # "assetModelPropertyArn": f"arn:aws:iotsitewise:us-west-2:123456789012:asset-model/a1b2c3d4-5678-90ab-cdef-11111EXAMPLE/properties/{humidity_property_id}",
                },

            ]
        }
        print(f"stub_list_asset_model_properties nextToken {nextToken} truncated {truncated}")
        if truncated:
            response["nextToken"] = "test-token"

        self._stub_bifurcator(
            "list_asset_model_properties", expected_params, response, error_code=error_code
        )
        
    @classmethod
    def properties_to_values(cls, asset_id, entry_id, values, time_ns):
        """
        Utility function to convert a values list to an entries parameter for batch_put_asset_property_value.
        This matches a function in IoTSitewiseWrapper
        """
        entries = []
        for value in values:
            epoch_ns = time_ns
            entry_id += 1
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
                "entryId": f"{entry_id}",
                "assetId": asset_id,
                "propertyId": value["propertyId"],
                "propertyValues": [
                    {
                        "value": property_value,
                        "timestamp": {
                            "timeInSeconds": int(epoch_ns / 1000000000),
                            "offsetInNanos": epoch_ns % 1000000000
                        }
                    }
                ]
            }
            entries.append(entry)
        return entries

    def stub_batch_put_asset_property_value(self, asset_id, entry_id, values, time_ns, error_code=None):
        entries= self.properties_to_values(asset_id, entry_id, values, time_ns)
        expected_params = {"entries": entries}
        response = {
            "errorEntries": []
        }

        self._stub_bifurcator(
            "batch_put_asset_property_value", expected_params, response, error_code=error_code
        )
    
    def stub_get_asset_property_value(self, asset_id, property_id, property_value, time_ns, error_code=None):
        expected_params = {"assetId": asset_id, "propertyId": property_id}
        response = {
            "propertyValue": {"value": {"doubleValue": property_value},
                              "timestamp" : {"timeInSeconds": int(time_ns / 1000000000),
                                             "offsetInNanos": time_ns % 1000000000

                                             }
                              }
        }

        self._stub_bifurcator(
            "get_asset_property_value", expected_params, response, error_code=error_code
        )
    
    def stub_create_portal(self, portal_name, iam_role, email, portal_id, error_code=None):
        expected_params = {
            "portalName": portal_name,
            "roleArn": iam_role,
            "portalContactEmail": email
        }
        response = {
            "portalId": portal_id,
            "portalArn": "arn:aws:iotsitewise:us-west-2:123456789012:portal/a1b2c3d4-5678-90ab-cdef-22222EXAMPLE",
            "portalStatus": {"state": "CREATING"},
            "portalStartUrl": f"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX{portal_id}",
            "ssoApplicationId" : "01234567-1234-0123-1234-0123456789ae"
        }
        self._stub_bifurcator(
            "create_portal", expected_params, response, error_code=error_code
        )

    def stub_describe_portal(self, portal_id, portal_name, error_code=None):
        expected_params = {"portalId": portal_id}
        response = {
            "portalId": portal_id,
            "portalName": portal_name,
            "portalArn": "arn:aws:iotsitewise:us-west-2:123456789012:portal/a1b2c3d4-5678-90ab-cdef-22222EXAMPLE",
            "portalStatus": {"state": "ACTIVE"},
            "portalStartUrl": f"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX{portal_id}",
            "portalClientId" : "12345678-1234-0123-1234-0123456789ae",
            "portalContactEmail" : "user@example.com",
            "portalCreationDate" : datetime.datetime(2015, 1, 1),
            "portalLastUpdateDate" : datetime.datetime(2015, 1, 1),
        }
        self._stub_bifurcator(
            "describe_portal", expected_params, response, error_code=error_code
        )
        
    def stub_create_gateway(self, gateway_name, my_thing, gateway_id, error_code=None):
        expected_params = {
            "gatewayName": gateway_name,
            "gatewayPlatform": {
                "greengrassV2": {"coreDeviceThingName": my_thing},
            },
            "tags" : {"Environment": "Production"},
        }
        response = {
            "gatewayId": gateway_id,
            "gatewayArn": "arn:aws:iotsitewise:us-west-2:123456789012:portal/a1b2c3d4-5678-90ab-cdef-22222EXAMPLE",
        }
        self._stub_bifurcator(
            "create_gateway", expected_params, response, error_code=error_code
        )

    def stub_describe_gateway(self, gateway_id, error_code=None):
        expected_params = {"gatewayId": gateway_id}
        response = {
            "gatewayId": gateway_id,
            "gatewayName": "MyGateway",
            "gatewayArn": "arn:aws:iotsitewise:us-west-2:123456789012:gateway/a1b2c3d4-5678-90ab-cdef-22222EXAMPLE",
            "gatewayPlatform": {
                "greengrassV2": {
                    "coreDeviceThingName": "MyThing"
                }
            },
            "gatewayCapabilitySummaries": [],
            "creationDate": datetime.datetime(2015, 1, 1),
            "lastUpdateDate": datetime.datetime(2015, 1, 1),
        }
        self._stub_bifurcator(
            "describe_gateway", expected_params, response, error_code=error_code
        )

    def stub_delete_gateway(self, gateway_id, error_code=None):
        expected_params = {"gatewayId": gateway_id}
        self._stub_bifurcator(
            "delete_gateway", expected_params, error_code=error_code
        )

    def stub_delete_portal(self, portal_id, error_code=None):
        expected_params = {"portalId": portal_id}

        response = {
            "portalStatus": {"state": "DELETING"},
        }
        self._stub_bifurcator(
            "delete_portal", expected_params, response, error_code=error_code
        )
    
    def stub_delete_asset(self, asset_id, error_code=None):
        expected_params = {"assetId": asset_id}

        response = {
            "assetStatus": {"state": "DELETING"},
        }
        self._stub_bifurcator(
            "delete_asset", expected_params, response, error_code=error_code
        )
    
    def stub_delete_asset_model(self, asset_model_id, error_code=None):
        expected_params = {"assetModelId": asset_model_id}

        response = {
            "assetModelStatus": {"state": "DELETING"},
        }
        self._stub_bifurcator(
            "delete_asset_model", expected_params, response, error_code=error_code
        )