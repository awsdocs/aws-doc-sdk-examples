# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
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
    os.environ["QUERY_START_DATE"] = "1706217941000"
    os.environ["QUERY_END_DATE"] = "1706218240994"
    runner = CloudWatchLogsQueryRunner()
    query_start_date, query_end_date = runner.fetch_environment_variables()
    start_date_iso1806, end_date_iso1806 = runner.convert_dates_to_iso1806(
        query_start_date, query_end_date
    )
    runner.execute_query(start_date_iso1806, end_date_iso1806)
