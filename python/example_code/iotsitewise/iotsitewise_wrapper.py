# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import logging
from typing import Any, Dict, List, Optional, Union

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


    @classmethod
    def from_client(cls) -> "IoTSitewiseWrapper":
        """
        Creates an IoTSitewiseWrapper instance with a default AWS IoT SiteWise client.

        :return: An instance of IoTSitewiseWrapper initialized with the default AWS IoT SiteWise client.
        """
        iotsitewise_client = boto3.client("iotsitewise")
        return cls(iotsitewise_client)

    # snippet-end:[python.example_code.iotsitewise.IoTSitewiseWrapper.decl]

