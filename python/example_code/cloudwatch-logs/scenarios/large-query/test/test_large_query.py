# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
from datetime import datetime, timedelta
import logging
import os
import sys

import boto3
from botocore.config import Config

sys.path.append("../")
from cloudwatch_query import CloudWatchQuery
from date_utilities import DateUtilities
from exec import CloudWatchLogsQueryRunner


def test_run_successfully():
    # Get the current datetime
    now = datetime.now()
    ten_days_ago = now - timedelta(days=10)
    query_start_date = DateUtilities.convert_datetime_to_unix_timestamp(ten_days_ago)
    query_end_date = DateUtilities.convert_datetime_to_unix_timestamp(now)
    runner = CloudWatchLogsQueryRunner()
    start_date_iso1806 = DateUtilities.convert_unix_timestamp_to_iso1806(query_start_date)
    end_date_iso1806 = DateUtilities.convert_unix_timestamp_to_iso1806(query_end_date)
    runner.execute_query(start_date_iso1806, end_date_iso1806)

