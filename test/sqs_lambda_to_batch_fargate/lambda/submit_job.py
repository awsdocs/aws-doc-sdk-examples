# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import boto3
import os

logger = logging.getLogger()
logger.setLevel(logging.INFO)

def lambda_handler(event, context):
    try:
        # Set up AWS Batch client.
        batch = boto3.client('batch')

        # Log key details.
        logger.info(f"JOB_NAME: {os.environ['JOB_NAME']}")
        logger.info(f"JOB_QUEUE: {os.environ['JOB_QUEUE']}")
        logger.info(f"JOB_DEFINITION: {os.environ['JOB_DEFINITION']}")

        # Set up job payload.
        payload = {
            'jobName': os.environ['JOB_NAME'],
            'jobQueue': os.environ['JOB_QUEUE'],
            'jobDefinition': os.environ['JOB_DEFINITION']
        }

        # Submit job.
        response = batch.submit_job(**payload)

        # Print job ID and status.
        logger.info(f"Submitted job {str(response)}")
    except Exception as e:
        logger.error(f"Job submission failed:\n{e}")
