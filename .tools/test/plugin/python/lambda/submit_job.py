# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import os

import boto3

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)


def handler(event, context):
    logger.debug(f"INCOMING EVENT: {event}")
    try:
        # Set up AWS Batch client.
        batch_client = boto3.client("batch")

        # Set up job payload.
        payload = {
            "jobName": os.environ["JOB_NAME"],
            "jobQueue": os.environ["JOB_QUEUE"],
            "jobDefinition": os.environ["JOB_DEFINITION"],
        }

        # Submit job.
        response = batch_client.submit_job(**payload)

        # Print job ID and status.
        logger.info(f"Submitted job {str(response)}")
    except Exception as e:
        logger.error(f"Job submission failed:\n{e}")
        raise e
