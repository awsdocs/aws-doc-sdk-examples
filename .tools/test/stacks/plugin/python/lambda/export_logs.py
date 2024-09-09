# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import json
import logging
import os
import io
import csv

import boto3

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

log_group_name = "/aws/batch/job"

s3_client = boto3.client("s3")
logs_client = boto3.client("logs")

tool_name = os.environ["TOOL_NAME"]
admin_bucket_name = os.environ["ADMIN_BUCKET_NAME"]
local_bucket_name = os.environ["LOCAL_BUCKET_NAME"]


def handler(event, context):
    logger.debug(f"Found EnvVar - LOCAL_BUCKET_NAME: {local_bucket_name}")
    logger.debug(f"Found EnvVar - ADMIN_BUCKET_NAME: {admin_bucket_name}")
    logger.debug(f"Found EnvVar - TOOL_NAME: {tool_name}")
    logger.debug(f"INCOMING EVENT: {event}")

    # Catch all non-triggering of events
    if event["detail-type"] not in {"Batch Job State Change"}:
        logger.info(f"Non-triggering Batch event: {event['detail-type']}")
        return
    if event["detail"]["status"] in {"TIMED_OUT"}:
        raise Exception(
            "Job timed out. Contact application owner or increase time out threshold"
        )
    if event["detail"]["status"] not in {"FAILED", "SUCCEEDED"}:
        logger.info(f"Non-triggering Batch status: STATUS: {event['detail']['status']}")
        return

    # Put logs and results CSV to S3
    try:
        for bucket in admin_bucket_name, local_bucket_name:
            get_and_put_logs(event["detail"], bucket)
        put_status(event["detail"]["status"], admin_bucket_name)
    except Exception as e:
        logger.error(json.dumps(f"Error: {str(e)}"))
        raise e


def get_and_put_logs(job_detail, bucket):
    """
    Puts logs to a cross-account S3 bucket
    :param bucket: Target bucket
    :param job_detail: Contains job_id and job_status. See https://docs.aws.amazon.com/batch/latest/APIReference/API_JobDetail.html.
    """
    job_id = job_detail["jobId"]
    job_status = job_detail["status"]

    try:
        # Get most recent log stream
        log_streams = logs_client.describe_log_streams(
            logGroupName=log_group_name,
            orderBy="LastEventTime",
            descending=True,
            limit=1,
        )

        # Get log events from the stream
        log_events = logs_client.get_log_events(
            logGroupName=log_group_name,
            logStreamName=log_streams["logStreams"][0]["logStreamName"],
            startFromHead=True,
        )
    except Exception as e:
        logger.error(f"Error getting log events from CloudWatch:\n{e}")
        raise

    log_file_name = f"{job_id}.log"

    log_file = "\n".join(
        [f"{e['timestamp']}, {e['message']}" for e in log_events["events"]]
    )

    try:
        response = s3_client.list_objects_v2(Bucket=bucket, Delimiter="/")
        objects = response.get("Contents", [])
        for obj in objects:
            key = obj["Key"]
            if (
                key.endswith(f"SUCCEEDED")
                or key.endswith(f"FAILED")
                or key.endswith(f"SUCCEEDED-{tool_name}.log")
                or key.endswith(f"FAILED-{tool_name}.log")
            ):
                s3_client.delete_object(Bucket=bucket, Key=key)
                logger.info(f"Deleted: {key}")

        s3_client.put_object(
            Body=log_file,
            Bucket=bucket,
            Key=job_status,
        )

        # Put logs to cross-account bucket LATEST directory
        s3_client.put_object(
            Body=log_file,
            Bucket=bucket,
            Key=f"latest/{job_detail['status']}-{tool_name}.log",
        )
        # Put logs to cross-account bucket ARCHIVE
        s3_client.put_object(
            Body=log_file,
            Bucket=bucket,
            Key=f"archive/{tool_name}/{job_detail['status']}/{log_file_name}",
        )
    except Exception as e:
        logger.error(f"Error writing logs to S3:\n{e}")
        raise

    logger.info(f"Log data saved successfully: {tool_name}/{log_file_name}")
    # return response


def put_status(status, bucket):
    """
    Updates the status of the tool test run to either FAILED or SUCCEEDED
    :param status:
    :param bucket:
    :return:
    """
    key = "consolidated-results.csv"
    try:
        # get CSV from s3
        response = s3_client.get_object(Bucket=bucket, Key=key)
        data = response["Body"].read().decode("utf-8")
    except Exception as e:
        logger.error(f"Error reading CSV from S3:\n{e}")
        raise

    try:
        # Read the CSV data into Python object.
        lines = csv.reader(io.StringIO(data))
        headers = next(lines)  # Skip the header row.
        updated_data = [headers]

        # Update the specific record
        for row in lines:
            if row[0] == tool_name:
                row[1] = status
            updated_data.append(row)

        # Convert updated data back to CSV format.
        output_buffer = io.StringIO()
        writer = csv.writer(output_buffer)
        writer.writerows(updated_data)
        updated_csv_data = output_buffer.getvalue()

        # Upload the updated CSV to S3, overwriting the original.
        s3_client.put_object(Bucket=bucket, Key=key, Body=updated_csv_data)
        logger.info("Successfully updated CSV in S3.")
        return
    except Exception as e:
        logger.error(f"Error processing and updating CSV in S3:\n{e}")
        raise
