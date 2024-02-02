# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import sys
from datetime import datetime, timedelta
import pytest

sys.path.append("../")
from date_utilities import DateUtilities
from exec import CloudWatchLogsQueryRunner


@pytest.mark.integ
def test_run_successfully():
    # Get the current datetime
    now = datetime.now()
    ten_days_ago = now - timedelta(days=10)
    query_start_date = DateUtilities.convert_datetime_to_unix_timestamp(ten_days_ago)
    query_end_date = DateUtilities.convert_datetime_to_unix_timestamp(now)
    runner = CloudWatchLogsQueryRunner()
    start_date_iso1806 = DateUtilities.convert_unix_timestamp_to_iso1806(
        query_start_date
    )
    end_date_iso1806 = DateUtilities.convert_unix_timestamp_to_iso1806(query_end_date)
    runner.execute_query(start_date_iso1806, end_date_iso1806)
