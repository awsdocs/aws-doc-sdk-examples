# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import sys
from datetime import datetime, timedelta
import pytest

sys.path.append("../")
from date_utilities import DateUtilities
from exec import CloudWatchLogsQueryRunner

date_utility = DateUtilities()
runner = CloudWatchLogsQueryRunner()


@pytest.mark.integ
def test_run_successfully():
    now = datetime.utcnow()
    ten_days_ago = now - timedelta(days=10)
    query_start_date = date_utility.convert_datetime_to_iso8601(ten_days_ago)
    query_end_date = date_utility.convert_datetime_to_iso8601(now)
    runner.execute_query(query_start_date, query_end_date)
