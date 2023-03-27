# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import boto3

logger = logging.getLogger()
logger.setLevel(logging.INFO)

def lambda_handler(event, context):
    try:
        # Set up Batch client
        batch = boto3.client('batch')

        # Set up job payload
        payload = {
            'jobName': 'ruby-integ',
            'jobQueue': 'arn:aws:batch:us-east-1:260778392212:job-queue/JobQueueEE3AD499-U9RwL1RI5OhB9W2r',
            'jobDefinition': 'arn:aws:batch:us-east-1:260778392212:job-definition/batchjobdeffromecrE0E30-24044f1878c8bae:5'
        }

        # Submit job
        response = batch.submit_job(**payload)

        # Print job ID and status
        logger.info(f"Submitted job {str(response)}")
    except Exception as e:
        logger.error(f"Job submission failed:\n{e}")
