#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Script to generate and upload 50,000 sample log entries to CloudWatch Logs.
This script creates logs spanning 5 minutes and uploads them in batches.
"""

import boto3
import time
from datetime import datetime

LOG_GROUP_NAME = "/workflows/cloudwatch-logs/large-query"
LOG_STREAM_NAME = "stream1"
TOTAL_ENTRIES = 50000
ENTRIES_PER_BATCH = 10000
FIVE_MINUTES_MS = 5 * 60 * 1000


def main():
    """Generate and upload log entries to CloudWatch Logs."""
    client = boto3.client('logs')
    
    # Calculate timestamps
    start_time_ms = int(time.time() * 1000)
    timestamp_increment = FIVE_MINUTES_MS // TOTAL_ENTRIES
    
    print(f"Generating {TOTAL_ENTRIES} log entries...")
    print(f"QUERY_START_DATE={start_time_ms}")
    
    entry_count = 0
    current_timestamp = start_time_ms
    
    # Generate and upload logs in batches
    num_batches = TOTAL_ENTRIES // ENTRIES_PER_BATCH
    
    for batch_num in range(num_batches):
        log_events = []
        
        for i in range(ENTRIES_PER_BATCH):
            log_events.append({
                'timestamp': current_timestamp,
                'message': f'Entry {entry_count}'
            })
            
            entry_count += 1
            current_timestamp += timestamp_increment
        
        # Upload batch
        try:
            client.put_log_events(
                logGroupName=LOG_GROUP_NAME,
                logStreamName=LOG_STREAM_NAME,
                logEvents=log_events
            )
            print(f"Uploaded batch {batch_num + 1}/{num_batches}")
        except Exception as e:
            print(f"Error uploading batch {batch_num + 1}: {e}")
            return 1
    
    end_time_ms = current_timestamp - timestamp_increment
    print(f"QUERY_END_DATE={end_time_ms}")
    print(f"Successfully uploaded {TOTAL_ENTRIES} log entries")
    
    return 0


if __name__ == "__main__":
    exit(main())
