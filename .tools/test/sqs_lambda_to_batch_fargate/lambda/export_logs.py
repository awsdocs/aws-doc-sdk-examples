import json
import logging
import os
import random

import boto3

s3_client = boto3.client("s3")
s3_resource = boto3.resource("s3")
logs_client = boto3.client("logs")

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

log_group_name = "/aws/batch/job"


def handler(event, context):
    logger.debug(f"BUCKET_NAME: {os.environ['BUCKET_NAME']}")
    logger.debug(f"INCOMING EVENT: {event}")

    if "Batch Job State Change" not in event["detail-type"]:
        logger.info(f"Non-triggering Batch event: {event['detail-type']}")
        return
    if "TIMED_OUT" in event["detail"]["status"]:
        raise Exception(
            "Job timed out. Contact application owner or increase time out threshold"
        )
    if event["detail"]["status"] not in ["FAILED", "SUCCEEDED"]:
        logger.info(f"Non-triggering Batch status: STATUS: {event['detail']['status']}")
        return

    try:
        get_and_put_logs(event["detail"])
    except Exception as e:
        logger.error(json.dumps(f"Error: {str(e)}"))
        raise e


def get_and_put_logs(job_detail):
    """
    Puts logs to a cross-account S3 bucket
    :param job_detail: Contains job_id and job_status
    :return:
    """
    job_id = job_detail["jobId"]
    job_status = job_detail["status"]

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

    log_file_name = f"{job_id}.log"

    log_file = "\n".join(
        [f"{e['timestamp']}, {e['message']}" for e in log_events["events"]]
    )

    # Copy logs to local and cross-account buckets
    for bucket in [os.environ["PRODUCER_BUCKET_NAME"], os.environ["BUCKET_NAME"]]:
        # Put status file into top-level directory
        s3_client.put_object(
            Body=log_file,
            Bucket=bucket,
            Key=job_status,
        )
        # Put logs to cross-account bucket LATEST directory
        s3_client.put_object(
            Body=log_file,
            Bucket=bucket,
            Key=f"latest/{os.environ['LANGUAGE_NAME']}.log",
        )
        # Put logs to cross-account bucket ARCHIVE
        s3_client.put_object(
            Body=log_file,
            Bucket=bucket,
            Key=f"archive/{os.environ['LANGUAGE_NAME']}/{log_file_name}",
        )

    logger.info(
        f"Log data saved successfully: {os.environ['LANGUAGE_NAME']}/{log_file_name}"
    )
